package jsignnet.erro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/JSignException.class */
public class JSignException extends Exception {
    private static final long serialVersionUID = 1;

    public JSignException(String mensagem) {
        super(mensagem);
    }

    public JSignException(Exception e) {
        super(e);
    }

    public JSignException(Exception e, String mensagem) {
        super(mensagem, e);
    }
}
