package jsignnet.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DateFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import jsignnet.aplicacao.JSignNet;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.erro.ErroDeES;
import jsignnet.erro.JSignException;
import jsignnet.infra.FormataUtil;
import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/ViewCertificado.class */
public class ViewCertificado extends JDialog {
    private static final long serialVersionUID = 1;
    private JPanel painelBotaoOk;
    private JButton botaoOk;
    private JTabbedPane jtp;
    private X509CertificadoWrapper listaCertificados;
    private JScrollPane painelPropriedade;
    private JTextArea valorPropriedade;
    private JTree arvorePropriedades;

    public ViewCertificado(Dialog parent, String titulo, boolean isModal, X509CertificadoWrapper certificado) {
        super(parent, titulo, isModal);
        this.listaCertificados = certificado;
        inicializaComponentes();
    }

    public ViewCertificado(Frame parent, String titulo, boolean isModal, X509CertificadoWrapper certificado) {
        super(parent, titulo, isModal);
        this.listaCertificados = certificado;
        inicializaComponentes();
    }

    private void inicializaComponentes() {
        Dimension tamanhoPainelScroll = new Dimension(500, 300 / 4);
        JPanel painelDadosCertificado = new JPanel();
        GridBagLayout layoutDadosCertificado = new GridBagLayout();
        painelDadosCertificado.setLayout(layoutDadosCertificado);
        GridBagConstraints constraintDadosCertificado = new GridBagConstraints();
        constraintDadosCertificado.gridx = 0;
        constraintDadosCertificado.gridy = 0;
        constraintDadosCertificado.gridwidth = 3;
        constraintDadosCertificado.gridheight = 1;
        constraintDadosCertificado.insets = new Insets(14, 8, 2, 8);
        constraintDadosCertificado.anchor = 18;
        painelDadosCertificado.add(new JLabel(Recursos.getString("MostraCertificado.labelEmitidoPara.texto")), constraintDadosCertificado);
        String[] campos = {Recursos.getString("MostraCertificado.campoNome.texto"), Recursos.getString("MostraCertificado.campoOrganizacao.texto"), Recursos.getString("MostraCertificado.campoUnidadeOrganizacional.texto"), Recursos.getString("MostraCertificado.campoSerie.texto"), Recursos.getString("MostraCertificado.campoKeystore.texto")};
        String[] valores = {this.listaCertificados.getEmitidoPara(), this.listaCertificados.getOrganizacao(), this.listaCertificados.getUnidadeOrganizacional(), this.listaCertificados.getSerie(), this.listaCertificados.getTipoKeyStore()};
        String[] tooltips = {Recursos.getString("MostraCertificado.campoNome.tooltip"), Recursos.getString("MostraCertificado.campoOrganizacao.tooltip"), Recursos.getString("MostraCertificado.campoUnidadeOrganizacional.tooltip"), Recursos.getString("MostraCertificado.campoSerie.tooltip")};
        formataFormulario(painelDadosCertificado, constraintDadosCertificado, campos, valores, tooltips);
        constraintDadosCertificado.gridx = 0;
        constraintDadosCertificado.gridy++;
        constraintDadosCertificado.gridwidth = 3;
        constraintDadosCertificado.gridheight = 1;
        constraintDadosCertificado.insets = new Insets(14, 8, 2, 8);
        constraintDadosCertificado.anchor = 18;
        painelDadosCertificado.add(new JLabel(Recursos.getString("MostraCertificado.labelEmitidoPor.texto")), constraintDadosCertificado);
        String[] campos2 = {Recursos.getString("MostraCertificado.campoNome.texto"), Recursos.getString("MostraCertificado.campoOrganizacao.texto"), Recursos.getString("MostraCertificado.campoUnidadeOrganizacional.texto")};
        String[] valores2 = {this.listaCertificados.getEmitidoPor(), this.listaCertificados.getOrganizacaoEmissora(), this.listaCertificados.getUnidadeOrganizacionalEmissora()};
        String[] tooltips2 = {Recursos.getString("MostraCertificado.campoNome.tooltip"), Recursos.getString("MostraCertificado.campoOrganizacao.tooltip"), Recursos.getString("MostraCertificado.campoUnidadeOrganizacional.tooltip")};
        formataFormulario(painelDadosCertificado, constraintDadosCertificado, campos2, valores2, tooltips2);
        constraintDadosCertificado.gridx = 0;
        constraintDadosCertificado.gridy++;
        constraintDadosCertificado.gridwidth = 3;
        constraintDadosCertificado.gridheight = 1;
        constraintDadosCertificado.insets = new Insets(14, 8, 2, 8);
        constraintDadosCertificado.anchor = 18;
        painelDadosCertificado.add(new JLabel(Recursos.getString("MostraCertificado.labelValidade.texto")), constraintDadosCertificado);
        String[] campos3 = {Recursos.getString("MostraCertificado.campoDataEmissao.texto"), Recursos.getString("MostraCertificado.campoDataExpiracao.texto")};
        String[] valores3 = {DateFormat.getDateInstance(2).format(this.listaCertificados.getDataEmissao()), DateFormat.getDateInstance(2).format(this.listaCertificados.getDataVencimento())};
        String[] tooltips3 = {Recursos.getString("MostraCertificado.campoDataEmissao.tooltip"), Recursos.getString("MostraCertificado.campoDataExpiracao.tooltip")};
        formataFormulario(painelDadosCertificado, constraintDadosCertificado, campos3, valores3, tooltips3);
        constraintDadosCertificado.gridx = 0;
        constraintDadosCertificado.gridy++;
        constraintDadosCertificado.gridwidth = 3;
        constraintDadosCertificado.gridheight = 1;
        constraintDadosCertificado.insets = new Insets(14, 8, 2, 8);
        constraintDadosCertificado.anchor = 18;
        painelDadosCertificado.add(new JLabel(Recursos.getString("MostraCertificado.labelImpressao.texto")), constraintDadosCertificado);
        String[] campos4 = {Recursos.getString("MostraCertificado.campoImpressaoDigitalSHA.texto"), Recursos.getString("MostraCertificado.campoImpressaoDigitalMD5.texto")};
        try {
            valores3 = new String[]{this.listaCertificados.getSHA1FingerPrint(), this.listaCertificados.getMD5FingerPrint()};
        } catch (JSignException e) {
            JSignNet.logger.warning("Erro calculando o thumbprint do certificado " + this.listaCertificados.getAlias());
        }
        String[] tooltips4 = {Recursos.getString("MostraCertificado.campoImpressaoDigitalSHA.tooltip"), Recursos.getString("MostraCertificado.campoImpressaoDigitalMD5.tooltip")};
        formataFormulario(painelDadosCertificado, constraintDadosCertificado, campos4, valores3, tooltips4);
        this.painelBotaoOk = new JPanel(new FlowLayout(2));
        this.botaoOk = new JButton(Recursos.getString("MostraCertificado.botaoOK.texto"));
        this.botaoOk.addActionListener(new ActionListener() { // from class: jsignnet.gui.ViewCertificado.1
            public void actionPerformed(ActionEvent evt) {
                ViewCertificado.this.fechaJanela();
            }
        });
        MutableTreeNode mutableTreeNode = null;
        X509CertificadoWrapper[] lista = this.listaCertificados.getCadeiaCertificadosWrapper();
        for (X509CertificadoWrapper x509CertificadoWrapper : lista) {
            MutableTreeNode viewCertificadoTreeNode = new ViewCertificadoTreeNode(x509CertificadoWrapper);
            if (mutableTreeNode != null) {
                viewCertificadoTreeNode.add(mutableTreeNode);
            }
            mutableTreeNode = viewCertificadoTreeNode;
        }
        JTree arvoreHierarquia = new JTree(mutableTreeNode);
        arvoreHierarquia.getSelectionModel().setSelectionMode(1);
        arvoreHierarquia.addTreeSelectionListener(new TreeSelectionListener() { // from class: jsignnet.gui.ViewCertificado.2
            public void valueChanged(TreeSelectionEvent tse) {
                TreePath tp = tse.getNewLeadSelectionPath();
                if (tp.getLastPathComponent() instanceof ViewCertificadoTreeNode) {
                    int[] selecao = ViewCertificado.this.arvorePropriedades.getSelectionRows();
                    ViewCertificado.this.populaPropriedades((ViewCertificadoTreeNode) tp.getLastPathComponent());
                    ViewCertificado.this.arvorePropriedades.setSelectionRows(selecao);
                }
            }
        });
        JScrollPane painelHierarquia = new JScrollPane(arvoreHierarquia, 20, 30);
        painelHierarquia.setPreferredSize(tamanhoPainelScroll);
        DefaultMutableTreeNode rootPropriedades = new DefaultMutableTreeNode("Certificado");
        DefaultTreeModel model = new DefaultTreeModel(rootPropriedades);
        this.arvorePropriedades = new JTree(model);
        this.arvorePropriedades.getSelectionModel().setSelectionMode(1);
        this.arvorePropriedades.addTreeSelectionListener(new TreeSelectionListener() { // from class: jsignnet.gui.ViewCertificado.3
            public void valueChanged(TreeSelectionEvent tse) {
                TreePath tp = tse.getNewLeadSelectionPath();
                if (tp != null && (tp.getLastPathComponent() instanceof ViewCertificadoPropriedadeTreeNode)) {
                    ViewCertificado.this.valorPropriedade.setText(((ViewCertificadoPropriedadeTreeNode) tp.getLastPathComponent()).getValor());
                }
            }
        });
        this.arvorePropriedades.setRootVisible(false);
        this.painelPropriedade = new JScrollPane(this.arvorePropriedades);
        this.painelPropriedade.setPreferredSize(tamanhoPainelScroll);
        this.painelPropriedade.setAutoscrolls(true);
        this.valorPropriedade = new JTextArea();
        this.valorPropriedade.setEditable(false);
        JScrollPane painelValorPropriedade = new JScrollPane(this.valorPropriedade);
        painelValorPropriedade.setPreferredSize(tamanhoPainelScroll);
        painelValorPropriedade.setAutoscrolls(true);
        JPanel painelDadosPropriedades = new JPanel();
        GridBagLayout layoutDadosPropriedade = new GridBagLayout();
        painelDadosPropriedades.setLayout(layoutDadosPropriedade);
        GridBagConstraints constraintDadosPropriedade = new GridBagConstraints();
        constraintDadosPropriedade.gridx = 0;
        constraintDadosPropriedade.gridy = 0;
        constraintDadosPropriedade.gridwidth = 1;
        constraintDadosPropriedade.gridheight = 1;
        constraintDadosPropriedade.weightx = 0.0d;
        constraintDadosPropriedade.weighty = 0.0d;
        constraintDadosPropriedade.insets = new Insets(14, 8, 2, 8);
        constraintDadosPropriedade.fill = 1;
        constraintDadosPropriedade.anchor = 10;
        painelDadosPropriedades.add(new JLabel(Recursos.getString("MostraCertificado.labelHierarquiaCertificado.texto")), constraintDadosPropriedade);
        constraintDadosPropriedade.gridx = 0;
        constraintDadosPropriedade.gridy = 1;
        constraintDadosPropriedade.gridwidth = 1;
        constraintDadosPropriedade.gridheight = 1;
        constraintDadosPropriedade.weightx = 1.0d;
        constraintDadosPropriedade.weighty = 1.0d;
        constraintDadosPropriedade.insets = new Insets(2, 8, 2, 8);
        constraintDadosPropriedade.fill = 1;
        constraintDadosPropriedade.anchor = 10;
        painelDadosPropriedades.add(painelHierarquia, constraintDadosPropriedade);
        constraintDadosPropriedade.gridx = 0;
        constraintDadosPropriedade.gridy = 2;
        constraintDadosPropriedade.weightx = 1.0d;
        constraintDadosPropriedade.weighty = 1.0d;
        constraintDadosPropriedade.weightx = 0.0d;
        constraintDadosPropriedade.weighty = 0.0d;
        constraintDadosPropriedade.insets = new Insets(14, 8, 2, 8);
        constraintDadosPropriedade.fill = 1;
        constraintDadosPropriedade.anchor = 10;
        painelDadosPropriedades.add(new JLabel(Recursos.getString("MostraCertificado.labelPropriedadeCertificado.texto")), constraintDadosPropriedade);
        constraintDadosPropriedade.gridx = 0;
        constraintDadosPropriedade.gridy = 3;
        constraintDadosPropriedade.gridwidth = 1;
        constraintDadosPropriedade.gridheight = 1;
        constraintDadosPropriedade.weightx = 1.0d;
        constraintDadosPropriedade.weighty = 1.0d;
        constraintDadosPropriedade.insets = new Insets(2, 8, 2, 8);
        constraintDadosPropriedade.anchor = 10;
        painelDadosPropriedades.add(this.painelPropriedade, constraintDadosPropriedade);
        constraintDadosPropriedade.gridx = 0;
        constraintDadosPropriedade.gridy = 4;
        constraintDadosPropriedade.gridwidth = 1;
        constraintDadosPropriedade.gridheight = 1;
        constraintDadosPropriedade.weightx = 0.0d;
        constraintDadosPropriedade.weighty = 0.0d;
        constraintDadosPropriedade.insets = new Insets(14, 8, 2, 8);
        constraintDadosPropriedade.fill = 1;
        constraintDadosPropriedade.anchor = 10;
        painelDadosPropriedades.add(new JLabel(Recursos.getString("MostraCertificado.labelValorCertificado.texto")), constraintDadosPropriedade);
        constraintDadosPropriedade.gridx = 0;
        constraintDadosPropriedade.gridy = 5;
        constraintDadosPropriedade.gridwidth = 1;
        constraintDadosPropriedade.gridheight = 1;
        constraintDadosPropriedade.weightx = 1.0d;
        constraintDadosPropriedade.weighty = 1.0d;
        constraintDadosPropriedade.insets = new Insets(2, 8, 2, 8);
        constraintDadosPropriedade.fill = 1;
        constraintDadosPropriedade.anchor = 10;
        painelDadosPropriedades.add(painelValorPropriedade, constraintDadosPropriedade);
        this.painelBotaoOk.add(this.botaoOk);
        this.jtp = new JTabbedPane();
        this.jtp.addTab(Recursos.getString("MostraCertificado.tab1.label"), painelDadosCertificado);
        this.jtp.addTab(Recursos.getString("MostraCertificado.tab2.label"), painelDadosPropriedades);
        getContentPane().add(this.jtp, "Center");
        getContentPane().add(this.painelBotaoOk, "South");
        setResizable(true);
        addWindowListener(new WindowAdapter() { // from class: jsignnet.gui.ViewCertificado.4
            public void windowClosing(WindowEvent evt) {
                ViewCertificado.this.fechaJanela();
            }
        });
        getRootPane().setDefaultButton(this.botaoOk);
        pack();
        setDefaultCloseOperation(0);
        SwingUtilities.invokeLater(new Runnable() { // from class: jsignnet.gui.ViewCertificado.5
            @Override // java.lang.Runnable
            public void run() {
                ViewCertificado.this.botaoOk.requestFocus();
            }
        });
    }

    public void formataFormulario(JPanel painel, GridBagConstraints gridBagConstraints, String[] labels, String[] valores, String[] tootips) {
        if (labels.length != valores.length) {
            throw new RuntimeException("Erro de parametros.");
        }
        int valorY = gridBagConstraints.gridy + 1;
        for (int i = 0; i < labels.length; i++) {
            JLabel lab = new JLabel(labels[i], 2);
            if (i < tootips.length) {
                lab.setToolTipText(tootips[i]);
            }
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = valorY + i;
            gridBagConstraints.insets = new Insets(0, 8, 2, 8);
            painel.add(lab, gridBagConstraints);
            JLabel valor = new JLabel(valores[i], 2);
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.insets = new Insets(0, 8, 2, 8);
            painel.add(valor, gridBagConstraints);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fechaJanela() {
        setVisible(false);
        dispose();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void populaPropriedades(ViewCertificadoTreeNode node) {
        TreePath path = this.arvorePropriedades.getSelectionPath();
        DefaultTreeModel model = this.arvorePropriedades.getModel();
        DefaultMutableTreeNode rootPropriedades = (DefaultMutableTreeNode) model.getRoot();
        rootPropriedades.removeAllChildren();
        rootPropriedades.insert(new ViewCertificadoPropriedadeTreeNode("Versão", node.getCertificado().getVersao()), 0);
        rootPropriedades.insert(new ViewCertificadoPropriedadeTreeNode("Número de Série", node.getCertificado().getSerie()), 1);
        rootPropriedades.insert(new ViewCertificadoPropriedadeTreeNode("Algoritmo de Assinatura", node.getCertificado().getAlgoritmo()), 2);
        rootPropriedades.insert(new ViewCertificadoPropriedadeTreeNode("Emissor", node.getCertificado().getEmitidoPor()), 3);
        ViewCertificadoPropriedadeTreeNode validade = new ViewCertificadoPropriedadeTreeNode("Validade", "");
        validade.add(new ViewCertificadoPropriedadeTreeNode("Data de Emissão", FormataUtil.formataData(node.getCertificado().getDataEmissao())));
        validade.add(new ViewCertificadoPropriedadeTreeNode("Data de Vencimento", FormataUtil.formataData(node.getCertificado().getDataVencimento())));
        rootPropriedades.insert(validade, 4);
        rootPropriedades.insert(new ViewCertificadoPropriedadeTreeNode("Assunto", node.getCertificado().getAssuntoPrincipal()), 5);
        ViewCertificadoPropriedadeTreeNode informacoesChavePublica = new ViewCertificadoPropriedadeTreeNode("Informações de Chave Pública", "");
        informacoesChavePublica.add(new ViewCertificadoPropriedadeTreeNode("Algoritmo de Chave Pública", node.getCertificado().getChavePublica().getAlgorithm()));
        informacoesChavePublica.add(new ViewCertificadoPropriedadeTreeNode("Chave Pública", node.getCertificado().getChavePublica().toString()));
        rootPropriedades.insert(informacoesChavePublica, 6);
        try {
            ViewCertificadoPropriedadeTreeNode informacoesExtensao = new ViewCertificadoPropriedadeTreeNode("Extensões", "");
            informacoesExtensao.add(new ViewCertificadoPropriedadeTreeNode("Identificador da Chave da Autoridade", node.getCertificado().getIdentificadorChaveAutoridade()));
            informacoesExtensao.add(new ViewCertificadoPropriedadeTreeNode("Uso da chave", node.getCertificado().getUsoChave()));
            informacoesExtensao.add(new ViewCertificadoPropriedadeTreeNode("Nome alternativo para assunto", node.getCertificado().getNomesAlternativos()));
            rootPropriedades.insert(informacoesExtensao, 7);
            ViewCertificadoPropriedadeTreeNode diretivasCertificados = new ViewCertificadoPropriedadeTreeNode("Diretivas dos Certificados", "");
            diretivasCertificados.add(new ViewCertificadoPropriedadeTreeNode("Ponto de Distribuição da Lista de Certificados Revogados", node.getCertificado().getPontoDistribuicaoCRL()));
            diretivasCertificados.add(new ViewCertificadoPropriedadeTreeNode("Uso Avançado da Chave", node.getCertificado().getUsoAvancadoChave()));
            rootPropriedades.insert(diretivasCertificados, 8);
        } catch (IOException e) {
            JSignNet.logger.warning("Exceção de IO acessando propriedades do certficado " + node.getCertificado().getAlias());
        } catch (ErroDeES e2) {
            JSignNet.logger.warning("Exceção de IO acessando propriedades do certficado " + node.getCertificado().getAlias());
        }
        model.reload();
        this.arvorePropriedades.setRootVisible(true);
        expandAll(this.arvorePropriedades);
        this.arvorePropriedades.addSelectionPath(path);
        this.arvorePropriedades.setLeadSelectionPath(path);
    }

    public void expandAll(JTree tree) {
        for (int row = 0; row < tree.getRowCount(); row++) {
            tree.expandRow(row);
        }
    }
}
