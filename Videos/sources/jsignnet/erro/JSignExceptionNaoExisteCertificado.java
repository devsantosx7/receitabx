package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/JSignExceptionNaoExisteCertificado.class */
public class JSignExceptionNaoExisteCertificado extends JSignException {
    private static final long serialVersionUID = 1;

    public JSignExceptionNaoExisteCertificado(Exception e) {
        super(e, Recursos.getString("Excecao.naoExisteCertificado"));
    }

    public JSignExceptionNaoExisteCertificado() {
        super(Recursos.getString("Excecao.naoExisteCertificado"));
    }
}
