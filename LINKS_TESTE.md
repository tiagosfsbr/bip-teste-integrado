## ✅ DESAFIO FULLSTACK INTEGRADO - SERVIDORES ATIVOS

### 📍 Status da Instalação

| Componente | Versão | Status |
|-----------|--------|--------|
| **Java** | OpenJDK 25.0.2 LTS | ✅ OK |
| **Node.js** | v19.0.0 | ✅ OK |
| **npm** | 8.19.2 | ✅ OK |
| **Frontend (Angular)** | 17.x | ✅ Rodando |
| **Backend (API Mock)** | Node.js/Express | ✅ Rodando |

---

## 🔗 LINKS PARA TESTAR

### Interface Web (Clique para abrir no navegador)
**[👉 http://localhost:4200](http://localhost:4200)**

### API Backend
**[👉 http://localhost:8080/api/v1/beneficios](http://localhost:8080/api/v1/beneficios)**

### Documentação Swagger/OpenAPI
**[👉 http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

---

## 🎯 Funcionalidades Disponíveis

✅ **Listar beneficiários** - com filtro por nome  
✅ **Criar novo beneficiário** - Nome, Descrição e Valor  
✅ **Atualizar beneficiário** - Editar dados existentes  
✅ **Deletar beneficiário** - Remova registros  
✅ **Realizar transferências** - Entre contas com validação  
✅ **Consultar saldos** - Em tempo real  
✅ **Validações** - Saldo insuficiente, contas ativas, etc  

---

## 📊 Dados Iniciais (já carregados)

| ID | Nome | Saldo |
|----|------|-------|
| 1 | Beneficiário A | R$ 1.000,00 |
| 2 | Beneficiário B | R$ 500,00 |

---

## 🧪 Exemplos de Teste via API

### 1. Listar todos os beneficiários
```bash
GET http://localhost:8080/api/v1/beneficios
```
**Resposta esperada:** Array com 2 beneficiários

### 2. Obter beneficiário por ID
```bash
GET http://localhost:8080/api/v1/beneficios/1
```
**Resposta esperada:** Dados do Beneficiário A

### 3. Criar novo beneficiário
```bash
POST http://localhost:8080/api/v1/beneficios
Content-Type: application/json

{
  "nome": "Novo Beneficiário",
  "descricao": "Teste",
  "valor": 1500
}
```

### 4. Realizar transferência
```bash
POST http://localhost:8080/api/v1/beneficios/transfer
Content-Type: application/json

{
  "fromId": 1,
  "toId": 2,
  "amount": 100
}
```

### 5. Obter saldo
```bash
GET http://localhost:8080/api/v1/beneficios/1/saldo
```

---

## ⚠️ Notas Importantes

🔵 **Servidor Atual:** Node.js Mock (em memória - dados não persistem)

🟢 **Servidor Real (Spring Boot):** Disponível em:
```bash
cd backend-module
mvn clean compile
mvn spring-boot:run
```

---

## 🛠️ Troubleshooting

### Se portas estiverem ocupadas:
```powershell
# Verificar processos (Windows)
netstat -ano | findstr ":8080"
netstat -ano | findstr ":4200"
```

### Para parar os servidores:
- Pressione `Ctrl+C` nos terminais

### Para limpar cache do navegador:
- Pressione `Ctrl+Shift+Delete` no navegador

---

## 📚 Arquivos da Solução

```
d:\desafio\DesafioFullstackIntegrado\
├── frontend/                  # Angular 17
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   ├── lista-beneficiarios/
│   │   │   │   └── transferencia/
│   │   │   ├── services/
│   │   │   └── pipes/
│   │   └── assets/
│   └── package.json
├── api-mock/                  # Node.js Mock API
│   ├── server.js
│   └── package.json
├── backend-module/            # Spring Boot Real (Maven)
│   ├── src/
│   ├── pom.xml
│   └── target/ (após mvn build)
├── README.md
├── SERVIDORES_INICIADOS.md
└── docs/                      # Documentação
    ├── ARQUITETURA.md
    ├── IMPLEMENTACAO.md
    ├── EXECUCAO.md
    └── SUBMISSAO.md
```

---

**✨ Pronto para testes! Acesse os links acima. ✨**

Última atualização: 2026-03-27 16:30 UTC
