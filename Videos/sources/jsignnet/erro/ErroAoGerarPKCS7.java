package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ErroAoGerarPKCS7.class */
public class ErroAoGerarPKCS7 extends JSignException {
    private static final long serialVersionUID = 1;

    public ErroAoGerarPKCS7(Exception e) {
        super(e, Recursos.getString("Excecao.erroAoGerarPKCS7"));
    }

    public ErroAoGerarPKCS7() {
        super(Recursos.getString("Excecao.erroAoGerarPKCS7"));
    }

    public ErroAoGerarPKCS7(Exception e, String mensagem) {
        super(e, mensagem);
    }
}
