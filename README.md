# Authentication-Boleto
ProjetoA3


Facilitar a autenticação e verificação de boletos bancários por parte de empresas e usuários;

Garantir segurança e integridade nas transações por meio de tokens de verificação;

| Módulo                                   | Descrição                                                                                         |
| ---------------------------------------- | ------------------------------------------------------------------------------------------------- |
| 🏢 **Cadastro de Empresa**               | Permite o cadastro de empresas emissoras de boletos, com preenchimento automático via CNPJ e CEP. |
| 💰 **Cadastro de Boletos**               | Permite a criação manual ou via upload CSV de boletos bancários.                                  |
| 🔐 **Autenticação de Boletos**           | Validação do boleto e geração de token de segurança.                                              |
| 📲 **Envio de Token (simulado via SMS)** | Envia código de verificação de forma segura.                                                      |
| ✅ **Confirmação de Token**               | Permite confirmar o boleto autenticado antes da aprovação do pagamento.                           |
| 📦 **Upload de Arquivo CSV**             | Importa boletos em lote com suporte a `multipart/form-data`.                                      |
| 📑 **Logs de Operações**                 | Registro completo de logs e status de autenticação.                                               |


1. Cadastro e consulta de empresas

```
Endpoints:
POST /api/empresas → cria uma empresa.

GET /api/empresas → lista todas as empresas.

GET /api/empresas/{cnpj} → busca empresa por CNPJ.

Fluxo:

A empresa é cadastrada no sistema.

Cada boleto criado estará vinculado a uma empresa
```
2. Criação de boleto

```
Endpoint: POST /api/boleto/criar

Fluxo:

O usuário envia um BoletoRequest com código de barras, valor, vencimento, CNPJ do beneficiário, etc.

O service (BoletoService) cria um novo Boleto:

Gera codigoAutenticacao.

Define StatusBoleto inicial (ex.: GERADO).

Salva no banco (BoletoRepository.save()).

Retorna um BoletoResponse com os dados do boleto.
```
