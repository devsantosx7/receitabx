package jsignnet.erro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/JSignExcecaoCancelamento.class */
public class JSignExcecaoCancelamento extends JSignException {
    private static final long serialVersionUID = 1;

    public JSignExcecaoCancelamento(String mensagem) {
        super(mensagem);
    }

    public JSignExcecaoCancelamento(Exception e) {
        super(e);
    }

    public JSignExcecaoCancelamento(Exception e, String mensagem) {
        super(e, mensagem);
    }
}
