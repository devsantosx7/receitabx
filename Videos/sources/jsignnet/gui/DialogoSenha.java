package jsignnet.gui;

import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/DialogoSenha.class */
public class DialogoSenha extends JDialog {
    private static final long serialVersionUID = 1;
    static final String CANCEL = "Cancelar";
    static final String OK = "OK";
    private JPasswordField _campoSenha;
    private final MediadorDialogoSenha _mediador;
    private JOptionPane _optionPane;

    public DialogoSenha(Dialog chamador) {
        super(chamador, true);
        this._mediador = new MediadorDialogoSenha(this);
        inicializar();
    }

    public DialogoSenha(Frame chamador) {
        super(chamador, true);
        this._mediador = new MediadorDialogoSenha(this);
        inicializar();
    }

    public char[] lerSenha(String titulo, String mensagem) {
        setTitle(titulo);
        getOptionPane().setMessage(new Object[]{mensagem, getCampoSenha()});
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return this._mediador.getSenha();
    }

    private void inicializar() {
        getContentPane().add(getOptionPane());
        pack();
        setResizable(false);
        addWindowListener(this._mediador);
        this._mediador.configurarTeclaEsc();
    }

    JPasswordField getCampoSenha() {
        if (this._campoSenha == null) {
            this._campoSenha = new JPasswordField(10);
            this._campoSenha.addActionListener(this._mediador);
        }
        return this._campoSenha;
    }

    JOptionPane getOptionPane() {
        if (this._optionPane == null) {
            Object[] botoes = {OK, CANCEL};
            this._optionPane = new JOptionPane((Object) null, 3, 2, (Icon) null, botoes);
            this._optionPane.addPropertyChangeListener("value", this._mediador);
        }
        return this._optionPane;
    }
}
