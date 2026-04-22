package jsignnet.erro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/JSignExcecaoCadeiaVazia.class */
public class JSignExcecaoCadeiaVazia extends JSignException {
    private static final long serialVersionUID = 1;

    public JSignExcecaoCadeiaVazia(String mensagem) {
        super(mensagem);
    }

    public JSignExcecaoCadeiaVazia(Exception e) {
        super(e);
    }

    public JSignExcecaoCadeiaVazia(Exception e, String mensagem) {
        super(e, mensagem);
    }
}
