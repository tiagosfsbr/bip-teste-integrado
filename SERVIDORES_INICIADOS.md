# 🚀 Desafio Fullstack Integrado - Servidores Iniciados!

## ✅ Status dos Servidores

Ambos os servidores estão **rodando com sucesso**!

### 📍 URLs de Acesso

| Serviço | URL | Status |
|---------|-----|--------|
| **Frontend (Angular)** | [http://localhost:4200](http://localhost:4200) | ✅ Ativo |
| **Backend API (Mock)** | [http://localhost:8080](http://localhost:8080) | ✅ Ativo |
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) | ✅ Ativo |

---

## 🎯 Como Testar a Aplicação

### 1. **Acessar a Interface Web**
Abra o navegador em: **[http://localhost:4200](http://localhost:4200)**

A aplicação Angular carregará com:
- ✅ Lista de beneficiários com filtro por nome
- ✅ Funcionalidade de transferência entre contas
- ✅ Saldos em tempo real
- ✅ Validações e mensagens de erro

### 2. **Testar via API REST**

#### Listar todos os beneficiários:
```bash
curl -X GET http://localhost:8080/api/v1/beneficios
```

#### Obter um beneficiário por ID:
```bash
curl -X GET http://localhost:8080/api/v1/beneficios/1
```

#### Criar novo beneficiário:
```bash
curl -X POST http://localhost:8080/api/v1/beneficios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Novo Beneficiário","descricao":"Teste","valor":1500}'
```

#### Realizar transferência:
```bash
curl -X POST http://localhost:8080/api/v1/beneficios/transfer \
  -H "Content-Type: application/json" \
  -d '{"fromId":1,"toId":2,"amount":100}'
```

#### Obter saldo de um beneficiário:
```bash
curl -X GET http://localhost:8080/api/v1/beneficios/1/saldo
```

---

## 📊 Dados Iniciais (já carregados)

| ID | Nome | Saldo Inicial |
|----|----|---|
| 1 | Beneficiário A | R$ 1.000,00 |
| 2 | Beneficiário B | R$ 500,00 |

---

## ⚠️ Notas Importantes

### 🟢 Servidor em Produção
Para começar o servidor **real em Spring Boot** (Java):
```bash
cd backend-module
mvn spring-boot:run
```

### 🔵 Servidor de Desenvolvimento (Atual)
Este é um servidor **mock em Node.js** que simula a API completa. Todos os dados estão **em memória** (não persistem após reiniciar).

### 📝 Documentação
- **README.md** - Documentação completa do projeto
- **docs/ARQUITETURA.md** - Arquitetura de camadas
- **docs/IMPLEMENTACAO.md** - Detalhes da implementação
- **docs/EXECUCAO.md** - Guia de execução

---

## 🧪 Casos de Teste Recomendados

1. **Listar beneficiários** - Deve retornar 2 registros
2. **Criar novo beneficiário** - Nome, descrição e valor
3. **Atualizar beneficiário** - Alterar dados existentes
4. **Transferência de valores** - Entre contas com validação
5. **Filtrar por nome** - Na interface web
6. **Saldos em tempo real** - Verificar atualização após transferência
7. **Validações** - Tentar transferir mais que o saldo (deve rejeitar)
8. **Deletar beneficiário** - Remover registro

---

## 🛠️ Tecnologias Utilizadas

- ✅ **Frontend**: Angular 17, TypeScript, SCSS, Responsive Design
- ✅ **Backend Mock**: Node.js, Express, Swagger
- ✅ **Backend Real**: Spring Boot 3.2, Java 17+, JPA/Hibernate
- ✅ **Database**: H2 (em desenvolvimento), embarcado
- ✅ **Testes**: JUnit 5, Mockito, 26+ test cases
- ✅ **Controle de Versão**: Git com commits estruturados

---

## 📞 Troubleshooting

### Porta 8080 ou 4200 já em uso?
```bash
# Verificar processos (Windows)
netstat -ano | findstr ":8080"
netstat -ano | findstr ":4200"

# Verificar processos (Mac/Linux)
lsof -i :8080
lsof -i :4200
```

### Limpar cache do navegador
- Pressione `Ctrl+Shift+Delete` e limpe o cache
- Ou use `Ctrl+Shift+R` para hard refresh

### Parar os servidores
- Use `Ctrl+C` nos terminais onde estão rodando
- Os servidores encerram gracefully

---

## ✨ Funcionalidades Implementadas

- ✅ CRUD completo de beneficiários
- ✅ Transferências com validação de saldo
- ✅ Transações ACID (Spring Boot)
- ✅ Pessimistic Locking para concorrência
- ✅ Filtros e buscas
- ✅ API REST documentada (Swagger)
- ✅ Interface responsiva (Mobile-first)
- ✅ Tratamento de erros robusto
- ✅ Testes automatizados com bom coverage
- ✅ Documentação completa

---

**Última atualização**: 2026-03-26 18:51
**Versão**: 1.0.0
**Status**: ✅ Pronto para Teste
