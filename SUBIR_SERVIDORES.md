# 🚀 Comandos para Subir os Servidores

Os servidores foram desligados. Use os comandos abaixo para iniciá-los novamente.

---

## **OPÇÃO 1: API Mock (Node.js) + Frontend (Angular)** ✅ RECOMENDADO

Abra **2 terminais** separados e execute:

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

## **OPÇÃO 2: Backend Real (Spring Boot) + Frontend (Angular)**

Abra **2 terminais** separados e execute:

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

Após iniciar os servidores com **OPÇÃO 1**:

| Serviço | URL |
|---------|-----|
| **Frontend** | http://localhost:4200 |
| **API Backend** | http://localhost:8080/api/v1/beneficios |
| **Health Check** | http://localhost:8080/api/v1/health |

---

## ⏹️ Para Parar os Servidores

Em cada terminal, pressione:
```
Ctrl + C
```

---

## 💡 Dicas Rápidas

- **Porta ocupada?**
  ```powershell
  netstat -ano | findstr ":8080"
  netstat -ano | findstr ":4200"
  ```

- **Limpar cache do navegador?**
  Pressione `Ctrl + Shift + Delete` no navegador

- **Reinstalar dependências?**
  ```bash
  cd api-mock && npm install
  cd ..\frontend && npm install
  ```

---

**Data**: 2026-03-27  
**Status**: Servidores parados - prontos para iniciar manualmente
