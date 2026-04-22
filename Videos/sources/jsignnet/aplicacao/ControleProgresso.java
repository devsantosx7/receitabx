package jsignnet.aplicacao;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ControleProgresso.class */
public interface ControleProgresso {
    void setMinimo(long j);

    void setMaximo(long j);

    void valorMudou(long j);

    boolean foiCancelado();

    void tarefaTerminou();
}
