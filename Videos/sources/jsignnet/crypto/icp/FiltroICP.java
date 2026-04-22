package jsignnet.crypto.icp;

import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.infra.IFiltro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/icp/FiltroICP.class */
public class FiltroICP implements IFiltro {
    private static FiltroICP _instancia = new FiltroICP();

    @Override // jsignnet.infra.IFiltro
    public boolean isCertificadoValido(X509CertificadoWrapper certificado) {
        return FiltroICPNomeAlternativo.singleton().isCertificadoValido(certificado) || FiltroICPNumeroSerieSujeito.singleton().isCertificadoValido(certificado);
    }

    private FiltroICP() {
    }

    public static FiltroICP singleton() {
        return _instancia;
    }
}
