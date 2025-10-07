package modelos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Scanner scanner = new Scanner(System.in);
        Conexao conexao = new Conexao();
        ValidarCep validarCep = new ValidarCep();
        ArquivoService arquivoService = new ArquivoService(gson);

        while (true) {
            System.out.println("Digite um cep para buscar os dados ou SAIR para encerrar o programa: ");
            String cep = scanner.nextLine().trim();

            if (cep.equalsIgnoreCase("SAIR")) {
                System.out.println("Encerrando o programa...");
                break;
            }

            String cepValidado = validarCep.validaCompleto(cep);
            if (cepValidado == null) {
                continue;
            }

            try {
                String json = conexao.buscarCep(cepValidado);

                if (json.contains("\"erro\": \true")) {
                    System.out.println("CEP não encontrado na base de dados.");
                    continue;
                }
                Endereco endereco = gson.fromJson(json, Endereco.class);

                arquivoService.salvarEndereco(endereco, "dados.json");

                System.out.println("\n============== ENDERECO ==============");
                System.out.println(endereco);
                System.out.println("Endereço salvo na base de dados.");
                System.out.println("======================================\n");
            } catch (IOException | InterruptedException e) {
                System.out.println("ERRO ao buscar CEP: " + e.getMessage());
            }
        }
        scanner.close();
    }
}

