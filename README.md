# Documentação Técnica — Sistema de Consulta de CEP via API (Java)

## 1. Visão Geral

A aplicação consome a **API pública ViaCEP**, que retorna informações em formato **JSON**, e armazena localmente os resultados, garantindo validação de entradas e prevenção de duplicatas.

O sistema foi desenvolvido utilizando **Java 17**, com as bibliotecas padrão de **HTTP Client** e **I/O**, além da **Gson** (Google) para serialização e desserialização de dados JSON.

---

## 2. Arquitetura e Estrutura do Projeto

```
modelos/
│
├── Conexao.java         # Responsável pela comunicação HTTP com a API ViaCEP
├── Main.java            # Ponto de entrada da aplicação
├── ValidarCep.java      # Módulo de validação e formatação de CEPs
├── Endereco.java        # Classe de modelo representando o retorno da API
└── ArquivoService.java  # Serviço de persistência e verificação de duplicatas
```

A estrutura segue o padrão **MVC simplificado**, separando responsabilidades entre entrada de dados, lógica de negócio e persistência.

---

## 3. Classe `Conexao`

### Descrição

Realiza a comunicação HTTP com a API **ViaCEP**, enviando requisições GET e processando as respostas recebidas.

### Principais Componentes

* **Atributo estático `HttpClient client`**
  Instância compartilhada do cliente HTTP, criada através do método `HttpClient.newHttpClient()`, conforme as práticas recomendadas da documentação oficial do Java.

* **Método `buscarCep(String cep)`**
  Executa a requisição e retorna o corpo da resposta em formato JSON.

#### Assinatura

```java
public String buscarCep(String cep) throws IOException, InterruptedException
```

#### Funcionamento

1. Monta a URL de requisição com o CEP informado.
2. Cria um `HttpRequest` utilizando o builder padrão.
3. Envia a requisição e recebe a resposta como `HttpResponse<String>`.
4. Valida o código de status HTTP:

   * **200 (OK):** retorna o corpo da resposta.
   * **400/404:** lança exceção de CEP inválido ou não encontrado.
   * **500:** lança exceção de erro interno do servidor.
   * **Demais códigos:** lança exceção genérica.

#### Exceções

* `IOException` – erro de comunicação ou resposta inválida.
* `InterruptedException` – interrupção do processo de requisição.

---

## 4. Classe `Main`

### Descrição

Controla o fluxo principal da aplicação, incluindo:

* leitura de entrada do usuário;
* validação e formatação do CEP;
* consumo da API;
* exibição e persistência dos resultados.

### Estrutura de Execução

1. Criação das instâncias de `Gson`, `Conexao`, `ValidarCep` e `ArquivoService`.
2. Leitura iterativa de CEPs via `Scanner`.
3. Encerramento quando o usuário digita “SAIR”.
4. Validação do CEP com `ValidarCep.validaCompleto`.
5. Consulta do CEP com `Conexao.buscarCep`.
6. Conversão da resposta JSON para `Endereco` com Gson.
7. Salvamento do resultado via `ArquivoService`.
8. Impressão formatada do endereço no console.

### Observações Técnicas

* Utiliza `setPrettyPrinting()` no `GsonBuilder` para legibilidade dos arquivos JSON.
* Implementa tratamento de exceções individualizado para IO e interrupções.
* Realiza controle de loop seguro com `scanner.close()` ao final.

---

## 5. Classe `ValidarCep`

### Descrição

Implementa a lógica de **sanitização, validação e formatação de CEPs** antes de serem utilizados na requisição.

### Métodos Principais

#### `public String limpaCep(String cep)`

Remove todos os caracteres não numéricos utilizando expressão regular (`[^0-9]`).

#### `private boolean ehValido(String cep)`

Valida se o CEP contém exatamente 8 dígitos numéricos.

#### `public String formatarCep(String cep)`

Aplica o formato padrão `xxxxx-xxx` para CEPs válidos.

#### `public boolean cepNaoEhZeros(String cep)`

Rejeita CEPs compostos exclusivamente por zeros (`00000000`).

#### `public String validaCompleto(String cep)`

Executa o processo de validação completo:

1. Verifica nulidade e vazio.
2. Confere se contém 8 dígitos.
3. Rejeita CEPs nulos ou compostos por zeros.
4. Retorna o CEP formatado.
   Em caso de falha, exibe mensagem e retorna `null`.

---

## 6. Classe `Endereco`

### Descrição

Representa o modelo de dados retornado pela API ViaCEP.
É uma classe POJO (Plain Old Java Object) utilizada para desserialização automática via Gson.

### Campos

| Atributo      | Tipo   | Descrição                      |
| ------------- | ------ | ------------------------------ |
| `cep`         | String | CEP do endereço.               |
| `logradouro`  | String | Nome da rua, avenida ou praça. |
| `complemento` | String | Complementos de endereço.      |
| `bairro`      | String | Bairro correspondente.         |
| `localidade`  | String | Cidade.                        |
| `uf`          | String | Unidade Federativa (estado).   |
| `ddd`         | String | Código DDD da localidade.      |

### Métodos

* `toString()` – retorna representação textual formatada do endereço.
* Getters públicos para os principais campos.

---

## 7. Classe `ArquivoService`

### Descrição

Responsável pela **persistência dos dados de endereço** em um arquivo local e pela **prevenção de duplicatas** de CEPs.

### Construtor

```java
public ArquivoService(Gson gson)
```

Recebe uma instância de `Gson` para manipulação dos dados JSON.

### Métodos

#### `public boolean cepJaExiste(String cep, String nomeArquivo)`

Verifica se o CEP informado já está presente no arquivo especificado.
Utiliza leitura de todas as linhas via `Files.readAllLines(Paths.get(nomeArquivo))`.

#### `public void salvarEndereco(Endereco endereco, String nomeArquivo)`

1. Verifica duplicidade chamando `cepJaExiste()`.
2. Caso o CEP seja inédito, grava o objeto `Endereco` convertido em JSON.
3. Usa `FileWriter` com `append = true` para preservar dados anteriores.
4. Garante fechamento de recurso com `try-with-resources`.

---

## 8. Tecnologias e Dependências

| Tecnologia                          | Finalidade                                     |
| ----------------------------------- | ---------------------------------------------- |
| **Java 17+**                        | Linguagem e runtime principal.                 |
| **Java HttpClient (java.net.http)** | Consumo de API REST.                           |
| **Gson (Google)**                   | Serialização/desserialização JSON.             |
| **java.nio.file**                   | Manipulação de arquivos com melhor desempenho. |

---

## 9. Tratamento de Erros e Boas Práticas

* Uso de **exceções verificadas** (`IOException`, `InterruptedException`) para garantir controle explícito de falhas.
* Aplicação de **validações prévias** para minimizar erros em tempo de execução.
* **Separação de responsabilidades** entre classes para promover manutenção e extensibilidade.
* Adoção de **instância única de HttpClient** conforme recomendação da documentação oficial do Java.

---

## 10. Possíveis Extensões

* Implementação de camada de logs estruturados (SLF4J/Logback).
* Persistência alternativa em banco de dados relacional (ex.: SQLite, PostgreSQL).
* Criação de testes unitários com JUnit 5.
* Implementação de exceções customizadas (`CepInvalidoException`, `ErroDeConexaoException`).
* Interface gráfica (Swing/JavaFX) para interação mais amigável.

---

## 11. Autor

**Nathan Preto**
Desenvolvedor Back-End | Estudo e implementação de integração com APIs REST em Java.
Projeto com finalidade educacional, fundamentado em práticas recomendadas da linguagem e em padrões de código legíveis, seguros e escaláveis.
