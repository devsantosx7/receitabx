package jsignnet.aplicacao;

import java.awt.Frame;
import javax.swing.JFrame;
import jsignnet.crypto.X509CertificadoUtil;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.gui.DialogoCertificados;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ExibidorCertificados.class */
public class ExibidorCertificados {
    public static void main(String[] args) throws Exception {
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(3);
        JSignNet.adicionaProviders();
        DialogoCertificados dc = new DialogoCertificados((Frame) jf, "Lista de certificados", false);
        dc.setBotaoOk("Sair", 65);
        dc.setLocationRelativeTo(null);
        dc.setVisible(true);
        System.out.println(dc.getCertificadoSelecionado());
        X509CertificadoWrapper cert = dc.getCertificadoSelecionado();
        String valor = X509CertificadoUtil.getNomesAlternativosSujeito(cert.getCadeiaCertificados()[0], "2.16.76.1.3.1");
        System.out.println(valor);
        jf.dispose();
    }
}
