package modelos;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Conexao {
    private static final HttpClient client = HttpClient.newHttpClient();

    public String buscarCep(String cep) throws IOException, InterruptedException {
        String adress = "https://viacep.com.br/ws/" + cep + "/json/";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(adress))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else if (response.statusCode() == 400 || response.statusCode() == 404) {
            throw new IOException("CEP não encontrado: " + response.statusCode());
        } else if (response.statusCode() == 500) {
            throw new IOException("Erro no servidor: " + response.statusCode());
        } else {
            throw new IOException("Erro inesperado: " + response.statusCode());
        }
    }
}
