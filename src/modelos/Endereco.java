package modelos;

public class Endereco {
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String ddd;


    @Override
    public String toString() {
        return String.format("CEP: %s\nLogradouro: %s\nBairro: %s\nLocalidade: %s\nUF: %s\n",
                cep, logradouro, bairro, localidade, uf);
    }

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public String getUf() {
        return uf;
    }
}
