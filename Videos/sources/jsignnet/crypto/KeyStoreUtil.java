package jsignnet.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import jsignnet.aplicacao.JSignNet;
import jsignnet.erro.CertificadoNaoFoiLido;
import jsignnet.erro.ErroDeLogin;
import jsignnet.erro.JSignException;
import jsignnet.erro.JSignRuntimeException;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/KeyStoreUtil.class */
public final class KeyStoreUtil {
    private KeyStoreUtil() {
    }

    private static KeyStore getKeystoreImplementacao(TipoKeyStore tipoKeystore) throws JSignException, KeyStoreException, NoSuchProviderException {
        KeyStore keyStore = null;
        try {
            if (tipoKeystore == TipoKeyStore.PKCS12) {
                keyStore = KeyStore.getInstance(tipoKeystore.toString(), "BC");
            } else if (tipoKeystore == TipoKeyStore.SUNMSCAPI) {
                keyStore = KeyStore.getInstance(tipoKeystore.toString(), "SunMSCAPI");
            } else if (tipoKeystore == TipoKeyStore.KEYCHAINSTORE) {
                keyStore = KeyStore.getInstance(tipoKeystore.toString(), "Apple");
            } else if (0 == 0) {
                keyStore = KeyStore.getInstance(tipoKeystore.toString());
            }
            return keyStore;
        } catch (KeyStoreException e) {
            JSignNet.logger.severe("Erro instanciando keystore: " + tipoKeystore.toString());
            throw new JSignException("Erro instanciando keystore");
        } catch (NoSuchProviderException ex) {
            ex.printStackTrace();
            JSignNet.logger.severe("Provider inexistente: " + tipoKeystore.toString());
            throw new JSignException("Provider inexistente");
        }
    }

    public static KeyStore leKeyStore(String arquivoKeystore, char[] senha, TipoKeyStore tipo) throws JSignException {
        return leKeyStore(new File(arquivoKeystore), senha, tipo);
    }

    public static KeyStore leKeyStore(File arquivoKeyStore, char[] senha, TipoKeyStore tipo) throws JSignException {
        try {
            FileInputStream fis = new FileInputStream(arquivoKeyStore);
            return leKeyStore(fis, senha, tipo);
        } catch (FileNotFoundException e) {
            throw new JSignException("Arquivo não encontrado em leKeyStore:" + arquivoKeyStore.toString());
        }
    }

    public static KeyStore leKeyStore(InputStream arquivoKeyStore, char[] senha, TipoKeyStore tipo) throws JSignException, IOException, KeyStoreException, NoSuchProviderException {
        KeyStore keyStore = getKeystoreImplementacao(tipo);
        try {
            try {
                try {
                    try {
                        keyStore.load(arquivoKeyStore, senha);
                        return keyStore;
                    } catch (NoSuchAlgorithmException e) {
                        throw new JSignException("Erro carregando do keystore de tipo " + tipo.toString() + ".\nO algoritmo usado na assinatura digital não está disponível.");
                    }
                } catch (CertificateException e2) {
                    throw new JSignException("Erro carregando do keystore de tipo " + tipo.toString() + ".\nUm dos certificados presentes no arquivo não pôde ser carregado devido a problemas.");
                }
            } catch (IOException e3) {
                e3.printStackTrace();
                if (tipo.equals(TipoKeyStore.PKCS12)) {
                    String mensagemErro = e3.getMessage();
                    if (mensagemErro.equalsIgnoreCase("stream does not represent a PKCS12 key store")) {
                        throw new JSignException("O arquivo selecionado não é um arquivo de certificado válido.");
                    }
                    if (mensagemErro.indexOf("wrong password or corrupted file") != -1) {
                        throw new JSignException("A senha informada está incorreta ou o arquivo foi corrompido.");
                    }
                    throw new JSignException("Erro carregando certificado de arquivo.\nErro:[" + mensagemErro + "]");
                }
                throw new JSignException("Erro carregando do keystore de tipo " + tipo.toString() + ", erro:" + e3.getMessage());
            } catch (ClassCastException e4) {
                throw new JSignException("Erro carregando do keystore de tipo " + tipo.toString() + ".\nArquivo inválido. Provavelmente não possui uma chave privada.");
            }
        } finally {
            if (arquivoKeyStore != null) {
                try {
                    arquivoKeyStore.close();
                } catch (IOException e5) {
                }
            }
        }
    }

    public static KeyStore leKeyStorePKCS11(String providerPKCS11, final char[] pinSmartcard) throws JSignException {
        try {
            if (Security.getProvider(providerPKCS11) == null) {
                throw new CertificadoNaoFoiLido();
            }
            CallbackHandler bogusHandler = new CallbackHandler() { // from class: jsignnet.crypto.KeyStoreUtil.1
                @Override // javax.security.auth.callback.CallbackHandler
                public void handle(Callback[] callbacks) throws UnsupportedCallbackException, IOException {
                    for (int i = 0; i < callbacks.length; i++) {
                        if (callbacks[i] instanceof PasswordCallback) {
                            PasswordCallback callback = (PasswordCallback) callbacks[i];
                            callback.setPassword(pinSmartcard);
                        }
                    }
                }
            };
            login(providerPKCS11, bogusHandler);
            KeyStore keyStore = KeyStore.getInstance(TipoKeyStore.PKCS11.toString(), providerPKCS11);
            try {
                keyStore.load(null, pinSmartcard);
                return keyStore;
            } catch (Exception e) {
                JSignNet.logger.warning("Erro em keyStore.load, provider " + providerPKCS11 + ", erro:" + e.getMessage());
                throw new JSignException(e, "Erro lendo o keystore PKCS#11 " + providerPKCS11);
            }
        } catch (FailedLoginException e2) {
            JSignNet.logger.info("Erro de login no provider " + providerPKCS11 + ", senha incorreta.");
            throw new ErroDeLogin(e2);
        } catch (LoginException e3) {
            JSignNet.logger.info("Erro de login no provider " + providerPKCS11 + ", exceção genérica:" + e3.getMessage());
            throw new ErroDeLogin(e3);
        } catch (GeneralSecurityException e4) {
            JSignNet.logger.severe("Erro de login no provider " + providerPKCS11 + ", exceção de segurança:" + e4.getMessage());
            throw new CertificadoNaoFoiLido(e4);
        }
    }

    private static void login(String nomeProvider, CallbackHandler handler) throws LoginException, JSignException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        try {
            Method login = Security.getProvider(nomeProvider).getClass().getMethod("login", Subject.class, CallbackHandler.class);
            try {
                login.invoke(Security.getProvider(nomeProvider), null, handler);
            } catch (InvocationTargetException exc) {
                if (LoginException.class.isInstance(exc.getCause())) {
                    throw ((LoginException) exc.getCause());
                }
                if (Exception.class.isInstance(exc.getCause())) {
                    throw new JSignException((Exception) exc.getCause());
                }
                if (Error.class.isInstance(exc.getCause())) {
                    throw ((Error) exc.getCause());
                }
                throw new JSignException("Erro de implementação.");
            } catch (Exception e) {
                JSignNet.logger.severe("Erro invocando login em PKCS#11 no provider " + nomeProvider + ", erro:" + e.getMessage());
                throw new JSignException(e, "Erro invocando login em PKCS#11 no provider " + nomeProvider);
            }
        } catch (Exception e2) {
            JSignNet.logger.severe("Provider PKCS#11 da sun não encontrado: " + e2.getMessage());
            throw new JSignException(e2, "Provider PKCS#11 da sun não encontrado.");
        }
    }

    public static KeyStore leKeyStore(TipoKeyStore tipo) throws JSignException {
        KeyStore keyStore = getKeystoreImplementacao(tipo);
        try {
            keyStore.load(null, null);
            return keyStore;
        } catch (Exception e) {
            throw new JSignException(e, "Erro carregando do keystore de tipo " + tipo.toString());
        }
    }

    public static void salvaNovaKeyStore(KeyStore keyStore, File arquivoDestino, char[] senha) throws JSignException, IOException {
        FileOutputStream fos = null;
        try {
            try {
                fos = new FileOutputStream(arquivoDestino);
                keyStore.store(fos, senha);
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        throw new JSignException("Erro fechando arquivo JKS:" + e.getMessage());
                    }
                }
            } catch (Throwable th) {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e2) {
                        throw new JSignException("Erro fechando arquivo JKS:" + e2.getMessage());
                    }
                }
                throw th;
            }
        } catch (Exception e3) {
            throw new JSignException("Erro salvando store no arquivo JKS:" + e3.getMessage());
        }
    }

    public static boolean importaCertificado(X509CertificadoWrapper certificado, KeyStore keyStoreDestino, String nomeArquivo, char[] pinCertificadoOrigem, char[] pinCertificadoDestino, char[] senhaContainerDestino) throws Exception {
        boolean resposta = false;
        if (!certificado.hasPrivateKey(certificado.getAlias())) {
            throw new JSignRuntimeException("Certificado não é válido.");
        }
        if (!isContainerJaPossuiCertificado(keyStoreDestino, certificado)) {
            keyStoreDestino.setKeyEntry(certificado.getAlias(), certificado.getChavePrivada(pinCertificadoOrigem), pinCertificadoDestino, certificado.getCadeiaCertificados());
            resposta = true;
            salvaNovaKeyStore(keyStoreDestino, new File(nomeArquivo), senhaContainerDestino);
        }
        return resposta;
    }

    public static boolean isContainerJaPossuiCertificado(KeyStore keystore, X509CertificadoWrapper certificado) throws KeyStoreException {
        boolean resposta = false;
        String alias = certificado.getAlias();
        Enumeration<String> aliases = keystore.aliases();
        if (aliases != null) {
            while (aliases.hasMoreElements() && !resposta) {
                String elemento = aliases.nextElement();
                if (elemento.equalsIgnoreCase(alias)) {
                    resposta = true;
                }
            }
        }
        return resposta;
    }
}
