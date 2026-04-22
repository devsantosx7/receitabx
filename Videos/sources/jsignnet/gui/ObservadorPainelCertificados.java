package jsignnet.gui;

import jsignnet.crypto.X509CertificadoWrapper;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/ObservadorPainelCertificados.class */
public interface ObservadorPainelCertificados {
    void certificadoSelecionado(PainelCertificados painelCertificados, X509CertificadoWrapper x509CertificadoWrapper);
}
