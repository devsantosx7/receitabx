package jsignnet.aplicacao;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cms.CMSSignedDataGenerator;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/Hash.class */
public enum Hash {
    MD5(CMSSignedDataGenerator.DIGEST_MD5, "MD5", false),
    SHA1(CMSSignedDataGenerator.DIGEST_SHA1, "SHA-1", false),
    SHA256(CMSSignedDataGenerator.DIGEST_SHA256, "SHA-256", false),
    SHA384(CMSSignedDataGenerator.DIGEST_SHA384, "SHA-384", false),
    SHA512(CMSSignedDataGenerator.DIGEST_SHA512, "SHA-512", false),
    SHA256withRSA(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId(), "RSA-SHA256", true),
    SHA384withRSA(PKCSObjectIdentifiers.sha384WithRSAEncryption.getId(), "RSA-SHA384", true),
    SHA512withRSA(PKCSObjectIdentifiers.sha512WithRSAEncryption.getId(), "RSA-SHA512", true),
    MESMO_DO_CERTIFICADO(null, null, false);

    private String _digestOID;
    private String _nomeAlgoritmo;
    private boolean _possuiAlgoritmoAssinatura;
    public static final Hash[] ALGORITMOS = {MD5, SHA1, SHA256, SHA384, SHA512};

    /* JADX WARN: Code restructure failed: missing block: B:22:0x007e, code lost:
    
        r0 = r0.digest();
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0085, code lost:
    
        r7.tarefaTerminou();
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x008a, code lost:
    
        r6.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0091, code lost:
    
        r14 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0093, code lost:
    
        jsignnet.aplicacao.JSignNet.logger.log(java.util.logging.Level.INFO, "Falha ao fechar dados no cálculo de hash", (java.lang.Throwable) r14);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public byte[] geraDigest(java.io.InputStream r6, jsignnet.aplicacao.ControleProgresso r7) throws java.security.NoSuchAlgorithmException, java.io.IOException {
        /*
            r5 = this;
            r0 = r6
            if (r0 != 0) goto Le
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = r0
            java.lang.String r2 = "Os dados para cálculo do hash precisam ser informados."
            r1.<init>(r2)
            throw r0
        Le:
            r0 = r7
            if (r0 != 0) goto L1a
            jsignnet.aplicacao.ControleProgressoPadrao r0 = new jsignnet.aplicacao.ControleProgressoPadrao
            r1 = r0
            r1.<init>()
            r7 = r0
        L1a:
            r0 = r5
            java.lang.String r0 = r0._nomeAlgoritmo     // Catch: java.lang.Throwable -> La3
            java.security.MessageDigest r0 = java.security.MessageDigest.getInstance(r0)     // Catch: java.lang.Throwable -> La3
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 32768(0x8000, float:4.5918E-41)
            byte[] r0 = new byte[r0]     // Catch: java.lang.Throwable -> La3
            r11 = r0
        L2b:
            r0 = r6
            r1 = r11
            int r0 = r0.read(r1)     // Catch: java.lang.Throwable -> La3
            r1 = r0
            r12 = r1
            if (r0 <= 0) goto L7e
            r0 = r7
            boolean r0 = r0.foiCancelado()     // Catch: java.lang.Throwable -> La3
            if (r0 == 0) goto L62
            r0 = 0
            r13 = r0
            r0 = r7
            r0.tarefaTerminou()
            r0 = r6
            r0.close()     // Catch: java.io.IOException -> L50
            goto L5f
        L50:
            r14 = move-exception
            java.util.logging.Logger r0 = jsignnet.aplicacao.JSignNet.logger
            java.util.logging.Level r1 = java.util.logging.Level.INFO
            java.lang.String r2 = "Falha ao fechar dados no cálculo de hash"
            r3 = r14
            r0.log(r1, r2, r3)
        L5f:
            r0 = r13
            return r0
        L62:
            r0 = r8
            r1 = r11
            r2 = 0
            r3 = r12
            r0.update(r1, r2, r3)     // Catch: java.lang.Throwable -> La3
            r0 = r9
            r1 = r12
            long r1 = (long) r1     // Catch: java.lang.Throwable -> La3
            long r0 = r0 + r1
            r9 = r0
            r0 = r7
            r1 = r9
            r0.valorMudou(r1)     // Catch: java.lang.Throwable -> La3
            goto L2b
        L7e:
            r0 = r8
            byte[] r0 = r0.digest()     // Catch: java.lang.Throwable -> La3
            r13 = r0
            r0 = r7
            r0.tarefaTerminou()
            r0 = r6
            r0.close()     // Catch: java.io.IOException -> L91
            goto La0
        L91:
            r14 = move-exception
            java.util.logging.Logger r0 = jsignnet.aplicacao.JSignNet.logger
            java.util.logging.Level r1 = java.util.logging.Level.INFO
            java.lang.String r2 = "Falha ao fechar dados no cálculo de hash"
            r3 = r14
            r0.log(r1, r2, r3)
        La0:
            r0 = r13
            return r0
        La3:
            r15 = move-exception
            r0 = r7
            r0.tarefaTerminou()
            r0 = r6
            r0.close()     // Catch: java.io.IOException -> Lb2
            goto Lc1
        Lb2:
            r16 = move-exception
            java.util.logging.Logger r0 = jsignnet.aplicacao.JSignNet.logger
            java.util.logging.Level r1 = java.util.logging.Level.INFO
            java.lang.String r2 = "Falha ao fechar dados no cálculo de hash"
            r3 = r16
            r0.log(r1, r2, r3)
        Lc1:
            r0 = r15
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: jsignnet.aplicacao.Hash.geraDigest(java.io.InputStream, jsignnet.aplicacao.ControleProgresso):byte[]");
    }

    Hash(String digestOID, String nomeAlgoritmo, boolean possuiAlgoritmoAssinatura) {
        this._digestOID = digestOID;
        this._nomeAlgoritmo = nomeAlgoritmo;
        this._possuiAlgoritmoAssinatura = possuiAlgoritmoAssinatura;
    }

    public String getDigestOID() {
        return this._digestOID;
    }

    public String getNomeAlgoritmo() {
        return this._nomeAlgoritmo;
    }

    @Override // java.lang.Enum
    public String toString() {
        if (this == MESMO_DO_CERTIFICADO) {
            return "Algoritmo de hashing igual ao do certificado usado para assinatura";
        }
        return "Algoritmo de hashing " + getNomeAlgoritmo();
    }

    public static Hash getHashDeNome(String nome) {
        if (nome == null) {
            throw new IllegalArgumentException("O nome do algoritmo precisa ser informado.");
        }
        for (int i = 0; i < ALGORITMOS.length; i++) {
            Hash alg = ALGORITMOS[i];
            if (removerHifen(alg.getNomeAlgoritmo()).equalsIgnoreCase(removerHifen(nome))) {
                return alg;
            }
        }
        return null;
    }

    private static String removerHifen(String texto) {
        StringBuilder sb = new StringBuilder();
        for (char c : texto.toCharArray()) {
            if (c != '-') {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static Hash getHashDeOID(String oid) {
        if (oid == null) {
            throw new IllegalArgumentException("O OID do algoritmo precisa ser informado.");
        }
        for (int i = 0; i < ALGORITMOS.length; i++) {
            Hash alg = ALGORITMOS[i];
            if (alg.getDigestOID().equalsIgnoreCase(oid)) {
                return alg;
            }
        }
        return null;
    }

    public boolean possuiAlgoritmoAssinatura() {
        return this._possuiAlgoritmoAssinatura;
    }
}
