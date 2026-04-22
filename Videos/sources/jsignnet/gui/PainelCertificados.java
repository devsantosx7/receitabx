package jsignnet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.erro.JSignException;
import jsignnet.infra.IContainer;
import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/PainelCertificados.class */
public class PainelCertificados extends JPanel {
    private static final long serialVersionUID = 1;
    private static final int DEFAULT_TABLE_WIDTH = 600;
    private static final int DEFAULT_TABLE_HEIGHT = 400;
    private JTable _listaCertificadosTabela;
    private JScrollPane _jspListaCertificadosTabela;
    private JButton _botaoDetalha;
    private Window _possuidora;
    private ListaCertificadosModel _listaCertificadosModel;
    private ArrayList<ObservadorPainelCertificados> _observadores;
    private String _tituloPainel;
    private String _tituloImporta;
    private DialogoSenha _dialogoSenha;

    static {
        inicializaComponente();
    }

    public PainelCertificados(Window possuidora) {
        this(possuidora, false);
    }

    public PainelCertificados(Window possuidora, boolean exibirICP) {
        super(new BorderLayout(10, 10));
        this._listaCertificadosModel = new ListaCertificadosModel();
        this._observadores = new ArrayList<>();
        this._tituloPainel = Recursos.getString("ListaCertificados.TextoBorda");
        this._tituloImporta = Recursos.getString("ListaCertificados.TituloProcura");
        setPreferredSize(new Dimension(DEFAULT_TABLE_WIDTH, DEFAULT_TABLE_HEIGHT));
        if (!(possuidora instanceof Dialog) && !(possuidora instanceof Frame)) {
            throw new IllegalArgumentException("Argumento \"possuidora\" deve ser Dialog ou Frame.");
        }
        this._possuidora = possuidora;
        if (possuidora instanceof Frame) {
            this._dialogoSenha = new DialogoSenha((Frame) possuidora);
        } else if (possuidora instanceof Dialog) {
            this._dialogoSenha = new DialogoSenha((Dialog) possuidora);
        }
        inicializaTabela(exibirICP);
    }

    public void setTitulo(String titulo) {
        if (titulo == null) {
            throw new IllegalArgumentException("O título do painel de certificados não pode ser null.");
        }
        this._tituloPainel = titulo;
        definirBorda();
    }

    public void setTituloPKCS12(String titulo) {
        if (titulo == null) {
            throw new IllegalArgumentException("O título PKCS12 não pode ser null.");
        }
        this._tituloImporta = titulo;
    }

    public void adicionarObservador(ObservadorPainelCertificados obs) {
        if (!this._observadores.contains(obs)) {
            this._observadores.add(obs);
        }
        obs.certificadoSelecionado(this, getCertificadoSelecionado());
    }

    public void removerObservador(ObservadorPainelCertificados obs) {
        this._observadores.remove(obs);
    }

    private void notificarObservadoresSelecaoCertificado(X509CertificadoWrapper cert) {
        Iterator<ObservadorPainelCertificados> i = this._observadores.iterator();
        while (i.hasNext()) {
            ObservadorPainelCertificados obs = i.next();
            obs.certificadoSelecionado(this, cert);
        }
    }

    public void setCertificados(List<X509CertificadoWrapper> certificados) {
        if (certificados == null) {
            throw new IllegalArgumentException("Lista de certificados nula.");
        }
        Iterator<X509CertificadoWrapper> i = certificados.iterator();
        while (i.hasNext()) {
            if (!(i.next() instanceof X509CertificadoWrapper)) {
                throw new IllegalArgumentException("A lista informada deve conter apenas objetos de tipo X509CertificadoWrapper.");
            }
        }
        X509CertificadoWrapper[] arrayCertificados = (X509CertificadoWrapper[]) certificados.toArray(new X509CertificadoWrapper[0]);
        this._listaCertificadosModel.carrega(arrayCertificados);
    }

    public void setCertificados(X509CertificadoWrapper[] certificados) {
        if (certificados == null) {
            throw new IllegalArgumentException("Lista de certificados nula.");
        }
        X509CertificadoWrapper[] arrayCertificados = (X509CertificadoWrapper[]) certificados.clone();
        this._listaCertificadosModel.carrega(arrayCertificados);
    }

    private void inicializaTabela(boolean exibirICP) {
        this._listaCertificadosTabela = new JTable(this._listaCertificadosModel);
        this._listaCertificadosTabela.setSelectionMode(0);
        this._listaCertificadosTabela.setShowGrid(false);
        this._listaCertificadosTabela.setRowMargin(0);
        this._listaCertificadosTabela.getColumnModel().setColumnMargin(0);
        this._listaCertificadosTabela.getTableHeader().setReorderingAllowed(false);
        this._listaCertificadosTabela.setAutoResizeMode(4);
        this._listaCertificadosTabela.setRowHeight(18);
        for (int iCnt = 0; iCnt < this._listaCertificadosTabela.getColumnCount(); iCnt++) {
            TableColumn column = this._listaCertificadosTabela.getColumnModel().getColumn(iCnt);
            column.setHeaderRenderer(new ListaCertificadosCabecalhoRend());
            column.setCellRenderer(new ListaCertificadosCelulaRenderizador());
        }
        TableColumn colunaTipo = this._listaCertificadosTabela.getColumnModel().getColumn(0);
        colunaTipo.setResizable(false);
        colunaTipo.setMinWidth(20);
        colunaTipo.setMaxWidth(20);
        colunaTipo.setPreferredWidth(20);
        TableColumn aliasCol = this._listaCertificadosTabela.getColumnModel().getColumn(1);
        aliasCol.setMinWidth(20);
        aliasCol.setMaxWidth(10000);
        aliasCol.setPreferredWidth(350);
        if (!exibirICP) {
            this._listaCertificadosTabela.removeColumn(this._listaCertificadosTabela.getColumnModel().getColumn(5));
        }
        this._listaCertificadosTabela.addKeyListener(new KeyAdapter() { // from class: jsignnet.gui.PainelCertificados.1
            public void keyTyped(KeyEvent e) {
                Container rootAncestor;
                char c = e.getKeyChar();
                if (c == '\t' && (rootAncestor = PainelCertificados.this._listaCertificadosTabela.getFocusCycleRootAncestor()) != null) {
                    FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
                    Component comp = policy.getComponentAfter(rootAncestor, PainelCertificados.this._listaCertificadosTabela);
                    if (comp != null) {
                        comp.requestFocus();
                    }
                }
            }
        });
        this._jspListaCertificadosTabela = new JScrollPane(this._listaCertificadosTabela, 20, 30);
        this._jspListaCertificadosTabela.getViewport().setBackground(this._listaCertificadosTabela.getBackground());
        add(this._jspListaCertificadosTabela, "Center");
        Box boxInferior = Box.createHorizontalBox();
        boxInferior.add(Box.createHorizontalGlue());
        boxInferior.add(getBotaoDetalha());
        add(boxInferior, "South");
        definirBorda();
        this._listaCertificadosTabela.getSelectionModel().addListSelectionListener(new ListSelectionListener() { // from class: jsignnet.gui.PainelCertificados.2
            public void valueChanged(ListSelectionEvent e) {
                if (PainelCertificados.this._listaCertificadosTabela.isEnabled() && !e.getValueIsAdjusting()) {
                    PainelCertificados.this.notificarSelecaoCertificadoLinhaSelecionada();
                }
            }
        });
        this._listaCertificadosTabela.addMouseListener(new MouseAdapter() { // from class: jsignnet.gui.PainelCertificados.3
            public void mouseClicked(MouseEvent evt) {
                if (PainelCertificados.this._listaCertificadosTabela.isEnabled()) {
                    PainelCertificados.this.listaCertificadosTabelaDuploClique(evt);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notificarSelecaoCertificadoLinhaSelecionada() {
        X509CertificadoWrapper certSelecionado = getCertificadoSelecionado();
        notificarObservadoresSelecaoCertificado(certSelecionado);
        getBotaoDetalha().setEnabled(certSelecionado != null);
    }

    public X509CertificadoWrapper getCertificadoSelecionado() {
        int linhaSelecionada = this._listaCertificadosTabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            return null;
        }
        return this._listaCertificadosModel.getCertificado(linhaSelecionada);
    }

    private void definirBorda() {
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(this._tituloPainel), BorderFactory.createEmptyBorder(3, 3, 3, 3))));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void listaCertificadosTabelaDuploClique(MouseEvent evento) {
        if (evento.getClickCount() > 1) {
            getBotaoDetalha().doClick();
        }
    }

    private JButton getBotaoDetalha() {
        if (this._botaoDetalha == null) {
            this._botaoDetalha = new JButton(Recursos.getString("ListaCertificados.BotaoDetalhe"));
            this._botaoDetalha.setEnabled(false);
            this._botaoDetalha.addActionListener(new ActionListener() { // from class: jsignnet.gui.PainelCertificados.4
                public void actionPerformed(ActionEvent e) {
                    try {
                        PainelCertificados.this.mostraCertificadoSelecionado();
                    } catch (JSignException ex) {
                        JOptionPane.showMessageDialog(PainelCertificados.this._possuidora, ex.getMessage(), "Erro", 0);
                    }
                }
            });
        }
        return this._botaoDetalha;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean mostraCertificadoSelecionado() throws JSignException {
        ViewCertificado viewCertificado;
        int iLinha = this._listaCertificadosTabela.getSelectedRow();
        if (iLinha == -1 || this._listaCertificadosTabela.getValueAt(iLinha, 0).equals(ListaCertificadosModel.KEY_ENTRY)) {
            return false;
        }
        if (this._possuidora instanceof Dialog) {
            viewCertificado = new ViewCertificado(this._possuidora, Recursos.getString("MostraCertificado.Titulo"), true, this._listaCertificadosModel.getCertificado(iLinha));
        } else {
            viewCertificado = new ViewCertificado(this._possuidora, Recursos.getString("MostraCertificado.Titulo"), true, this._listaCertificadosModel.getCertificado(iLinha));
        }
        viewCertificado.setLocationRelativeTo(this);
        viewCertificado.setVisible(true);
        return true;
    }

    public int incluirCertificadosPKCS12(IContainer componente) throws JSignException {
        return incluirCertificadosPKCS12(componente, Recursos.getString("ListaCertificados.TituloSenha"), Recursos.getString("ListaCertificados.Senha"));
    }

    public int incluirCertificadosPKCS12(IContainer componente, String titulo, String mensagem) throws JSignException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FiltroPKCS12());
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle(this._tituloImporta);
        int resultado = fileChooser.showDialog(this._possuidora, Recursos.getString("ListaCertificados.BotaoEscolha"));
        if (resultado == 0) {
            File arquivo = fileChooser.getSelectedFile();
            char[] senha = this._dialogoSenha.lerSenha(titulo, mensagem);
            if (senha != null) {
                Map<String, X509CertificadoWrapper> mapaCertificados = componente.getMapaCertificadosPKCS12(arquivo, senha);
                this._listaCertificadosModel.adicionar(mapaCertificados.values());
                return mapaCertificados.size();
            }
            return -1;
        }
        return -1;
    }

    public void desabilitar() {
        this._listaCertificadosTabela.setEnabled(false);
        this._jspListaCertificadosTabela.setEnabled(false);
        this._listaCertificadosTabela.setFocusable(false);
        setEnabled(false);
    }

    public void selecionarCertificado(int indice) {
        if (indice < 0 || indice >= this._listaCertificadosModel.getRowCount()) {
            throw new IllegalArgumentException("O índice " + indice + " é inválido. Especifique um valor entre 0 e " + (this._listaCertificadosModel.getRowCount() - 1));
        }
        this._listaCertificadosTabela.setRowSelectionInterval(indice, indice);
        getBotaoDetalha().setEnabled(true);
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/PainelCertificados$FiltroPKCS12.class */
    private final class FiltroPKCS12 extends FileFilter {
        private FiltroPKCS12() {
        }

        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".pfx") || f.getName().toLowerCase().endsWith(".p12");
        }

        public String getDescription() {
            return "Arquivos de certificado";
        }
    }

    public int getNumCertificados() {
        if (this._listaCertificadosModel == null) {
            return 0;
        }
        return this._listaCertificadosModel.getRowCount();
    }

    public void setMnemonicoBotaoDetalha(int key) {
        this._botaoDetalha.setMnemonic(key);
    }

    static void inicializaComponente() {
        UIManager.put("FileChooser.lookInLabelText", Recursos.getString("FileChooser.lookInLabelText"));
        UIManager.put("FileChooser.upFolderToolTipText", Recursos.getString("FileChooser.upFolderToolTipText"));
        UIManager.put("FileChooser.filesOfTypeLabelText", Recursos.getString("FileChooser.filesOfTypeLabelText"));
        UIManager.put("FileChooser.fileNameLabelText", Recursos.getString("FileChooser.fileNameLabelText"));
        UIManager.put("FileChooser.homeFolderToolTipText", Recursos.getString("FileChooser.homeFolderToolTipText"));
        UIManager.put("FileChooser.newFolderToolTipText", Recursos.getString("FileChooser.newFolderToolTipText"));
        UIManager.put("FileChooser.listViewButtonToolTipTextlist", Recursos.getString("FileChooser.listViewButtonToolTipTextlist"));
        UIManager.put("FileChooser.detailsViewButtonToolTipText", Recursos.getString("FileChooser.detailsViewButtonToolTipText"));
        UIManager.put("FileChooser.saveButtonText", Recursos.getString("FileChooser.saveButtonText"));
        UIManager.put("FileChooser.openButtonText", Recursos.getString("FileChooser.openButtonText"));
        UIManager.put("FileChooser.cancelButtonText", Recursos.getString("FileChooser.cancelButtonText"));
        UIManager.put("FileChooser.updateButtonText", Recursos.getString("FileChooser.updateButtonText"));
        UIManager.put("FileChooser.helpButtonText", Recursos.getString("FileChooser.helpButtonText"));
        UIManager.put("FileChooser.saveButtonToolTipText", Recursos.getString("FileChooser.saveButtonToolTipText"));
        UIManager.put("FileChooser.openButtonToolTipText", Recursos.getString("FileChooser.openButtonToolTipText"));
        UIManager.put("FileChooser.cancelButtonToolTipText", Recursos.getString("FileChooser.cancelButtonToolTipText"));
        UIManager.put("FileChooser.updateButtonToolTipText", Recursos.getString("FileChooser.updateButtonToolTipText"));
        UIManager.put("FileChooser.helpButtonToolTipText", Recursos.getString("FileChooser.helpButtonToolTipText"));
    }
}
