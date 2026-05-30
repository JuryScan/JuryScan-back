# 💰 Sistema de Pagamento e Créditos - JuryScan

> **Documentação técnica do sistema de compra, gerenciamento e consumo de créditos**

---

## 📋 Índice

- [1. Visão Geral](#1-visão-geral)
- [2. Preços e Valores](#2-preços-e-valores)
- [3. Sistema de Pagamento](#3-sistema-de-pagamento)
- [4. Consumo de Créditos](#4-consumo-de-créditos)
- [5. Sistema de Leads](#5-sistema-de-leads)
- [6. Fluxogramas](#6-fluxogramas)

---

## 1. Visão Geral

O JuryScan utiliza um **sistema de créditos** para monetizar serviços. Os créditos são a moeda interna da plataforma e são utilizados principalmente por **advogados** para adquirir leads de clientes potenciais.

### Componentes Principais

- **Wallet (Carteira)**: Armazena o saldo de créditos de cada usuário
- **Transaction (Transação)**: Registra histórico de compras e consumos
- **Stripe**: Gateway de pagamento integrado
- **Webhook**: Processa confirmações de pagamento automaticamente

---

## 2. Preços e Valores

### 💵 Tabela de Preços

| Item | Valor Unitário | Observações |
|------|----------------|-------------|
| **1 Crédito** | **R$ 0,20** | Preço fixo por crédito |
| **Aquisição de Lead** | **10 Créditos** | Custo para advogado adquirir um lead |
| **Aquisição de Lead** | **R$ 2,00** | Equivalente em reais (10 × R$ 0,20) |

### 📊 Exemplos de Compra

| Valor Pago (R$) | Créditos Recebidos | Cálculo |
|-----------------|-------------------|---------|
| R$ 10,00 | 50 créditos | 1.000 centavos ÷ 20 centavos |
| R$ 20,00 | 100 créditos | 2.000 centavos ÷ 20 centavos |
| R$ 50,00 | 250 créditos | 5.000 centavos ÷ 20 centavos |
| R$ 100,00 | 500 créditos | 10.000 centavos ÷ 20 centavos |

### 🔢 Fórmula de Conversão

```java
// Configuração: TokenPricingConfig.java
PRICE_PER_TOKEN_IN_CENTS = 20  // R$ 0,20 = 20 centavos

// Cálculo de créditos recebidos
créditos = valorPagoEmCentavos ÷ 20

// Exemplo: R$ 10,00 = 1000 centavos
créditos = 1000 ÷ 20 = 50 créditos
```

---

## 3. Sistema de Pagamento

### 🔄 Fluxo Completo de Compra de Créditos

```
┌─────────────────────────────────────────────────────────────────┐
│                    FLUXO DE COMPRA DE CRÉDITOS                  │
└─────────────────────────────────────────────────────────────────┘

1. SOLICITAÇÃO DE CHECKOUT
   │
   ├─► Usuário (Frontend)
   │   └─► POST /api/product-checkout/checkout
   │       Body: {
   │         "name": "Pacote de 50 créditos",
   │         "amount": 1000,      // R$ 10,00 em centavos
   │         "quantity": 1
   │       }
   │
   ├─► Backend (ProductCheckoutController)
   │   └─► StripeService.checkoutProducts()
   │       │
   │       ├─► Cria sessão no Stripe
   │       ├─► Associa userId ao clientReferenceId
   │       └─► Retorna URL de pagamento
   │
   └─► Resposta: {
         "status": "SUCCESS",
         "sessionId": "cs_test_...",
         "sessionUrl": "https://checkout.stripe.com/pay/..."
       }

2. PAGAMENTO NO STRIPE
   │
   └─► Usuário é redirecionado para página do Stripe
       └─► Realiza pagamento (cartão, PIX, etc.)

3. CONFIRMAÇÃO AUTOMÁTICA (WEBHOOK)
   │
   ├─► Stripe notifica o backend
   │   └─► POST /api/webhook/stripe/checkout-success
   │       Header: Stripe-Signature (validação de segurança)
   │
   ├─► StripeWebhookService.handleCheckoutSessionCompleted()
   │   │
   │   ├─► Verifica se já foi processado (idempotência)
   │   ├─► Extrai dados da sessão:
   │   │   ├─► userId (clientReferenceId)
   │   │   └─► amountTotal (valor pago)
   │   │
   │   ├─► Calcula créditos: tokens = amountTotal ÷ 20
   │   │
   │   ├─► Adiciona créditos à carteira
   │   │   └─► WalletService.addCredits(userId, tokens)
   │   │
   │   └─► Registra transação
   │       └─► Transaction:
   │           ├─► tipo: COMPRA
   │           ├─► quantidade: tokens
   │           └─► stripeCheckoutId: session_id
   │
   └─► Créditos disponíveis na carteira do usuário ✅
```

### 🔑 Endpoints

#### Iniciar Checkout
```http
POST /api/product-checkout/checkout
Authorization: Bearer {token}

Request Body:
{
  "name": "Pacote de 100 créditos",   // Nome do produto
  "amount": 2000,                      // R$ 20,00 (em centavos)
  "quantity": 1                         // Quantidade (sempre 1 para créditos)
}

Response:
{
  "success": true,
  "message": "Checkout realizado com sucesso",
  "data": {
    "status": "SUCCESS",
    "sessionId": "cs_test_abc123...",
    "sessionUrl": "https://checkout.stripe.com/pay/cs_test_abc123..."
  }
}
```

#### Webhook Stripe (Interno)
```http
POST /api/webhook/stripe/checkout-success
Header: Stripe-Signature: {signature}

Body: {evento Stripe em formato JSON}
```

### 🔐 Segurança

1. **Validação de Assinatura**: O webhook valida a assinatura Stripe para garantir autenticidade
2. **Idempotência**: Verifica se a transação já foi processada (evita duplicação)
3. **Transação Atômica**: Usa `@Transactional` para garantir consistência

---

## 4. Consumo de Créditos

### 📉 Tipos de Consumo

Atualmente, existe **apenas 1 tipo de consumo** no sistema:

| Tipo | Descrição | Custo | Quem Consome |
|------|-----------|-------|--------------|
| **AQUISICAO_LEAD** | Advogado adquire acesso aos dados de um cliente interessado | 10 créditos | Advogado |

### 💳 Histórico de Transações

Toda movimentação de créditos é registrada na tabela `tb_transacao`:

```sql
CREATE TABLE tb_transacao (
  id UUID PRIMARY KEY,
  id_usuario UUID NOT NULL,
  tipo_transacao VARCHAR(10) NOT NULL,  -- COMPRA, CONSUMO, AQUISICAO_LEAD
  quantidade INT NOT NULL,               -- Quantidade de créditos
  stripe_checkout_id VARCHAR(255),       -- ID da sessão Stripe (apenas COMPRA)
  data_criacao TIMESTAMP DEFAULT now()
);
```

### 📊 Exemplos de Transações

```
┌──────────────┬──────────────┬─────────────────────┬────────────┬──────────────────────────┐
│ Tipo         │ Quantidade   │ Stripe Checkout ID  │ Descrição  │ Impacto no Saldo         │
├──────────────┼──────────────┼─────────────────────┼────────────┼──────────────────────────┤
│ COMPRA       │ +50          │ cs_test_abc123      │ Pagamento  │ Saldo + 50               │
│ AQUISICAO_   │ -10          │ null                │ Lead       │ Saldo - 10               │
│ LEAD         │              │                     │ adquirido  │                          │
└──────────────┴──────────────┴─────────────────────┴────────────┴──────────────────────────┘
```

---

## 5. Sistema de Leads

### 🎯 Visão Geral

O **Sistema de Leads** conecta usuários comuns (potenciais clientes) com advogados. É o principal caso de uso para consumo de créditos.

### 👥 Atores

1. **Usuário Comum**: Faz análise CNIS e solicita serviço de advogado (cria lead)
2. **Advogado**: Visualiza leads disponíveis e compra acesso aos dados dos clientes

### 🔄 Fluxo Completo de Leads

```
┌─────────────────────────────────────────────────────────────────────┐
│                    FLUXO COMPLETO DO SISTEMA DE LEADS                │
└─────────────────────────────────────────────────────────────────────┘

FASE 1: CRIAÇÃO DO LEAD (Usuário Comum)
────────────────────────────────────────
1. Usuário Comum faz análise do documento CNIS
   └─► POST /api/analysis/upload
       └─► Retorna: { "id": "analysis-123", ... }

2. Usuário decide solicitar serviço de advogado
   └─► POST /api/leads/request
       Body: { "analysisId": "analysis-123" }
       │
       ├─► Validações:
       │   ├─► Apenas usuário COMUM pode criar
       │   ├─► Análise deve existir e pertencer ao usuário
       │   └─► Não pode existir lead duplicado para mesma análise
       │
       └─► Lead Criado:
           ├─► status: DISPONIVEL
           ├─► custoCreditos: 10
           ├─► advogadoId: null
           └─► dataAquisicao: null

FASE 2: VISUALIZAÇÃO DE LEADS (Advogado)
─────────────────────────────────────────
3. Advogado lista leads disponíveis
   └─► GET /api/leads/available?page=0&page_size=10
       │
       └─► Retorna leads com DADOS LIMITADOS:
           ├─► nomeCliente: "João Silva"
           ├─► tituloAnalise: "Análise CNIS"
           ├─► custoCreditos: 10
           ├─► SEM: email, telefone, CPF, análise completa
           └─► Status: DISPONIVEL

FASE 3: AQUISIÇÃO DO LEAD (Advogado)
─────────────────────────────────────
4. Advogado decide adquirir o lead
   └─► POST /api/leads/{leadId}/acquire
       │
       ├─► Validações:
       │   ├─► Apenas usuário ADVOGADO pode adquirir
       │   ├─► Lead deve estar DISPONIVEL
       │   └─► Advogado deve ter saldo ≥ 10 créditos
       │
       ├─► Processamento (@Transactional):
       │   │
       │   ├─► 1. Debita 10 créditos da carteira
       │   │   └─► WalletService.deductCredits(advogadoId, 10)
       │   │       └─► wallet.saldo -= 10
       │   │
       │   ├─► 2. Registra transação
       │   │   └─► Transaction:
       │   │       ├─► tipo: AQUISICAO_LEAD
       │   │       ├─► quantidade: 10
       │   │       └─► stripeCheckoutId: null
       │   │
       │   └─► 3. Atualiza lead
       │       └─► Lead:
       │           ├─► status: DISPONIVEL → ADQUIRIDO
       │           ├─► advogadoId: {id-do-advogado}
       │           └─► dataAquisicao: {timestamp-atual}
       │
       └─► Retorna lead atualizado

FASE 4: ACESSO AOS DADOS COMPLETOS (Advogado)
──────────────────────────────────────────────
5. Advogado acessa dados completos do lead adquirido
   └─► GET /api/leads/{leadId}/details
       │
       ├─► Validações:
       │   ├─► Lead deve pertencer ao advogado
       │   └─► Status deve ser ADQUIRIDO
       │
       └─► Retorna DADOS COMPLETOS:
           ├─► Dados do Cliente:
           │   ├─► nomeCompleto: "João Silva"
           │   ├─► email: "joao.silva@email.com"
           │   ├─► telefone: "81999999999"
           │   ├─► cpf: "12345678900"
           │   └─► dataNascimento: "1990-01-15"
           │
           └─► Análise Completa:
               ├─► titulo: "Análise CNIS"
               ├─► descricaoGeral: "..."
               ├─► relatorioSumarioJuridico: "..."
               └─► sumario: "..."
```

### 📊 Estados do Lead

```
┌──────────────┐
│  DISPONIVEL  │ ── Lead criado, aguardando aquisição
└──────┬───────┘
       │
       ├─► (Advogado adquire) ─────► ┌──────────────┐
       │                              │  ADQUIRIDO   │
       │                              └──────────────┘
       │
       ├─► (Cliente cancela) ────────► ┌──────────────┐
       │                                │  CANCELADO   │
       │                                └──────────────┘
       │
       └─► (Tempo expira) ───────────► ┌──────────────┐
                                        │  EXPIRADO    │
                                        └──────────────┘
```

### 🔐 Controle de Acesso aos Dados

| Status Lead | Dados Visíveis para Advogado | Dados Completos |
|-------------|------------------------------|-----------------|
| **DISPONIVEL** | ✅ Nome, Título da Análise, Custo | ❌ Sem email, telefone, CPF, análise completa |
| **ADQUIRIDO** | ✅ Todos os dados | ✅ Email, telefone, CPF, análise completa |
| **CANCELADO** | ❌ Não listado | ❌ Não acessível |
| **EXPIRADO** | ❌ Não listado | ❌ Não acessível |

### 🔑 Endpoints do Sistema de Leads

#### 1. Criar Lead (Usuário Comum)
```http
POST /api/leads/request
Authorization: Bearer {token-usuario-comum}

Request:
{
  "analysisId": "123e4567-e89b-12d3-a456-426614174000"
}

Response:
{
  "success": true,
  "message": "Solicitação de advogado criada com sucesso",
  "data": {
    "id": "lead-abc-123",
    "status": "DISPONIVEL",
    "custoCreditos": 10,
    ...
  }
}
```

#### 2. Listar Leads Disponíveis (Advogado)
```http
GET /api/leads/available?page=0&page_size=10
Authorization: Bearer {token-advogado}

Response:
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "lead-abc-123",
        "nomeCliente": "João Silva",
        "tituloAnalise": "Análise CNIS",
        "status": "DISPONIVEL",
        "custoCreditos": 10
      }
    ],
    "totalElements": 15,
    "page": 0,
    "pageSize": 10
  }
}
```

#### 3. Adquirir Lead (Advogado)
```http
POST /api/leads/{leadId}/acquire
Authorization: Bearer {token-advogado}

Response:
{
  "success": true,
  "message": "Lead adquirido com sucesso",
  "data": {
    "id": "lead-abc-123",
    "status": "ADQUIRIDO",
    "advogadoId": "adv-xyz-789",
    "dataAquisicao": "2026-05-30T14:30:00"
  }
}
```

#### 4. Ver Dados Completos (Advogado)
```http
GET /api/leads/{leadId}/details
Authorization: Bearer {token-advogado}

Response:
{
  "success": true,
  "data": {
    "id": "lead-abc-123",
    "nomeCompleto": "João Silva",
    "email": "joao.silva@email.com",
    "telefone": "81999999999",
    "cpf": "12345678900",
    "analise": {
      "titulo": "Análise CNIS",
      "descricaoGeral": "...",
      "relatorioSumarioJuridico": "..."
    }
  }
}
```

#### 5. Minhas Solicitações (Usuário Comum)
```http
GET /api/leads/my-requests?page=0&page_size=10
Authorization: Bearer {token-usuario-comum}

Response:
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "lead-abc-123",
        "status": "ADQUIRIDO",
        "advogadoId": "adv-xyz-789"
      }
    ]
  }
}
```

#### 6. Cancelar Lead (Usuário Comum)
```http
DELETE /api/leads/{leadId}/cancel
Authorization: Bearer {token-usuario-comum}

Response:
{
  "success": true,
  "message": "Solicitação cancelada com sucesso"
}
```

---

## 6. Fluxogramas

### 💰 Resumo: Compra de Créditos

```
Usuário → Checkout → Stripe → Webhook → +Créditos na Carteira
  (1)        (2)        (3)      (4)            (5)

1. Solicita checkout (valor + quantidade)
2. Backend cria sessão Stripe
3. Usuário paga no Stripe
4. Stripe notifica backend via webhook
5. Backend adiciona créditos automaticamente
```

### 🎯 Resumo: Aquisição de Lead

```
Cliente → Cria Lead → Advogado Lista → Advogado Adquire → Acesso Total
  (1)        (2)           (3)              (4)               (5)

1. Cliente solicita serviço (POST /leads/request)
2. Lead fica DISPONIVEL
3. Advogado vê leads disponíveis (GET /leads/available)
4. Advogado compra lead: -10 créditos (POST /leads/{id}/acquire)
5. Advogado acessa dados completos (GET /leads/{id}/details)
```

---

## 📝 Observações Importantes

### ⚠️ Validações Críticas

1. **Saldo Insuficiente**: Se advogado não tem 10 créditos, retorna erro `402 Payment Required`
2. **Lead Duplicado**: Não é possível criar 2 leads para mesma análise
3. **Permissões**: Apenas COMUM cria leads, apenas ADVOGADO adquire
4. **Idempotência**: Webhook processa cada pagamento apenas 1 vez
5. **Dados Sensíveis**: Apenas leads ADQUIRIDOS mostram dados completos

### 💡 Boas Práticas

- Use `@Transactional` em operações que mexem com créditos
- Valide saldo ANTES de debitar
- Registre TODAS as transações (auditoria)
- Webhook deve ser idempotente
- Validar assinatura Stripe em produção

---

## 🚀 Próximas Implementações (Sugestões)

- [ ] Sistema de pacotes com desconto (ex: 100 créditos por R$ 18,00)
- [ ] Créditos bônus em primeira compra
- [ ] Expiração de leads (após X dias como DISPONIVEL → EXPIRADO)
- [ ] Histórico detalhado de transações no frontend
- [ ] Relatório de consumo de créditos
- [ ] Sistema de reembolso/estorno

---

**Versão**: 1.0  
**Última Atualização**: 30 de Maio, 2026  
**Autor**: Sistema JuryScan

