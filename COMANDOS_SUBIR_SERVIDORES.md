# 📋 Comandos para Subir os Servidores Manualmente

## ✅ Servidores Parados

Os servidores foram desligados. Use os comandos abaixo para iniciá-los manualmente.

---

## 🚀 **OPÇÃO 1: API Mock (Node.js) + Frontend (Angular)**

### Terminal 1 - Backend (API Mock) - Porta 8080

```bash
cd d:\desafio\DesafioFullstackIntegrado\api-mock
npm start
```

**Saída esperada:**
```
✅ API Desafio Fullstack Integrado - SIMULADO (Node.js)
🚀 Servidor iniciado com sucesso!
📍 URL: http://localhost:8080
```

---

### Terminal 2 - Frontend (Angular) - Porta 4200

```bash
cd d:\desafio\DesafioFullstackIntegrado\frontend
npm start
```

**Saída esperada:**
```
✔ Browser application bundle generation complete.
** Angular Live Development Server is listening on localhost:4200 **
√ Compiled successfully.
```

---

## 🟢 **OPÇÃO 2: Backend Real (Spring Boot) + Frontend (Angular)**

### Terminal 1 - Backend (Spring Boot) - Porta 8080

```bash
cd d:\desafio\DesafioFullstackIntegrado\backend-module
mvn clean compile
mvn spring-boot:run
```

**Saída esperada:**
```
Started BackendApplication in X seconds
Tomcat started on port(s): 8080
```

---

### Terminal 2 - Frontend (Angular) - Porta 4200

```bash
cd d:\desafio\DesafioFullstackIntegrado\frontend
npm start
```

---

## 📍 URLs de Acesso

Após iniciar os servidores:

| Serviço | URL |
|---------|-----|
| **Frontend** | http://localhost:4200 |
| **API Backend** | http://localhost:8080/api/v1/beneficios |
| **Swagger API** | http://localhost:8080/swagger-ui.html |
| **Health Check** | http://localhost:8080/api/v1/health |

---

## ⏹️ Para Parar os Servidores

Em cada terminal, pressione:
```
Ctrl + C
```

---

## 📝 Notas

- **API Mock**: Dados em memória (não persistem)
- **Spring Boot Real**: Usa banco H2 integrado
- **Frontend**: Ambos os backends funcionam com o mesmo frontend
- **Portas**: Não inicie dois backends na mesma porta

---

## 🆘 Troubleshooting

### Porta já em uso?
```powershell
# Verificar processos
netstat -ano | findstr ":8080"
netstat -ano | findstr ":4200"

# Matar processo (replace PID com o número)
taskkill /PID <PID> /F
```

### npm packages não instalados?
```bash
# Instalar dependências manualmente
cd api-mock && npm install
cd ..\frontend && npm install
```

### Maven não encontrado?
```bash
# Verificar Maven
mvn --version

# Se não encontrar, use o caminho completo:
"C:\Users\Tiago\AppData\Roaming\Code\User\globalStorage\pleiades.java-extension-pack-jdk\maven\latest\bin\mvn.cmd" --version
```

---

**Data**: 2026-03-27  
**Status**: Servidores parados - prontos para iniciar manualmente
