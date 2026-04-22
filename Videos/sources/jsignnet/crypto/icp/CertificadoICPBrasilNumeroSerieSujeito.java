package jsignnet.crypto.icp;

import java.io.IOException;
import java.util.Date;
import jsignnet.crypto.X509CertificadoWrapper;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/icp/CertificadoICPBrasilNumeroSerieSujeito.class */
public class CertificadoICPBrasilNumeroSerieSujeito extends CertificadoICPBrasil {
    private boolean ePessoaFisica;
    private boolean ePessoaJuridica;
    private long ni;

    public CertificadoICPBrasilNumeroSerieSujeito(X509CertificadoWrapper certificado) {
        super(certificado);
        if (!FiltroICPNumeroSerieSujeito.singleton().isCertificadoValido(certificado)) {
            throw new IllegalArgumentException("O certificado informado não segue a estrutura da ICP-Brasil.");
        }
        String numeroSerieSujeito = getNumeroSerieSujeito();
        this.ePessoaFisica = numeroSerieSujeito.length() == 11;
        this.ePessoaJuridica = numeroSerieSujeito.length() == 14;
        try {
            this.ni = Long.parseLong(numeroSerieSujeito);
        } catch (NumberFormatException e) {
            this.ni = -1L;
        }
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public boolean eCertificadoPessoaFisica() throws IOException {
        return this.ePessoaFisica;
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public boolean eCertificadoPessoaJuridica() throws IOException {
        return this.ePessoaJuridica;
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public long getNI() {
        return this.ni;
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public long getNIResponsavel() {
        return -1L;
    }

    @Override // jsignnet.crypto.icp.CertificadoICPBrasil
    public Date getDataNascimento() {
        return null;
    }
}
