# JuryScan-back
Back-end da aplicação em Spring do projeto **JuryScan**.

## ⚙️ Funcionalidades

- 🔐 Autenticação e Autorização (endpoints de login e controle de níveis de usuário)
- 📄 Upload e Processamento de Documentos (orquestração com agentes de IA para interpretação dos dados)
- 🧾 Gestão de Entidades (CRUD para as entidades propostas)
- 📬 Integração com E-mail (envio de notificações)
- 📈 Dashboards e Métricas (endpoints para métricas de uso)
- 🌐 Documentação da API (documentação no padrão Swagger)
- 🛡️ Segurança e Boas Práticas (criptografia, validações de entrada e tratamento de exceções)

## 🍁 Ambientes
A aplicação possui dois ambientes de execução
- prod (ambiente de produção e deploy)
- dev (ambiente de desenvolvimento e testes)

## 🏠 Como rodar localmente
### Pré-requisitos
- JDK 21
- Maven
- PostgreSQL (para uso em produção)

### Instalação
1. Clone o repositório
````shell
git clone https://github.com/JuryScan/JuryScan-back.git && cd JuryScan-back
````
2. Abra o diretório na sua IDE de preferência

3. Configure as variáveis de ambiente com base no arquivo template `.env.example`

## 📂 Estrutura e padronização do projeto
O Projeto segue uma estrutura e padronização como forma de manter o uso de boas práticas
````shell
📁 src/
 ├── 📁 main/
 │   ├── 📁 java/
 │   │   └── 📁 unicap/
 │   │       └── 📁 juryscan/
 │   │           ├── 📁 config/              # Classes de configuração (CORS, JavaMail, etc.)
 │   │           ├── 📁 controller/          # Controllers REST
 │   │           ├── 📁 service/             # Regras de negócio
 │   │           │   └── 📁 impi/            # Implementações dos serviços
 │   │           ├── 📁 mapper/              # MapStruct mappers
 │   │           ├── 📁 model/               # Entidades JPA
 │   │           ├── 📁 repository/          # Interfaces de acesso ao banco (Spring Data)
 │   │           ├── 📁 dto/                 # Data Transfer Objects
 │   │           │   ├── 📁 request/         # DTOs para criação
 │   │           │   │   └── EntityRequestDTO.java  # Exemplo de DTO de entrada
 │   │           │   ├── 📁 response/        # DTOs para resposta
 │   │           │   └── 📁 update/          # DTOs para atualização
 │   │           ├── 📁 utils/               # Classes utilitárias
 │   │           ├── 📁 enum/                # Enumerações
 │   │           └── 📁 exceptions/          # Classes de exceção personalizadas
 │   └── 📁 resources/
 │       ├── 📁 static/                      # Arquivos estáticos (imagens)
 │       ├── 📁 templates/                   # Templates Thymeleaf
 │       └── 📁 db/
 │           └── 📁 migration/               # Scripts de migração do banco (Flyway)
 └── 📁 test/                                # Testes automatizados
````

# 📄 Documentação
A documentação da API do JuryScan pode ser acessada no endpoint `/swagger-ui/index.html`

# ❗ Observações
Algumas observações consideráveis para essa aplicação
- O H2 Database pode ser apenas utilizado em ambiente de desenvolvimento. A visualização do esquema pode ser visualizado no endpoint `/h2-console`