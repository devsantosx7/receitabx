package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/CertificadoNaoFoiLido.class */
public class CertificadoNaoFoiLido extends JSignException {
    private static final long serialVersionUID = 1;

    public CertificadoNaoFoiLido(Exception e) {
        super(e, Recursos.getString("Excecao.certificadoNaoFoiLido"));
    }

    public CertificadoNaoFoiLido() {
        super(Recursos.getString("Excecao.certificadoNaoFoiLido"));
    }
}
