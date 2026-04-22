package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ErroDeLogin.class */
public class ErroDeLogin extends JSignException {
    private static final long serialVersionUID = 1;

    public ErroDeLogin(Exception e) {
        super(e, Recursos.getString("Excecao.erroDeLogin"));
    }

    public ErroDeLogin() {
        super(Recursos.getString("Excecao.erroDeLeituraDeKeystore"));
    }
}
