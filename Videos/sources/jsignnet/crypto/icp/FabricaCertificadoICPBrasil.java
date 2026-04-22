package jsignnet.crypto.icp;

import jsignnet.crypto.X509CertificadoWrapper;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/icp/FabricaCertificadoICPBrasil.class */
public class FabricaCertificadoICPBrasil {
    public static CertificadoICPBrasil criarCertificado(X509CertificadoWrapper certificado) {
        if (FiltroICPNomeAlternativo.singleton().isCertificadoValido(certificado)) {
            return new CertificadoICPBrasilNomeAlternativo(certificado);
        }
        if (FiltroICPNumeroSerieSujeito.singleton().isCertificadoValido(certificado)) {
            return new CertificadoICPBrasilNumeroSerieSujeito(certificado);
        }
        throw new IllegalArgumentException("O certificado informado não segue a estrutura da ICP-Brasil.");
    }
}
