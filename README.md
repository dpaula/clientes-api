# CLIENTES-API

Projeto Teste para candidatar a uma vaga na Builders

API simples para gerenciamento de Clientes

# Ambiente Para Desenvolvimento

## Pré requisito

- [IntelliJ](https://www.jetbrains.com/idea/download/)
- Maven 3.6+
- Java 11
- Docker 18+
- docker-compose

### Subindo API localmente para desenvolvimento

Ao clonar o Projeto e fazer o build para baixar as dependências do maven, basta seguir os pré-requisitos abaixo para
execução da API

Existem dois pré requisitos para execução da API:

- Servidor Banco de dados
    - Caso for executar API pela IDE, se faz necessário subir o banco via docker antes (a porta 1433 do host deve estar
      liberada)
        - Para subir o Servidor de Banco de Dados via docker basta estar na raiz do projeto, e executar o comando abaixo
        - ```docker-compose -f docker-db.yaml up -d```
- Servidor Redis
    - Importante salientar que apenas será necessário levantar o Redis, caso for executada API via docker, caso for
      executar internamente pela IDE, não se faz necessário, pois o cache neste caso será utilizado em memória pelo
      Spring Bot

## Principais Tecnologias Utilizadas na API

- Java 11
- Spring Boot 2.3
- Spring Data
- Spring Actuator
- Spring Validation
- Cache com Redis
- Lombok
- Swagger v3
- MSSQl
- Micrometer (para exportar metricas Prometheus)
- Docker

## Principais Regras Utilizadas

#### Cadastro de Clientes

- Utilizado apenas 3 propriedades para cadastro de clientes (nome, e-mail e data de nascimento), entretanto todos são
  obrigatórios
- Campo e-mail, foi utilizado com único
- Ao incluir um Cliente o sistema registra data e hora de inclusão
- Incluída política de idade mínima e máxima (definição via arquivo de propriedades)

#### Alteração de Clientes

- Sistema valida apenas o e-mail ao efetuar alteração, onde não deve ser alterado para um e-mail já cadastrado
- Validação da política de idade mínima e máxima
- Ao alterar, o sistema registar data e hora da alteração do Cliente

#### Listar Clientes

- Implementado filtro dinâmico para listagem de Clientes Paginados através de parametrização
- Campos utilizados no filtro: nome, e-mail, idade e data de nascimento
- Os parâmetros podem ser combinados para uma filtragem mais exata
- Caso não for informado nenhum filtro, será retornado todos os Clientes

## Testando API

A principal documentação da API foi exportada via Swagger (link abaixo), e é a maneira mais simples (e eficiente) para
efetuar testes

A segunda opção para fácil utilização da API, é através do Postman, onde pode ser importado por
este [arquivo](https://github.com/dpaula/clientes-api/blob/4f44c098c71cbc8e528942f842c5bfd1204fcd72/CLIENTES-API.postman_collection.json)

## Acessando API

#### Localmente

- Ao executar o projeto (via API ou docker-compose) basta acessar o endereço http://localhost:2550/swagger-ui.html

#### Externamente

- O deploy da última versão (1.0.0) API foi realizada na plataforma do Google Cloud e pode ser acessada no
  endereço http://35.247.249.163:2550/swagger-ui.html

- API estará disponível até 20/07/2021, caso esteja indisponível ou expirou a data, basta solicitar para que seja
  disponibilizada novamente

