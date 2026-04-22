package jsignnet.crypto;

import java.util.HashMap;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/TipoKeyStore.class */
public class TipoKeyStore {
    public static final int JCEKS_ID = 0;
    public static final int JKS_ID = 1;
    public static final int PKCS11_ID = 2;
    public static final int PKCS12_ID = 3;
    public static final int BKS_ID = 4;
    public static final int UBER_ID = 5;
    public static final int CAPI_ID = 6;
    public static final int KEYCHAINSTORE_ID = 7;
    private final String tipo;
    public final int _idTipo;
    public static final TipoKeyStore JCEKS = new TipoKeyStore("JCEKS", 0);
    public static final TipoKeyStore JKS = new TipoKeyStore("JKS", 1);
    public static final TipoKeyStore PKCS11 = new TipoKeyStore("PKCS11", 2);
    public static final TipoKeyStore PKCS12 = new TipoKeyStore("PKCS12", 3);
    public static final TipoKeyStore BKS = new TipoKeyStore("BKS", 4);
    public static final TipoKeyStore UBER = new TipoKeyStore("UBER", 5);
    public static final TipoKeyStore SUNMSCAPI = new TipoKeyStore("Windows-MY", 6);
    public static final TipoKeyStore KEYCHAINSTORE = new TipoKeyStore("KeychainStore", 7);
    private static final HashMap<String, String> MAPA_STRING = new HashMap<>();

    static {
        MAPA_STRING.put(JKS.toString(), "JKS");
        MAPA_STRING.put(SUNMSCAPI.toString(), "CAPI");
    }

    private TipoKeyStore(String tipoKeystore, int idKeyStore) {
        this.tipo = tipoKeystore;
        this._idTipo = idKeyStore;
    }

    public String toString() {
        return this.tipo;
    }

    public static String formataTexto(String valor) {
        if (MAPA_STRING.containsKey(valor)) {
            return MAPA_STRING.get(valor);
        }
        return valor;
    }
}
