package jsignnet.gui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/ListaCertificadosCelulaRenderizador.class */
class ListaCertificadosCelulaRenderizador extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1;

    ListaCertificadosCelulaRenderizador() {
    }

    public Component getTableCellRendererComponent(JTable jTable, Object valor, boolean isSelecionado, boolean isFoco, int iLinha, int iColuna) {
        ImageIcon icone;
        JLabel cedula = super.getTableCellRendererComponent(jTable, valor, isSelecionado, isFoco, iLinha, iColuna);
        if (iColuna == 0) {
            if (ListaCertificadosModel.KEY_PAIR_ENTRY.equals(valor)) {
                icone = new ImageIcon(getClass().getResource(Recursos.getString("ListaCertificadosCedula.KeyPairEntry.imagem")));
                cedula.setToolTipText(Recursos.getString("ListaCertificadosCedula.KeyPair.tooltip"));
            } else if (ListaCertificadosModel.TRUST_CERT_ENTRY.equals(valor)) {
                icone = new ImageIcon(getClass().getResource(Recursos.getString("ListaCertificadosCedula.TrustCert.imagem")));
                cedula.setToolTipText(Recursos.getString("ListaCertificadosCedula.TrustCert.tooltip"));
            } else {
                icone = new ImageIcon(getClass().getResource(Recursos.getString("ListaCertificadosCedula.Key.imagem")));
                cedula.setToolTipText(Recursos.getString("ListaCertificadosCedula.Key.tooltip"));
            }
            cedula.setIcon(icone);
            cedula.setText("");
            cedula.setVerticalAlignment(0);
            cedula.setHorizontalAlignment(0);
        } else if (iColuna == 2) {
            cedula.setText(nullToBlank(valor));
            cedula.setToolTipText(getText());
        } else {
            cedula.setText(nullToBlank(valor));
            cedula.setToolTipText(getText());
        }
        cedula.setBorder(new EmptyBorder(0, 5, 0, 5));
        return cedula;
    }

    private static String nullToBlank(Object fonte) {
        return fonte != null ? fonte.toString() : "";
    }
}
