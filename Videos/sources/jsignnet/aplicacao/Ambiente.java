package jsignnet.aplicacao;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/Ambiente.class */
public class Ambiente {
    private static final String WINDOWS = "win";
    private static final String MACOSX = "mac";

    public boolean windows() {
        return verificarSO(WINDOWS);
    }

    public boolean macosx() {
        return verificarSO(MACOSX);
    }

    public boolean linux() {
        return (windows() || macosx()) ? false : true;
    }

    private boolean verificarSO(String so) {
        String nomeSO = System.getProperty("os.name").toLowerCase();
        return nomeSO.indexOf(so.toLowerCase()) > -1;
    }
}
