package jsignnet.crypto;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JDialog;
import jsignnet.aplicacao.ControleProgresso;
import jsignnet.aplicacao.FileInputStreamParcial;
import jsignnet.aplicacao.Hash;
import jsignnet.aplicacao.JSignNet;
import jsignnet.erro.CertificadoNaoFoiLido;
import jsignnet.erro.ErroAoGerarPKCS7;
import jsignnet.erro.ErroDeES;
import jsignnet.erro.JSignExcecaoCadeiaVazia;
import jsignnet.erro.JSignExcecaoCancelamento;
import jsignnet.erro.JSignExcecaoFatal;
import jsignnet.erro.JSignException;
import jsignnet.gui.DialogoSenha;
import jsignnet.infra.Recursos;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.DLTaggedObject;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInfoGeneratorBuilder;
import org.bouncycastle.cms.SimpleAttributeTableGenerator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/X509CertificadoWrapper.class */
public class X509CertificadoWrapper {
    private static final String PREFIXO_ERRO_ASSINATURA = "Erro ao assinar digitalmente. ";
    private static final String PADRAO_ALGORITMO_ASSINATURA = "(\\w+)with(\\w+)";
    private KeyStore keystore;
    private X509Certificate[] cadeiaCertificadosX509;
    private String alias;
    private char[] senhaContainer;
    private boolean _fAddDefaultAttributes;
    private char[] _ultimaSenhaFornecida;
    private boolean _reutilizarSenha;
    private static final Map<Class<?>, Object> MENSAGENS_ERRO_ASSINATURA = new HashMap();

    public X509CertificadoWrapper(String alias, KeyStore ks, char[] senha) throws JSignExcecaoCadeiaVazia, KeyStoreException, CertificateException {
        this._fAddDefaultAttributes = false;
        this._ultimaSenhaFornecida = null;
        this._reutilizarSenha = false;
        this.alias = alias;
        this.keystore = ks;
        if (senha != null) {
            setSenhaContainer(senha);
        }
        populaCertificados();
    }

    public X509CertificadoWrapper(X509Certificate certificate) {
        this._fAddDefaultAttributes = false;
        this._ultimaSenhaFornecida = null;
        this._reutilizarSenha = false;
        this.cadeiaCertificadosX509 = new X509Certificate[1];
        this.cadeiaCertificadosX509[0] = certificate;
    }

    protected X509CertificadoWrapper(X509CertificadoWrapper outro) {
        this._fAddDefaultAttributes = false;
        this._ultimaSenhaFornecida = null;
        this._reutilizarSenha = false;
        this.alias = outro.alias;
        this.keystore = outro.keystore;
        if (outro.senhaContainer != null) {
            this.senhaContainer = (char[]) outro.senhaContainer.clone();
        }
        if (outro.cadeiaCertificadosX509 != null) {
            this.cadeiaCertificadosX509 = (X509Certificate[]) outro.cadeiaCertificadosX509.clone();
        }
        this._fAddDefaultAttributes = outro._fAddDefaultAttributes;
    }

    public void setAdicionarAtributosAssinatura(boolean f) {
        this._fAddDefaultAttributes = f;
    }

    public String getEmitidoPara() {
        return extraiPrimeiraEntrada("CN", getCertificado().getSubjectDN().toString());
    }

    public String getEmitidoPor() {
        return extraiPrimeiraEntrada("CN", getCertificado().getIssuerDN().toString());
    }

    public String getOrganizacaoEmissora() {
        return extraiPrimeiraEntrada("O", getCertificado().getIssuerDN().toString());
    }

    public String getUnidadeOrganizacionalEmissora() {
        return extraiPrimeiraEntrada("OU", getCertificado().getIssuerDN().toString());
    }

    public Date getDataEmissao() {
        return getCertificado().getNotBefore();
    }

    public Date getDataVencimento() {
        return getCertificado().getNotAfter();
    }

    private String extraiPrimeiraEntrada(String campo, String valor) {
        StringTokenizer token = new StringTokenizer(valor, ",");
        while (token.hasMoreElements()) {
            String item = token.nextToken();
            if (item.indexOf(campo + "=") > -1) {
                return item.substring(item.indexOf("=") + 1);
            }
        }
        return "";
    }

    public KeyStore getKeystore() {
        return this.keystore;
    }

    public String getAlias() {
        return this.alias;
    }

    protected X509Certificate getCertificado() {
        return this.cadeiaCertificadosX509[0];
    }

    public X509Certificate[] getCadeiaCertificados() {
        return this.cadeiaCertificadosX509;
    }

    public X509CertificadoWrapper[] getCadeiaCertificadosWrapper() {
        X509CertificadoWrapper[] cadeiaWrapper = new X509CertificadoWrapper[this.cadeiaCertificadosX509.length];
        for (int i = 0; i < this.cadeiaCertificadosX509.length; i++) {
            cadeiaWrapper[i] = new X509CertificadoWrapper(this.cadeiaCertificadosX509[i]);
        }
        return cadeiaWrapper;
    }

    private void populaCertificados() throws JSignExcecaoCadeiaVazia, KeyStoreException, CertificateException {
        if (getKeystore().isKeyEntry(getAlias())) {
            this.cadeiaCertificadosX509 = X509CertificadoUtil.converteCertificados(getKeystore().getCertificateChain(getAlias()));
            if (this.cadeiaCertificadosX509.length <= 0) {
                throw new JSignExcecaoCadeiaVazia("Não foi possível recuperar a cadeia do certificado " + getAlias() + '.');
            }
        } else {
            this.cadeiaCertificadosX509 = new X509Certificate[1];
            this.cadeiaCertificadosX509[0] = X509CertificadoUtil.converteCertificado(getKeystore().getCertificate(getAlias()));
        }
    }

    public String getOrganizacao() {
        return extraiPrimeiraEntrada("O", getCertificado().getIssuerDN().toString());
    }

    public String getUnidadeOrganizacional() {
        return extraiPrimeiraEntrada("OU", getCertificado().getIssuerDN().toString());
    }

    public String getSerie() {
        return getCertificado().getSerialNumber().toString();
    }

    public String getNumeroSerieHexa() {
        BigInteger serial = getCertificado().getSerialNumber();
        byte[] rbSerial = serial.toByteArray();
        StringBuffer sb = new StringBuffer();
        for (int b : rbSerial) {
            if (b < 0) {
                b += 256;
            }
            String byteHexa = Integer.toHexString(b);
            if (byteHexa.length() == 1) {
                sb.append('0');
            }
            sb.append(byteHexa);
        }
        return sb.toString();
    }

    public String getVersao() {
        return "Versão " + String.valueOf(getCertificado().getVersion());
    }

    public String getAlgoritmo() {
        return getCertificado().getSigAlgName();
    }

    public String getAssuntoPrincipal() {
        return getCertificado().getSubjectDN().getName();
    }

    public byte[] getEncoded() throws CertificadoNaoFoiLido {
        try {
            byte[] resposta = getCertificado().getEncoded();
            return resposta;
        } catch (CertificateEncodingException e) {
            throw new CertificadoNaoFoiLido(e);
        }
    }

    public String getSHA1FingerPrint() throws CertificadoNaoFoiLido {
        try {
            byte[] fingerprint = Hash.SHA1.geraDigest(new ByteArrayInputStream(getEncoded()), null);
            return formatarFingerPrint(fingerprint);
        } catch (IOException e) {
            JSignNet.logger.log(Level.SEVERE, "Falha ao ler a memória", (Throwable) e);
            throw new JSignExcecaoFatal("Falha ao ler a memória", e);
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            throw new JSignExcecaoFatal("SHA1 não disponível.", e2);
        }
    }

    private static String formatarFingerPrint(byte[] bFingerPrint) {
        Formatter formatter = new Formatter();
        for (int i = 0; i < bFingerPrint.length - 1; i++) {
            formatter.format("%02x:", Byte.valueOf(bFingerPrint[i]));
        }
        formatter.format("%02x", Byte.valueOf(bFingerPrint[bFingerPrint.length - 1]));
        String retorno = formatter.toString().toUpperCase();
        formatter.close();
        return retorno;
    }

    public String getMD5FingerPrint() throws CertificadoNaoFoiLido {
        try {
            byte[] fingerprint = Hash.MD5.geraDigest(new ByteArrayInputStream(getEncoded()), null);
            return formatarFingerPrint(fingerprint);
        } catch (IOException e) {
            JSignNet.logger.log(Level.SEVERE, "Falha ao ler a memória", (Throwable) e);
            throw new JSignExcecaoFatal("Falha ao ler a memória", e);
        } catch (NoSuchAlgorithmException e2) {
            e2.printStackTrace();
            throw new JSignExcecaoFatal("MD5 não disponível.", e2);
        }
    }

    public PrivateKey getChavePrivada(char[] pin) throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        PrivateKey chave = (PrivateKey) getKeystore().getKey(getAlias(), pin);
        if (chave == null && ((pin == null || Arrays.equals(new char[0], pin)) && getProvider().getName().equals("Apple"))) {
            chave = (PrivateKey) getKeystore().getKey(getAlias(), new char[]{'.'});
        }
        return chave;
    }

    public PublicKey getChavePublica() {
        return getCertificado().getPublicKey();
    }

    public String getAlgoritmoChavePublica() {
        return getChavePublica().getAlgorithm();
    }

    public String getNomesAlternativos() throws IOException {
        return X509CertificadoUtil.getNomesAlternativosSujeito(getCertificado());
    }

    public List<X509Certificate> listaCadeiaCertificados() {
        return listaCadeiaCertificados(true);
    }

    public String getIdentificadorChaveAutoridade() throws IOException {
        return X509CertificadoUtil.getIdentificadorChaveAutoridade(getCertificado());
    }

    public List<X509Certificate> listaCadeiaCertificados(boolean flagAnexaTodaCadeia) {
        ArrayList<X509Certificate> lista = new ArrayList<>();
        if (flagAnexaTodaCadeia) {
            X509Certificate[] cadeiaCertificados = getCadeiaCertificados();
            for (X509Certificate x509Certificate : cadeiaCertificados) {
                lista.add(x509Certificate);
            }
        } else {
            lista.add(getCertificado());
        }
        return lista;
    }

    public byte[] assinaPKCS7(Hash algoritmo, byte[] dados, char[] pin, boolean flagCadeiaCertificados, boolean flagAnexaMensagemOriginal) throws ErroAoGerarPKCS7 {
        return assinarDigitalmente(algoritmo, pin, flagCadeiaCertificados, flagAnexaMensagemOriginal, new CMSProcessableByteArray(dados), this._fAddDefaultAttributes, null, null);
    }

    public byte[] assinarDigitalmente(Hash hash, char[] pin, boolean flagCadeiaCertificados, boolean flagAnexaMensagemOriginal, CMSTypedData dados, boolean fAdicionarAtributosPadroes, AttributeTable atributosAutenticados, AttributeTable atributosNaoAutenticados) throws ErroAoGerarPKCS7 {
        try {
            String algoritmo = definirAlgoritmoAssinatura(hash);
            if (!getKeystore().isKeyEntry(getAlias())) {
                return null;
            }
            if (fAdicionarAtributosPadroes) {
                atributosAutenticados.add(CMSAttributes.signingTime, new DERSet(new DERUTCTime(new Date())));
            }
            SimpleAttributeTableGenerator geradorTabelaAtributosNaoAutenticados = new SimpleAttributeTableGenerator(atributosNaoAutenticados);
            DefaultSignedAttributeTableGenerator geradorTabelaAtributosAutenticados = new DefaultSignedAttributeTableGenerator(atributosAutenticados);
            SignerInfoGeneratorBuilder signerInfoBuilder = new SignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build());
            signerInfoBuilder.setUnsignedAttributeGenerator(geradorTabelaAtributosNaoAutenticados);
            signerInfoBuilder.setSignedAttributeGenerator(geradorTabelaAtributosAutenticados);
            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            JcaContentSignerBuilder contentSigner = new JcaContentSignerBuilder(algoritmo);
            contentSigner.setProvider(getNomeProvider());
            generator.addSignerInfoGenerator(signerInfoBuilder.build(contentSigner.build(getChavePrivada(pin)), new X509CertificateHolder(getCertificado().getEncoded())));
            ArrayList<X509CertificateHolder> signingChainHolder = new ArrayList<>();
            for (X509Certificate cert : listaCadeiaCertificados(flagCadeiaCertificados)) {
                signingChainHolder.add(new X509CertificateHolder(cert.getEncoded()));
            }
            generator.addCertificates(new JcaCertStore(signingChainHolder));
            return generator.generate(dados, flagAnexaMensagemOriginal).getEncoded();
        } catch (GeneralSecurityException | CMSException | IOException | ProviderException | OperatorCreationException | JSignException e) {
            throw encapsularExcecaoAssinatura(e);
        }
    }

    private String definirAlgoritmoAssinatura(Hash hash) throws JSignException {
        if (hash.equals(Hash.MESMO_DO_CERTIFICADO)) {
            return getAlgoritmo();
        }
        if (hash.possuiAlgoritmoAssinatura()) {
            return hash.name();
        }
        if (eAlgoritmoCurvaEliptica() && hash == Hash.MD5) {
            throw new IllegalArgumentException("Um certificado digital baseado em criptografia de curvas elípticas não pode usar MD5 como algoritmo de hashing. Apenas a família SHA pode ser usada.");
        }
        return hash.name() + "with" + getAlgoritmoAssinatura();
    }

    public Hash getHash() {
        String nomeAlgoritmoAssinatura = getCertificado().getSigAlgName();
        if (nomeAlgoritmoAssinatura != null) {
            Pattern padrao = Pattern.compile(PADRAO_ALGORITMO_ASSINATURA, 2);
            Matcher matcher = padrao.matcher(nomeAlgoritmoAssinatura);
            if (matcher.matches()) {
                String nomeAlgoritmoHashing = matcher.group(1).toUpperCase();
                Hash hash = Hash.getHashDeNome(nomeAlgoritmoHashing);
                if (hash != null) {
                    return hash;
                }
            }
        }
        return Hash.SHA1;
    }

    private String getAlgoritmoAssinatura() throws JSignException {
        String nomeAlgoritmoAssinatura = getCertificado().getSigAlgName();
        if (nomeAlgoritmoAssinatura != null) {
            Pattern padrao = Pattern.compile(PADRAO_ALGORITMO_ASSINATURA, 2);
            Matcher matcher = padrao.matcher(nomeAlgoritmoAssinatura);
            if (matcher.matches() && matcher.groupCount() == 2) {
                return matcher.group(2).toUpperCase();
            }
            return "RSA";
        }
        throw new JSignException("Erro buscando algoritmo de assinatura do certificado.");
    }

    public boolean eAlgoritmoCurvaEliptica() {
        int tamanhoParteQueDefineSomenteAlgoritmoDeAssinatura = CMSSignedGenerator.ENCRYPTION_ECDSA.length() - 2;
        String prefixo = CMSSignedGenerator.ENCRYPTION_ECDSA.substring(0, tamanhoParteQueDefineSomenteAlgoritmoDeAssinatura);
        return getCertificado().getSigAlgOID().startsWith(prefixo);
    }

    static {
        MENSAGENS_ERRO_ASSINATURA.put(IOException.class, "Erro ao assinar digitalmente. Falha ao codificar a assinatura digital.");
        MENSAGENS_ERRO_ASSINATURA.put(CMSException.class, "Erro ao assinar digitalmente. Falha ao assinar os dados.");
        MENSAGENS_ERRO_ASSINATURA.put(KeyStoreException.class, "Erro ao assinar digitalmente. Falha ao acessar o certificado digital. Verifique se o certificado consegue ser acessado por outros programas.");
        MENSAGENS_ERRO_ASSINATURA.put(UnrecoverableKeyException.class, "Erro ao assinar digitalmente. Falha ao acessar a chave privada do certificado digital. Provavelmente a senha informada está errada.");
        MENSAGENS_ERRO_ASSINATURA.put(NoSuchAlgorithmException.class, "Erro ao assinar digitalmente. Um algoritmo necessário para usar o certificado digital não é suportado.");
        MENSAGENS_ERRO_ASSINATURA.put(CertificateException.class, "Erro ao assinar digitalmente. Falha ao acessar o certificado. Verifique se o certificado funciona corretamente com outros programas.");
        MENSAGENS_ERRO_ASSINATURA.put(InvalidAlgorithmParameterException.class, "Erro ao assinar digitalmente. O algoritmo de assinatura não é suportado pelo programa.");
        MENSAGENS_ERRO_ASSINATURA.put(CertStoreException.class, "Erro ao assinar digitalmente. Falha ao acessar o cerificado digital. Verifique se o certificado consegue ser usado por outros programas.");
        MENSAGENS_ERRO_ASSINATURA.put(NoSuchProviderException.class, "Erro ao assinar digitalmente. Provedor de funções criptográficas não encontrado. Falha interna grave. Reinstale a aplicação.");
    }

    private static ErroAoGerarPKCS7 encapsularExcecaoAssinatura(Exception e) {
        String msg = (String) MENSAGENS_ERRO_ASSINATURA.get(e.getClass());
        if (msg == null) {
            msg = Recursos.getString("Excecao.erroAoGerarPKCS7");
        }
        return new ErroAoGerarPKCS7(e, msg);
    }

    public byte[] assinaPKCS7(Hash algoritmo, File arquivo, long tamanhoArquivo, char[] pin, boolean flagCadeiaCertificados, boolean flagAnexaMensagemOriginal) throws ErroAoGerarPKCS7 {
        return assinarDigitalmente(algoritmo, pin, flagCadeiaCertificados, flagAnexaMensagemOriginal, new CMSProcessableFile(arquivo), this._fAddDefaultAttributes, null, null);
    }

    public Provider getProvider() {
        if (getKeystore() == null) {
            return null;
        }
        return getKeystore().getProvider();
    }

    public String getNomeProvider() {
        if (getProvider() == null) {
            return "";
        }
        String resposta = getProvider().getName();
        if (resposta.equals("SUN") || resposta.equals("Apple")) {
            resposta = "BC";
        }
        return resposta;
    }

    public String getNomeKeystore() {
        if (getKeystore() == null) {
            return "";
        }
        if (getKeystore().getType().equals(TipoKeyStore.SUNMSCAPI.toString())) {
            return "Internet Explorer";
        }
        if (getKeystore().getType().equals(TipoKeyStore.PKCS11.toString())) {
            String nomeProvider = getKeystore().getProvider().getName();
            if (nomeProvider.startsWith("SunPKCS11-")) {
                return nomeProvider.substring(10);
            }
        } else if (getKeystore().getType().equals(TipoKeyStore.PKCS12.toString())) {
            return "Arquivo PKCS12";
        }
        return TipoKeyStore.formataTexto(getKeystore().getType());
    }

    public char[] getSenhaContainer() {
        return this.senhaContainer;
    }

    protected void setSenhaContainer(char[] senha) {
        this.senhaContainer = senha;
    }

    public boolean hasPrivateKey(String alias) throws KeyStoreException {
        boolean resposta = false;
        if (getKeystore() != null) {
            resposta = getKeystore().isKeyEntry(alias);
        }
        return resposta;
    }

    public String getPontoDistribuicaoCRL() {
        String resposta = null;
        byte[] valorCampo = getCertificado().getExtensionValue("2.5.29.31");
        if (valorCampo != null) {
            resposta = new String(valorCampo, 12, valorCampo.length - 12);
        }
        return resposta;
    }

    public String getUsoChave() throws ErroDeES {
        try {
            return X509CertificadoUtil.getUsoChave(getCertificado());
        } catch (IOException e) {
            throw new ErroDeES(e);
        }
    }

    public String getUsoAvancadoChave() throws IOException {
        return X509CertificadoUtil.getUsoExtendidoChave(getCertificado());
    }

    public String getTipoKeyStore() {
        if (getKeystore() == null) {
            return "";
        }
        return getKeystore().getType();
    }

    public String getValorExtensao(String oid) throws JSignException {
        try {
            return X509CertificadoUtil.getNomesAlternativosSujeito(getCertificado(), oid);
        } catch (IOException e) {
            throw new JSignException(e, "Erro buscando extensão " + oid + " do certificado.");
        }
    }

    public List<String> getOIDsSubjectAlternativeName() throws IOException {
        ArrayList<String> resposta = new ArrayList<>();
        byte[] extensao = getCertificado().getExtensionValue("2.5.29.17");
        if (extensao != null) {
            byte[] octetos = X509CertificadoUtil.toDER(extensao).getOctets();
            ASN1Sequence nomes = X509CertificadoUtil.toDER(octetos);
            int len = nomes.size();
            for (int i = 0; i < len; i++) {
                DLTaggedObject objeto = nomes.getObjectAt(i);
                if (objeto.getTagNo() == 0) {
                    ASN1Sequence objetoCodificadoASN1 = objeto.getBaseObject();
                    String sOid = objetoCodificadoASN1.getObjectAt(0).getId();
                    resposta.add(sOid);
                }
            }
        }
        return resposta;
    }

    public List<String> getOIDsPoliticasCertificados() throws IOException {
        byte[] extensao = getCertificado().getExtensionValue("2.5.29.32");
        if (extensao == null) {
            return null;
        }
        byte[] octetos = X509CertificadoUtil.toDER(extensao).getOctets();
        ASN1Sequence nomes = X509CertificadoUtil.toDER(octetos);
        ArrayList<String> resposta = new ArrayList<>();
        int len = nomes.size();
        for (int i = 0; i < len; i++) {
            ASN1Sequence seq = nomes.getObjectAt(i);
            int n = seq.size();
            for (int j = 0; j < n; j++) {
                if (ASN1ObjectIdentifier.class.isInstance(seq.getObjectAt(j))) {
                    ASN1ObjectIdentifier oid = seq.getObjectAt(j);
                    resposta.add(oid.getId());
                }
            }
        }
        return resposta;
    }

    public String toString() throws SecurityException {
        StringBuffer saida = new StringBuffer();
        saida.append(getClass().getName());
        saida.append("\n");
        Method[] lsMetodos = getClass().getMethods();
        for (Method metodo : lsMetodos) {
            if (metodo.getParameterTypes().length == 0 && !metodo.getReturnType().isPrimitive() && !metodo.getName().equalsIgnoreCase("tostring")) {
                try {
                    saida.append(metodo.getName() + ":" + metodo.invoke(this, new Object[0]) + "\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e2) {
                    e2.printStackTrace();
                } catch (InvocationTargetException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return saida.toString();
    }

    public boolean eCertificadoMaquina() {
        try {
            return getUsoAvancadoChave().indexOf("1.3.6.1.5.5.7.3.1") != -1;
        } catch (IOException e) {
            return false;
        }
    }

    public byte[] assinar(Hash algoritmo, byte[] mensagemOriginal, char[] pin, boolean flagAnexaCadeiaCertificados, boolean flagAnexaMensagemOriginal, Window janelaPai) throws ErroAoGerarPKCS7 {
        char[] senha;
        DialogoSenha leitorSenha;
        byte[] assinatura;
        if (this._reutilizarSenha) {
            senha = this._ultimaSenhaFornecida;
        } else {
            senha = pin;
        }
        try {
            assinatura = assinaPKCS7(algoritmo, mensagemOriginal, senha, flagAnexaCadeiaCertificados, flagAnexaMensagemOriginal);
        } catch (ErroAoGerarPKCS7 e) {
            if (getKeystore().getType().equals(TipoKeyStore.SUNMSCAPI.toString())) {
                throw e;
            }
            if (JDialog.class.isInstance(janelaPai)) {
                leitorSenha = new DialogoSenha((Dialog) janelaPai);
            } else {
                leitorSenha = new DialogoSenha((Frame) janelaPai);
            }
            senha = leitorSenha.lerSenha(Recursos.getString("LoginContainer.titulo"), Recursos.getString("LoginContainer.mensagem"));
            assinatura = assinaPKCS7(Hash.MD5, mensagemOriginal, senha, true, true);
        }
        this._ultimaSenhaFornecida = senha;
        return assinatura;
    }

    public byte[] assinar(Hash algoritmo, File arquivo, long tamanhoArquivo, char[] pin, boolean flagAnexaCadeiaCertificados, boolean flagAnexaMensagemOriginal, Window janelaPai) throws JSignException {
        char[] senha;
        DialogoSenha leitorSenha;
        byte[] assinatura;
        if (this._reutilizarSenha) {
            senha = this._ultimaSenhaFornecida;
        } else {
            senha = pin;
        }
        try {
            assinatura = assinaPKCS7(algoritmo, arquivo, tamanhoArquivo, senha, flagAnexaCadeiaCertificados, flagAnexaMensagemOriginal);
        } catch (JSignException e) {
            if (getKeystore().getType().equals(TipoKeyStore.SUNMSCAPI.toString())) {
                throw e;
            }
            if (JDialog.class.isInstance(janelaPai)) {
                leitorSenha = new DialogoSenha((Dialog) janelaPai);
            } else {
                leitorSenha = new DialogoSenha((Frame) janelaPai);
            }
            senha = leitorSenha.lerSenha(Recursos.getString("LoginContainer.titulo"), Recursos.getString("LoginContainer.mensagem"));
            assinatura = assinaPKCS7(algoritmo, arquivo, tamanhoArquivo, senha, flagAnexaCadeiaCertificados, flagAnexaMensagemOriginal);
        }
        this._ultimaSenhaFornecida = senha;
        return assinatura;
    }

    public byte[] assinarHash(Hash algoritmo, File arquivo, long tamanhoArquivo, char[] pin, boolean flagAnexaCadeiaCertificados, boolean flagAnexaMensagemOriginal, ControleProgresso progresso, Window janelaPai) throws JSignException, NoSuchAlgorithmException, IOException {
        char[] senha;
        DialogoSenha leitorSenha;
        byte[] assinatura;
        progresso.setMinimo(0L);
        progresso.setMaximo(arquivo.length());
        InputStream in = new FileInputStreamParcial(arquivo, tamanhoArquivo);
        byte[] hash = algoritmo.geraDigest(in, progresso);
        if (progresso.foiCancelado()) {
            progresso.tarefaTerminou();
            throw new JSignExcecaoCancelamento("Assinatura digital cancelada");
        }
        if (this._reutilizarSenha) {
            senha = this._ultimaSenhaFornecida;
        } else {
            senha = pin;
        }
        try {
            try {
                assinatura = assinaPKCS7(algoritmo, hash, senha, flagAnexaCadeiaCertificados, flagAnexaMensagemOriginal);
                progresso.tarefaTerminou();
            } catch (JSignException e) {
                if (progresso.foiCancelado()) {
                    throw new JSignExcecaoCancelamento("Assinatura digital cancelada");
                }
                if (getKeystore().getType().equals(TipoKeyStore.SUNMSCAPI.toString())) {
                    throw e;
                }
                if (JDialog.class.isInstance(janelaPai)) {
                    leitorSenha = new DialogoSenha((Dialog) janelaPai);
                } else {
                    leitorSenha = new DialogoSenha((Frame) janelaPai);
                }
                senha = leitorSenha.lerSenha(Recursos.getString("LoginContainer.titulo"), Recursos.getString("LoginContainer.mensagem"));
                assinatura = assinaPKCS7(algoritmo, hash, senha, flagAnexaCadeiaCertificados, flagAnexaMensagemOriginal);
                progresso.tarefaTerminou();
            }
            this._ultimaSenhaFornecida = senha;
            return assinatura;
        } catch (Throwable th) {
            progresso.tarefaTerminou();
            throw th;
        }
    }

    public void setReutilizarSenha(boolean reutilizarSenha) {
        this._reutilizarSenha = reutilizarSenha;
    }

    public String getNumeroSerieSujeito() {
        X500Name name = new X500Name(getCertificado().getSubjectX500Principal().getName());
        RDN[] rdns = name.getRDNs(BCStyle.SERIALNUMBER);
        if (rdns.length == 0) {
            return null;
        }
        return rdns[0].getFirst().getValue().toString();
    }
}
