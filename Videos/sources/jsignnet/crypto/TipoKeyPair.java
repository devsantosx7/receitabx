package jsignnet.crypto;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/crypto/TipoKeyPair.class */
public class TipoKeyPair {
    public static final TipoKeyPair RSA = new TipoKeyPair("RSA");
    public static final TipoKeyPair DSA = new TipoKeyPair("DSA");
    public static final TipoKeyPair ECDSA = new TipoKeyPair("ECDSA");
    private final String tipo;

    private TipoKeyPair(String sType) {
        this.tipo = sType;
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.tipo.equals(RSA.toString())) {
            return RSA;
        }
        if (this.tipo.equals(DSA.toString())) {
            return DSA;
        }
        if (this.tipo.equals(ECDSA.toString())) {
            return ECDSA;
        }
        throw new InvalidObjectException("Sem resolucao do par de chave.");
    }

    public String toString() {
        return this.tipo;
    }
}
