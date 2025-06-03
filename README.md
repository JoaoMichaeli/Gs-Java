
# 🌍 EcoDenúncia

## 📖 Descrição
O EcoDenúncia é uma plataforma web desenvolvida para combater o descarte irregular de lixo nas cidades. Seu principal objetivo é permitir que cidadãos denunciem pontos críticos de acúmulo de lixo, entulho e descartes clandestinos, que causam enchentes, entupimento de bueiros e degradação ambiental.

A solução promove a conscientização, fortalece a comunicação entre a população e os órgãos públicos e contribui diretamente para uma cidade mais limpa, organizada e sustentável.

---

## 🎯 Objetivos
- ✅ Promover a conscientização ambiental.
- ✅ Reduzir impactos causados por lixo em locais inadequados.
- ✅ Facilitar a comunicação entre cidadãos e órgãos públicos.
- ✅ Monitorar e acompanhar denúncias em tempo real.
- ✅ Contribuir para a melhoria da qualidade urbana.

---

## 🚀 Funcionalidades
- 🗑️ **Cadastro de denúncias com:**
  - Descrição detalhada
  - Localização via CEP (preenchimento automático com ViaCEP)
  - Envio de fotos (feature futura)

- 🔍 Consulta e acompanhamento de denúncias (status: Pendente, Em andamento, Resolvido)

- 👥 Autenticação e cadastro de usuários (cidadãos e administradores) com JWT

- 🏛️ Gestão de denúncias por administradores

- 📍 Cadastro de localidades: estados, cidades, bairros e endereços

- 📑 Filtros, ordenação e paginação nas buscas

---

## 🏗️ Arquitetura
- **Model:** Entidades principais (Usuario, Denuncia, Endereco, OrgaoPublico)  
- **Repository:** Comunicação com o banco via JPA  
- **Service:** Lógica de negócios e validações  
- **Controller:** Manipulação das requisições HTTP (REST API)  
- **Segurança:** Autenticação via JWT (Spring Security)  

---

## 🧠 Tecnologias Utilizadas
- Backend: Java 17 + Spring Boot  
- Segurança: Spring Security + JWT  
- Banco de Dados: Oracle Database (ou PostgreSQL)  
- ORM: JPA + Hibernate  
- API Externa: ViaCEP (consulta de CEP)  
- Documentação: Swagger/OpenAPI  
- Frontend (Opcional): React, Angular ou Thymeleaf  

---

## 🔗 Endpoints Principais

| Entidade          | Endpoint             | Função                                |
|-------------------|----------------------|-------------------------------------|
| Estado            | `/state`             | Cadastro e consulta de estados      |
| Cidade            | `/city`              | Cadastro e consulta de cidades      |
| Bairro            | `/neighborhood`      | Cadastro e consulta de bairros      |
| Localização       | `/location`          | Cadastro de endereços                |
| Órgão Público     | `/public-organization` | Cadastro de órgãos responsáveis     |
| Denúncia          | `/complaint`         | Criar, consultar e atualizar denúncias |
| Acompanhamento denúncia | `/report-followup`   | Atualização de status das denúncias |
| Usuário           | `/users`             | Cadastro e gestão de usuários        |
| Autenticação      | `/login`             | Login e geração de token JWT         |

---

## 🔑 Autenticação (JWT)
Todas as rotas protegidas exigem um token JWT.

Envie o token no header da requisição:

```http
Authorization: Bearer {seu_token}
```

---

## 🏗️ Fluxo de Uso
1. 👤 O cidadão realiza seu cadastro.  
2. 🗺️ Informa o local da denúncia via CEP (autocompleta o endereço).  
3. 🗑️ Descreve o problema e cria a denúncia.  
4. 🏛️ O órgão público responsável recebe, avalia e atualiza o status.  
5. ✅ O cidadão pode acompanhar o andamento até a resolução.  

---

## 📚 Exemplos de Requisições

### 🔐 Login  
**POST /login**

```json
{
  "email": "usuario@email.com",
  "senha": "suaSenha123"
}
```

Resposta:

```json
{
  "token": "jwt_token_aqui"
}
```

---

### 👤 Cadastro de Usuário  
**POST /users**

```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senhaSegura123",
  "role": "ADMIN"
}
```

---

### 🌎 Cadastro de Estado  
**POST /state**

```json
{
  "nome": "São Paulo",
  "uf": "SP"
}
```

---

### 🏙️ Cadastro de Cidade  
**POST /city**

```json
{
  "nome": "São Paulo",
  "idEstado": 1
}
```

---

### 🏛️ Cadastro de Órgão Público  
**POST /public-organization**

```json
{
  "nome": "Prefeitura Municipal",
  "areaAtuacao": "Meio Ambiente"
}
```

---

### 📍 Cadastro de Localização  
**POST /location**

```json
{
  "logradouro": "Rua das Flores",
  "numero": "123",
  "complemento": "Apto 12",
  "cep": "01001-000",
  "idBairro": 1
}
```

---

### 🚨 Cadastro de Denúncia  
**POST /complaint**

```json
{
  "idUsuario": 5,
  "idLocalizacao": 12,
  "idOrgao": 3,
  "dataHora": "2025-05-28T15:00:00Z",
  "descricao": "Descarte irregular de lixo na calçada."
}
```

---

### 🔄 Atualização de Status da Denúncia  
**POST /report-followup**

```json
{
  "status": "Em andamento",
  "descricao": "Equipe enviada para averiguar a situação.",
  "denunciaId": 7
}
```

---

### 🔍 Filtros, Ordenação e Paginação

- Filtros: `?nome=São Paulo`  
- Ordenação: `?sort=nome,asc` ou `?sort=dataHora,desc`  
- Paginação: `?page=0&size=10`  

**Exemplo:**

```http
GET /complaint?sort=dataHora,desc&page=0&size=10&descricao=entulho
Authorization: Bearer {token}
```

---

## 🧪 Testando no Swagger

Acesse: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Utilize filtros, ordenação e paginação.

Clique em "Execute" para testar os endpoints.

---

## 🌎 Impacto Ambiental e Social
- ♻️ Contribuição para uma cidade mais limpa e sustentável.  
- 🌧️ Redução de enchentes causadas por descarte irregular.  
- 🏙️ Melhoria na qualidade de vida urbana.  
- 🤝 Facilita a comunicação entre cidadãos e órgãos públicos.
