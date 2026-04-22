package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ErroDeLeituraDeKeystore.class */
public class ErroDeLeituraDeKeystore extends JSignException {
    private static final long serialVersionUID = 1;

    public ErroDeLeituraDeKeystore(Exception e) {
        super(e, Recursos.getString("Excecao.erroDeLeituraDeKeystore"));
    }

    public ErroDeLeituraDeKeystore() {
        super(Recursos.getString("Excecao.erroDeLeituraDeKeystore"));
    }
}
