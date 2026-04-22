package jsignnet.crypto.icp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.erro.JSignException;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/icp/CertificadoICPBrasilNomeAlternativo.class */
public class CertificadoICPBrasilNomeAlternativo extends CertificadoICPBrasil {
    private static List<String> oidsPessoaFisica = Arrays.asList("2.16.76.1.3.1", "2.16.76.1.3.5");
    private static List<String> oidsPessoaJuridica = Arrays.asList("2.16.76.1.3.2", "2.16.76.1.3.3", "2.16.76.1.3.4");

    public CertificadoICPBrasilNomeAlternativo(X509CertificadoWrapper certificado) {
        super(certificado);
        if (!FiltroICPNomeAlternativo.singleton().isCertificadoValido(certificado)) {
            throw new IllegalArgumentException("O certificado informado não segue a estrutura da ICP-Brasil.");
        }
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public boolean eCertificadoPessoaFisica() throws IOException {
        return getOIDsSubjectAlternativeName().containsAll(oidsPessoaFisica);
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public boolean eCertificadoPessoaJuridica() throws IOException {
        return getOIDsSubjectAlternativeName().containsAll(oidsPessoaJuridica);
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public long getNI() throws JSignException, IOException {
        try {
            if (eCertificadoPessoaFisica()) {
                return Long.parseLong(getValorExtensao("2.16.76.1.3.1").substring(8, 19));
            }
            if (eCertificadoPessoaJuridica()) {
                return Long.parseLong(getValorExtensao("2.16.76.1.3.3"));
            }
            return -1L;
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public long getNIResponsavel() throws JSignException, IOException {
        try {
            if (eCertificadoPessoaJuridica()) {
                return Long.parseLong(getValorExtensao("2.16.76.1.3.4").substring(8, 19));
            }
            return -1L;
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public Date getDataNascimento() throws JSignException, NumberFormatException, IOException {
        try {
            if (eCertificadoPessoaFisica()) {
                String data = getValorExtensao("2.16.76.1.3.1").substring(0, 8);
                int dia = Integer.parseInt(data.substring(0, 2));
                int mes = Integer.parseInt(data.substring(2, 4));
                int ano = Integer.parseInt(data.substring(4, 8));
                Calendar c = GregorianCalendar.getInstance();
                c.set(ano, mes - 1, dia);
                return c.getTime();
            }
            if (eCertificadoPessoaJuridica()) {
                String data2 = getValorExtensao("2.16.76.1.3.4").substring(0, 8);
                int dia2 = Integer.parseInt(data2.substring(0, 2));
                int mes2 = Integer.parseInt(data2.substring(2, 4));
                int ano2 = Integer.parseInt(data2.substring(4, 8));
                Calendar c2 = GregorianCalendar.getInstance();
                c2.set(ano2, mes2 - 1, dia2);
                return c2.getTime();
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
