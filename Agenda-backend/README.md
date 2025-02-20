# Agenda de Contatos (PJ/PF) 📒

[![Licença](https://img.shields.io/badge/license-MIT-blue)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)

API backend para gestão de contatos, permitindo cadastro diferenciado entre **Pessoas Físicas (PF)** e **Pessoas Jurídicas (PJ)** com operações CRUD completas.

---

## 🚀 Funcionalidades

- **Cadastro diferenciado** por tipo de contato (PF/PJ)
- **Validação automática** de CPF, CNPJ e campos obrigatórios
- **Consultas filtradas** por nome, tipo de contato ou documento
- **Endpoints RESTful** para:
  - Criação, leitura, atualização e exclusão de contatos
  - Listagem paginada de todos os registros

---

## 🛠️ Tecnologias

**Backend:**

- Java 17
- Spring Boot 3.4.2
- Maven (gestão de dependências)

**Banco de Dados:**

- PostgreSQL
- Spring Data JPA
- Hibernate Spatial

---

## ⚙️ Pré-requisitos

- JDK 17
- Maven 3.6+
- PostgreSQL v15+

---

## 📦 Instalação

```bash
# Postgres
# Abra o postgres e execute a query a seguir para gerar o banco de dados
CREATE DATABASE agenda_contatos;

# Configure o application.properties
# (ajuste src/main/resources/application.properties com suas credenciais do PostgreSQL)

# Clone o repositório
git clone https://github.com/seu-usuario/agenda-pj-pf.git

# Instale as dependências e construa o projeto
mvn clean install



# Execute a aplicação
mvn spring-boot:run
```

---

## 📡 Endpoints da API

**Pessoa Jurídica (PJ)**

### Cria Pj ###

- Método: ```POST```
- URL: ```http://localhost:8080/api/pjuridica```
- Content-Type: ```application/json```

### Exemplo de Requisição: ###
``` 
{
  "cnpj": "13.347.249/0001-10",
  "razaoSocial": "Empresa XYZ Ltda",
  "nomeFantasia": "XYZ",
  "telefone": "11987654321",
  "email": "contato@xyz.com",
  "endereco": "Rua das Flores, 123 - Centro",
  "cep": "86020121"
}
```

**Pessoa Física (PF)**

### Cria Pf ###

- Método: ```POST```
- URL: ```http://localhost:8080/api/pfisica```
- Content-Type: ```application/json```

### Exemplo de Requisição: ###
``` 
{
  "cpf": "639.346.190-02",
  "nome": "Rodrigo Almeida",
  "dataNascimento": "1988-03-18",
  "telefone": "51987654321",
  "cep": "90010-320",
  "email": "rodrigo.almeida@example.com",
  "endereco": "Avenida Borges de Medeiros, 1200 - Praia de Belas"
}
```

### Endpoints Comuns (Para ambos os tipos) ###
Alterar o endpoint apos API para mudar
 - api/pjuridica - ```Pessoa Juridica```
 - api/pfisica - ```Pessoa Fisica```
#### Listar Todos os Contatos ####
 - Método: ```GET```
 - URL: ```http://localhost:8080/api/pjuridica```

Buscar por CNPJ
 - Método: ```GET```
 - URL: ```http://localhost:8080/api/pjuridica/filtrar-por-cnpj?prefixo={{CNPJ}}```

Atualizar Contato
 - Método: ```PUT```
 - URL: ```http://localhost:8080/api/pjuridica/{{CNPJ}}```
 - Content-Type: ```application/json```

Deletar Contato
 - Método: ```DELETE```
 - URL: ```http://localhost:8080/api/pjuridica/{{CNPJ}}```

### Observações ###
 - Para requisições inválidas, a API retornará um objeto JSON com detalhes do erro:
```
{
  "timestamp": "2023-10-05T12:34:56.789Z",
  "status": 400,
  "error": "Bad Request",
  "message": "CNPJ inválido",
  "path": "/api/pjuridica"
}
```
