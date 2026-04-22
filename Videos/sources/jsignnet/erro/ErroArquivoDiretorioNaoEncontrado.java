package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ErroArquivoDiretorioNaoEncontrado.class */
public class ErroArquivoDiretorioNaoEncontrado extends JSignException {
    private static final long serialVersionUID = 1;

    public ErroArquivoDiretorioNaoEncontrado(Exception e) {
        super(e, Recursos.getString("Excecao.erroArquivoDiretorioNaoEncontrado"));
    }

    public ErroArquivoDiretorioNaoEncontrado() {
        super(Recursos.getString("Excecao.erroArquivoDiretorioNaoEncontrado"));
    }
}
