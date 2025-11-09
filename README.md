# Authentication-Boleto
ProjetoA3


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
