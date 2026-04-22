package jsignnet.infra;

import jsignnet.crypto.X509CertificadoWrapper;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/infra/IFiltro.class */
public interface IFiltro {
    boolean isCertificadoValido(X509CertificadoWrapper x509CertificadoWrapper);
}
