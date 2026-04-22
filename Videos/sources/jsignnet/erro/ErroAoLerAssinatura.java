package jsignnet.erro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ErroAoLerAssinatura.class */
public class ErroAoLerAssinatura extends JSignException {
    private static final long serialVersionUID = 1;

    public ErroAoLerAssinatura(String mensagem) {
        super(mensagem);
    }

    public ErroAoLerAssinatura(Exception e) {
        super(e);
    }

    public ErroAoLerAssinatura(Exception e, String mensagem) {
        super(e, mensagem);
    }
}
