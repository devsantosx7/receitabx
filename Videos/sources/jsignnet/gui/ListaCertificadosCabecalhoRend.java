package jsignnet.gui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/ListaCertificadosCabecalhoRend.class */
class ListaCertificadosCabecalhoRend extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1;

    ListaCertificadosCabecalhoRend() {
    }

    public Component getTableCellRendererComponent(JTable jTable, Object valor, boolean isSelecionada, boolean isFoco, int iLinha, int iColuna) {
        JLabel header = jTable.getColumnModel().getColumn(iColuna).getHeaderRenderer();
        if (iColuna == 0) {
            header.setText("");
            ImageIcon icon = new ImageIcon(getClass().getResource(Recursos.getString("ListaCertificadosCabecalho.ColunaTipo.imagem")));
            header.setIcon(icon);
            header.setHorizontalAlignment(0);
            header.setVerticalAlignment(0);
            header.setToolTipText(Recursos.getString("ListaCertificadosCabecalho.ColunaTipo.tooltip"));
        } else {
            header.setText((String) valor);
            header.setHorizontalAlignment(2);
            if (iColuna == 1) {
                header.setToolTipText(Recursos.getString("ListaCertificadosCabecalho.ColunaEmitidoPara.tooltip"));
            } else {
                header.setToolTipText(Recursos.getString("ListaCertificadosCabecalho.ColunaEmitidoPor.tooltip"));
            }
        }
        header.setBorder(new CompoundBorder(new BevelBorder(0), new EmptyBorder(0, 5, 0, 5)));
        return header;
    }
}
