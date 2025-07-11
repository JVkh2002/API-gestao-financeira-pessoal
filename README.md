# 🧾 API de Gestão Financeira Pessoal

Esta é uma API REST desenvolvida em Java com Spring Boot para controle financeiro pessoal.  
Permite que usuários registrem receitas, despesas e consultem o saldo total, com autenticação baseada em JWT.

---

## ⚙️ Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- MySQL
- Postman (testes)
- Maven

---

## 📦 Funcionalidades

- ✅ Registro de usuários
- ✅ Autenticação via JWT
- ✅ Registro de receitas e despesas
- ✅ Consulta de transações
- ✅ Cálculo de saldo total
- ✅ Associação de transações por usuário logado

---

## 🚀 Como executar localmente

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/nome-do-repo.git
cd nome-do-repo
```

2.Configure o application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/financas
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

3.Crie o banco de dados:

CREATE DATABASE financas;

4.Execute o programa:

```bash
./mvnw spring-boot:run
```

## Autenticação:

```http
POST /auth/registrar
```

```json
{
  "email": "joao@email.com",
  "senha": "123456"
}
```


## Login:

```http
POST /auth/login
```


```json
{
  "email": "joao@email.com",
  "senha": "123456"
}

{
  "token": "Bearer eyJhbGciOi..."
}
```

Use esse token no cabeçalho para acessar as rotas protegidas:

Authorization: Bearer SEU_TOKEN


## Exemplo de transação:

```http
POST /transacoes
Authorization: Bearer SEU_TOKEN
```

```json
{
  "descricao": "Salário",
  "valor": 2500.00,
  "tipo": "RECEITA",
  "data": "2025-07-10"
}
```


## Consulta de saldo:

```http
GET /saldo
Authorization: Bearer SEU_TOKEN
```


🛠️ Próximas melhorias
Filtragem por mês e ano

Relatórios por categoria

Frontend em React ou Vue

Deploy na nuvem (Render, Heroku, etc)
