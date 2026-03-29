const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');

const app = express();
const PORT = 8080;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// Dados em memória (simulando banco de dados)
let beneficiarios = [
  { id: 1, nome: 'Beneficio A', descricao: 'Descrição A', valor: 1000.00, ativo: true, version: 0 },
  { id: 2, nome: 'Beneficio B', descricao: 'Descrição B', valor: 500.00, ativo: true, version: 0 }
];

let nextId = 3;

// Swagger Documentation
const swaggerUi = require('swagger-ui-express');
const swaggerDocument = {
  swagger: "2.0",
  info: {
    title: "API Desafio Fullstack Integrado",
    version: "1.0.0",
    description: "API para gerenciamento de beneficiários com transferências seguras"
  },
  basePath: "/api/v1",
  paths: {
    "/beneficios": {
      get: {
        description: "Listar todos os beneficiários",
        parameters: [
          { name: "nome", in: "query", type: "string", description: "Filtro por nome" }
        ],
        responses: {
          200: { description: "Lista de beneficiários" }
        }
      },
      post: {
        description: "Criar novo beneficiário",
        parameters: [
          { name: "body", in: "body", schema: { $ref: "#/definitions/Beneficio" } }
        ],
        responses: {
          201: { description: "Beneficiário criado" }
        }
      }
    },
    "/beneficios/{id}": {
      get: {
        description: "Obter beneficiário por ID",
        parameters: [
          { name: "id", in: "path", required: true, type: "integer" }
        ],
        responses: {
          200: { description: "Beneficiário encontrado" },
          404: { description: "Beneficiário não encontrado" }
        }
      },
      put: {
        description: "Atualizar beneficiário",
        parameters: [
          { name: "id", in: "path", required: true, type: "integer" },
          { name: "body", in: "body", schema: { $ref: "#/definitions/Beneficio" } }
        ],
        responses: {
          200: { description: "Beneficiário atualizado" }
        }
      },
      delete: {
        description: "Deletar beneficiário",
        parameters: [
          { name: "id", in: "path", required: true, type: "integer" }
        ],
        responses: {
          204: { description: "Beneficiário deletado" }
        }
      }
    },
    "/beneficios/transfer": {
      post: {
        description: "Realizar transferência entre beneficiários",
        parameters: [
          { name: "body", in: "body", schema: { $ref: "#/definitions/TransferRequest" } }
        ],
        responses: {
          200: { description: "Transferência realizada com sucesso" },
          400: { description: "Erro na transferência" }
        }
      }
    },
    "/beneficios/{id}/saldo": {
      get: {
        description: "Obter saldo de um beneficiário",
        parameters: [
          { name: "id", in: "path", required: true, type: "integer" }
        ],
        responses: {
          200: { description: "Saldo obtido" }
        }
      }
    }
  },
  definitions: {
    Beneficio: {
      type: "object",
      properties: {
        id: { type: "integer" },
        nome: { type: "string" },
        descricao: { type: "string" },
        valor: { type: "number" },
        ativo: { type: "boolean" },
        version: { type: "integer" }
      }
    },
    TransferRequest: {
      type: "object",
      properties: {
        fromId: { type: "integer" },
        toId: { type: "integer" },
        amount: { type: "number" }
      }
    }
  }
};

app.use('/swagger-ui.html', swaggerUi.serve, swaggerUi.setup(swaggerDocument));

// === ENDPOINTS ===

// GET /beneficios - Listar todos
app.get('/api/v1/beneficios', (req, res) => {
  const { nome } = req.query;
  
  let result = beneficiarios;
  if (nome) {
    result = result.filter(b => b.nome.toLowerCase().includes(nome.toLowerCase()));
  }
  
  res.json(result);
});

// GET /beneficios/ativos - Listar apenas ativos
app.get('/api/v1/beneficios/ativos', (req, res) => {
  const ativos = beneficiarios.filter(b => b.ativo);
  res.json(ativos);
});

// GET /beneficios/:id - Obter por ID
app.get('/api/v1/beneficios/:id', (req, res) => {
  const { id } = req.params;
  const beneficio = beneficiarios.find(b => b.id === parseInt(id));
  
  if (!beneficio) {
    return res.status(404).json({ error: 'Beneficiário não encontrado' });
  }
  
  res.json(beneficio);
});

// POST /beneficios - Criar novo
app.post('/api/v1/beneficios', (req, res) => {
  const { nome, descricao, valor } = req.body;
  
  if (!nome || !nome.trim()) {
    return res.status(400).json({ error: 'Nome do beneficiário é obrigatório' });
  }
  
  if (valor === undefined || valor < 0) {
    return res.status(400).json({ error: 'Valor deve ser maior ou igual a zero' });
  }
  
  const novo = {
    id: nextId,
    nome,
    descricao,
    valor: parseFloat(valor),
    ativo: true,
    version: 0
  };
  
  beneficiarios.push(novo);
  nextId++;
  
  res.status(201).json(novo);
});

// PUT /beneficios/:id - Atualizar
app.put('/api/v1/beneficios/:id', (req, res) => {
  const { id } = req.params;
  const { nome, descricao, valor, ativo } = req.body;
  
  const beneficio = beneficiarios.find(b => b.id === parseInt(id));
  
  if (!beneficio) {
    return res.status(404).json({ error: 'Beneficiário não encontrado' });
  }
  
  if (nome && !nome.trim()) {
    return res.status(400).json({ error: 'Nome não pode ser vazio' });
  }
  
  if (valor !== undefined && valor < 0) {
    return res.status(400).json({ error: 'Valor deve ser maior ou igual a zero' });
  }
  
  if (nome) beneficio.nome = nome;
  if (descricao !== undefined) beneficio.descricao = descricao;
  if (valor !== undefined) beneficio.valor = parseFloat(valor);
  if (ativo !== undefined) beneficio.ativo = ativo;
  beneficio.version++;
  
  res.json(beneficio);
});

// DELETE /beneficios/:id - Deletar
app.delete('/api/v1/beneficios/:id', (req, res) => {
  const { id } = req.params;
  const index = beneficiarios.findIndex(b => b.id === parseInt(id));
  
  if (index === -1) {
    return res.status(404).json({ error: 'Beneficiário não encontrado' });
  }
  
  beneficiarios.splice(index, 1);
  res.status(204).send();
});

// POST /beneficios/transfer - Transferência
app.post('/api/v1/beneficios/transfer', (req, res) => {
  const { fromId, toId, amount } = req.body;
  
  // Validações
  if (!fromId || !toId) {
    return res.status(400).json({ error: 'IDs de origem e destino devem ser informados' });
  }
  
  if (!amount || amount <= 0) {
    return res.status(400).json({ error: 'Valor da transferência deve ser maior que zero' });
  }
  
  if (fromId === toId) {
    return res.status(400).json({ error: 'Não é possível transferir para a mesma conta' });
  }
  
  const from = beneficiarios.find(b => b.id === parseInt(fromId));
  const to = beneficiarios.find(b => b.id === parseInt(toId));
  
  if (!from) {
    return res.status(400).json({ error: 'Beneficiário de origem (ID: ' + fromId + ') não encontrado' });
  }
  
  if (!to) {
    return res.status(400).json({ error: 'Beneficiário de destino (ID: ' + toId + ') não encontrado' });
  }
  
  if (!from.ativo) {
    return res.status(400).json({ error: 'Conta de origem está inativa' });
  }
  
  if (!to.ativo) {
    return res.status(400).json({ error: 'Conta de destino está inativa' });
  }
  
  if (from.valor < amount) {
    return res.status(400).json({ error: 'Saldo insuficiente. Saldo disponível: ' + from.valor + ', Valor solicitado: ' + amount });
  }
  
  // Realizar transferência
  from.valor -= amount;
  to.valor += amount;
  from.version++;
  to.version++;
  
  res.json({ message: 'Transferência realizada com sucesso' });
});

// GET /beneficios/:id/saldo - Obter saldo
app.get('/api/v1/beneficios/:id/saldo', (req, res) => {
  const { id } = req.params;
  const beneficio = beneficiarios.find(b => b.id === parseInt(id));
  
  if (!beneficio) {
    return res.status(404).json({ error: 'Beneficiário não encontrado' });
  }
  
  res.json({ saldo: beneficio.valor });
});

// Health check
app.get('/api/v1/health', (req, res) => {
  res.json({ status: 'UP', message: 'API Desafio Fullstack Integrado' });
});

// Iniciar servidor
app.listen(PORT, () => {
  console.log(`
═══════════════════════════════════════════════════════════════════════════════
✅ API Desafio Fullstack Integrado - SIMULADO (Node.js)
═══════════════════════════════════════════════════════════════════════════════

🚀 Servidor iniciado com sucesso!
📍 URL: http://localhost:${PORT}

📚 Documentação:
   ✨ Swagger UI: http://localhost:${PORT}/swagger-ui.html
   📖 API Docs: http://localhost:${PORT}/v3/api-docs

🔗 Endpoints Disponíveis:
   ✅ GET    /api/v1/beneficios              - Listar todos
   ✅ GET    /api/v1/beneficios/ativos       - Listar ativos
   ✅ GET    /api/v1/beneficios/{id}         - Obter por ID
   ✅ POST   /api/v1/beneficios              - Criar novo
   ✅ PUT    /api/v1/beneficios/{id}         - Atualizar
   ✅ DELETE /api/v1/beneficios/{id}         - Deletar
   ✅ POST   /api/v1/beneficios/transfer     - Transferência
   ✅ GET    /api/v1/beneficios/{id}/saldo   - Obter saldo

📊 Dados Iniciais:
   • ID 1: Beneficiário A - R$ 1.000,00
   • ID 2: Beneficiário B - R$ 500,00

⚠️  NOTA: Este é um servidor SIMULADO em Node.js para testes rápidos.
   O servidor REAL em Spring Boot pode ser executado com:
   cd backend-module && mvn spring-boot:run

═══════════════════════════════════════════════════════════════════════════════
  Pressione Ctrl+C para parar o servidor...
═══════════════════════════════════════════════════════════════════════════════
  `);
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\n\n🛑 Servidor finalizado!');
  process.exit(0);
});
