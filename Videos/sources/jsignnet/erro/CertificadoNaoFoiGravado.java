package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/CertificadoNaoFoiGravado.class */
public class CertificadoNaoFoiGravado extends JSignException {
    private static final long serialVersionUID = 1;

    public CertificadoNaoFoiGravado(Exception e) {
        super(e, Recursos.getString("Excecao.certificadoNaoFoiGravado"));
    }

    public CertificadoNaoFoiGravado() {
        super(Recursos.getString("Excecao.certificadoNaoFoiGravado"));
    }
}
