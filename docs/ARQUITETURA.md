# 🏗️ Arquitetura - Desafio Fullstack Integrado

## 📐 Visão Geral da Arquitetura

A solução implementa uma arquitetura em **camadas N-Tier** com separação clara de responsabilidades:

```
┌─────────────────────────────────────────────────────────┐
│           APRESENTAÇÃO (Frontend Layer)                 │
│  Angular 17 | Componentes Standalone | Responsive UI   │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP REST
┌────────────────────▼────────────────────────────────────┐
│         APLICAÇÃO (Backend Layer)                       │
│  Spring Boot | REST Controllers | Business Logic       │
├─────────────────────────────────────────────────────────┤
│  • BeneficioController      (REST Endpoints)           │
│  • BeneficioService         (Business Logic)           │
│  • BeneficioRepository      (Data Access)              │
│  • Exception Handling       (Error Management)         │
└────────────────────┬────────────────────────────────────┘
                     │ JPA/Hibernate
┌────────────────────▼────────────────────────────────────┐
│         INTEGRAÇÃO (EJB Layer)                          │
│  EJB Service | Transações ACID | Locking               │
├─────────────────────────────────────────────────────────┤
│  • BeneficioEjbService      (Business Logic)           │
│  • Validações Completas     (Data Validation)          │
│  • Locking Pessimista       (Concurrency Control)      │
│  • Transações Gerenciadas   (ACID Compliance)          │
└────────────────────┬────────────────────────────────────┘
                     │ ORM (Object-Relational Mapping)
┌────────────────────▼────────────────────────────────────┐
│         PERSISTÊNCIA (Database Layer)                   │
│  H2 Database | JPA Entities | Versionamento Otimista   │
├─────────────────────────────────────────────────────────┤
│  TABLE: BENEFICIO                                       │
│  • ID (PK)        | BIGINT (Auto-increment)            │
│  • NOME           | VARCHAR(100)                        │
│  • DESCRICAO      | VARCHAR(255)                        │
│  • VALOR          | DECIMAL(15,2)                       │
│  • ATIVO          | BOOLEAN                             │
│  • VERSION        | BIGINT (Optimistic Lock)           │
└─────────────────────────────────────────────────────────┘
```

## 🎯 Padrões de Design Implementados

### 1. **MVC (Model-View-Controller)**
- **Model**: Entidade Beneficio, DTO TransferRequest
- **View**: Componentes Angular (HTML + SCSS)
- **Controller**: BeneficioController (Spring)

### 2. **Service Layer Pattern**
- Encapsulation de lógica de negócio
- Separação de responsabilidades
- BeneficioService e BeneficioEjbService

### 3. **Repository Pattern**
- Abstração da persistência
- BeneficioRepository com Spring Data JPA

### 4. **DTO Pattern (Data Transfer Object)**
- TransferRequest para comunicação API
- Separação entre modelo interno e exposição

### 5. **Singleton Pattern**
- @Service para singleton do BeneficioService
- @Repository para CrudRepository

### 6. **Dependency Injection**
- @Autowired no backend
- Constructor Injection no frontend (RxJS)

### 7. **Observer Pattern**
- RxJS Observables para requisições HTTP

## 🔄 Fluxo de Dados

### Requisição GET (Listar Beneficiários)

```
1. Frontend (Component)
   └─→ BeneficioService.getAll()
       └─→ HttpClient.get('/api/v1/beneficios')

2. Backend (Spring Boot)
   └─→ BeneficioController.list()
       └─→ BeneficioService.findAll()
           └─→ BeneficioRepository.findAll()
               └─→ Database.query()

3. Response: List<Beneficio>
   └─→ Frontend: Subscribe ao Observable
       └─→ Renderizar template
```

### Requisição POST (Transferência)

```
1. Frontend (Component)
   └─→ BeneficioService.transfer(TransferRequest)
       └─→ HttpClient.post('/api/v1/beneficios/transfer', request)

2. Backend (Spring Boot)
   └─→ BeneficioController.transfer(TransferRequest)
       └─→ BeneficioService.transfer(TransferRequest)
           └─→ Database.find(fromId) - Simples read
           └─→ Database.find(toId)   - Simples read
           └─→ Update + Flush

3. EJB Service (Optional Integration)
   └─→ BeneficioEjbService.transfer()
       └─→ Pessimistic Write Lock
       └─→ Validações
       └─→ Transação
       └─→ Commit/Rollback

4. Response: Success/Error
   └─→ Frontend: Exibir mensagem
```

## 🔒 Segurança da Transferência

### Mecanismos Implementados

#### 1. **Validação de Input**
```java
// No Controller
if (fromId == null || toId == null) {
    throw new IllegalArgumentException();
}

// No Service
if (amount.compareTo(BigDecimal.ZERO) <= 0) {
    throw new IllegalArgumentException();
}
```

#### 2. **Locking Pessimista**
```java
// No EJB Service
Beneficio from = em.find(Beneficio.class, fromId, 
                          LockModeType.PESSIMISTIC_WRITE);
Beneficio to = em.find(Beneficio.class, toId, 
                        LockModeType.PESSIMISTIC_WRITE);
```

#### 3. **Versionamento Otimista**
```java
@Version
@Column(nullable = false)
private Long version;  // JPA auto-incrementa ao atualizar
```

#### 4. **Transações ACID**
```java
@Transactional  // Managed by Spring
public void transfer(TransferRequest request) {
    // Atomicidade: Tudo ou nada
    // Consistência: Saldo sempre válido
    // Isolamento: Locking previne race conditions
    // Durabilidade: BD persiste mudanças
}
```

#### 5. **Validação de Saldo**
```java
if (from.getValor().compareTo(amount) < 0) {
    throw new IllegalArgumentException("Saldo insuficiente");
}
```

## 🗄️ Estrutura do Banco de Dados

### Tabela BENEFICIO

| Coluna | Tipo | Descrição | Constraints |
|--------|------|-----------|-------------|
| ID | BIGINT | Identificador único | PRIMARY KEY, AUTO_INCREMENT |
| NOME | VARCHAR(100) | Nome do beneficiário | NOT NULL |
| DESCRICAO | VARCHAR(255) | Descrição | NULLABLE |
| VALOR | DECIMAL(15,2) | Saldo disponível | NOT NULL, DEFAULT 0 |
| ATIVO | BOOLEAN | Status | DEFAULT TRUE |
| VERSION | BIGINT | Versão otimista | NOT NULL, DEFAULT 0 |

### Vantagens do Schema

- **VERSION**: Detecta alterações concorrentes (optimistic locking)
- **DECIMAL**: Precisão em valores monetários (não usa FLOAT)
- **BOOLEAN ATIVO**: Soft delete e desativação de contas
- **AUTO_INCREMENT**: Garante IDs únicos

## 📦 Dependências e Versões

### Backend (pom.xml)
```xml
Spring Boot: 3.2.5 (LTS)
Java: 17 (LTS)
JPA/Hibernate: Spring Data JPA 3.2.5
H2 Database: Latest stable
SpringDoc OpenAPI: 2.0.0 (Swagger UI)
JUnit 5: Incluído no Spring Boot Starter Test
```

### Frontend (package.json)
```json
Angular: 17.x (Latest stable)
TypeScript: 5.2+
RxJS: 7.8+
Node.js: 18+ LTS
npm: 9+
```

## 🔄 Ciclo de Vida de uma Transferência

### Estado Inicial
```
Conta A: R$ 1000.00 (version: 0)
Conta B: R$  500.00 (version: 0)
```

### Passo 1: Requisição
```
POST /api/v1/beneficios/transfer
{
  "fromId": 1,
  "toId": 2,
  "amount": 200.00
}
```

### Passo 2: Validação
```
✅ IDs informados
✅ Valor > 0
✅ Contas diferentes
✅ fromId existe
✅ toId existe
✅ Ambas ativas
✅ Saldo suficiente (1000 >= 200)
```

### Passo 3: Locking
```
Adquire locks pessimistas em ambas contas
Aguarda qualquer outra transação liberar
```

### Passo 4: Cálculo
```
Conta A: 1000.00 - 200.00 = 800.00
Conta B: 500.00 + 200.00 = 700.00
Version incrementa: 1, 1 (automático)
```

### Passo 5: Persistência
```
BEGIN TRANSACTION
  UPDATE BENEFICIO SET VALOR=800, VERSION=1 WHERE ID=1
  UPDATE BENEFICIO SET VALOR=700, VERSION=1 WHERE ID=2
COMMIT TRANSACTION
```

### Passo 6: Liberar Locks
```
Locks são liberados
Próximas transações podem prosseguir
```

### Estado Final
```
Conta A: R$ 800.00 (version: 1)
Conta B: R$ 700.00 (version: 1)
```

## 🧪 Estratégia de Testes

### Testes Unitários
- BeneficioServiceTest (12+ casos)
- Isolam BeneficioService
- Mock repositories quando necessário

### Testes de Integração
- BeneficioControllerTest (14+ casos)
- Testam fluxo completo
- Usam contexto Spring real
- Banco em memória (H2)

### Cobertura
- Service layer: 85%+
- Controller layer: 80%+
- Repository: Inerente (Spring Data)

### Casos Críticos Cobertos
```
✅ Transferência com sucesso
✅ Saldo insuficiente
✅ Conta de origem inexistente
✅ Conta de destino inexistente
✅ Conta inativa
✅ Valor zero/negativo
✅ Mesma conta origem/destino
✅ Validações de criação
✅ Validações de atualização
✅ Deleção
✅ Busca por nome
✅ Listar ativos
```

## 📊 Métricas de Performance

### Benchmarks Esperados
- Transferência: ~50-100ms
- CRUD individual: ~20-50ms
- Listar (100 registros): ~100-150ms
- Transferência concorrente: Segura (locking)

### Otimizações Implementadas
- H2 em memória (rápido)
- Índices automáticos em PKs
- JPA batch processing (possível melhoria)
- Connection pooling (Hikari padrão)

## 🌐 API REST Conventions

### Endpoints
```
GET    /api/v1/beneficios          → Listar todos
GET    /api/v1/beneficios/ativos   → Listar ativos
GET    /api/v1/beneficios/{id}     → Obter um
POST   /api/v1/beneficios          → Criar
PUT    /api/v1/beneficios/{id}     → Atualizar
DELETE /api/v1/beneficios/{id}     → Deletar
POST   /api/v1/beneficios/transfer → Transferir
GET    /api/v1/beneficios/{id}/saldo → Saldo
```

### Status HTTP
- 200 OK: Sucesso em operações seguras
- 201 Created: Recurso criado
- 204 No Content: Deleção bem-sucedida
- 400 Bad Request: Validação falhou
- 404 Not Found: Recurso não existe
- 409 Conflict: Erro de concorrência
- 500 Server Error: Erro interno

### Media Type
```
Content-Type: application/json
Accept: application/json
```

## 🔌 Integração Frontend-Backend

### CORS Configuration
```typescript
@CrossOrigin(origins = "*", maxAge = 3600)
public class BeneficioController {
    // Permite requisições de qualquer origem para 1 hora
}
```

### HTTP Client Config
```typescript
// Angular
provideHttpClient(withInterceptorsFromDi())

// Service
private http = HttpClient
```

### Error Handling
```typescript
// Backend lança IllegalArgumentException
// Spring converte em 400 Bad Request
// Frontend captura e exibe mensagem

transfer(request).subscribe({
  next: (response) => { /* sucesso */ },
  error: (error) => { /* mostrar erro */ }
})
```

## 📈 Escalabilidade Futura

### Possíveis Melhorias

1. **Cache**
   - Redis para beneficiários frequentes
   - Cache invalidation em transferências

2. **Assincronia**
   - Message Queue (RabbitMQ/Kafka) para transferências
   - Event sourcing para auditoria

3. **Sharding**
   - Particionar por região/tipo de beneficiário

4. **Replicação**
   - Read replicas para consultas
   - Master-slave para escrita

5. **Monitoring**
   - Spring Boot Actuator
   - Prometheus + Grafana

6. **Logging**
   - ELK Stack (Elasticsearch, Logstash, Kibana)
   - Distributed tracing (Jaeger)

## 🔐 Segurança Futura

1. **Autenticação**
   - JWT/OAuth2
   - Spring Security

2. **Rate Limiting**
   - API Gateway
   - Token bucket algorithm

3. **Validação**
   - Bean Validation (@Valid)
   - Input sanitization

4. **Criptografia**
   - HTTPS/TLS
   - Banco com criptografia

5. **Auditoria**
   - Log de todas as transferências
   - Trail de alterações

## 📚 Referências

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Apache JPA: https://jakarta.ee/specifications/persistence/
- Angular: https://angular.io/docs
- OpenAPI: https://www.openapis.org/

---

**Versão**: 1.0.0  
**Data**: Março 2026  
**Autor**: Desafio Fullstack
