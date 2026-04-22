package jsignnet.erro;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/JSignRuntimeException.class */
public class JSignRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1;
    private String mensagem;

    public JSignRuntimeException(String mensagem) {
        setMensagem(mensagem);
    }

    public JSignRuntimeException(Exception excecao) {
        super(excecao);
    }

    public String getMensagem() {
        return this.mensagem;
    }

    protected void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
