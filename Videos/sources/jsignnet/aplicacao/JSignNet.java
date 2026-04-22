package jsignnet.aplicacao;

import java.security.Provider;
import java.security.Security;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.security.jgss.SunProvider;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/JSignNet.class */
public class JSignNet {
    static final String JRE_REQUERIDA_PKCS11 = "1.5.0";
    private static final String JAVA_SEM_SUNPROVIDER = "17.0.0";
    public static Logger logger = Logger.getLogger("serpro.jsignnet");
    private static boolean fProvidersAdicionados;
    public static final String NOME_CONTAINER_DEFAULT = "mycontainer.jks";
    public static final String SENHA_CONTAINER_DEFAULT = "receita01";
    static final String windowsSO = "win";
    static final String appleSO = "mac";

    static {
        logger.addHandler(new ConsoleHandler());
        adicionaProviders();
    }

    public static void setLogger(Logger logger2) {
        logger = logger2;
    }

    static void adicionaProviders() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (fProvidersAdicionados) {
            return;
        }
        if (checkSO(windowsSO)) {
            try {
                Class<?> classeSunMSCAPI = Class.forName("sun.security.mscapi.SunMSCAPI");
                Object provider = classeSunMSCAPI.newInstance();
                if (provider instanceof Provider) {
                    Security.addProvider((Provider) provider);
                }
            } catch (ClassNotFoundException e) {
                logger.warning("Sem acesso aos certificados do IE.\n" + e.getLocalizedMessage());
            } catch (IllegalAccessException e2) {
                logger.warning("Sem acesso aos certificados do IE.\n" + e2.getLocalizedMessage());
            } catch (InstantiationException e3) {
                logger.warning("Sem acesso aos certificados do IE.\n" + e3.getLocalizedMessage());
            }
        }
        Security.addProvider(new BouncyCastleProvider());
        if (checkJRE(JRE_REQUERIDA_PKCS11) && !checkJRE(JAVA_SEM_SUNPROVIDER)) {
            Security.addProvider(new SunProvider());
        }
        fProvidersAdicionados = true;
    }

    static boolean checkJRE(String versaoComparacao) {
        ComparableVersion versaoAtual = new ComparableVersion(System.getProperty("java.version"));
        ComparableVersion versaoRequerida = new ComparableVersion(versaoComparacao);
        return versaoAtual.compareTo(versaoRequerida) >= 0;
    }

    static boolean checkSO(String so) {
        String nomeSO = System.getProperty("os.name").toLowerCase();
        return nomeSO.indexOf(so.toLowerCase()) > -1;
    }

    public static LeitorCertificados getLeitorCertificados() {
        return new LeitorCertificados();
    }

    public static ValidadorAssinatura getValidadorAssinatura() {
        return new ValidadorAssinatura();
    }
}
