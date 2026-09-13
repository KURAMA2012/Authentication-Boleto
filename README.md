# Authentication-Boleto
ProjetoA3

**Secrets:** copy `.env.example` → `.env`, export the variables (Spring Boot does not load `.env` by itself), and never commit passwords. Details: [docs/security.md](docs/security.md).

Apresentação
https://youtu.be/3DozZnX-OZg parte 1
https://youtu.be/zjEa-hGpCfQ parte 2
https://youtu.be/3XuRNAA6-4s parte 3



Facilitar a autenticação e verificação de boletos bancários por parte de empresas e usuários;

Garantir segurança e integridade nas transações por meio de tokens de verificação;
```
| Módulo                                   | Descrição                                                                                         |
| ---------------------------------------- | ------------------------------------------------------------------------------------------------- |
| 🏢 **Cadastro de Empresa**               | Permite o cadastro de empresas emissoras de boletos, com preenchimento automático via CNPJ e CEP. |
| 💰 **Cadastro de Boletos**               | Permite a criação manual ou via upload CSV de boletos bancários.                                  |
| 🔐 **Autenticação de Boletos**           | Validação do boleto e geração de token de segurança.                                              |
| 📲 **Envio de Token (simulado via SMS)** | Envia código de verificação de forma segura.                                                      |
| ✅ **Confirmação de Token**               | Permite confirmar o boleto autenticado antes da aprovação do pagamento.                           |
| 📦 **Upload de Arquivo CSV**             | Importa boletos em lote com suporte a `multipart/form-data`.                                      |
| 📑 **Logs de Operações**                 | Registro completo de logs e status de autenticação.                                               |
```

🚀 Tecnologias Utilizadas
☕ Back-end (Spring Boot)
```
Java 17
Spring Boot 3
Spring Web / Data JPA
Spring AMQP (RabbitMQ)
Spring Batch (processamento em lote)
PostgreSQL
Swagger UI
RestTemplate (para integração com APIs públicas)
Lombok
Maven
```

📱 Front-end (Ionic + Angular)
```
Ionic 7 / Angular 17

Capacitor 6

PrimeNG / Ionic Components
```

🌐 APIs Externas
```
ReceitaWS → Consulta automática de dados da empresa pelo CNPJ

ViaCEP → Consulta de endereço automático pelo CEP
```

⚙️ Arquitetura do Sistema

O sistema é composto por dois projetos principais:

```🧩 1. AuthenticationBoleto (Serviço Principal)

Responsável por:

Cadastro e autenticação de boletos

Validação de tokens

Consulta de empresas (CNPJ / CEP)

Registro de logs

Comunicação com o RabbitMQ
```

```
⚙️ 2. BoletoBatchService (Serviço Secundário)

Responsável por:

Processar arquivos CSV com boletos em massa

Ler, validar e enviar boletos para a fila do RabbitMQ

Publicar mensagens que são consumidas pelo serviço principal

🐇 RabbitMQ

Atua como mensageiro entre os dois serviços.

O Batch Service envia mensagens (boletos processados) para uma fila.

O AuthenticationBoleto consome essas mensagens, valida e armazena os boletos no banco de dados.
```
📦 Estrutura dos Projetos

```
🔹 AuthenticationBoleto
com.A3.projeto.Faculdade.AuthenticationBoleto
 ┣ 📁 controller          → Endpoints REST (Boleto, Empresa, Token, Consulta)
 ┣ 📁 entity              → Entidades JPA (Boleto, Empresa, TokenVerificacao)
 ┣ 📁 service             → Regras de negócio e integração com RabbitMQ
 ┣ 📁 repository          → Interfaces JPA
 ┣ 📁 config              → Configuração de CORS, RabbitMQ e segurança
 ┣ 📁listener             →  Escuta mensagens do RabbitMQ
 ┣ 📁 dto                 → Objetos de transferência de dados (Request/Response)
 ┗ 📄 AuthenticationBoletoApplication.java
```
```
🔹 BoletoBatchService
 com.A3.projeto.Faculdade.BoletoBatchService
 ┣ 📁 producer          → Envia boletos para a fila RabbitMQ
 ┣ 📁 service           → Lógica de leitura e parsing do CSV
 ┣ 📁 config            → Configuração do RabbitMQ
 ┗ 📄 BoletoBatchApplication.java
```

``` Fluxo do Sistema
🧭 Etapas:

Empresa se cadastra — dados são validados via ReceitaWS e ViaCEP;

Empresa cria boletos manualmente ou envia CSV via upload (Ionic);

O Batch Service processa o CSV e envia boletos ao RabbitMQ;

O AuthenticationBoleto consome a fila e armazena os boletos no banco;

O usuário (pagador) valida o boleto e recebe um token de confirmação;

O sistema valida o token e autentica o pagamento do boleto.```
```
