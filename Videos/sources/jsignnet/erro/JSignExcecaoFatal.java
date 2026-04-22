package jsignnet.erro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/JSignExcecaoFatal.class */
public class JSignExcecaoFatal extends RuntimeException {
    private static final long serialVersionUID = 1;

    public JSignExcecaoFatal() {
    }

    public JSignExcecaoFatal(String message) {
        super(message);
    }

    public JSignExcecaoFatal(Throwable cause) {
        super(cause);
    }

    public JSignExcecaoFatal(String message, Throwable cause) {
        super(message, cause);
    }
}
