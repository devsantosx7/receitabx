package jsignnet.aplicacao;

import java.awt.Component;
import javax.swing.ProgressMonitor;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/TelaProgressoPadrao.class */
public class TelaProgressoPadrao implements ControleProgresso {
    private ProgressMonitor _monitor;
    private long _min;
    private long _max;

    public TelaProgressoPadrao(Component pai, Object mensagem) {
        this._monitor = new ProgressMonitor(pai, mensagem, (String) null, 0, 100);
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void valorMudou(long novoValor) {
        this._monitor.setProgress((int) (((novoValor - this._min) / (this._max - this._min)) * 100.0d));
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public boolean foiCancelado() {
        return this._monitor.isCanceled();
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void setMinimo(long min) {
        this._min = min;
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void setMaximo(long max) {
        this._max = max;
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void tarefaTerminou() {
        this._monitor.close();
    }
}
