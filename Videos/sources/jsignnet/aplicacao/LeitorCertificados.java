package jsignnet.aplicacao;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.Provider;
import java.security.ProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import jsignnet.crypto.KeyStoreUtil;
import jsignnet.crypto.TipoKeyStore;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.erro.CertificadoNaoFoiLido;
import jsignnet.erro.ErroArquivoDiretorioNaoEncontrado;
import jsignnet.erro.ErroDeES;
import jsignnet.erro.ErroDeLeituraDeKeystore;
import jsignnet.erro.JSignExcecaoCadeiaVazia;
import jsignnet.erro.JSignException;
import jsignnet.infra.IContainer;
import jsignnet.infra.IFiltro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/LeitorCertificados.class */
public class LeitorCertificados implements IContainer {
    private IFiltro _filtro;

    static {
        JSignNet.adicionaProviders();
    }

    protected LeitorCertificados() {
    }

    public synchronized IFiltro getFiltro() {
        return this._filtro;
    }

    public synchronized void setFiltro(IFiltro filtro) {
        this._filtro = filtro;
    }

    @Override // jsignnet.infra.IContainer
    public Map<String, X509CertificadoWrapper> getMapaCertificadosCAPI() throws JSignException, IllegalAccessException, NoSuchFieldException, KeyStoreException, IllegalArgumentException {
        if (JSignNet.checkSO("win")) {
            KeyStore keystoreCAPI = KeyStoreUtil.leKeyStore(TipoKeyStore.SUNMSCAPI);
            fixAliases(keystoreCAPI);
            Map<String, X509CertificadoWrapper> resultado = populaCertificados(keystoreCAPI, null);
            return resultado;
        }
        return Collections.emptyMap();
    }

    private Map<String, X509CertificadoWrapper> populaCertificados(KeyStore ks, char[] senha) throws JSignException, KeyStoreException {
        Hashtable<String, X509CertificadoWrapper> resposta = new Hashtable<>();
        IFiltro filtro = getFiltro();
        try {
            Enumeration<?> enumeration = ks.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                if (ks.isKeyEntry(alias)) {
                    try {
                        X509CertificadoWrapper novaEntrada = new X509CertificadoWrapper(alias, ks, senha);
                        if (filtro == null || filtro.isCertificadoValido(novaEntrada)) {
                            resposta.put(alias, novaEntrada);
                        }
                    } catch (JSignExcecaoCadeiaVazia e) {
                        JSignNet.logger.log(Level.WARNING, "Falha ao ler a cadeia do certificado " + alias, (Throwable) e);
                    }
                }
            }
            return resposta;
        } catch (KeyStoreException e2) {
            throw new ErroDeLeituraDeKeystore(e2);
        } catch (CertificateException e3) {
            throw new CertificadoNaoFoiLido(e3);
        }
    }

    private Map<String, X509CertificadoWrapper> populaCertificadosConfiaveis(KeyStore ks, char[] senha) throws JSignException, KeyStoreException {
        Hashtable<String, X509CertificadoWrapper> resposta = new Hashtable<>();
        IFiltro filtro = getFiltro();
        try {
            Enumeration<?> enumeration = ks.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                if (ks.isCertificateEntry(alias)) {
                    try {
                        X509CertificadoWrapper novaEntrada = new X509CertificadoWrapper(alias, ks, senha);
                        if (filtro == null || filtro.isCertificadoValido(novaEntrada)) {
                            resposta.put(alias, novaEntrada);
                        }
                    } catch (JSignExcecaoCadeiaVazia e) {
                        JSignNet.logger.log(Level.WARNING, "Falha ao ler a cadeia do certificado " + alias, (Throwable) e);
                    }
                }
            }
            return resposta;
        } catch (KeyStoreException e2) {
            throw new ErroDeLeituraDeKeystore(e2);
        } catch (CertificateException e3) {
            throw new CertificadoNaoFoiLido(e3);
        }
    }

    @Override // jsignnet.infra.IContainer
    public Map<String, X509CertificadoWrapper> getMapaCertificadosJKS(File arquivo, char[] senha) throws JSignException {
        return getMapaCertificados(arquivo, senha, TipoKeyStore.JKS);
    }

    @Override // jsignnet.infra.IContainer
    public Map<String, X509CertificadoWrapper> getMapaCertificadosKeyChainStore() throws JSignException {
        Map<String, X509CertificadoWrapper> resposta = new Hashtable<>();
        if (JSignNet.checkSO("mac")) {
            KeyStore KeyChainStore = KeyStoreUtil.leKeyStore(TipoKeyStore.KEYCHAINSTORE);
            resposta.putAll(populaCertificados(KeyChainStore, null));
        }
        return resposta;
    }

    @Override // jsignnet.infra.IContainer
    public Map<String, X509CertificadoWrapper> getMapaCertificadosPKCS11(String pathArquivoConfiguracao, char[] pin) throws JSignException {
        if (JSignNet.checkJRE("1.5.0")) {
            try {
                Provider provider = instanciaSunPKCS11(pathArquivoConfiguracao);
                Security.addProvider(provider);
                KeyStore keystorePKCS11 = KeyStoreUtil.leKeyStorePKCS11(provider.getName(), pin);
                return populaCertificados(keystorePKCS11, pin);
            } catch (ProviderException e) {
                JSignNet.logger.warning("Erro instanciando provider PKCS11:" + e.getMessage());
                return Collections.emptyMap();
            } catch (CertificadoNaoFoiLido e2) {
                return Collections.emptyMap();
            }
        }
        return Collections.emptyMap();
    }

    @Override // jsignnet.infra.IContainer
    public Map<String, X509CertificadoWrapper> getMapaCertificadosPKCS12(File arquivo, char[] senha) throws JSignException {
        return getMapaCertificados(arquivo, senha, TipoKeyStore.PKCS12);
    }

    protected Map<String, X509CertificadoWrapper> getMapaCertificados(File arquivo, char[] senha, TipoKeyStore tipo) throws JSignException {
        Map<String, X509CertificadoWrapper> resposta = new HashMap<>();
        KeyStore keystore = KeyStoreUtil.leKeyStore(arquivo, senha, tipo);
        resposta.putAll(populaCertificados(keystore, senha));
        return resposta;
    }

    public Map<String, X509CertificadoWrapper> getCertificadosConfiaveisJKS(File arquivo, char[] senha) throws JSignException {
        return populaCertificadosConfiaveis(KeyStoreUtil.leKeyStore(arquivo, senha, TipoKeyStore.JKS), senha);
    }

    public Map<String, X509CertificadoWrapper> getCertificadosConfiaveisJKS(InputStream arquivo, char[] senha) throws JSignException {
        return populaCertificadosConfiaveis(KeyStoreUtil.leKeyStore(arquivo, senha, TipoKeyStore.JKS), senha);
    }

    private Provider instanciaSunPKCS11(String caminhoArquivoCFG) throws JSignException {
        org.apache.maven.artifact.versioning.ComparableVersion versaoAtual = new org.apache.maven.artifact.versioning.ComparableVersion(System.getProperty("java.version"));
        org.apache.maven.artifact.versioning.ComparableVersion versao9 = new org.apache.maven.artifact.versioning.ComparableVersion("9");
        if (versaoAtual.compareTo(versao9) >= 0) {
            return instanciaSunPKCS11JavaRecente(caminhoArquivoCFG);
        }
        return instanciaSunPKCS11Java8(caminhoArquivoCFG);
    }

    private Provider instanciaSunPKCS11Java8(String caminhoArquivoCFG) throws JSignException, NoSuchMethodException, SecurityException {
        try {
            Constructor<?> construtor = Class.forName("sun.security.pkcs11.SunPKCS11").getConstructor(String.class);
            try {
                return (Provider) construtor.newInstance(caminhoArquivoCFG);
            } catch (InvocationTargetException e) {
                JSignNet.logger.log(Level.SEVERE, "Erro carregando configuração PKCS#11 " + caminhoArquivoCFG + ", erro:" + e.getTargetException().getMessage(), e.getTargetException());
                throw new JSignException(e, "Erro instanciando provider sun PKCS11 configurando " + caminhoArquivoCFG);
            } catch (Exception e2) {
                JSignNet.logger.log(Level.SEVERE, "Erro carregando configuração PKCS#11 " + caminhoArquivoCFG + ", erro:" + e2.getMessage(), (Throwable) e2);
                throw new JSignException(e2, "Erro instanciando provider sun PKCS11 configurando " + caminhoArquivoCFG);
            }
        } catch (Exception e3) {
            JSignNet.logger.severe("Erro instanciando provider sun PKCS11:" + e3.getMessage());
            throw new JSignException(e3, "Erro instanciando provider sun PKCS11:" + e3.getMessage());
        }
    }

    private Provider instanciaSunPKCS11JavaRecente(String caminhoArquivoCFG) throws JSignException, NoSuchMethodException, SecurityException {
        try {
            Provider p = Security.getProvider("SunPKCS11");
            Method configurar = Provider.class.getMethod("configure", String.class);
            return (Provider) configurar.invoke(p, caminhoArquivoCFG);
        } catch (IllegalAccessException e) {
            throw new ProviderException(e);
        } catch (NoSuchMethodException e2) {
            throw new ProviderException(e2);
        } catch (InvocationTargetException e3) {
            throw new ProviderException(e3);
        }
    }

    @Override // jsignnet.infra.IContainer
    public Map<String, String> getHardwarePKCS11(File diretorio) throws JSignException, IOException {
        Map<String, String> resposta = new HashMap<>();
        try {
            if (diretorio.exists()) {
                File[] listagem = diretorio.listFiles(new FileFilter() { // from class: jsignnet.aplicacao.LeitorCertificados.1
                    @Override // java.io.FileFilter
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(".cfg");
                    }
                });
                Properties propriedades = new Properties();
                for (int i = 0; i < listagem.length; i++) {
                    propriedades.load(new FileInputStream(listagem[i]));
                    resposta.put((String) propriedades.get("name"), listagem[i].getAbsolutePath());
                }
            }
            return resposta;
        } catch (FileNotFoundException e) {
            throw new ErroArquivoDiretorioNaoEncontrado();
        } catch (IOException e2) {
            throw new ErroDeES();
        }
    }

    private void fixAliases(KeyStore keyStore) throws IllegalAccessException, NoSuchFieldException, IllegalArgumentException {
        try {
            Field field = keyStore.getClass().getDeclaredField("keyStoreSpi");
            field.setAccessible(true);
            KeyStoreSpi keyStoreVeritable = (KeyStoreSpi) field.get(keyStore);
            if ("sun.security.mscapi.KeyStore$MY".equals(keyStoreVeritable.getClass().getName())) {
                Field field2 = keyStoreVeritable.getClass().getEnclosingClass().getDeclaredField("entries");
                field2.setAccessible(true);
                Object entriesObject = field2.get(keyStoreVeritable);
                if (entriesObject instanceof Map) {
                    return;
                }
                Collection<?> entries = (Collection) field2.get(keyStoreVeritable);
                for (Object entry : entries) {
                    Field field3 = entry.getClass().getDeclaredField("certChain");
                    field3.setAccessible(true);
                    X509Certificate[] certificates = (X509Certificate[]) field3.get(entry);
                    if (certificates.length != 0) {
                        String hashCode = Integer.toString(certificates[0].hashCode());
                        Field field4 = entry.getClass().getDeclaredField("alias");
                        field4.setAccessible(true);
                        String alias = (String) field4.get(entry);
                        if (!alias.equals(hashCode)) {
                            field4.set(entry, alias.concat(" - ").concat(hashCode));
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            JSignNet.logger.log(Level.SEVERE, "Erro ao corrigir o alias do provider " + keyStore.getProvider().getName() + " :" + e.getMessage(), (Throwable) e);
        } catch (IllegalArgumentException e2) {
            JSignNet.logger.log(Level.SEVERE, "Erro ao corrigir o alias do provider " + keyStore.getProvider().getName() + " :" + e2.getMessage(), (Throwable) e2);
        } catch (NoSuchFieldException e3) {
            JSignNet.logger.log(Level.SEVERE, "Erro ao corrigir o alias do provider " + keyStore.getProvider().getName() + " :" + e3.getMessage(), (Throwable) e3);
        } catch (SecurityException e4) {
            JSignNet.logger.log(Level.SEVERE, "Erro ao corrigir o alias do provider " + keyStore.getProvider().getName() + " :" + e4.getMessage(), (Throwable) e4);
        }
    }
}
