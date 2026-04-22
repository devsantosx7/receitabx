package jsignnet.aplicacao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.erro.ErroAoLerAssinatura;
import jsignnet.erro.JSignExcecaoCancelamento;
import jsignnet.erro.JSignException;
import jsignnet.infra.CMSProcessablePartialFile;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ValidadorAssinatura.class */
public class ValidadorAssinatura {
    private static final char[] HEX_ARRAY;

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ValidadorAssinatura$ConstrutorSignerInformationVerifier.class */
    private interface ConstrutorSignerInformationVerifier {
        SignerInformationVerifier construir(X509Certificate x509Certificate) throws OperatorCreationException;
    }

    static {
        JSignNet.adicionaProviders();
        HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    }

    protected ValidadorAssinatura() {
    }

    public boolean verificar(byte[] assinatura, byte[] conteudoAssinado) throws JSignException {
        return verificar(assinatura, (CMSProcessable) new CMSProcessableByteArray(conteudoAssinado), (ConstrutorSignerInformationVerifier) new ConstrutorSignerInformationVerifierCompleto());
    }

    public boolean verificarIgnorandoDataAssinatura(byte[] assinatura, byte[] conteudoAssinado) throws JSignException {
        return verificar(assinatura, (CMSProcessable) new CMSProcessableByteArray(conteudoAssinado), (ConstrutorSignerInformationVerifier) new ConstrutorSignerInformationVerifierSemConferirData());
    }

    public boolean verificar(byte[] assinatura, File arquivo, long tamanhoArquivo) throws JSignException {
        return verificar(assinatura, new CMSProcessablePartialFile(arquivo, tamanhoArquivo), new ConstrutorSignerInformationVerifierCompleto());
    }

    public boolean verificarIgnorandoDataAssinatura(byte[] assinatura, File arquivo, long tamanhoArquivo) throws JSignException {
        return verificar(assinatura, new CMSProcessablePartialFile(arquivo, tamanhoArquivo), new ConstrutorSignerInformationVerifierSemConferirData());
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ValidadorAssinatura$ConstrutorSignerInformationVerifierCompleto.class */
    private static class ConstrutorSignerInformationVerifierCompleto implements ConstrutorSignerInformationVerifier {
        private ConstrutorSignerInformationVerifierCompleto() {
        }

        @Override // jsignnet.aplicacao.ValidadorAssinatura.ConstrutorSignerInformationVerifier
        public SignerInformationVerifier construir(X509Certificate certificado) throws OperatorCreationException {
            return new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(certificado);
        }
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ValidadorAssinatura$ConstrutorSignerInformationVerifierSemConferirData.class */
    private static class ConstrutorSignerInformationVerifierSemConferirData implements ConstrutorSignerInformationVerifier {
        private ConstrutorSignerInformationVerifierSemConferirData() {
        }

        @Override // jsignnet.aplicacao.ValidadorAssinatura.ConstrutorSignerInformationVerifier
        public SignerInformationVerifier construir(X509Certificate certificado) throws OperatorCreationException {
            return new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(certificado.getPublicKey());
        }
    }

    private boolean verificar(byte[] assinatura, CMSProcessable conteudoAssinado, ConstrutorSignerInformationVerifier construtorVerificador) throws JSignException, CMSException {
        boolean resposta = false;
        try {
            Store certs = getCertificateStore(assinatura, conteudoAssinado);
            for (SignerInformation assinante : getAssinantes(assinatura, conteudoAssinado)) {
                if (resposta) {
                    break;
                }
                X509Certificate certificadoAssinante = getCertificadoAssinante(assinante, certs);
                if (certificadoAssinante != null) {
                    resposta = assinante.verify(construtorVerificador.construir(certificadoAssinante));
                }
            }
            return resposta;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                if (e.getMessage().indexOf("content hash found in signed attributes different") != -1) {
                    return false;
                }
                JSignNet.logger.severe("Erro no processo de verificação da assinatura: " + e.getMessage());
            } else {
                JSignNet.logger.severe("Erro no processo de verificação da assinatura: " + e.toString());
            }
            throw new JSignException(e, "Erro no processo de verificação da assinatura: " + e.getMessage());
        }
    }

    private Store getCertificateStore(byte[] assinatura, CMSProcessable conteudoAssinado) throws CMSException {
        if (null == Security.getProvider("BC")) {
            Security.addProvider(new BouncyCastleProvider());
        }
        CMSSignedData signData = new CMSSignedData(conteudoAssinado, assinatura);
        return signData.getCertificates();
    }

    private Iterable<SignerInformation> getAssinantes(byte[] assinatura, CMSProcessable conteudoAssinado) throws CMSException {
        CMSSignedData signData = new CMSSignedData(conteudoAssinado, assinatura);
        SignerInformationStore signers = signData.getSignerInfos();
        Collection<SignerInformation> c = signers.getSigners();
        return c;
    }

    public Map<String, X509CertificadoWrapper> getCertificadosAssinantes(byte[] assinatura, byte[] conteudoAssinado) throws JSignException {
        try {
            return getCertificadosAssinantes(assinatura, (CMSProcessable) new CMSProcessableByteArray(conteudoAssinado));
        } catch (CMSException e) {
            throw new JSignException(e, "Erro no processo de obtenção da data da assinatura: " + e.getMessage());
        } catch (CertificateException e2) {
            throw new JSignException(e2, "Erro no processo de obtenção da data da assinatura: " + e2.getMessage());
        }
    }

    public Map<String, X509CertificadoWrapper> getCertificadosAssinantes(byte[] assinatura, File arquivo, long tamanhoArquivo) throws JSignException {
        try {
            return getCertificadosAssinantes(assinatura, new CMSProcessablePartialFile(arquivo, tamanhoArquivo));
        } catch (CMSException e) {
            throw new JSignException(e, "Erro no processo de obtenção da data da assinatura: " + e.getMessage());
        } catch (CertificateException e2) {
            throw new JSignException(e2, "Erro no processo de obtenção da data da assinatura: " + e2.getMessage());
        }
    }

    private Map<String, X509CertificadoWrapper> getCertificadosAssinantes(byte[] assinatura, CMSProcessable conteudoAssinado) throws CMSException, CertificateException {
        CMSSignedData signData = new CMSSignedData(conteudoAssinado, assinatura);
        Store certs = signData.getCertificates();
        Map<String, X509CertificadoWrapper> certificados = new HashMap<>();
        for (SignerInformation assinante : getAssinantes(assinatura, conteudoAssinado)) {
            X509Certificate certificado = getCertificadoAssinante(assinante, certs);
            certificados.put(toString(assinante.getSID()), new X509CertificadoWrapper(certificado));
        }
        return certificados;
    }

    private X509Certificate getCertificadoAssinante(SignerInformation assinante, Store certs) throws CertificateException {
        Collection<X509CertificateHolder> certCollection = certs.getMatches(assinante.getSID());
        Iterator<X509CertificateHolder> certIt = certCollection.iterator();
        if (certIt != null) {
            X509CertificateHolder certHolder = certIt.next();
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
            return cert;
        }
        return null;
    }

    public boolean verificarHash(Hash algoritmo, byte[] assinatura, File arquivo, long tamanhoArquivo, ControleProgresso progresso) throws JSignException, NoSuchAlgorithmException, IOException {
        progresso.setMinimo(0L);
        progresso.setMaximo(arquivo.length());
        InputStream in = new FileInputStreamParcial(arquivo, tamanhoArquivo);
        byte[] hash = algoritmo.geraDigest(in, progresso);
        if (hash == null) {
            progresso.tarefaTerminou();
            throw new JSignExcecaoCancelamento("Usuário cancelou a verificação de assinatura digital.");
        }
        try {
            boolean zVerificar = verificar(assinatura, hash);
            progresso.tarefaTerminou();
            return zVerificar;
        } catch (Throwable th) {
            progresso.tarefaTerminou();
            throw th;
        }
    }

    public String getOIDAlgoritmoAssinatura(byte[] assinatura) throws ErroAoLerAssinatura {
        if (assinatura == null) {
            throw new IllegalArgumentException("A assinatura deve ser informada.");
        }
        try {
            CMSSignedData signData = new CMSSignedData(assinatura);
            try {
                SignerInformationStore signers = signData.getSignerInfos();
                Collection<?> c = signers.getSigners();
                if (c.size() > 1) {
                    throw new IllegalArgumentException("Assinatura tem mais de um assinante.");
                }
                if (c.size() <= 0) {
                    throw new IllegalArgumentException("Assinatura sem assinante.");
                }
                Iterator<?> it = c.iterator();
                SignerInformation signer = (SignerInformation) it.next();
                return signer.getDigestAlgOID();
            } catch (NullPointerException e) {
                throw new ErroAoLerAssinatura("A assinatura digital não é válida.");
            }
        } catch (CMSException e2) {
            throw new ErroAoLerAssinatura("Não foi possível identificar o algoritmo na assinatura.");
        } catch (IllegalArgumentException e3) {
            throw new ErroAoLerAssinatura("A assinatura digital não é válida.");
        }
    }

    public Hash getAlgoritmoAssinatura(byte[] assinatura) throws ErroAoLerAssinatura {
        String oidAlgoritmo = getOIDAlgoritmoAssinatura(assinatura);
        return Hash.getHashDeOID(oidAlgoritmo);
    }

    public byte[] extrairCertificado(byte[] assinatura) throws JSignException {
        try {
            if (null == Security.getProvider("BC")) {
                Security.addProvider(new BouncyCastleProvider());
            }
            CMSSignedData signData = new CMSSignedData(assinatura);
            Store certs = signData.getCertificates();
            SignerInformationStore signers = signData.getSignerInfos();
            Collection<?> c = signers.getSigners();
            if (c.size() != 1) {
                return null;
            }
            Iterator<?> it = c.iterator();
            SignerInformation signer = (SignerInformation) it.next();
            Collection<?> certCollection = certs.getMatches(signer.getSID());
            Iterator<?> certIt = certCollection.iterator();
            X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
            return cert.getEncoded();
        } catch (Exception e) {
            JSignNet.logger.severe("Erro no processo de extração do certificado: " + e.getMessage());
            throw new JSignException(e, "Erro no processo de extração do certificado: " + e.getMessage());
        }
    }

    public Map<String, Date> getDataAssinaturas(byte[] assinatura, byte[] conteudoAssinado) throws JSignException {
        try {
            return getDataAssinaturas(assinatura, (CMSProcessable) new CMSProcessableByteArray(conteudoAssinado));
        } catch (CMSException e) {
            throw new JSignException(e, "Erro no processo de obtenção da data da assinatura: " + e.getMessage());
        } catch (ParseException e2) {
            throw new JSignException(e2, "Erro no processo de obtenção da data da assinatura: " + e2.getMessage());
        }
    }

    public Map<String, Date> getDataAssinaturas(byte[] assinatura, File arquivo, long tamanhoArquivo) throws JSignException {
        try {
            return getDataAssinaturas(assinatura, new CMSProcessablePartialFile(arquivo, tamanhoArquivo));
        } catch (ParseException e) {
            throw new JSignException(e, "Erro no processo de obtenção da data da assinatura: " + e.getMessage());
        } catch (CMSException e2) {
            throw new JSignException(e2, "Erro no processo de obtenção da data da assinatura: " + e2.getMessage());
        }
    }

    private Map<String, Date> getDataAssinaturas(byte[] assinatura, CMSProcessable conteudoAssinado) throws CMSException, ParseException {
        Map<String, Date> datas = new HashMap<>();
        for (SignerInformation assinante : getAssinantes(assinatura, conteudoAssinado)) {
            AttributeTable signedAttributes = assinante.getSignedAttributes();
            Attribute signingTime = signedAttributes.get(CMSAttributes.signingTime);
            if (signingTime != null) {
                Date dataHoraAssinatura = signingTime.getAttrValues().getObjectAt(0).getDate();
                datas.put(toString(assinante.getSID()), dataHoraAssinatura);
            }
        }
        return datas;
    }

    private String toString(SignerId id) {
        String serial = id.getSerialNumber() != null ? id.getSerialNumber().toString(16) : "";
        return bytesToHex(id.getSubjectKeyIdentifier()) + "/" + serial;
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[(j * 2) + 1] = HEX_ARRAY[v & 15];
        }
        return new String(hexChars);
    }
}
