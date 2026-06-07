# 📋 Fluxo de Leads - JuryScan

Este documento descreve o fluxo completo de geração e aquisição de leads no sistema JuryScan, desde a criação da solicitação pelo usuário comum até a aquisição por um advogado.

## 📖 Índice

1. [Visão Geral](#visão-geral)
2. [Pré-requisitos](#pré-requisitos)
3. [Parte 1: Usuário Comum](#parte-1-usuário-comum---criar-solicitação-de-lead)
4. [Parte 2: Advogado](#parte-2-advogado---visualizar-e-adquirir-leads)
5. [Estados do Lead](#estados-do-lead)
6. [Resumo do Fluxo](#resumo-do-fluxo)
7. [Regras de Negócio](#regras-de-negócio-importantes)
8. [Estrutura de Dados](#estrutura-de-dados)

---

## 🎯 Visão Geral

O sistema de leads permite que usuários comuns solicitem contato de advogados através da criação de leads vinculados às suas análises de processos. Advogados podem visualizar leads disponíveis e adquiri-los mediante pagamento de créditos, obtendo acesso aos dados completos do cliente.

**Base URL:** `/api/v1/leads`

---

## 📌 Pré-requisitos

- ✅ O usuário comum deve estar autenticado
- ✅ O usuário comum deve ter uma **análise de processo** já criada
- ✅ O lead é vinculado a uma análise específica
- ✅ Advogados precisam ter créditos suficientes para adquirir leads

---

## PARTE 1: Usuário Comum - Criar Solicitação de Lead

### 1️⃣ Criar uma Solicitação de Lead

Endpoint para o usuário comum criar uma solicitação de contato com advogados.

**Endpoint:** `POST /api/v1/leads/request`

**Headers:**
```http
Authorization: Bearer {token_do_usuario_comum}
Content-Type: application/json
```

**Request Body:**
```json
{
  "analysisId": "uuid-da-analise"
}
```

**Response - Sucesso (201):**
```json
{
  "success": true,
  "message": "Solicitação de advogado criada com sucesso",
  "data": {
    "id": "uuid-do-lead",
    "usuarioClienteId": "uuid-do-cliente",
    "nomeCliente": "Nome do Cliente",
    "analiseId": "uuid-da-analise",
    "tituloAnalise": "Titulo da Análise",
    "status": "DISPONIVEL",
    "custoCreditos": 10,
    "dataCriacao": "2026-06-06T10:00:00.000Z",
    "dataAquisicao": null,
    "advogadoId": null
  },
  "statusCode": 201
}
```

**Validações realizadas:**
- ✅ Usuário deve ser do tipo `COMUM`
- ✅ A análise deve existir no sistema
- ✅ A análise deve pertencer ao usuário autenticado
- ✅ Não pode existir outro lead para a mesma análise
- ✅ O lead é criado com status `DISPONIVEL` e custo padrão de **10 créditos**

**Possíveis Erros:**
| Status | Erro | Descrição |
|--------|------|-----------|
| 404 | ResourceNotFoundException | Usuário ou análise não encontrado |
| 400 | IllegalStateException | Usuário não é do tipo COMUM |
| 400 | IllegalStateException | Análise não pertence ao usuário |
| 400 | IllegalStateException | Já existe lead para esta análise |

---

### 2️⃣ Visualizar Minhas Solicitações

Endpoint para o usuário comum visualizar suas solicitações de lead.

**Endpoint:** `GET /api/v1/leads/my-requests?page=0&page_size=10`

**Headers:**
```http
Authorization: Bearer {token_do_usuario_comum}
```

**Query Parameters:**
| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| page | int | Sim | Número da página (base 0) |
| page_size | int | Sim | Quantidade de itens por página |

**Response - Sucesso (200):**
```json
{
  "success": true,
  "message": "Solicitações encontradas com sucesso",
  "data": {
    "items": [
      {
        "id": "uuid-do-lead",
        "usuarioClienteId": "uuid-do-cliente",
        "nomeCliente": "Nome do Cliente",
        "analiseId": "uuid-da-analise",
        "tituloAnalise": "Titulo da Análise",
        "status": "DISPONIVEL",
        "custoCreditos": 10,
        "dataCriacao": "2026-06-06T10:00:00.000Z",
        "dataAquisicao": null,
        "advogadoId": null
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  },
  "statusCode": 200
}
```

**Response - Sem Resultados (204):**
```json
{
  "success": true,
  "message": "Nenhuma solicitação encontrada",
  "statusCode": 204
}
```

---

### 3️⃣ Cancelar Solicitação

Endpoint para o usuário comum cancelar uma solicitação de lead que ainda não foi adquirida.

**Endpoint:** `DELETE /api/v1/leads/{leadId}/cancel`

**Headers:**
```http
Authorization: Bearer {token_do_usuario_comum}
```

**Path Parameters:**
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| leadId | UUID | ID do lead a ser cancelado |

**Response - Sucesso (200):**
```json
{
  "success": true,
  "message": "Solicitação cancelada com sucesso",
  "statusCode": 200
}
```

**Regras de Cancelamento:**
- ❌ **Não pode cancelar** se o lead já foi adquirido por um advogado
- ❌ **Não pode cancelar** se o lead já está com status cancelado
- ✅ Apenas o proprietário do lead pode cancelá-lo
- ✅ Status será alterado para `CANCELADO`

**Possíveis Erros:**
| Status | Erro | Descrição |
|--------|------|-----------|
| 404 | ResourceNotFoundException | Lead não encontrado |
| 400 | IllegalStateException | Lead não pertence ao usuário |
| 400 | IllegalStateException | Lead já foi adquirido |
| 400 | IllegalStateException | Lead já está cancelado |

---

## PARTE 2: Advogado - Visualizar e Adquirir Leads

### 4️⃣ Listar Leads Disponíveis

Endpoint para advogados visualizarem todos os leads disponíveis no sistema.

**Endpoint:** `GET /api/v1/leads/available?page=0&page_size=10`

**Headers:**
```http
Authorization: Bearer {token_do_advogado}
```

**Query Parameters:**
| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| page | int | Sim | Número da página (base 0) |
| page_size | int | Sim | Quantidade de itens por página |

**Response - Sucesso (200):**
```json
{
  "success": true,
  "message": "Leads disponíveis encontrados com sucesso",
  "data": {
    "items": [
      {
        "id": "uuid-do-lead",
        "usuarioClienteId": "uuid-do-cliente",
        "nomeCliente": "Nome do Cliente",
        "analiseId": "uuid-da-analise",
        "tituloAnalise": "Titulo da Análise",
        "status": "DISPONIVEL",
        "custoCreditos": 10,
        "dataCriacao": "2026-06-06T10:00:00.000Z",
        "dataAquisicao": null,
        "advogadoId": null
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  },
  "statusCode": 200
}
```

**Response - Sem Leads (204):**
```json
{
  "success": true,
  "message": "Nenhum lead disponível",
  "statusCode": 204
}
```

**Observações:**
- 📋 Retorna apenas leads com status `DISPONIVEL`
- 🔒 Dados sensíveis do cliente ainda **não** são exibidos
- 💰 Mostra o custo em créditos para adquirir o lead

---

### 5️⃣ Adquirir um Lead ⭐

Endpoint principal para advogados adquirirem um lead. Esta é a ação que conecta o advogado ao cliente.

**Endpoint:** `POST /api/v1/leads/{leadId}/acquire`

**Headers:**
```http
Authorization: Bearer {token_do_advogado}
```

**Path Parameters:**
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| leadId | UUID | ID do lead a ser adquirido |

**Response - Sucesso (200):**
```json
{
  "success": true,
  "message": "Lead adquirido com sucesso",
  "data": {
    "id": "uuid-do-lead",
    "usuarioClienteId": "uuid-do-cliente",
    "nomeCliente": "Nome do Cliente",
    "analiseId": "uuid-da-analise",
    "tituloAnalise": "Titulo da Análise",
    "status": "ADQUIRIDO",
    "custoCreditos": 10,
    "dataCriacao": "2026-06-06T10:00:00.000Z",
    "dataAquisicao": "2026-06-06T11:30:00.000Z",
    "advogadoId": "uuid-do-advogado"
  },
  "statusCode": 200
}
```

**Processo de Aquisição:**

1. ✅ **Validação do Usuário**
   - Verifica se o usuário é do tipo `ADVOGADO`

2. ✅ **Validação do Lead**
   - Verifica se o lead existe
   - Verifica se o lead está com status `DISPONIVEL`

3. 💰 **Verificação de Saldo**
   - Consulta o saldo de créditos do advogado
   - Verifica se possui créditos suficientes (padrão: 10 créditos)

4. 💸 **Débito de Créditos**
   - Debita os créditos da carteira do advogado
   - Cria uma transação do tipo `AQUISICAO_LEAD`

5. 🔄 **Atualização do Lead**
   - Status alterado para `ADQUIRIDO`
   - Advogado vinculado ao lead
   - Data de aquisição registrada (timestamp atual)

6. 📧 **Acesso aos Dados**
   - Advogado ganha acesso aos dados completos do cliente

**Possíveis Erros:**
| Status | Erro | Descrição |
|--------|------|-----------|
| 404 | ResourceNotFoundException | Lead não encontrado |
| 400 | IllegalStateException | Usuário não é do tipo ADVOGADO |
| 400 | IllegalStateException | Lead não está disponível |
| 402 | InsufficientCreditsException | Saldo de créditos insuficiente |

**Exemplo de Erro - Saldo Insuficiente:**
```json
{
  "success": false,
  "message": "Saldo insuficiente. Saldo atual: 5, créditos necessários: 10",
  "statusCode": 402
}
```

---

### 6️⃣ Ver Detalhes Completos do Lead

Endpoint para advogados visualizarem os dados completos de um lead adquirido, incluindo informações de contato do cliente.

**Endpoint:** `GET /api/v1/leads/{leadId}/details`

**Headers:**
```http
Authorization: Bearer {token_do_advogado}
```

**Path Parameters:**
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| leadId | UUID | ID do lead adquirido |

**Response - Sucesso (200):**
```json
{
  "success": true,
  "message": "Detalhes do lead obtidos com sucesso",
  "data": {
    "id": "uuid-do-lead",
    "status": "ADQUIRIDO",
    "custoCreditos": 10,
    "dataCriacao": "2026-06-06T10:00:00.000Z",
    "dataAquisicao": "2026-06-06T11:30:00.000Z",
    "clienteId": "uuid-do-cliente",
    "nomeCompleto": "Nome Completo do Cliente",
    "email": "cliente@email.com",
    "telefone": "+55 11 98765-4321",
    "cpf": "123.456.789-00",
    "dataNascimento": "1990-01-01",
    "analise": {
      "id": "uuid-da-analise",
      "titulo": "Título da Análise",
      "numeroProcesso": "1234567-89.2026.8.00.0000",
      "linkProcesso": "https://...",
      "descricao": "Descrição detalhada...",
      "relatorioSumario": "Sumário da análise...",
      "dataCriacao": "2026-06-05T15:00:00.000Z"
    }
  },
  "statusCode": 200
}
```

**Validações de Acesso:**
- ✅ Apenas o advogado que **adquiriu** o lead pode ver os detalhes
- ✅ Lead precisa estar com status `ADQUIRIDO`
- 🔒 Dados de contato sensíveis são revelados **apenas** após aquisição

**Possíveis Erros:**
| Status | Erro | Descrição |
|--------|------|-----------|
| 404 | ResourceNotFoundException | Lead não encontrado |
| 403 | IllegalStateException | Você não tem permissão para acessar este lead |
| 400 | IllegalStateException | Lead não foi adquirido |

---

### 7️⃣ Listar Meus Leads Adquiridos

Endpoint para advogados visualizarem todos os leads que já adquiriram.

**Endpoint:** `GET /api/v1/leads/acquired?page=0&page_size=10`

**Headers:**
```http
Authorization: Bearer {token_do_advogado}
```

**Query Parameters:**
| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| page | int | Sim | Número da página (base 0) |
| page_size | int | Sim | Quantidade de itens por página |

**Response - Sucesso (200):**
```json
{
  "success": true,
  "message": "Leads adquiridos encontrados com sucesso",
  "data": {
    "items": [
      {
        "id": "uuid-do-lead",
        "usuarioClienteId": "uuid-do-cliente",
        "nomeCliente": "Nome do Cliente",
        "analiseId": "uuid-da-analise",
        "tituloAnalise": "Titulo da Análise",
        "status": "ADQUIRIDO",
        "custoCreditos": 10,
        "dataCriacao": "2026-06-06T10:00:00.000Z",
        "dataAquisicao": "2026-06-06T11:30:00.000Z",
        "advogadoId": "uuid-do-advogado"
      }
    ],
    "totalElements": 3,
    "totalPages": 1,
    "currentPage": 0,
    "pageSize": 10
  },
  "statusCode": 200
}
```

**Response - Sem Leads (204):**
```json
{
  "success": true,
  "message": "Nenhum lead adquirido",
  "statusCode": 204
}
```

---

## 📊 Estados do Lead

O lead pode ter os seguintes status durante seu ciclo de vida:

### Enum: `StatusLeadEnum`

| Status | Descrição | Transições Permitidas |
|--------|-----------|----------------------|
| `DISPONIVEL` | Lead criado e aguardando advogado | → ADQUIRIDO<br>→ CANCELADO<br>→ EXPIRADO |
| `ADQUIRIDO` | Lead foi adquirido por um advogado | → *estado final* |
| `CANCELADO` | Cliente cancelou a solicitação | → *estado final* |
| `EXPIRADO` | Lead expirou por tempo limite | → *estado final* |

### Diagrama de Estados

```
┌─────────────┐
│   CRIAÇÃO   │
└──────┬──────┘
       │
       ▼
┌─────────────┐     ┌──────────────┐
│ DISPONIVEL  │────▶│  ADQUIRIDO   │
└──────┬──────┘     └──────────────┘
       │                    ▲
       ├────────────────────┘
       │                (advogado adquire)
       │
       ├───────────▶┌─────────────┐
       │            │  CANCELADO  │
       │            └─────────────┘
       │             (cliente cancela)
       │
       └───────────▶┌─────────────┐
                    │   EXPIRADO  │
                    └─────────────┘
                     (tempo limite)
```

---

## 💡 Resumo do Fluxo

### Fluxo Completo Simplificado

```
1. 👤 Cliente (COMUM)
   │
   ├─ POST /api/v1/leads/request
   │  (Cria lead com status DISPONIVEL)
   │
   ▼
2. 📋 Lead fica disponível no sistema
   │
   ├─ Status: DISPONIVEL
   │  Custo: 10 créditos
   │
   ▼
3. 👨‍⚖️ Advogado visualiza leads
   │
   ├─ GET /api/v1/leads/available
   │
   ▼
4. 👨‍⚖️ Advogado decide adquirir
   │
   ├─ POST /api/v1/leads/{leadId}/acquire
   │  • Verifica saldo (10 créditos)
   │  • Debita créditos
   │  • Cria transação
   │  • Atualiza lead para ADQUIRIDO
   │
   ▼
5. 🔓 Lead adquirido com sucesso
   │
   ├─ Status: ADQUIRIDO
   │  Dados do cliente liberados
   │
   ▼
6. 👨‍⚖️ Advogado acessa detalhes
   │
   ├─ GET /api/v1/leads/{leadId}/details
   │  • Email do cliente
   │  • Telefone do cliente
   │  • CPF e dados completos
   │  • Análise completa
   │
   ▼
7. 📞 Advogado pode contatar o cliente!
```

---

## 🔒 Regras de Negócio Importantes

### Permissões

| Ação | Usuário COMUM | Usuário ADVOGADO |
|------|---------------|------------------|
| Criar lead | ✅ Sim | ❌ Não |
| Ver próprios leads | ✅ Sim | ❌ Não |
| Cancelar lead | ✅ Sim (se não adquirido) | ❌ Não |
| Ver leads disponíveis | ❌ Não | ✅ Sim |
| Adquirir lead | ❌ Não | ✅ Sim |
| Ver detalhes do lead | ❌ Não | ✅ Sim (apenas se adquiriu) |
| Ver leads adquiridos | ❌ Não | ✅ Sim |

### Restrições

- ✅ Cada análise pode ter **apenas 1 lead**
- ✅ Lead disponível pode ser adquirido por **apenas 1 advogado**
- ✅ Uma vez adquirido, o lead **não pode ser cancelado** pelo cliente
- ✅ Cliente só pode cancelar leads com status `DISPONIVEL`
- 💰 Custo padrão fixo: **10 créditos**
- 🔄 Aquisição é uma **transação atômica** (débito + atualização)

### Segurança e Privacidade

- 🔒 **Dados sensíveis protegidos**: Email, telefone e CPF do cliente são revelados **apenas** após aquisição
- 🔐 **Controle de acesso**: Apenas o advogado que adquiriu pode ver os detalhes completos
- 🛡️ **Validação de propriedade**: Sistema valida se a análise pertence ao usuário antes de criar o lead
- 💳 **Verificação de saldo**: Sistema verifica créditos antes de permitir aquisição

### Integridade Financeira

- 💰 Débito de créditos acontece **antes** da atualização do lead
- 📝 **Transação registrada**: Toda aquisição gera um registro em `tb_transaction`
- 🔄 Operação de aquisição é **@Transactional** (rollback automático em caso de erro)

---

## 🗄️ Estrutura de Dados

### Modelo: Lead

```java
@Entity(name = "tb_lead")
public class Lead {
    private UUID id;                      // ID único do lead
    private User usuarioCliente;          // Cliente que criou o lead
    private Analysis analise;             // Análise vinculada
    private User advogado;                // Advogado que adquiriu (null se disponível)
    private StatusLeadEnum status;        // Status atual do lead
    private Integer custoCreditos;        // Custo em créditos (padrão: 10)
    private Timestamp dataCriacao;        // Data de criação automática
    private Timestamp dataAquisicao;      // Data de aquisição (null se não adquirido)
    private Timestamp dataExpiracao;      // Data de expiração (não implementado)
}
```

### DTOs

#### LeadCreateRequestDTO
```json
{
  "analysisId": "UUID"  // ID da análise a ser vinculada
}
```

#### LeadResponseDTO
```json
{
  "id": "UUID",
  "usuarioClienteId": "UUID",
  "nomeCliente": "string",
  "analiseId": "UUID",
  "tituloAnalise": "string",
  "status": "StatusLeadEnum",
  "custoCreditos": "integer",
  "dataCriacao": "timestamp",
  "dataAquisicao": "timestamp",
  "advogadoId": "UUID"
}
```

#### LeadDetailedResponseDTO
```json
{
  "id": "UUID",
  "status": "StatusLeadEnum",
  "custoCreditos": "integer",
  "dataCriacao": "timestamp",
  "dataAquisicao": "timestamp",
  "clienteId": "UUID",
  "nomeCompleto": "string",
  "email": "string",
  "telefone": "string",
  "cpf": "string",
  "dataNascimento": "date",
  "analise": "AnalysisResponseDTO"
}
```

---

## 🗄️ Banco de Dados

### Tabela: tb_lead

```sql
CREATE TABLE IF NOT EXISTS tb_lead (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario_cliente UUID NOT NULL,
    id_analise UUID NOT NULL,
    id_advogado UUID,
    status VARCHAR(15) NOT NULL DEFAULT 'DISPONIVEL',
    custo_creditos INT NOT NULL DEFAULT 10,
    data_criacao TIMESTAMP(3) DEFAULT now(),
    data_aquisicao TIMESTAMP(3),
    data_expiracao TIMESTAMP(3),

    CONSTRAINT fk_lead_usuario_cliente FOREIGN KEY (id_usuario_cliente) 
        REFERENCES tb_usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_lead_analise FOREIGN KEY (id_analise) 
        REFERENCES tb_analise (id) ON DELETE CASCADE,
    CONSTRAINT fk_lead_advogado FOREIGN KEY (id_advogado) 
        REFERENCES tb_usuario (id) ON DELETE SET NULL,
    CONSTRAINT chk_status_lead CHECK (status IN 
        ('DISPONIVEL', 'ADQUIRIDO', 'EXPIRADO', 'CANCELADO')),
    CONSTRAINT uq_lead_analise UNIQUE (id_analise)
);
```

### Índices

```sql
CREATE INDEX idx_lead_status ON tb_lead (status);
CREATE INDEX idx_lead_advogado ON tb_lead (id_advogado);
CREATE INDEX idx_lead_cliente ON tb_lead (id_usuario_cliente);
CREATE INDEX idx_lead_data_criacao ON tb_lead (data_criacao DESC);
```

---

## 📝 Exemplos de Uso

### Exemplo 1: Cliente criando um lead

```bash
curl -X POST http://localhost:8080/api/v1/leads/request \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "analysisId": "123e4567-e89b-12d3-a456-426614174000"
  }'
```

### Exemplo 2: Advogado listando leads disponíveis

```bash
curl -X GET "http://localhost:8080/api/v1/leads/available?page=0&page_size=10" \
  -H "Authorization: Bearer eyJhbGc..."
```

### Exemplo 3: Advogado adquirindo um lead

```bash
curl -X POST http://localhost:8080/api/v1/leads/123e4567-e89b-12d3-a456-426614174000/acquire \
  -H "Authorization: Bearer eyJhbGc..."
```

### Exemplo 4: Advogado visualizando detalhes do lead

```bash
curl -X GET http://localhost:8080/api/v1/leads/123e4567-e89b-12d3-a456-426614174000/details \
  -H "Authorization: Bearer eyJhbGc..."
```

---

## 🔧 Configuração

### Custo do Lead

O custo padrão do lead é configurado na classe `LeadService`:

```java
private static final Integer DEFAULT_LEAD_COST = 10;
```

Para alterar o custo, modifique esta constante ou implemente um sistema de precificação dinâmica.

---

## 📚 Referências

- **Controller:** `unicap.juryscan.controller.LeadController`
- **Service:** `unicap.juryscan.service.lead.LeadService`
- **Model:** `unicap.juryscan.model.Lead`
- **Repository:** `unicap.juryscan.repository.LeadRepository`
- **Enum:** `unicap.juryscan.enums.StatusLeadEnum`
- **Migration:** `V6__create-lead-table-db.sql`

---

## 📅 Histórico

- **2026-06-06**: Documentação criada com o fluxo completo de leads

---

**Desenvolvido por:** Equipe JuryScan  
**Última atualização:** 06/06/2026

