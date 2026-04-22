package jsignnet.infra;

import java.io.File;
import java.util.Map;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.erro.JSignException;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/infra/IContainer.class */
public interface IContainer {
    Map<String, X509CertificadoWrapper> getMapaCertificadosPKCS12(File file, char[] cArr) throws JSignException;

    Map<String, X509CertificadoWrapper> getMapaCertificadosJKS(File file, char[] cArr) throws JSignException;

    Map<String, X509CertificadoWrapper> getMapaCertificadosPKCS11(String str, char[] cArr) throws JSignException;

    Map<String, String> getHardwarePKCS11(File file) throws JSignException;

    Map<String, X509CertificadoWrapper> getMapaCertificadosCAPI() throws JSignException;

    Map<String, X509CertificadoWrapper> getMapaCertificadosKeyChainStore() throws JSignException;
}
