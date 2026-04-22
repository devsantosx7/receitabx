package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ArquivoNaoEncontrado.class */
public class ArquivoNaoEncontrado extends JSignException {
    private static final long serialVersionUID = 1;

    public ArquivoNaoEncontrado(Exception e) {
        super(e, Recursos.getString("Excecao.arquivoNaoEncontrado"));
    }

    public ArquivoNaoEncontrado() {
        super(Recursos.getString("Excecao.arquivoNaoEncontrado"));
    }
}
