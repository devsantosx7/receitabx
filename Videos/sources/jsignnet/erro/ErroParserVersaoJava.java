package jsignnet.erro;

import jsignnet.infra.Recursos;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/erro/ErroParserVersaoJava.class */
public class ErroParserVersaoJava extends JSignException {
    private static final long serialVersionUID = 1;

    public ErroParserVersaoJava(Exception e) {
        super(e, Recursos.getString("Excecao.erroParserVersaoJava"));
    }

    public ErroParserVersaoJava() {
        super(Recursos.getString("Excecao.erroParserVersaoJava"));
    }
}
