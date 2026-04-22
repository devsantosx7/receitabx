package jsignnet.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/MediadorDialogoSenha.class */
public class MediadorDialogoSenha extends WindowAdapter implements ActionListener, PropertyChangeListener {
    private static final Integer NENHUM = new Integer(-1);
    private char[] _senhaDigitada;
    private final DialogoSenha _tela;

    public MediadorDialogoSenha(DialogoSenha tela) {
        this._tela = tela;
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this._tela.getCampoSenha()) {
            this._tela.getOptionPane().setValue("OK");
        }
    }

    void configurarTeclaEsc() {
        KeyStroke escape = KeyStroke.getKeyStroke(27, 0, false);
        AbstractAction abstractAction = new AbstractAction() { // from class: jsignnet.gui.MediadorDialogoSenha.1
            private static final long serialVersionUID = 1;

            public void actionPerformed(ActionEvent e) {
                MediadorDialogoSenha.this._tela.getOptionPane().setValue("Cancelar");
            }
        };
        this._tela.getRootPane().getInputMap(1).put(escape, "ESCAPE");
        this._tela.getRootPane().getActionMap().put("ESCAPE", abstractAction);
    }

    public char[] getSenha() {
        return this._senhaDigitada;
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("value") && evt.getSource() == this._tela.getOptionPane()) {
            Object valor = this._tela.getOptionPane().getValue();
            if (valor.equals(NENHUM)) {
                return;
            }
            if (valor.equals("OK")) {
                this._senhaDigitada = this._tela.getCampoSenha().getPassword();
            }
            if (valor.equals("Cancelar")) {
                this._senhaDigitada = null;
            }
            this._tela.getCampoSenha().requestFocus();
            this._tela.getCampoSenha().setText((String) null);
            this._tela.setVisible(false);
            this._tela.getOptionPane().setValue(NENHUM);
        }
    }

    public void windowClosing(WindowEvent evt) {
        this._tela.getOptionPane().setValue("Cancelar");
    }

    public void windowOpened(WindowEvent evt) {
        this._senhaDigitada = null;
        SwingUtilities.invokeLater(new Runnable() { // from class: jsignnet.gui.MediadorDialogoSenha.2
            @Override // java.lang.Runnable
            public void run() {
                MediadorDialogoSenha.this._tela.getCampoSenha().requestFocus();
            }
        });
    }
}
