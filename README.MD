# Agenda de Contatos (PJ/PF) 📒

[![Licença](https://img.shields.io/badge/license-MIT-blue)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Angular](https://img.shields.io/badge/Angular-15-red)](https://angular.io/)

Aplicação desenvolvida para gestão de contatos, permitindo cadastro diferenciado entre **Pessoas Físicas (PF)** e **Pessoas Jurídicas (PJ)** com operações CRUD completas.

---

## 🚀 Funcionalidades

- **Cadastro diferenciado** por tipo de contato (PF/PJ).
- **Validação automática** de CPF, CNPJ e campos obrigatórios.
- **Consultas filtradas** por CPF, CNPJ ou prefixos.
- **Geolocalização** com integração à API do Google Maps.
- **Endpoints RESTful** para:
  - Criação, leitura, atualização e exclusão de contatos.
  - Listagem de todos os registros.

---

## 🛠️ Tecnologias Utilizadas

### Backend:

- **Java 17**
- **Spring Boot 3.4.2**
- **PostgreSQL** com extensão **PostGIS**
- **Hibernate Spatial**
- **Maven**

### Frontend:

- **Angular CLI 15**
- **TypeScript**
- **HTML5 e CSS3**

---

## ⚙️ Pré-requisitos

- **Node.js** v18.19.1 e npm v10.2.4 (para o front-end).
- **JDK 17** e Maven 3.6+ (para o back-end).
- **PostgreSQL** v15+ com extensão **PostGIS** habilitada.
