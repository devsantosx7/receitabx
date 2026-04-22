package jsignnet.infra;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/infra/FormataUtil.class */
public class FormataUtil {
    private FormataUtil() {
    }

    public static String formataData(Date data) {
        if (data == null) {
            return "";
        }
        DateFormat formatador = new SimpleDateFormat(getPadraoFormatacaoData());
        return formatador.format(data);
    }

    private static String getPadraoFormatacaoData() {
        return "dd/MM/yyyy";
    }

    public static int formataInt(String valor) {
        return Integer.parseInt(valor);
    }
}
