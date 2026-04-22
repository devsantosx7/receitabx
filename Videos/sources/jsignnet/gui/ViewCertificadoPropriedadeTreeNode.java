package jsignnet.gui;

import javax.swing.tree.DefaultMutableTreeNode;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/ViewCertificadoPropriedadeTreeNode.class */
public class ViewCertificadoPropriedadeTreeNode extends DefaultMutableTreeNode {
    private static final long serialVersionUID = 1;
    private String valor;

    public ViewCertificadoPropriedadeTreeNode(String nomePropriedade, String valorPropriedade) {
        super(nomePropriedade);
        this.valor = valorPropriedade;
    }

    public String getValor() {
        return this.valor;
    }
}
