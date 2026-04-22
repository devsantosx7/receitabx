package jsignnet.aplicacao;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ControleProgressoPadrao.class */
public class ControleProgressoPadrao implements ControleProgresso {
    @Override // jsignnet.aplicacao.ControleProgresso
    public boolean foiCancelado() {
        return false;
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void setMinimo(long min) {
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void setMaximo(long max) {
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void valorMudou(long novoValor) {
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void tarefaTerminou() {
    }
}
