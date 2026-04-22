package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ErroDeES.class */
public class ErroDeES extends JSignException {
    private static final long serialVersionUID = 1;

    public ErroDeES(Exception e) {
        super(e, Recursos.getString("Excecao.erroDeES"));
    }

    public ErroDeES() {
        super(Recursos.getString("Excecao.erroDeES"));
    }
}
