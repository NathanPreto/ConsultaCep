package modelos;

public class ValidarCep {

    public String limpaCep(String cep) {
        return cep.replaceAll("[^0-9]", "");
    }

    private boolean ehValido(String cep) {
        String cepLimpo = limpaCep(cep);
        return cepLimpo.length() == 8; // Retorna true se tiver exatamente 8 dígitos
    }


    public String formatarCep(String cep) {
        String cepLimpo = limpaCep(cep);

        if (ehValido(cepLimpo)) {
            return cepLimpo.substring(0, 5) + "-" + cepLimpo.substring(5);
        }
        return cep;
    }

    public boolean cepNaoEhZeros(String cep) {
        String cepLimpo = limpaCep(cep);
        return !cepLimpo.equals("00000000");
    }


    public String validaCompleto(String cep) {
        if (cep == null || cep.isEmpty()) {
            System.out.println("CEP não pode ser vazio.");
            return null;
        }

        String cepLimpo = limpaCep(cep);

        if (!ehValido(cepLimpo)) {
            System.out.println("CEP deve ter exatamente 8 dígitos.");
            return null;
        }

        if (!cepNaoEhZeros(cepLimpo)) {
            System.out.println("CEP não pode ser 00000000.");
            return null;
        }

        return formatarCep(cepLimpo);
    }

}
