package jsignnet.aplicacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ConfiguradorPKCS11.class */
public class ConfiguradorPKCS11 {
    public static final Confirmacao CONFIGURAR_TODOS = new ConfigurarTodos();
    public static final String NOME_ARQUIVO_INDICE = "naodrivers.dat";
    private final File diretorioArquivosConfiguracao;
    private Ambiente ambiente = new Ambiente();
    private Map<Integer, List<BigInteger>> hashesDriversConfigurados = new HashMap();

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ConfiguradorPKCS11$Confirmacao.class */
    public interface Confirmacao {
        boolean criarArquivoConfiguracaoPara(File file);
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ConfiguradorPKCS11$ConfigurarTodos.class */
    private static final class ConfigurarTodos implements Confirmacao {
        private ConfigurarTodos() {
        }

        @Override // jsignnet.aplicacao.ConfiguradorPKCS11.Confirmacao
        public boolean criarArquivoConfiguracaoPara(File arquivoDriver) {
            return true;
        }
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ConfiguradorPKCS11$IndiceNaoDrivers.class */
    private static class IndiceNaoDrivers {
        private HashSet<ItemIndice> indice;

        /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ConfiguradorPKCS11$IndiceNaoDrivers$ItemIndice.class */
        private static class ItemIndice {
            private final String nome;
            private final long tamanho;

            public ItemIndice(File arquivo) {
                this.nome = arquivo.getAbsolutePath();
                this.tamanho = arquivo.length();
            }

            public ItemIndice(String nome, long tamanho) {
                this.nome = nome;
                this.tamanho = tamanho;
            }

            public int hashCode() {
                int result = (31 * 17) + (this.nome != null ? this.nome.hashCode() : 0);
                return (31 * result) + ((int) (this.tamanho ^ (this.tamanho >>> 32)));
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                ItemIndice object = (ItemIndice) o;
                if (this.nome != null) {
                    if (!this.nome.equals(object.nome)) {
                        return false;
                    }
                } else if (object.nome != null) {
                    return false;
                }
                return this.tamanho == object.tamanho;
            }
        }

        private IndiceNaoDrivers() {
            this.indice = new HashSet<>();
        }

        public boolean naoEDriver(File arquivo) {
            return this.indice.contains(new ItemIndice(arquivo));
        }

        public void guardarComoNaoDriver(File arquivo) {
            this.indice.add(new ItemIndice(arquivo));
        }

        public void guardarComoNaoDriver(String caminhoArquivo, long tamanho) {
            this.indice.add(new ItemIndice(caminhoArquivo, tamanho));
        }

        public void salvar(GravadorIndice gravador) {
            Iterator<ItemIndice> it = this.indice.iterator();
            while (it.hasNext()) {
                ItemIndice item = it.next();
                gravador.registrar(item.nome, item.tamanho);
            }
            gravador.gravar();
        }

        public int tamanho() {
            return this.indice.size();
        }
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ConfiguradorPKCS11$GravadorIndice.class */
    private static class GravadorIndice {
        private Map<String, Long> dados = new HashMap();
        private final File destino;

        public GravadorIndice(File destino) {
            this.destino = destino;
        }

        public void registrar(String nome, long tamanho) {
            this.dados.put(nome, Long.valueOf(tamanho));
        }

        public void gravar() {
            PrintWriter pw = null;
            try {
                try {
                    pw = new PrintWriter(this.destino);
                    for (Map.Entry<String, Long> dado : this.dados.entrySet()) {
                        if (dado.getKey().indexOf("===") == -1) {
                            pw.println(String.format("%s===%d", dado.getKey(), dado.getValue()));
                        }
                    }
                    this.dados.clear();
                    if (pw != null) {
                        pw.close();
                    }
                } catch (IOException e) {
                    JSignNet.logger.log(Level.WARNING, "Não conseguiu gravar o arquivo de índice de não drivers em " + this.destino.getAbsolutePath(), (Throwable) e);
                    if (pw != null) {
                        pw.close();
                    }
                }
            } catch (Throwable th) {
                if (pw != null) {
                    pw.close();
                }
                throw th;
            }
        }
    }

    /* loaded from: jsignnet-3.6.0.jar:jsignnet/aplicacao/ConfiguradorPKCS11$LeitorIndice.class */
    private static class LeitorIndice {
        private static final String SEPARADOR = "===";

        private LeitorIndice() {
        }

        public IndiceNaoDrivers ler(File origem) throws IOException {
            BufferedReader br = null;
            try {
                try {
                    br = new BufferedReader(new FileReader(origem));
                    IndiceNaoDrivers indice = new IndiceNaoDrivers();
                    while (true) {
                        String linha = br.readLine();
                        if (linha == null) {
                            break;
                        }
                        String[] partes = linha.split(SEPARADOR);
                        if (partes.length == 2) {
                            String nome = partes[0];
                            String tamanho = partes[1];
                            indice.guardarComoNaoDriver(nome, Long.parseLong(tamanho));
                        }
                    }
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                        }
                    }
                    return indice;
                } catch (IOException e2) {
                    IndiceNaoDrivers indiceNaoDrivers = new IndiceNaoDrivers();
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e3) {
                        }
                    }
                    return indiceNaoDrivers;
                }
            } catch (Throwable th) {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        }
    }

    public ConfiguradorPKCS11(File diretorioArquivosConfiguracao) {
        if (diretorioArquivosConfiguracao == null) {
            throw new NullPointerException("O diretório de configuração de tokens precisa ser informado.");
        }
        this.diretorioArquivosConfiguracao = diretorioArquivosConfiguracao;
    }

    public void configurarDriversPKCS11(Confirmacao confirmacao) {
        List<File> arquivosConfiguracao = listarArquivosConfiguracao();
        extrairBiblioteca(arquivosConfiguracao);
        Map<String, File> driversNaoConfigurados = reterApenasDriversNaoConfigurados(procurarBibliotecasPKCS11());
        for (String nome : driversNaoConfigurados.keySet()) {
            File bibliotecaEncontrada = driversNaoConfigurados.get(nome);
            if (confirmacao.criarArquivoConfiguracaoPara(bibliotecaEncontrada)) {
                Map<String, String> conteudoConfiguracao = criarConteudoArquivoConfiguracao(nome, bibliotecaEncontrada);
                File novoArquivoConfiguracao = novoNomeArquivoConfiguracao(nome);
                gravarArquivoConfiguracao(conteudoConfiguracao, novoArquivoConfiguracao);
            }
        }
    }

    private Map<String, File> reterApenasDriversNaoConfigurados(Map<String, File> drivers) {
        List<File> arquivosConfiguracao = listarArquivosConfiguracao();
        List<File> bibliotecasConfiguradas = extrairBiblioteca(arquivosConfiguracao);
        Map<String, File> copia = new HashMap<>(drivers);
        Iterator<String> it = copia.keySet().iterator();
        while (it.hasNext()) {
            String nome = it.next();
            File bibliotecaEncontrada = copia.get(nome);
            try {
                if (estaPresente(bibliotecaEncontrada, bibliotecasConfiguradas)) {
                    it.remove();
                }
            } catch (IOException e) {
                JSignNet.logger.log(Level.WARNING, nome, (Throwable) e);
                it.remove();
            }
        }
        return copia;
    }

    private Map<String, String> criarConteudoArquivoConfiguracao(String nome, File driver) {
        Map<String, String> nova = new HashMap<>();
        nova.put("name", nome);
        nova.put("library", driver.getAbsolutePath());
        return nova;
    }

    private void gravarArquivoConfiguracao(Map<String, String> conteudo, File destino) {
        PrintWriter pw = null;
        try {
            try {
                File pasta = destino.getParentFile();
                if (!pasta.exists()) {
                    pasta.mkdirs();
                }
                pw = new PrintWriter(destino);
                pw.println("# Configuracao automatica de PKCS#11 - JSignNet");
                pw.println("name=" + conteudo.get("name"));
                pw.println("library=" + conteudo.get("library"));
                if (pw != null) {
                    pw.close();
                }
            } catch (IOException e) {
                JSignNet.logger.log(Level.WARNING, "Não gravou configuração para " + conteudo, (Throwable) e);
                if (pw != null) {
                    pw.close();
                }
            }
        } catch (Throwable th) {
            if (pw != null) {
                pw.close();
            }
            throw th;
        }
    }

    private List<File> listarArquivosConfiguracao() {
        List<File> arquivos = new ArrayList<>();
        if (this.diretorioArquivosConfiguracao.exists()) {
            File[] listagem = this.diretorioArquivosConfiguracao.listFiles(new FileFilter() { // from class: jsignnet.aplicacao.ConfiguradorPKCS11.1
                @Override // java.io.FileFilter
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(".cfg");
                }
            });
            for (File listado : listagem) {
                arquivos.add(listado);
            }
        }
        return arquivos;
    }

    private List<File> extrairBiblioteca(List<File> configuracoes) {
        List<File> bibliotecas = new ArrayList<>();
        for (File configuracao : configuracoes) {
            try {
                File extraida = extrairBiblioteca(configuracao);
                if (extraida != null) {
                    bibliotecas.add(extraida);
                }
            } catch (IOException e) {
                JSignNet.logger.log(Level.WARNING, "Falha ao ler a biblioteca do arquivo " + configuracao, (Throwable) e);
            }
        }
        return bibliotecas;
    }

    private File extrairBiblioteca(File configuracao) throws IOException {
        BufferedReader leitor = new BufferedReader(new FileReader(configuracao));
        try {
            for (String linha = leitor.readLine(); linha != null; linha = leitor.readLine()) {
                String[] partes = linha.split("=");
                if (partes.length == 2) {
                    String chave = partes[0];
                    String valor = partes[1];
                    if (chave.trim().equalsIgnoreCase("library")) {
                        File file = new File(valor.trim());
                        leitor.close();
                        return file;
                    }
                }
            }
            return null;
        } finally {
            leitor.close();
        }
    }

    private boolean estaPresente(File arquivo, List<File> lista) throws NoSuchAlgorithmException, IOException {
        BigInteger hash = calcularHash(conteudo(arquivo));
        List<BigInteger> hashes = hashesDe(lista);
        return hashes.contains(hash);
    }

    private List<BigInteger> hashesDe(List<File> lista) throws IOException {
        int hash = lista.hashCode();
        if (this.hashesDriversConfigurados.containsKey(Integer.valueOf(hash))) {
            return this.hashesDriversConfigurados.get(Integer.valueOf(hash));
        }
        List<BigInteger> novosHashes = mapearHashEm(lista);
        this.hashesDriversConfigurados.put(Integer.valueOf(hash), novosHashes);
        return novosHashes;
    }

    private List<BigInteger> mapearHashEm(List<File> lista) throws IOException {
        List<BigInteger> saida = new ArrayList<>();
        for (File arquivo : lista) {
            saida.add(calcularHash(conteudo(arquivo)));
        }
        return saida;
    }

    private File novoNomeArquivoConfiguracao(String nome) {
        String nomeLimpo = limparNome(nome);
        File novoNome = new File(this.diretorioArquivosConfiguracao, "jsignnet-automatico-" + nomeLimpo + ".cfg");
        int i = 2;
        while (novoNome.exists()) {
            novoNome = new File(this.diretorioArquivosConfiguracao, "jsignnet-automatico-" + nomeLimpo + i + ".cfg");
            i++;
        }
        return novoNome;
    }

    private String limparNome(String nome) {
        return nome.replaceAll(" ", "").toLowerCase();
    }

    private Iterable<File> pastasBibliotecasSistema() {
        if (this.ambiente.linux() || this.ambiente.macosx()) {
            List<File> pastas = new ArrayList<>();
            pastas.add(new File("/usr/local/lib"));
            pastas.add(new File("/usr/lib"));
            return pastas;
        }
        if (this.ambiente.windows()) {
            String caminhoWindows = System.getenv("windir");
            return Collections.singletonList(new File(new File(caminhoWindows), "System32"));
        }
        return Collections.emptyList();
    }

    private Map<String, File> procurarBibliotecasPKCS11() throws IOException {
        LeitorIndice leitor = new LeitorIndice();
        File arquivoIndice = new File(this.diretorioArquivosConfiguracao, NOME_ARQUIVO_INDICE);
        IndiceNaoDrivers indice = leitor.ler(arquivoIndice);
        return procurarBibliotecasPKCS11(indice);
    }

    private Map<String, File> procurarBibliotecasPKCS11(IndiceNaoDrivers indice) throws NoSuchAlgorithmException {
        int tamanhoOriginalIndice = indice.tamanho();
        Map<String, File> nomeArquivo = new HashMap<>();
        try {
            int quantidadeArquivos = 0;
            for (File arquivo : listarArquivos(pastasBibliotecasSistema())) {
                quantidadeArquivos++;
                if (!indice.naoEDriver(arquivo)) {
                    try {
                        if (eBibliotecaPKCS11(arquivo)) {
                            nomeArquivo.put(criarNomeToken(arquivo), arquivo);
                        } else {
                            indice.guardarComoNaoDriver(arquivo);
                        }
                    } catch (IOException e) {
                        JSignNet.logger.log(Level.WARNING, "Não conseguiu acessar o arquivo " + arquivo, (Throwable) e);
                    }
                }
            }
            double proporcao = quantidadeArquivos != 0 ? tamanhoOriginalIndice / quantidadeArquivos : 0.0d;
            if (proporcao > 1.25d || Math.abs(proporcao) < 0.001d) {
                File arquivoIndice = new File(this.diretorioArquivosConfiguracao, NOME_ARQUIVO_INDICE);
                GravadorIndice gravador = new GravadorIndice(arquivoIndice);
                indice.salvar(gravador);
            }
            eliminarArquivosDuplicados(nomeArquivo);
        } catch (IOException e2) {
            JSignNet.logger.log(Level.WARNING, "Não listou arquivos", (Throwable) e2);
        }
        return nomeArquivo;
    }

    private Iterable<File> listarArquivos(Iterable<File> pastas) throws IOException {
        List<File> arquivos = new ArrayList<>();
        for (File pasta : pastas) {
            for (File arquivo : pasta.listFiles()) {
                if (arquivo.isFile()) {
                    arquivos.add(arquivo);
                }
            }
        }
        return arquivos;
    }

    private void eliminarArquivosDuplicados(Map<String, File> arquivos) throws NoSuchAlgorithmException, IOException {
        HashMap<BigInteger, File> hashes = new HashMap<>();
        Iterator<Map.Entry<String, File>> i = arquivos.entrySet().iterator();
        while (i.hasNext()) {
            File arquivo = i.next().getValue();
            if (!arquivo.isFile()) {
                i.remove();
            } else {
                BigInteger hash = calcularHash(conteudo(arquivo));
                if (hashes.containsKey(hash)) {
                    i.remove();
                } else {
                    hashes.put(hash, arquivo);
                }
            }
        }
    }

    private byte[] conteudo(File arquivo) throws IOException {
        FileInputStream fis = new FileInputStream(arquivo);
        try {
            byte[] buffer = new byte[4096];
            byte[] conteudo = new byte[(int) arquivo.length()];
            int pos = 0;
            while (true) {
                int quantidade = fis.read(buffer);
                if (quantidade >= 0) {
                    System.arraycopy(buffer, 0, conteudo, pos, quantidade);
                    pos += quantidade;
                } else {
                    return conteudo;
                }
            }
        } finally {
            fis.close();
        }
    }

    private BigInteger calcularHash(byte[] dados) throws NoSuchAlgorithmException {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.update(dados);
            return new BigInteger(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo MD5 não disponível.");
        }
    }

    private boolean eBibliotecaPKCS11(File arquivo) throws IOException {
        List<byte[]> assinatura = new ArrayList<>();
        assinatura.add("C_Sign".getBytes("ascii"));
        assinatura.add("C_SignInit".getBytes("ascii"));
        assinatura.add("C_SignUpdate".getBytes("ascii"));
        assinatura.add("C_Login".getBytes("ascii"));
        assinatura.add("C_Logout".getBytes("ascii"));
        assinatura.add("C_OpenSession".getBytes("ascii"));
        byte[] conteudo = conteudo(arquivo);
        for (byte[] metodo : assinatura) {
            if (!contem(conteudo, metodo)) {
                return false;
            }
        }
        return true;
    }

    private boolean contem(byte[] dados, byte[] trecho) {
        int iDados = 0;
        int iTrecho = 0;
        int iMarca = -1;
        while (iDados + trecho.length < dados.length) {
            if (dados[iDados] != trecho[iTrecho]) {
                iTrecho = 0;
                if (iMarca == -1) {
                    iDados++;
                } else {
                    iDados = iMarca + 1;
                    iMarca = -1;
                }
            } else {
                if (iMarca == -1) {
                    iMarca = iDados;
                }
                iTrecho++;
                iDados++;
                if (iTrecho == trecho.length) {
                    return true;
                }
            }
        }
        return false;
    }

    private String criarNomeToken(File arquivo) {
        Map<String, String> conversoes = new HashMap<>();
        conversoes.put("neoid", "NeoID");
        conversoes.put("serpro", "NeoID");
        conversoes.put("et", "SafeNet_eToken");
        conversoes.put("etoken", "eToken_SafeNet");
        String nome = arquivo.getName().replaceAll("lib", "").replaceAll("p11", "").replaceAll("PKCS11", "").replaceAll("Pkcs11", "").replaceAll(".so", "").replaceAll(".dll", "").replaceAll("pkcs11", "");
        if (conversoes.containsKey(nome.toLowerCase())) {
            return conversoes.get(nome.toLowerCase());
        }
        return nome;
    }
}
