package jsignnet.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import jsignnet.aplicacao.ControleProgresso;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/TelaProgressoIndeterminado.class */
class TelaProgressoIndeterminado extends JDialog implements ControleProgresso {
    private static final long serialVersionUID = 1;
    private boolean _fCancelado;
    private JLabel _lbMsg;
    private JButton _btCancelar;
    private boolean _exibeCancelar;
    private Runnable _acaoCancelamento;

    public TelaProgressoIndeterminado(Frame owner, String titulo, boolean exibeCancelar, Icon iconeCarregando, Icon iconeDivisoria) {
        super(owner, true);
        this._fCancelado = false;
        this._exibeCancelar = true;
        if (titulo != null) {
            setTitle(titulo);
        } else {
            setTitle(owner.getTitle());
        }
        setLocationRelativeTo(owner);
        this._exibeCancelar = exibeCancelar;
        montarGUI(iconeCarregando, iconeDivisoria);
        pack();
    }

    public TelaProgressoIndeterminado(Dialog owner, String titulo, boolean exibeCancelar, Icon iconeCarregando, Icon iconeDivisoria) {
        super(owner, true);
        this._fCancelado = false;
        this._exibeCancelar = true;
        setTitle(titulo);
        setLocationRelativeTo(owner);
        this._exibeCancelar = exibeCancelar;
        montarGUI(iconeCarregando, iconeDivisoria);
        pack();
    }

    private void montarGUI(Icon iconeCarregando, Icon iconeDivisoria) {
        getContentPane().setLayout(new BorderLayout());
        if (iconeCarregando != null) {
            this._lbMsg = new JLabel("", iconeCarregando, 11);
            this._lbMsg.setHorizontalAlignment(0);
            this._lbMsg.setName("TPI.lbMsg");
            this._lbMsg.setBorder(new EmptyBorder(12, 24, 12, 24));
            this._lbMsg.setIconTextGap(12);
            getContentPane().add(this._lbMsg, "Center");
        }
        Box box = Box.createVerticalBox();
        if (this._exibeCancelar) {
            Box boxFaixa = Box.createHorizontalBox();
            if (iconeDivisoria != null) {
                boxFaixa.add(Box.createHorizontalGlue());
                JLabel lbFaixa = new JLabel(iconeDivisoria);
                boxFaixa.add(lbFaixa);
            }
            boxFaixa.add(Box.createHorizontalGlue());
            box.add(boxFaixa);
            this._btCancelar = new JButton(new AbstractAction("Cancelar") { // from class: jsignnet.gui.TelaProgressoIndeterminado.1
                private static final long serialVersionUID = 1;

                public void actionPerformed(ActionEvent e) {
                    TelaProgressoIndeterminado.this._fCancelado = true;
                    TelaProgressoIndeterminado.this._btCancelar.setEnabled(false);
                    if (TelaProgressoIndeterminado.this._acaoCancelamento != null) {
                        TelaProgressoIndeterminado.this._acaoCancelamento.run();
                    }
                }
            });
            this._btCancelar.setName("TPI.btCancelar");
            this._btCancelar.setHorizontalTextPosition(11);
            this._btCancelar.setVerticalTextPosition(0);
            Box boxCancelar = Box.createHorizontalBox();
            boxCancelar.add(Box.createHorizontalGlue());
            boxCancelar.add(this._btCancelar);
            boxCancelar.add(Box.createHorizontalGlue());
            boxCancelar.setBorder(new EmptyBorder(6, 6, 6, 6));
            box.add(boxCancelar);
        }
        getContentPane().add(box, "South");
        setDefaultCloseOperation(0);
    }

    public void setTexto(String texto) {
        this._lbMsg.setText(texto);
        pack();
        setLocationRelativeTo(getOwner());
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public boolean foiCancelado() {
        return this._fCancelado;
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void setMaximo(long maximo) {
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void tarefaTerminou() {
        setVisible(false);
    }

    public void setAcaoCancelamento(Runnable acao) {
        this._acaoCancelamento = acao;
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void setMinimo(long min) {
    }

    @Override // jsignnet.aplicacao.ControleProgresso
    public void valorMudou(long novoValor) {
    }
}
