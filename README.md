# user-mail-sender-ms

Projeto com **dois microservicos Spring Boot**:

- `user`: gerencia usuarios e publica evento de cadastro no RabbitMQ.
- `email`: consome evento, envia email (Resend via SMTP) e persiste historico de envio.

## Como o sistema funciona

Fluxo principal de ponta a ponta:

1. Cliente chama `POST /users` no servico `user`.
2. `user` salva o usuario no Postgres (`tb_users`).
3. `user` publica uma mensagem JSON na fila `email-queue`.
4. `email` consome a mensagem com `@RabbitListener`.
5. `email` monta e envia email via `JavaMailSender` (SMTP Resend).
6. `email` salva o resultado no Postgres (`tb_email`) com status `PENDING`, `SENT` ou `FAILED`.

## Arquitetura

- **API User**: REST + JPA + Rabbit Producer + OpenAPI.
- **API Email**: Rabbit Consumer + JavaMail + JPA.
- **Banco**: 1 Postgres por microservico.
- **Mensageria**: RabbitMQ (local ou CloudAMQP via variaveis de ambiente).

## Stack tecnica

- Java 21
- Spring Boot 4.0.3
- Spring Web MVC
- Spring Data JPA + Hibernate
- Flyway
- RabbitMQ (Spring AMQP)
- JavaMailSender
- PostgreSQL
- Lombok
- Springdoc OpenAPI (no microservico `user`)
- Maven

## Estrutura do repositorio

```text
user-mail-sender-ms/
  user/   -> microservico de usuarios (producer)
  email/  -> microservico de email (consumer)
```

## Requisitos

- Java 21
- Maven 3.9+
- Docker + Docker Compose
- Conta/instancia RabbitMQ (CloudAMQP ou local)
- Chave da Resend (SMTP)

## Configuracao de ambiente

### 1) Banco do microservico `user`

Use como base o arquivo `user/.env.example` e crie seu `user/.env` local.

```dotenv
DB_USERNAME=seu_usuario_postgres_user
DB_PASSWORD=sua_senha_postgres_user
RABBITMQ_ADDRESSES=localhost:5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_VHOST=/
RABBITMQ_SSL_ENABLED=false
```

### 2) Banco + SMTP do microservico `email`

Use como base o arquivo `email/.env.example` e crie seu `email/.env` local.

```dotenv
DB_EMAIL_USERNAME=seu_usuario_postgres_email
DB_EMAIL_PASSWORD=sua_senha_postgres_email
MAIL_USERNAME=seu_usuario_smtp
RESEND_API_KEY=sua_chave_resend
RABBITMQ_ADDRESSES=localhost:5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
RABBITMQ_VHOST=/
RABBITMQ_SSL_ENABLED=false
```

Exemplo rapido para criar os arquivos locais:

```bash
cp user/.env.example user/.env
cp email/.env.example email/.env
```

> Importante: nunca versione o arquivo `.env` com valores reais.

## Subindo infraestrutura local (Postgres)

### Banco do `user`

```bash
cd /home/lucca/projetos/user-mail-sender-ms/user
docker compose up -d
```

### Banco do `email`

```bash
cd /home/lucca/projetos/user-mail-sender-ms/email
docker compose up -d
```

## RabbitMQ / CloudAMQP

Os dois servicos leem configuracao do RabbitMQ por variaveis de ambiente:

- `RABBITMQ_ADDRESSES` (ex.: `localhost:5672` ou host CloudAMQP)
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`
- `RABBITMQ_VHOST`
- `RABBITMQ_SSL_ENABLED` (`true` quando a instancia exigir TLS/SSL)

A fila usada pelos dois lados e:

- `app.rabbitmq.queue=email-queue`

Para CloudAMQP, preencha essas variaveis com os dados da sua instancia.

### Sobre criacao automatica da fila

- O servico `email` tem `Queue` declarada em `email/src/main/java/dev/lucca/email/configuration/RabbitConfiguration.java`.
- A fila e criada automaticamente **quando o consumer sobe** e quando o usuario Rabbit tem permissao de `configure` no vhost.
- Se conectar no CloudAMQP e nao ver fila, normalmente e:
  - consumer nao iniciou,
  - nome da fila diferente,
  - permissao insuficiente no vhost,
  - ou declaracao desabilitada por permissao/politica.

## Executando os microservicos

### 1) Subir `email` primeiro (consumer)

```bash
cd /home/lucca/projetos/user-mail-sender-ms/email
./mvnw spring-boot:run
```

### 2) Subir `user` (producer)

```bash
cd /home/lucca/projetos/user-mail-sender-ms/user
./mvnw spring-boot:run
```

Portas padrao:

- `email`: `8080`
- `user`: `8081`

## API do microservico `user`

Base URL: `http://localhost:8081/users`

### Criar usuario

`POST /users`

Exemplo de body:

```json
{
  "name": "Lucca",
  "email": "lucca@example.com"
}
```

Efeitos:

- persiste em `tb_users`
- publica evento para `email-queue`

### Listar usuarios

`GET /users/list`

### Buscar por email

`GET /users/email/{email}`

Exemplo:

```bash
curl "http://localhost:8081/users/email/lucca@example.com"
```

### Atualizar usuario

`PUT /users/{id}`

Body:

```json
{
  "name": "Novo Nome",
  "email": "novo@email.com"
}
```

### Deletar usuario

`DELETE /users/{id}`

## Swagger / OpenAPI (`user`)

- UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

Configuracoes em:

- `user/src/main/java/dev/lucca/user/configuration/OpenApiConfiguration.java`
- `user/src/main/java/dev/lucca/user/doc/UserControllerDoc.java`

## Contrato de mensageria

Evento publicado pelo `user` e consumido pelo `email`:

```json
{
  "userId": "uuid",
  "emailTo": "destinatario@email.com",
  "EmailSubject": "Cadastro realizado com sucesso",
  "body": "Ola <nome>, sua conta foi criada com sucesso."
}
```

Campos definidos em:

- `user/src/main/java/dev/lucca/user/dto/EmailDto.java`
- `email/src/main/java/dev/lucca/email/dto/EmailDto.java`

## Modelo de dados

### Banco `user` - tabela `tb_users`

| Coluna | Tipo | Restricoes |
|---|---|---|
| `user_id` | UUID | PK |
| `name` | VARCHAR(255) | NOT NULL |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE |

### Banco `email` - tabela `tb_email`

| Coluna | Tipo | Restricoes |
|---|---|---|
| `email_id` | UUID | PK |
| `user_id` | UUID | - |
| `email_from` | VARCHAR(255) | - |
| `email_to` | VARCHAR(255) | - |
| `email_subject` | VARCHAR(255) | - |
| `body` | TEXT | - |
| `send_date_email` | TIMESTAMP | - |
| `status_email` | VARCHAR(255) | enum em aplicacao (`PENDING`, `SENT`, `FAILED`, `DELIVERED`) |

## Tratamento de erros (user)

O microservico `user` possui `@RestControllerAdvice` em:

- `user/src/main/java/dev/lucca/user/handler/GlobalExceptionHandler.java`

Caso especial mapeado:

- email ja utilizado -> `409 CONFLICT`

Excecao customizada:

- `user/src/main/java/dev/lucca/user/exception/EmailAlreadyUsedException.java`

## Testes

Executar testes do `user`:

```bash
cd /home/lucca/projetos/user-mail-sender-ms/user
./mvnw test
```

Executar testes do `email`:

```bash
cd /home/lucca/projetos/user-mail-sender-ms/email
./mvnw test
```




