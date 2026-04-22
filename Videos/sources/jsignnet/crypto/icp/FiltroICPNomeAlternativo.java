package jsignnet.crypto.icp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.infra.IFiltro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/icp/FiltroICPNomeAlternativo.class */
public class FiltroICPNomeAlternativo implements IFiltro {
    private static List<String> oidsPessoaFisica = Arrays.asList("2.16.76.1.3.1", "2.16.76.1.3.5");
    private static List<String> oidsPessoaJuridica = Arrays.asList("2.16.76.1.3.2", "2.16.76.1.3.3", "2.16.76.1.3.4");
    private static FiltroICPNomeAlternativo _instancia = new FiltroICPNomeAlternativo();

    private FiltroICPNomeAlternativo() {
    }

    public static FiltroICPNomeAlternativo singleton() {
        return _instancia;
    }

    @Override // jsignnet.infra.IFiltro
    public boolean isCertificadoValido(X509CertificadoWrapper certificado) {
        try {
            List<String> oids = certificado.getOIDsSubjectAlternativeName();
            return oids.containsAll(oidsPessoaFisica) || oids.containsAll(oidsPessoaJuridica);
        } catch (IOException e) {
            return false;
        }
    }
}
