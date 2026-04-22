package jsignnet.gui;

import java.util.Collection;
import java.util.Iterator;
import javax.swing.table.AbstractTableModel;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.crypto.icp.FiltroICP;
import jsignnet.infra.FormataUtil;
import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/ListaCertificadosModel.class */
class ListaCertificadosModel extends AbstractTableModel {
    private static final long serialVersionUID = 1;
    public static String KEY_PAIR_ENTRY = Recursos.getString("ListaCertificadosModelo.KeyPairEntry");
    public static String TRUST_CERT_ENTRY = Recursos.getString("ListaCertificadosModelo.TrustCertEntry");
    public static String KEY_ENTRY = Recursos.getString("ListaCertificadosModelo.KeyEntry");
    private String[] nomeColuna = {Recursos.getString("ListaCertificados.Tipo"), Recursos.getString("ListaCertificados.EmitidoPara"), Recursos.getString("ListaCertificados.EmitidoPor"), Recursos.getString("ListaCertificados.DataExpiracao"), Recursos.getString("ListaCertificados.NomeContainer"), Recursos.getString("ListaCertificados.ICPBrasil")};
    private Object[][] dados = new Object[0][0];
    private X509CertificadoWrapper[] _arrayCertificados = new X509CertificadoWrapper[0];

    public void carrega(X509CertificadoWrapper[] arrayCertificados) {
        this._arrayCertificados = (X509CertificadoWrapper[]) arrayCertificados.clone();
        int linha = 0;
        this.dados = new Object[this._arrayCertificados.length][6];
        for (int i = 0; i < this._arrayCertificados.length; i++) {
            X509CertificadoWrapper elemento = this._arrayCertificados[i];
            this.dados[linha][0] = new String(TRUST_CERT_ENTRY);
            this.dados[linha][1] = elemento.getEmitidoPara();
            this.dados[linha][2] = elemento.getEmitidoPor();
            this.dados[linha][3] = FormataUtil.formataData(elemento.getDataVencimento());
            this.dados[linha][4] = elemento.getNomeKeystore();
            this.dados[linha][5] = FiltroICP.singleton().isCertificadoValido(elemento) ? "Sim" : "Não";
            linha++;
        }
        fireTableDataChanged();
    }

    public void adicionar(Collection<X509CertificadoWrapper> certificados) {
        int cnull = 0;
        for (Object objeto : certificados) {
            if (objeto == null) {
                cnull++;
            } else if (!(objeto instanceof X509CertificadoWrapper)) {
                throw new IllegalArgumentException("Coleção contém um objeto inválido.");
            }
        }
        int cnovos = certificados.size() - cnull;
        X509CertificadoWrapper[] novoArray = new X509CertificadoWrapper[this._arrayCertificados.length + cnovos];
        System.arraycopy(this._arrayCertificados, 0, novoArray, 0, this._arrayCertificados.length);
        int iarray = this._arrayCertificados.length;
        Iterator<X509CertificadoWrapper> i = certificados.iterator();
        while (i.hasNext()) {
            int i2 = iarray;
            iarray++;
            novoArray[i2] = i.next();
        }
        carrega(novoArray);
    }

    public X509CertificadoWrapper getCertificado(int linha) {
        if (linha < 0 || linha >= this._arrayCertificados.length) {
            throw new IllegalArgumentException("Não existe certificado digital na linha " + linha);
        }
        return this._arrayCertificados[linha];
    }

    public int getColumnCount() {
        return this.nomeColuna.length;
    }

    public int getRowCount() {
        return this.dados.length;
    }

    public String getColumnName(int iColuna) {
        return this.nomeColuna[iColuna];
    }

    public Object getValueAt(int iLinha, int iColuna) {
        return this.dados[iLinha][iColuna];
    }

    public Class<?> getColumnClass(int iColuna) {
        return getValueAt(0, iColuna).getClass();
    }

    public boolean isCellEditable(int iLinha, int iColuna) {
        return false;
    }
}
