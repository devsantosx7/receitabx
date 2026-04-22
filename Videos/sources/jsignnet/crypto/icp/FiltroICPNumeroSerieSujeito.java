package jsignnet.crypto.icp;

import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.infra.IFiltro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/icp/FiltroICPNumeroSerieSujeito.class */
public class FiltroICPNumeroSerieSujeito implements IFiltro {
    static final int TAMANHO_CNPJ = 14;
    static final int TAMANHO_CPF = 11;
    private static FiltroICPNumeroSerieSujeito _instancia = new FiltroICPNumeroSerieSujeito();

    private FiltroICPNumeroSerieSujeito() {
    }

    public static FiltroICPNumeroSerieSujeito singleton() {
        return _instancia;
    }

    @Override // jsignnet.infra.IFiltro
    public boolean isCertificadoValido(X509CertificadoWrapper certificado) {
        String numeroSerieSujeito = certificado.getNumeroSerieSujeito();
        return (numeroSerieSujeito == null || numeroSerieSujeito.isEmpty() || (numeroSerieSujeito.length() != TAMANHO_CPF && numeroSerieSujeito.length() != TAMANHO_CNPJ)) ? false : true;
    }
}
