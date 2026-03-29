# 📖 Guia de Execução - Desafio Fullstack Integrado

## ✅ Pré-requisitos

Certifique-se de ter instalado:

- **Java 17+**: `java -version`
- **Maven 3.8+**: `mvn -version`
- **Node.js 18+**: `node -v`
- **npm 9+**: `npm -v`

## 🚀 Passos de Execução

### 1️⃣ Preparar Banco de Dados

O banco de dados H2 é criado automaticamente na primeira execução do backend com dados seed.

**Estrutura da tabela:**
```sql
CREATE TABLE BENEFICIO (
  ID BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  NOME VARCHAR(100) NOT NULL,
  DESCRICAO VARCHAR(255),
  VALOR DECIMAL(15,2) NOT NULL,
  ATIVO BOOLEAN DEFAULT TRUE,
  VERSION BIGINT DEFAULT 0
);
```

**Dados iniciais:**
- Beneficiário A: R$ 1000.00
- Beneficiário B: R$ 500.00

### 2️⃣ Executar Backend Spring Boot

```bash
# Navegar para o diretório do backend
cd backend-module

# Limpar builds anteriores
mvn clean

# Compilar o projeto
mvn compile

# Executa testes (opcional)
mvn test

# Construir JAR
mvn package

# Iniciar a aplicação
mvn spring-boot:run
```

**Saída esperada:**
```
[INFO] Starting BackendApplication v0.0.1-SNAPSHOT using Java 17.0.x
[INFO] Application started on port 8080
[INFO] Dados iniciais carregados com sucesso!
```

**A API estará disponível em:**
- Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console` (user: sa, password: vazio)

### 3️⃣ Executar Frontend Angular

**Em outro terminal:**

```bash
# Navegar para o diretório frontend
cd frontend

# Instalar dependências
npm install
# ou
npm ci  # para exatidão em CI/CD

# Iniciar o servidor de desenvolvimento
npm start
```

**Saída esperada:**
```
✔ Compiled successfully.

Application bundle generated successfully.
Application running at http://localhost:4200
```

**O frontend estará disponível em:**
- http://localhost:4200

### 4️⃣ Testar a Aplicação

#### Via Web UI

1. Abrir `http://localhost:4200` no navegador
2. Página "Beneficiários" deve exibir a lista
3. Clicar em "Transferência" para testar transferência
4. Testar formulários

#### Via API (Swagger)

1. Abrir `http://localhost:8080/swagger-ui.html`
2. Expandir endpoints e testar cada um
3. Exemplo de transferência:
   ```json
   {
     "fromId": 1,
     "toId": 2,
     "amount": 100.00
   }
   ```

#### Via cURL

```bash
# Listar beneficiários
curl http://localhost:8080/api/v1/beneficios

# Transferir
curl -X POST http://localhost:8080/api/v1/beneficios/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromId": 1,
    "toId": 2,
    "amount": 100.00
  }'

# Consultar saldo
curl http://localhost:8080/api/v1/beneficios/1/saldo
```

### 5️⃣ Executar Testes

#### Testes Backend

```bash
cd backend-module

# Todos os testes
mvn test

# Teste específico
mvn test -Dtest=BeneficioServiceTest
mvn test -Dtest=BeneficioControllerTest

# Com cobertura
mvn test jacoco:report

# Gerar relatório HTML (target/site/jacoco/index.html)
```

#### Testes Frontend (opcional implementação)

```bash
cd frontend
npm test
```

---

## 🔍 Validações Implementadas

### No Serviço EJB

- ✅ Validação de saldo insuficiente
- ✅ Validação de contas ativas
- ✅ Validação de IDs válidos
- ✅ Validação de valores positivos
- ✅ Locking pessimista para evitar lost update
- ✅ Transação ACID com rollback automático

### No Backend Spring Boot

- ✅ Validação em todos os endpoints
- ✅ Tratamento de exceções
- ✅ Resposta HTTP apropriada
- ✅ Mensagens de erro descritivas

### No Frontend Angular

- ✅ Validação de formulários
- ✅ Exibição de mensagens de erro
- ✅ Feedback visual (loading states)
- ✅ Validações client-side (UX)

---

## 📊 Exemplo de Fluxo Completo

### Cenário: Transferir R$ 200 de Conta A para Conta B

#### 1. Backend

```bash
# Dados iniciais
GET /api/v1/beneficios
# Retorna id do Conta A (1) e Conta B (2)
```

#### 2. Frontend (Transferência)

```
1. Seleciona "Conta A" como origem (id: 1)
2. Seleciona "Conta B" como destino (id: 2)
3. Digita valor: 200.00
4. Clica em "Transferir"
```

#### 3. API Request

```json
POST /api/v1/beneficios/transfer
{
  "fromId": 1,
  "toId": 2,
  "amount": 200.00
}
```

#### 4. Backend Processing

```
1. Valida entrada (OK)
2. Busca Conta A com lock pessimista
3. Busca Conta B com lock pessimista
4. Valida existência (OK)
5. Valida estado (OK)
6. Valida saldo 1000 >= 200 (OK)
7. Conta A: 1000 - 200 = 800
8. Conta B: 500 + 200 = 700
9. Persist e Flush
10. Commit automático
```

#### 5. Frontend Response

```json
200 OK
"Transferência realizada com sucesso"
```

#### 6. Verificação

```bash
GET /api/v1/beneficios/1/saldo
# Retorna: 800.00

GET /api/v1/beneficios/2/saldo
# Retorna: 700.00
```

---

## 🐛 Troubleshooting

### Erro: Port 8080 já em uso

```bash
# Encontrar processo usando port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Matar processo (macOS/Linux)
kill -9 <PID>

# Ou usar porta diferente
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Erro: Port 4200 já em uso

```bash
# Usar porta diferente
ng serve --port 4300
```

### Erro: CORS

Certifique-se de que o backend tem:
```java
@CrossOrigin(origins = "*", maxAge = 3600)
```

### Erro: Conexão com banco de dados

Verifique se a URL do banco está correta em `application.yml`:
```yaml
datasource:
  url: jdbc:h2:mem:testdb
```

### Erro: Maven não encontrado

```bash
export PATH=$PATH:~/tools/maven/bin  # Linux/macOS
set PATH=%PATH%;C:\Maven\bin  # Windows
```

### Erro: Node não encontrado

Reinstale Node.js de https://nodejs.org/

---

## 📈 Performance Esperada

- Backend startup: ~3-5 segundos
- Frontend build: ~10-15 segundos
- Testes backend: ~5-10 segundos
- Transferência latência: ~100ms

---

## ✨ Funcionalidades Disponíveis

- [x] CRUD completo de beneficiários
- [x] Transferência com validação de saldo
- [x] Locking pessimista para concorrência
- [x] Swagger/OpenAPI documentação
- [x] Testes automatizados (26+)
- [x] Frontend responsivo
- [x] Tratamento de erros robusto
- [x] Dados seed automáticos

---

## 🔗 Links Rápidos

| Recurso | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| API Base | http://localhost:8080/api/v1 |
| Swagger | http://localhost:8080/swagger-ui.html |
| H2 Console | http://localhost:8080/h2-console |
| API Docs JSON | http://localhost:8080/v3/api-docs |

---

## 📝 Comandos Quick Reference

```bash
# Backend
mvn clean compile
mvn test
mvn package
mvn spring-boot:run

# Frontend
npm install
npm start
npm run build

# Testing
mvn test -Dtest=BeneficioServiceTest
mvn test -Dtest=BeneficioControllerTest
```

---

## ✅ Checklist de Execução

- [ ] Java 17+ instalado
- [ ] Maven 3.8+ instalado
- [ ] Node.js 18+ instalado
- [ ] Backend compilado e rodando
- [ ] Frontend rodando
- [ ] Swagger acessível
- [ ] Testes passando
- [ ] Listagem funcionando
- [ ] Transferência funcionando
- [ ] Aplicação responsiva

---

**Tempo estimado para setup completo**: 5-10 minutos

**Versão**: 1.0.0  
**Data**: Março 2026
