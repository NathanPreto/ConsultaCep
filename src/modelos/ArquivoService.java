package modelos;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ArquivoService {
    private Gson gson;

    public ArquivoService(Gson gson) {
        this.gson = gson;
    }

    public boolean cepJaExiste(String cep, String nomeArquivo) {
        try {
            if (!Files.exists(Paths.get(nomeArquivo))) {
                return false;
            }

            List<String> linhas = Files.readAllLines(Paths.get(nomeArquivo));

            for (String linha : linhas) {
                if (linha.contains("\"cep\": \"" + cep + "\"")) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao verificar duplicatas." + e.getMessage());
        }
        return false;
    }

    public void salvarEndereco(Endereco endereco, String nomeArquivo) throws IOException {
        if (cepJaExiste(endereco.getCep(), nomeArquivo)) {
            System.out.println("Este CEP já foi salvo anteriormente!");
            return;
        }
        try (FileWriter writer = new FileWriter(nomeArquivo, true)) {
            writer.write(gson.toJson(endereco));
            writer.write(System.lineSeparator());
        }
    }
}
