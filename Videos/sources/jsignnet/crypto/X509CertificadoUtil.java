package jsignnet.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import jsignnet.aplicacao.JSignNet;
import jsignnet.erro.JSignException;
import jsignnet.infra.Recursos;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Strings;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/X509CertificadoUtil.class */
public final class X509CertificadoUtil {
    private static final String X509_CERT_TYPE = "X.509";
    private static final String AUTHORITY_KEY_IDENTIFIER_OID = "2.5.29.35";
    private static final String SUBJECT_ALTERNATIVE_NAME_OID = "2.5.29.17";
    private static final String KEY_USAGE_OID = "2.5.29.15";
    private static final String EXTENDED_KEY_USAGE_OID = "2.5.29.37";

    private X509CertificadoUtil() {
    }

    public static X509Certificate[] converteCertificados(Certificate[] arrayCertificados) throws CertificateException {
        X509Certificate[] resposta = new X509Certificate[arrayCertificados.length];
        for (int i = 0; i < arrayCertificados.length; i++) {
            resposta[i] = converteCertificado(arrayCertificados[i]);
        }
        return resposta;
    }

    public static X509Certificate converteCertificado(Certificate certIn) throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance(X509_CERT_TYPE);
        ByteArrayInputStream stream = new ByteArrayInputStream(certIn.getEncoded());
        return (X509Certificate) cf.generateCertificate(stream);
    }

    public static String getIdentificadorChaveAutoridade(X509Certificate certificado) throws IOException {
        byte[] extensionValue = certificado.getExtensionValue(AUTHORITY_KEY_IDENTIFIER_OID);
        if (extensionValue == null) {
            return "";
        }
        byte[] bOctets = toDER(extensionValue).getOctets();
        ASN1Sequence asn1Seq = toDER(bOctets);
        DEROctetString chaveIdentificacao = null;
        ASN1Sequence autoridadeCertificadoraASN1 = null;
        DEROctetString numeroSerialCertificado = null;
        int tamanho = asn1Seq.size();
        for (int i = 0; i < tamanho; i++) {
            DLTaggedObject derTagObjeto = asn1Seq.getObjectAt(i);
            ASN1Sequence baseObject = derTagObjeto.getBaseObject();
            switch (derTagObjeto.getTagNo()) {
                case 0:
                    chaveIdentificacao = (DEROctetString) baseObject;
                    break;
                case 1:
                    if (baseObject instanceof ASN1Sequence) {
                        autoridadeCertificadoraASN1 = baseObject;
                        break;
                    } else {
                        autoridadeCertificadoraASN1 = new DERSequence(baseObject);
                        break;
                    }
                case 2:
                    numeroSerialCertificado = (DEROctetString) baseObject;
                    break;
            }
        }
        StringBuffer resposta = new StringBuffer();
        if (chaveIdentificacao != null) {
            byte[] bKeyIdent = chaveIdentificacao.getOctets();
            resposta.append(MessageFormat.format("Chave: {0}", converteByteParaStringHexa(bKeyIdent)));
            resposta.append('\n');
        }
        if (autoridadeCertificadoraASN1 != null) {
            resposta.append("contato");
            resposta.append('\n');
            int len = autoridadeCertificadoraASN1.size();
            for (int i2 = 0; i2 < len; i2++) {
                DLTaggedObject generalName = autoridadeCertificadoraASN1.getObjectAt(i2);
                resposta.append('\t');
                resposta.append(getNome(generalName));
                resposta.append('\n');
            }
        }
        if (numeroSerialCertificado != null) {
            byte[] bCertSerialNumber = numeroSerialCertificado.getOctets();
            resposta.append(MessageFormat.format("Número Serial {0}", converteByteParaStringHexa(bCertSerialNumber)));
            resposta.append('\n');
        }
        return resposta.toString();
    }

    static ASN1Object toDER(byte[] bytes) throws IOException {
        ASN1InputStream in = new ASN1InputStream(new ByteArrayInputStream(bytes));
        try {
            ASN1Primitive object = in.readObject();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            return object;
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    private static String converteByteParaStringHexa(ASN1Integer inteiroDER) {
        String numeroControle = inteiroDER.getValue().toString(16).toUpperCase();
        StringBuffer resposta = new StringBuffer();
        for (int i = 0; i < numeroControle.length(); i++) {
            resposta.append(numeroControle.charAt(i));
            if ((i + 1) % 4 == 0 && i + 1 != numeroControle.length()) {
                resposta.append(' ');
            }
        }
        return resposta.toString();
    }

    private static String converteByteParaStringHexa(byte[] bytes) {
        StringBuffer resposta = new StringBuffer(new BigInteger(1, bytes).toString(16).toUpperCase());
        if (resposta.length() > 4) {
            for (int i = 4; i < resposta.length(); i += 5) {
                resposta.insert(i, ' ');
            }
        }
        return resposta.toString();
    }

    private static String getNome(DLTaggedObject nomeDER) {
        StringBuffer resposta = new StringBuffer();
        switch (nomeDER.getTagNo()) {
            case 0:
                ASN1Sequence objetoASN1 = nomeDER.getBaseObject();
                String sOid = objetoASN1.getObjectAt(0).getId();
                String sVal = converteObjetoEmHex(objetoASN1.getObjectAt(1));
                resposta.append(MessageFormat.format(Recursos.getString("OutrosNomes"), sOid, sVal));
                break;
            case 1:
                DEROctetString rfc822 = nomeDER.getBaseObject();
                String sRfc822 = new String(rfc822.getOctets());
                resposta.append(MessageFormat.format(Recursos.getString("NomeRfc822"), sRfc822));
                break;
            case 2:
                DEROctetString dns = nomeDER.getBaseObject();
                String sDns = new String(dns.getOctets());
                resposta.append(MessageFormat.format(Recursos.getString("NomeDNS"), sDns));
                break;
            case 3:
            case TipoKeyStore.UBER_ID /* 5 */:
            default:
                resposta.append(MessageFormat.format(Recursos.getString("TipoNaoSuportado"), "" + nomeDER.getTagNo()));
                break;
            case 4:
                ASN1Sequence diretorioASN1 = nomeDER.getBaseObject();
                X500Name nome = X500Name.getInstance(diretorioASN1);
                resposta.append(MessageFormat.format(Recursos.getString("NomeDiretorio"), nome.toString()));
                break;
            case TipoKeyStore.CAPI_ID /* 6 */:
                DEROctetString uri = nomeDER.getBaseObject();
                String sUri = new String(uri.getOctets());
                resposta.append(MessageFormat.format(Recursos.getString("NomeURI"), sUri));
                break;
            case TipoKeyStore.KEYCHAINSTORE_ID /* 7 */:
                DEROctetString enderecoIp = nomeDER.getBaseObject();
                byte[] ip = enderecoIp.getOctets();
                StringBuffer ipString = new StringBuffer();
                int bl = ip.length;
                for (int i = 0; i < bl; i++) {
                    ipString.append(ip[i] & 255);
                    if (i + 1 < ip.length) {
                        ipString.append('.');
                    }
                }
                resposta.append(MessageFormat.format(Recursos.getString("EnderecoIp"), ipString.toString()));
                break;
            case 8:
                DEROctetString idRegistro = nomeDER.getBaseObject();
                byte[] idRegistroByte = idRegistro.getOctets();
                StringBuffer idRegistroString = new StringBuffer();
                for (int i2 = 0; i2 < idRegistroByte.length; i2++) {
                    idRegistroString.append(idRegistroByte[i2] & 255);
                    if (i2 + 1 < idRegistroByte.length) {
                        idRegistroString.append('.');
                    }
                }
                resposta.append(MessageFormat.format(Recursos.getString("IdRegistro"), idRegistroString.toString()));
                break;
        }
        return resposta.toString();
    }

    public static String getNomesAlternativosSujeito(X509Certificate certificado) throws IOException {
        byte[] extensao = certificado.getExtensionValue(SUBJECT_ALTERNATIVE_NAME_OID);
        if (extensao == null) {
            return "";
        }
        byte[] octetos = toDER(extensao).getOctets();
        ASN1Sequence nomes = toDER(octetos);
        StringBuffer resposta = new StringBuffer();
        int len = nomes.size();
        for (int i = 0; i < len; i++) {
            resposta.append(getNome(nomes.getObjectAt(i)));
            resposta.append('\n');
        }
        return resposta.toString();
    }

    public static String getNomesAlternativosSujeito(X509Certificate certificado, String oid) throws IOException {
        String resposta = "";
        byte[] extensao = certificado.getExtensionValue(SUBJECT_ALTERNATIVE_NAME_OID);
        if (extensao != null) {
            byte[] octetos = toDER(extensao).getOctets();
            ASN1Sequence generalNames = toDER(octetos);
            int i = 0;
            int len = generalNames.size();
            while (true) {
                if (i >= len) {
                    break;
                }
                DLTaggedObject umGeneralName = generalNames.getObjectAt(i);
                if (umGeneralName.getTagNo() == 0) {
                    ASN1Sequence objetoCodificadoASN1 = umGeneralName.getBaseObject();
                    String sOid = objetoCodificadoASN1.getObjectAt(0).getId();
                    if (sOid.equals(oid)) {
                        ASN1TaggedObject envelopeValor = objetoCodificadoASN1.getObjectAt(1);
                        ASN1Object valor = envelopeValor.getBaseObject();
                        resposta = valor instanceof DERPrintableString ? DERPrintableString.getInstance(valor).getString() : valor instanceof DEROctetString ? Strings.fromByteArray(DEROctetString.getInstance(valor).getOctets()) : DERUTF8String.getInstance(valor).getString();
                    }
                }
                i++;
            }
        }
        return resposta;
    }

    private static String converteObjetoEmHex(Object objeto) {
        if (objeto instanceof ASN1String) {
            return ((ASN1String) objeto).getString();
        }
        if (objeto instanceof ASN1Integer) {
            return converteByteParaStringHexa((ASN1Integer) objeto);
        }
        if (objeto instanceof byte[]) {
            return converteByteParaStringHexa((byte[]) objeto);
        }
        if (objeto instanceof ASN1TaggedObject) {
            ASN1TaggedObject tagObj = (ASN1TaggedObject) objeto;
            return "[" + tagObj.getTagNo() + "] " + converteObjetoEmHex(tagObj.getBaseObject());
        }
        if (objeto instanceof DEROctetString) {
            return Strings.fromByteArray(((DEROctetString) objeto).getOctets());
        }
        ASN1UTF8String resposta = ASN1UTF8String.getInstance(objeto);
        return resposta.getString();
    }

    public static String getUsoChave(X509Certificate certificado) throws IOException {
        byte[] extensao = certificado.getExtensionValue(KEY_USAGE_OID);
        if (extensao == null) {
            return "";
        }
        byte[] octetos = toDER(extensao).getOctets();
        DERBitString derBitString = toDER(octetos);
        StringBuffer resposta = new StringBuffer();
        byte[] arrayBytes = derBitString.getBytes();
        boolean keyAgreement = false;
        for (int i = 0; i < arrayBytes.length; i++) {
            boolean[] b = new boolean[8];
            b[7] = (arrayBytes[i] & 128) == 128;
            b[6] = (arrayBytes[i] & 64) == 64;
            b[5] = (arrayBytes[i] & 32) == 32;
            b[4] = (arrayBytes[i] & 16) == 16;
            b[3] = (arrayBytes[i] & 8) == 8;
            b[2] = (arrayBytes[i] & 4) == 4;
            b[1] = (arrayBytes[i] & 2) == 2;
            b[0] = (arrayBytes[i] & 1) == 1;
            if (i == 0) {
                if (b[7]) {
                    resposta.append(Recursos.getString("DigitalSignatureKeyUsageString"));
                    resposta.append('\n');
                }
                if (b[6]) {
                    resposta.append(Recursos.getString("NonRepudiationKeyUsageString"));
                    resposta.append('\n');
                }
                if (b[5]) {
                    resposta.append(Recursos.getString("KeyEnciphermentKeyUsageString"));
                    resposta.append('\n');
                }
                if (b[4]) {
                    resposta.append(Recursos.getString("DataEnciphermentKeyUsageString"));
                    resposta.append('\n');
                }
                if (b[3]) {
                    resposta.append(Recursos.getString("KeyAgreementKeyUsageString"));
                    resposta.append('\n');
                    keyAgreement = true;
                }
                if (b[2]) {
                    resposta.append(Recursos.getString("KeyCertSignKeyUsageString"));
                    resposta.append('\n');
                }
                if (b[1]) {
                    resposta.append(Recursos.getString("CrlSignKeyUsageString"));
                    resposta.append('\n');
                }
                if (b[0] && keyAgreement) {
                    resposta.append(Recursos.getString("EncipherOnlyKeyUsageString"));
                    resposta.append('\n');
                }
            } else if (i == 1 && b[7] && keyAgreement) {
                resposta.append(Recursos.getString("DecipherOnlyKeyUsageString"));
                resposta.append('\n');
            }
        }
        return resposta.toString();
    }

    public static String getUsoExtendidoChave(X509Certificate certificado) throws IOException {
        String descricaoRecurso;
        byte[] extensao = certificado.getExtensionValue(EXTENDED_KEY_USAGE_OID);
        if (extensao == null) {
            return "";
        }
        byte[] octetos = toDER(extensao).getOctets();
        ASN1Sequence sequenciaASN1 = toDER(octetos);
        StringBuffer resposta = new StringBuffer();
        int len = sequenciaASN1.size();
        for (int i = 0; i < len; i++) {
            String oid = sequenciaASN1.getObjectAt(i).getId();
            try {
                descricaoRecurso = Recursos.getString(oid);
            } catch (MissingResourceException e) {
                descricaoRecurso = Recursos.getString("RecursoDesconhecido");
            }
            resposta.append(MessageFormat.format(descricaoRecurso, oid));
            resposta.append('\n');
        }
        return resposta.toString();
    }

    public static X509CertificadoWrapper certificadoFromBytes(byte[] dados) throws JSignException, CertificateException {
        try {
            Certificate cert = CertificateFactory.getInstance("X509", "BC").generateCertificate(new ASN1InputStream(dados));
            return new X509CertificadoWrapper(converteCertificado(cert));
        } catch (Exception e) {
            JSignNet.logger.severe("Erro no processo de parsing do certificado: " + e.getMessage());
            throw new JSignException(e, "Erro no processo de parsing do certificado: " + e.getMessage());
        }
    }
}
