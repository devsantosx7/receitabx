package jsignnet.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import jsignnet.crypto.X509CertificadoWrapper;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/ViewCertificadoTreeNode.class */
public class ViewCertificadoTreeNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1;
    private X509CertificadoWrapper certificado;

    public ViewCertificadoTreeNode(X509CertificadoWrapper certificado) {
        super(certificado.getEmitidoPara());
        this.certificado = certificado;
    }

    public X509CertificadoWrapper getCertificado() {
        return this.certificado;
    }
}
