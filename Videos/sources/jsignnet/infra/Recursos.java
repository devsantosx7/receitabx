package jsignnet.infra;

import java.util.ResourceBundle;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/infra/Recursos.class */
public class Recursos {
    private static String nomeArquivoRecursos = "propriedades.Recursos";
    private static ResourceBundle fonteRecursos;

    private Recursos() {
    }

    public static String getString(String nomeRecurso) {
        return getFonteRecursos().getString(nomeRecurso);
    }

    protected static String getNomeArquivoRecursos() {
        return nomeArquivoRecursos;
    }

    public static void setNomeArquivoRecursos(String nomeArquivo) {
        nomeArquivoRecursos = nomeArquivo;
    }

    protected static ResourceBundle getFonteRecursos() {
        if (fonteRecursos == null) {
            fonteRecursos = ResourceBundle.getBundle(getNomeArquivoRecursos());
        }
        return fonteRecursos;
    }

    public static void atualizarFonteRecursos() {
        fonteRecursos = ResourceBundle.getBundle(getNomeArquivoRecursos());
    }
}
