package jsignnet.crypto.icp;

import java.io.IOException;
import java.util.Date;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.erro.JSignException;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/icp/CertificadoICPBrasil.class */
public abstract class CertificadoICPBrasil extends X509CertificadoWrapper {
    private static String[][] OIDS_E_TIPOS = {new String[]{"2.16.76.1.2.1", "A1"}, new String[]{"2.16.76.1.2.2", "A2"}, new String[]{"2.16.76.1.2.3", "A3"}, new String[]{"2.16.76.1.2.4", "A4"}, new String[]{"2.16.76.1.2.101", "S1"}, new String[]{"2.16.76.1.2.102", "S2"}, new String[]{"2.16.76.1.2.103", "S3"}, new String[]{"2.16.76.1.2.104", "S4"}};

    public abstract boolean eCertificadoPessoaFisica() throws IOException;

    public abstract boolean eCertificadoPessoaJuridica() throws IOException;

    public abstract long getNI() throws JSignException, IOException;

    public abstract long getNIResponsavel() throws JSignException, IOException;

    public abstract Date getDataNascimento() throws JSignException, IOException;

    protected CertificadoICPBrasil(X509CertificadoWrapper certificado) {
        super(certificado);
    }

    public String getTipoCertificado() throws IOException {
        for (String oidATestar : getOIDsPoliticasCertificados()) {
            for (int iOID = 0; iOID < OIDS_E_TIPOS.length; iOID++) {
                if (oidATestar.startsWith(OIDS_E_TIPOS[iOID][0])) {
                    return OIDS_E_TIPOS[iOID][1];
                }
            }
        }
        return null;
    }
}
