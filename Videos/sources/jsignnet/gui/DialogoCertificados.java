package jsignnet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import jsignnet.aplicacao.JSignNet;
import jsignnet.aplicacao.LeitorCertificados;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.crypto.icp.FiltroICP;
import jsignnet.erro.JSignException;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/DialogoCertificados.class */
public class DialogoCertificados extends JDialog implements ObservadorPainelCertificados {
    private static final long serialVersionUID = 1;
    private PainelCertificados _painel;
    private X509CertificadoWrapper _certSelecionado;
    private JButton _botaoAbrir;
    private JButton _botaoRecarregar;
    private Action _acaoOk;
    private JButton _botaoCancelar;
    private boolean _fCertificadoSelecionado;
    private boolean _fCancelado;
    private boolean _fOk;
    private Logger _logger;
    private String _msgErroCarregamentoCertificado;
    private boolean _incluidoCertificadoPKCS12;
    private LeitorCertificados _leitorCertificados;
    private JButton _botaoAjuda;
    private JButton _botaoOk;
    private ConfiguradorContainers _configurador;
    private Icon _iconeProgresso;
    private Icon _iconeDivisoria;
    private String _tituloErro;
    private String _tituloSemCertificados;
    private String _msgSemCertificados;
    private boolean _exibirMsgSemCertificados;
    private boolean _apenasICP;

    public DialogoCertificados(Dialog owner) throws HeadlessException {
        this(owner, owner.getTitle(), true);
    }

    public DialogoCertificados(Frame owner) throws HeadlessException {
        this(owner, owner.getTitle(), true);
    }

    public DialogoCertificados(Dialog owner, String title) throws HeadlessException {
        this(owner, title, true);
    }

    public DialogoCertificados(Frame owner, String title) throws HeadlessException {
        this(owner, title, true);
    }

    public DialogoCertificados(Dialog owner, String title, GraphicsConfiguration gc) throws HeadlessException {
        this(owner, title, gc, true);
    }

    public DialogoCertificados(Frame owner, String title, GraphicsConfiguration gc) {
        this(owner, title, gc, true);
    }

    public DialogoCertificados(Frame owner, String title, GraphicsConfiguration gc, boolean apenasICP) {
        super(owner, title, true, gc);
        this._acaoOk = new AcaoOk();
        this._fCancelado = false;
        this._msgErroCarregamentoCertificado = "Erro ao encontrar os certificados instalados.\nAlguns de seus certificados não serão exibidos.\nVerifique se outros programas conseguem exibir todos os seus certificados digitais instalados e tente novamente.";
        this._leitorCertificados = JSignNet.getLeitorCertificados();
        this._iconeProgresso = ImagensPadrao.CARREGANDO;
        this._iconeDivisoria = ImagensPadrao.FAIXA;
        this._tituloErro = "Receitanet";
        this._exibirMsgSemCertificados = false;
        this._apenasICP = true;
        this._apenasICP = apenasICP;
        montarGUI();
    }

    public DialogoCertificados(Dialog owner, String title, GraphicsConfiguration gc, boolean apenasICP) {
        super(owner, title, true, gc);
        this._acaoOk = new AcaoOk();
        this._fCancelado = false;
        this._msgErroCarregamentoCertificado = "Erro ao encontrar os certificados instalados.\nAlguns de seus certificados não serão exibidos.\nVerifique se outros programas conseguem exibir todos os seus certificados digitais instalados e tente novamente.";
        this._leitorCertificados = JSignNet.getLeitorCertificados();
        this._iconeProgresso = ImagensPadrao.CARREGANDO;
        this._iconeDivisoria = ImagensPadrao.FAIXA;
        this._tituloErro = "Receitanet";
        this._exibirMsgSemCertificados = false;
        this._apenasICP = true;
        this._apenasICP = apenasICP;
        montarGUI();
    }

    public DialogoCertificados(Frame owner, String title, boolean apenasICP) {
        super(owner, title, true);
        this._acaoOk = new AcaoOk();
        this._fCancelado = false;
        this._msgErroCarregamentoCertificado = "Erro ao encontrar os certificados instalados.\nAlguns de seus certificados não serão exibidos.\nVerifique se outros programas conseguem exibir todos os seus certificados digitais instalados e tente novamente.";
        this._leitorCertificados = JSignNet.getLeitorCertificados();
        this._iconeProgresso = ImagensPadrao.CARREGANDO;
        this._iconeDivisoria = ImagensPadrao.FAIXA;
        this._tituloErro = "Receitanet";
        this._exibirMsgSemCertificados = false;
        this._apenasICP = true;
        this._apenasICP = apenasICP;
        montarGUI();
    }

    public DialogoCertificados(Dialog owner, String title, boolean apenasICP) {
        super(owner, title, true);
        this._acaoOk = new AcaoOk();
        this._fCancelado = false;
        this._msgErroCarregamentoCertificado = "Erro ao encontrar os certificados instalados.\nAlguns de seus certificados não serão exibidos.\nVerifique se outros programas conseguem exibir todos os seus certificados digitais instalados e tente novamente.";
        this._leitorCertificados = JSignNet.getLeitorCertificados();
        this._iconeProgresso = ImagensPadrao.CARREGANDO;
        this._iconeDivisoria = ImagensPadrao.FAIXA;
        this._tituloErro = "Receitanet";
        this._exibirMsgSemCertificados = false;
        this._apenasICP = true;
        this._apenasICP = apenasICP;
        montarGUI();
    }

    private void montarGUI() {
        getContentPane().setLayout(new BorderLayout(5, 5));
        this._painel = new PainelCertificados(this, !this._apenasICP);
        this._painel.setName("PainelCertificados");
        this._painel.adicionarObservador(this);
        this._painel.setTitulo("Selecione um certificado digital");
        this._painel.setPreferredSize(new Dimension(600, 200));
        this._painel.setMnemonicoBotaoDetalha(88);
        getContentPane().add(this._painel, "Center");
        getContentPane().add(montarPainelInferior(), "South");
        pack();
        addWindowListener(new WindowAdapter() { // from class: jsignnet.gui.DialogoCertificados.1
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                DialogoCertificados.this.carregarCertificadosIniciais();
                DialogoCertificados.this.repaint();
            }
        });
    }

    private Component montarPainelInferior() {
        Box box = Box.createVerticalBox();
        box.add(montarPainelBotoes());
        return box;
    }

    public void setAjuda(Action acao, Icon icone) {
        if (acao == null) {
            throw new IllegalArgumentException("A ação de ajuda precisa ser especificada.");
        }
        this._botaoAjuda.setAction(acao);
        if (icone != null) {
            this._botaoAjuda.setIcon(icone);
            this._botaoAjuda.setPreferredSize(new Dimension(icone.getIconWidth(), icone.getIconHeight()));
            this._botaoAjuda.setText("");
            this._botaoAjuda.setContentAreaFilled(false);
            this._botaoAjuda.setFocusPainted(false);
            this._botaoAjuda.setBorderPainted(false);
        }
        KeyStroke tecla = (KeyStroke) acao.getValue("AcceleratorKey");
        String chaveAcao = getClass().getName() + ".ajuda";
        this._botaoAjuda.getInputMap(2).put(tecla, chaveAcao);
        this._botaoAjuda.getActionMap().put(chaveAcao, acao);
        this._botaoAjuda.setVisible(true);
        this._botaoAjuda.invalidate();
    }

    public void setBotaoOk(String texto, int mnemonic) {
        this._botaoOk.setText(texto);
        this._botaoOk.setMnemonic(mnemonic);
    }

    private Component montarPainelBotoes() {
        Box box = Box.createHorizontalBox();
        this._botaoAjuda = new JButton();
        this._botaoAjuda.setName("DI.btAjuda");
        this._botaoAjuda.setVisible(false);
        box.add(this._botaoAjuda);
        box.add(Box.createHorizontalStrut(6));
        this._botaoAbrir = new JButton(new AcaoAbrirArquivo());
        this._botaoAbrir.setName("DI.btAbrir");
        this._botaoAbrir.setMnemonic(66);
        box.add(this._botaoAbrir);
        box.add(Box.createHorizontalStrut(6));
        this._botaoRecarregar = new JButton(new AcaoRecarregar());
        this._botaoRecarregar.setName("DI.btRecarregar");
        this._botaoRecarregar.setMnemonic(84);
        box.add(this._botaoRecarregar);
        box.add(Box.createHorizontalGlue());
        this._botaoOk = new JButton(this._acaoOk);
        this._botaoOk.setName("DI.btOk");
        this._botaoOk.setMnemonic(79);
        this._acaoOk.setEnabled(false);
        box.add(this._botaoOk);
        box.add(Box.createHorizontalStrut(6));
        AcaoCancelar acaoCancelar = new AcaoCancelar();
        this._botaoCancelar = new JButton(acaoCancelar);
        this._botaoCancelar.setName("DI.btSair");
        this._botaoCancelar.setMnemonic(67);
        KeyStroke teclaCancelar = (KeyStroke) acaoCancelar.getValue("AcceleratorKey");
        String chaveAcaoCancelar = getClass().getName() + ".cancelar";
        this._botaoCancelar.getInputMap(2).put(teclaCancelar, chaveAcaoCancelar);
        this._botaoCancelar.getActionMap().put(chaveAcaoCancelar, acaoCancelar);
        box.add(this._botaoCancelar);
        box.setBorder(new EmptyBorder(6, 6, 6, 6));
        return box;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void carregarCertificadosIniciais() {
        new Thread(new AcaoCarregarCertificados(), "DialogoCertificados:Carregando certificados digitais").start();
    }

    @Override // jsignnet.gui.ObservadorPainelCertificados
    public void certificadoSelecionado(PainelCertificados painel, X509CertificadoWrapper selecionado) {
        if (!this._painel.equals(painel) || selecionado == null) {
            return;
        }
        this._certSelecionado = selecionado;
        this._fCertificadoSelecionado = true;
        atualizarHabilitacaoComandoOK();
        if (this._certSelecionado.getDataVencimento().compareTo(new Date()) < 0) {
            JOptionPane.showMessageDialog(this, "O certificado selecionado provavelmente já passou de sua data de validade.\nPode não ser possível usá-lo nesta aplicação.", this._tituloErro, 2);
        }
    }

    private void atualizarHabilitacaoComandoOK() {
        this._acaoOk.setEnabled(this._fCertificadoSelecionado);
    }

    public X509CertificadoWrapper getCertificadoSelecionado() {
        if (this._fCancelado || !this._fOk) {
            return null;
        }
        return this._certSelecionado;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ConfiguradorContainers obterConfigurador() {
        if (this._configurador == null) {
            setConfigurador((File) null);
        }
        return this._configurador;
    }

    public void setConfigurador(ConfiguradorContainers conf) {
        this._configurador = conf;
        if (conf != null) {
            this._leitorCertificados.setFiltro(conf.getFiltro());
        }
    }

    public void setConfigurador(File diretorioConfiguracaoPKCS11) {
        ConfiguradorContainers configurador = new ConfiguradorContainers(this._leitorCertificados);
        if (diretorioConfiguracaoPKCS11 != null) {
            configurador.setPathContainerPKCS11(diretorioConfiguracaoPKCS11.getAbsolutePath());
        }
        if (this._apenasICP) {
            configurador.setFiltro(FiltroICP.singleton());
            this._leitorCertificados.setFiltro(FiltroICP.singleton());
        }
        configurador.adicionaContainer(ConfiguradorContainers.CAPI);
        configurador.adicionaContainer(ConfiguradorContainers.PKCS11);
        configurador.adicionaContainer(ConfiguradorContainers.KEYCHAINSTORE);
        configurador.setDono(this);
        this._configurador = configurador;
    }

    public void setLogger(Logger logger) {
        this._logger = logger;
    }

    public void setMsgErroCarregamentoCertificado(String titulo, String mensagem) {
        if (mensagem != null) {
            this._msgErroCarregamentoCertificado = mensagem;
        }
        if (titulo != null) {
            this._tituloErro = titulo;
        }
    }

    public void setLogotipo(JComponent logotipo) {
        if (logotipo != null) {
            getContentPane().add(logotipo, "North");
            invalidate();
            pack();
        }
    }

    public void setIconesTelaProgresso(Icon iconeProgresso, Icon iconeDivisoria) {
        this._iconeProgresso = iconeProgresso;
        this._iconeDivisoria = iconeDivisoria;
    }

    public void setMsgSemCertificados(String titulo, String mensagem) {
        if (titulo == null || mensagem == null) {
            throw new IllegalArgumentException("O título e a mensagem não podem ser nulos.");
        }
        this._tituloSemCertificados = titulo;
        this._msgSemCertificados = mensagem;
        this._exibirMsgSemCertificados = true;
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/DialogoCertificados$AcaoCarregarCertificados.class */
    private final class AcaoCarregarCertificados implements Runnable {
        private volatile TelaProgressoIndeterminado _telaProgresso;
        X509CertificadoWrapper[] _certificados;

        private AcaoCarregarCertificados() {
        }

        @Override // java.lang.Runnable
        public void run() {
            Runnable acaoFimCarregamento = new Runnable() { // from class: jsignnet.gui.DialogoCertificados.AcaoCarregarCertificados.1
                @Override // java.lang.Runnable
                public void run() throws InterruptedException {
                    while (!AcaoCarregarCertificados.this._telaProgresso.isVisible()) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                        }
                    }
                    Cursor padrao = Cursor.getDefaultCursor();
                    AcaoCarregarCertificados.this.trocarCursorMouse(padrao);
                    AcaoCarregarCertificados.this._telaProgresso.setVisible(false);
                    AcaoCarregarCertificados.this._telaProgresso.dispose();
                    DialogoCertificados.this._painel.repaint();
                    if ((AcaoCarregarCertificados.this._certificados == null || AcaoCarregarCertificados.this._certificados.length == 0) && DialogoCertificados.this._exibirMsgSemCertificados) {
                        JOptionPane.showMessageDialog(DialogoCertificados.this, DialogoCertificados.this._msgSemCertificados, DialogoCertificados.this._tituloSemCertificados, 1);
                    }
                }
            };
            exibirTelaProgresso(acaoFimCarregamento);
            try {
                Cursor ampulheta = Cursor.getPredefinedCursor(3);
                trocarCursorMouse(ampulheta);
                this._certificados = DialogoCertificados.this.obterConfigurador().getArrayCertificados();
                if (this._telaProgresso == null || !this._telaProgresso.foiCancelado()) {
                    DialogoCertificados.this._painel.setCertificados(this._certificados);
                }
            } finally {
                SwingUtilities.invokeLater(acaoFimCarregamento);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void trocarCursorMouse(Cursor cursor) {
            DialogoCertificados.this.setCursor(cursor);
        }

        private void exibirTelaProgresso(final Runnable acaoCancelamento) {
            SwingUtilities.invokeLater(new Runnable() { // from class: jsignnet.gui.DialogoCertificados.AcaoCarregarCertificados.2
                @Override // java.lang.Runnable
                public void run() {
                    AcaoCarregarCertificados.this._telaProgresso = new TelaProgressoIndeterminado((Dialog) DialogoCertificados.this, "Verificando os certificados digitais...", true, DialogoCertificados.this._iconeProgresso, DialogoCertificados.this._iconeDivisoria);
                    AcaoCarregarCertificados.this._telaProgresso.setTexto("Verificando os certificados digitais. Favor aguardar alguns instantes.");
                    AcaoCarregarCertificados.this._telaProgresso.setAcaoCancelamento(acaoCancelamento);
                    AcaoCarregarCertificados.this._telaProgresso.setVisible(true);
                }
            });
        }
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/DialogoCertificados$AcaoAbrirArquivo.class */
    private class AcaoAbrirArquivo extends AbstractAction {
        private static final long serialVersionUID = 1;

        public AcaoAbrirArquivo() {
            super("Buscar certificado");
        }

        public void actionPerformed(ActionEvent e) {
            try {
                int numIncluidos = DialogoCertificados.this._painel.incluirCertificadosPKCS12(DialogoCertificados.this._leitorCertificados, DialogoCertificados.this.getTitle(), "Informe a senha para o certificado digital");
                if (numIncluidos > 0) {
                    DialogoCertificados.this._acaoOk.setEnabled(false);
                    DialogoCertificados.this._painel.selecionarCertificado(DialogoCertificados.this._painel.getNumCertificados() - 1);
                    DialogoCertificados.this._incluidoCertificadoPKCS12 = true;
                } else if (numIncluidos == 0) {
                    JOptionPane.showMessageDialog(DialogoCertificados.this, "Nenhum certificado foi carregado do arquivo.\nOu não há certificados no arquivo ou nenhum dos certificados\npresentes no arquivo é válido para esta aplicação.", DialogoCertificados.this._tituloErro, 2);
                }
            } catch (JSignException exc) {
                JOptionPane.showMessageDialog(DialogoCertificados.this, DialogoCertificados.this._msgErroCarregamentoCertificado, DialogoCertificados.this._tituloErro, 0);
                if (DialogoCertificados.this._logger != null) {
                    DialogoCertificados.this._logger.log(Level.WARNING, "Falha ao carregar certificados.", (Throwable) exc);
                }
            }
        }
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/DialogoCertificados$AcaoRecarregar.class */
    private class AcaoRecarregar extends AbstractAction {
        private static final long serialVersionUID = 1;

        public AcaoRecarregar() {
            super("Atualizar lista");
        }

        public void actionPerformed(ActionEvent e) {
            DialogoCertificados.this.obterConfigurador().limparCache();
            DialogoCertificados.this.carregarCertificadosIniciais();
            if (DialogoCertificados.this._incluidoCertificadoPKCS12) {
                JOptionPane.showMessageDialog(DialogoCertificados.this, "Os certificados que foram carregados de um arquivo foram removidos da lista.\nUse o comando \"Buscar certificado\" e os importe novamente caso necessário.", DialogoCertificados.this._tituloErro, 2);
                DialogoCertificados.this._incluidoCertificadoPKCS12 = false;
            }
        }
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/DialogoCertificados$AcaoOk.class */
    private class AcaoOk extends AbstractAction {
        private static final long serialVersionUID = 1;

        public AcaoOk() {
            super("Ok");
        }

        public void actionPerformed(ActionEvent e) {
            DialogoCertificados.this._fOk = true;
            DialogoCertificados.this.dispose();
        }
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/DialogoCertificados$AcaoCancelar.class */
    private class AcaoCancelar extends AbstractAction {
        private static final long serialVersionUID = 1;

        public AcaoCancelar() {
            super("Cancelar");
            putValue("AcceleratorKey", KeyStroke.getKeyStroke(27, 0));
        }

        public void actionPerformed(ActionEvent e) {
            DialogoCertificados.this._fCancelado = true;
            DialogoCertificados.this.dispose();
        }
    }
}
