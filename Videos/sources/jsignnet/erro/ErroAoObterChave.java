package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ErroAoObterChave.class */
public class ErroAoObterChave extends JSignException {
    private static final long serialVersionUID = 1;

    public ErroAoObterChave(Exception e) {
        super(e, Recursos.getString("Excecao.erroAoObterChave"));
    }

    public ErroAoObterChave() {
        super(Recursos.getString("Excecao.erroAoObterChave"));
    }
}
