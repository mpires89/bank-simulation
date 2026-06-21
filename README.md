# Bank Simulation

Uma API simples em Spring Boot que simula operações bancárias como saques, depósitos, transferências e consulta de saldo, atendendo a todos os requisitos propostos.

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.1+**
- **H2 Database** (banco de dados em memória)
- **Flyway** (Migrations do banco de dados)
- **Springdoc OpenAPI (Swagger)** (Documentação da API)
- **Maven** (Gerenciador de dependências)

## Documentação da API (Swagger)

A aplicação possui a integração com o Swagger (OpenAPI) para que você possa consultar e testar os endpoints através de uma interface interativa.

Com a aplicação rodando localmente, basta acessar o link no navegador:
👉 **http://localhost:8080/docs**

## Versionamento de Rotas

Exemplos de chamadas disponíveis:
- `POST /reset`
- `GET /balance?account_id={id}`
- `POST /event`

## Como executar o projeto localmente

1. Clone o repositório ou baixe o código fonte.
2. Na raiz do projeto, execute o comando do Maven Wrapper para compilar o código:
   ```bash
   ./mvnw clean package
   ```
3. Inicie o servidor Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```
A aplicação estará disponível em `http://localhost:8080`.

## Como rodar os testes automatizados

Os testes automatizados cobrem 100% da especificação exigida para o fluxo de simulação, utilizando o `MockMvc` para isolar requisições HTTP e banco de dados. Os testes foram quebrados por métodos (responsabilidade única) para melhor rastreio e validação de cenários.

Para executar a bateria completa de testes, rode:
```bash
./mvnw test
```
