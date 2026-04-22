package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/NenhumCertificadoValidoException.class */
public class NenhumCertificadoValidoException extends JSignException {
    private static final long serialVersionUID = 1;

    public NenhumCertificadoValidoException(Exception e) {
        super(e, Recursos.getString("Excecao.nenhumCertificadoValido"));
    }

    public NenhumCertificadoValidoException() {
        super(Recursos.getString("Excecao.nenhumCertificadoValido"));
    }
}
