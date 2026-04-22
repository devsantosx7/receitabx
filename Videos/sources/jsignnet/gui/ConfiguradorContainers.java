package jsignnet.gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.swing.JOptionPane;
import jsignnet.aplicacao.JSignNet;
import jsignnet.crypto.TipoKeyStore;
import jsignnet.crypto.X509CertificadoWrapper;
import jsignnet.erro.ErroDeLogin;
import jsignnet.erro.JSignException;
import jsignnet.infra.IContainer;
import jsignnet.infra.IFiltro;
import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/gui/ConfiguradorContainers.class */
public class ConfiguradorContainers {
    private IFiltro filtro;
    private IContainer iContainer;
    private Window dono;
    private ArrayList<Object[]> objetos = new ArrayList<>();
    private Map<String, X509CertificadoWrapper> certificados;
    private String pathContainerPKCS11;
    private Map<String, JSignException> erros;
    public static final TipoKeyStore CAPI = TipoKeyStore.SUNMSCAPI;
    public static final TipoKeyStore JKS = TipoKeyStore.JKS;
    public static final TipoKeyStore PKCS11 = TipoKeyStore.PKCS11;
    public static final TipoKeyStore PKCS12 = TipoKeyStore.PKCS12;
    public static final TipoKeyStore KEYCHAINSTORE = TipoKeyStore.KEYCHAINSTORE;

    public ConfiguradorContainers(IContainer gerenciadorCertificados) {
        this.iContainer = gerenciadorCertificados;
    }

    public void adicionaContainer(TipoKeyStore tipo) {
        adicionaContainer(tipo, null, null);
    }

    public void adicionaContainer(TipoKeyStore tipo, File arquivo) {
        adicionaContainer(tipo, null, arquivo);
    }

    public void adicionaContainer(TipoKeyStore tipo, char[] senha, File arquivo) {
        Object[] objeto = {tipo, senha, arquivo};
        if (tipo == TipoKeyStore.SUNMSCAPI) {
            this.objetos.add(0, objeto);
        } else {
            this.objetos.add(objeto);
        }
    }

    public IFiltro getFiltro() {
        return this.filtro;
    }

    public void setFiltro(IFiltro filtro) {
        this.filtro = filtro;
    }

    public Window getDono() {
        return this.dono;
    }

    public void setDono(Window dono) {
        if (dono != null && !(dono instanceof Frame) && !(dono instanceof Dialog)) {
            throw new IllegalArgumentException("Dono precisa ser um Frame ou Dialog");
        }
        this.dono = dono;
    }

    public void setDialogoPai(Dialog pai) {
        setDono(pai);
    }

    public X509CertificadoWrapper[] getArrayCertificados() {
        if (this.certificados == null) {
            carregaCertificados();
        }
        ArrayList<X509CertificadoWrapper> resultado = new ArrayList<>();
        for (X509CertificadoWrapper certificado : this.certificados.values()) {
            if (getFiltro() == null || getFiltro().isCertificadoValido(certificado)) {
                resultado.add(certificado);
            }
        }
        return (X509CertificadoWrapper[]) resultado.toArray(new X509CertificadoWrapper[0]);
    }

    private void carregaCertificados() {
        this.certificados = new Hashtable();
        if (this.objetos == null || getIContainer() == null) {
            return;
        }
        Iterator<Object[]> iterator = this.objetos.iterator();
        while (iterator.hasNext()) {
            Object[] elemento = iterator.next();
            switch (((TipoKeyStore) elemento[0])._idTipo) {
                case 1:
                    adicionarCertificados(selecionaCertificadosJKS((File) elemento[2], (char[]) elemento[1]));
                    break;
                case 2:
                    adicionarCertificados(selecionaCertificadosPKCS11());
                    break;
                case 3:
                    adicionarCertificados(selecionaCertificadosPKCS12((File) elemento[2], (char[]) elemento[1]));
                    break;
                case 4:
                case TipoKeyStore.UBER_ID /* 5 */:
                default:
                    getErros().put("ERRO", new JSignException("Tipo incompativel"));
                    break;
                case TipoKeyStore.CAPI_ID /* 6 */:
                    adicionarCertificados(selecionaCertificadosCAPI());
                    break;
                case TipoKeyStore.KEYCHAINSTORE_ID /* 7 */:
                    adicionarCertificados(selecionaCertificadosKeyChainStore());
                    break;
            }
        }
    }

    private void adicionarCertificados(Map<String, X509CertificadoWrapper> origem) {
        HashSet<String> seriais = new HashSet<>();
        for (X509CertificadoWrapper certificado : this.certificados.values()) {
            seriais.add(certificado.getSerie());
        }
        for (Map.Entry<String, X509CertificadoWrapper> entrada : origem.entrySet()) {
            if (!seriais.contains(entrada.getValue().getSerie())) {
                this.certificados.put(entrada.getKey(), entrada.getValue());
                seriais.add(entrada.getValue().getSerie());
            }
        }
    }

    private Map<String, X509CertificadoWrapper> selecionaCertificadosKeyChainStore() {
        try {
            return getIContainer().getMapaCertificadosKeyChainStore();
        } catch (JSignException e) {
            getErros().put("KeyChainStore", e);
            JSignNet.logger.warning("Exceção buscando certificados KeyChainStore:" + e.getMessage());
            return Collections.emptyMap();
        }
    }

    private Map<String, X509CertificadoWrapper> selecionaCertificadosPKCS12(File arquivo, char[] pin) {
        Map<String, X509CertificadoWrapper> certificados = null;
        try {
            certificados = getIContainer().getMapaCertificadosPKCS12(arquivo, pin);
        } catch (JSignException e) {
            getErros().put("PKCS12", e);
            JSignNet.logger.warning("Exceção buscando certificados PKCS12 diferente de ErroDeLogin:" + e.getMessage());
        }
        return certificados;
    }

    private Map<String, X509CertificadoWrapper> selecionaCertificadosJKS(File arquivo, char[] pin) {
        if (arquivo == null) {
            return Collections.emptyMap();
        }
        Map<String, X509CertificadoWrapper> certificados = new Hashtable<>();
        try {
            certificados.putAll(getIContainer().getMapaCertificadosJKS(arquivo, pin));
        } catch (JSignException e) {
            getErros().put("JKS", e);
            JSignNet.logger.warning("Exceção buscando certificados JKS diferente de ErroDeLogin:" + e.getMessage());
        }
        return certificados;
    }

    private Map<String, X509CertificadoWrapper> selecionaCertificadosCAPI() {
        Map<String, X509CertificadoWrapper> certificados = Collections.emptyMap();
        try {
            certificados = getIContainer().getMapaCertificadosCAPI();
        } catch (JSignException e) {
            getErros().put("CAPI", e);
            JSignNet.logger.warning("Exceção buscando certificados CAPI diferente de ErroDeLogin:" + e.getMessage());
        }
        return certificados;
    }

    private Map<String, X509CertificadoWrapper> selecionaCertificadosPKCS11() {
        Map<String, String> mapaHardwarePKCS11;
        Map<String, X509CertificadoWrapper> certificados = new Hashtable<>();
        try {
            File diretorioConfiguracao = new File(getPathContainerPKCS11());
            mapaHardwarePKCS11 = getIContainer().getHardwarePKCS11(diretorioConfiguracao);
        } catch (JSignException e) {
            getErros().put("PKCS11", e);
            JSignNet.logger.warning("Exceção buscando certificados PKCS11 diferente de ErroDeLogin:" + e.getMessage());
        }
        if (mapaHardwarePKCS11.size() <= 0) {
            return certificados;
        }
        for (String container : mapaHardwarePKCS11.keySet()) {
            try {
                certificados.putAll(getIContainer().getMapaCertificadosPKCS11(mapaHardwarePKCS11.get(container), null));
            } catch (ErroDeLogin e2) {
                try {
                    if (this.dono != null) {
                        char[] senha = null;
                        if (this.dono instanceof Frame) {
                            senha = new DialogoSenha(this.dono).lerSenha(Recursos.getString("LoginContainer.titulo"), Recursos.getString("LoginContainer.mensagem") + " " + container);
                        } else if (this.dono instanceof Dialog) {
                            senha = new DialogoSenha(this.dono).lerSenha(Recursos.getString("LoginContainer.titulo"), Recursos.getString("LoginContainer.mensagem") + " " + container);
                        }
                        if (senha != null) {
                            certificados.putAll(getIContainer().getMapaCertificadosPKCS11(mapaHardwarePKCS11.get(container), senha));
                        }
                    }
                } catch (ErroDeLogin e3) {
                    JOptionPane.showMessageDialog(this.dono, Recursos.getString("Login.erro"), Recursos.getString("Login.titulo"), 0);
                } catch (JSignException e4) {
                    getErros().put(container, e4);
                    JSignNet.logger.warning("Exceção buscando certificados PKCS11 diferente de ErroDeLogin:" + e4.getMessage());
                }
            } catch (JSignException e5) {
                getErros().put(container, e5);
                JSignNet.logger.warning("Exceção buscando certificados PKCS11 diferente de ErroDeLogin:" + e5.getMessage());
            }
        }
        return certificados;
    }

    protected IContainer getIContainer() {
        return this.iContainer;
    }

    protected void setIContainer(IContainer container) {
        this.iContainer = container;
    }

    public String getPathContainerPKCS11() {
        if (this.pathContainerPKCS11 == null) {
            this.pathContainerPKCS11 = System.getProperty("user.home") + File.separator + "pkcs11";
        }
        return this.pathContainerPKCS11;
    }

    public void setPathContainerPKCS11(String pathContainerPKCS11) {
        this.pathContainerPKCS11 = pathContainerPKCS11;
    }

    public Map<String, JSignException> getErros() {
        if (this.erros == null) {
            this.erros = new Hashtable();
        }
        return this.erros;
    }

    public void limparCache() {
        this.certificados = null;
    }
}
