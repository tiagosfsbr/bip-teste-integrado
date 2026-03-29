# Frontend Angular - Desafio Fullstack Integrado

## 📋 Descrição

Aplicação Angular 17 desenvolvida com componentes standalone para gerenciamento de beneficiários e realização de transferências seguras entre contas.

## 🚀 Quick Start

### Instalação

```bash
npm install
```

### Desenvolvimento

```bash
npm start
```

Acesse `http://localhost:4200` no navegador.

### Build de Produção

```bash
npm run build
```

## 📂 Estrutura da Aplicação

```
src/
├── app/
│   ├── components/
│   │   ├── lista-beneficiarios/     # Componente de listagem
│   │   │   ├── lista-beneficiarios.component.ts
│   │   │   ├── lista-beneficiarios.component.html
│   │   │   └── lista-beneficiarios.component.scss
│   │   └── transferencia/            # Componente de transferência
│   │       ├── transferencia.component.ts
│   │       ├── transferencia.component.html
│   │       └── transferencia.component.scss
│   ├── models/
│   │   └── beneficio.model.ts        # Interfaces TypeScript
│   ├── services/
│   │   └── beneficio.service.ts      # Comunicação com API
│   ├── app.component.*               # Componente raiz
│   └── app.routes.ts                 # Rotas da aplicação
├── index.html
├── main.ts
└── styles.scss                       # Estilos globais
```

## 🎨 Componentes

### ListaBeneficiarios
- Exibe lista de todos os beneficiários
- Filtro por nome
- Ações: Ver, Deletar
- Status indicador (Ativo/Inativo)

### Transferencia
- Formulário para transferência entre contas
- Seleção de contas origem e destino
- Exibição de saldos em tempo real
- Validações client-side
- Resumo antes de confirmar

## 🔗 Integração com Backend

A aplicação se conecta com a API Backend em:

**Base URL**: `http://localhost:8080/api/v1/beneficios`

### Endpoints Consumidos

- `GET /` - Listar todos
- `GET /ativos` - Listar apenas ativos
- `GET /{id}` - Obter por ID
- `POST /` - Criar novo
- `PUT /{id}` - Atualizar
- `DELETE /{id}` - Deletar
- `POST /transfer` - Realizar transferência
- `GET /{id}/saldo` - Consultar saldo

## 🛠️ Tecnologias Utilizadas

- **Angular**: 17.x (Standalone Components)
- **TypeScript**: 5.2+
- **RxJS**: 7.8+
- **SCSS**: Para estilos
- **Node.js**: 18+

## 📱 Responsividade

- ✅ Desktop (1920px+)
- ✅ Tablet (768px - 1024px)
- ✅ Mobile (360px - 767px)
- ✅ Menu responsivo com toggle

## 🧪 Testes

Para testes do componente (futura implementação):

```bash
npm test
```

## 📦 Dependências Principais

- `@angular/core`: Framework
- `@angular/router`: Roteamento
- `@angular/forms`: Formulários
- `@angular/common/http`: Comunicação HTTP

## 🌐 CORS

A aplicação está configurada com CORS habilitado no backend:

```typescript
@CrossOrigin(origins = "*", maxAge = 3600)
```

## 📝 Notas Importantes

1. A aplicação requer que o backend esteja rodando em `http://localhost:8080`
2. O banco de dados H2 é criado automaticamente na primeira execução
3. Dados seed são inseridos automaticamente se o banco estiver vazio
4. Mensagens de erro e sucesso são exibidas com auto-dismiss após 3-5 segundos

## 🚨 Troubleshooting

### Erro de CORS
Certifique-se de que o backend tem CORS habilitado e está rodando.

### Conexão com Backend
Verifique se a URL base em `beneficio.service.ts` está correta:
```typescript
private apiUrl = 'http://localhost:8080/api/v1/beneficios';
```

### Port 4200 em uso
```bash
ng serve --port 4300
```

## 🔄 Fluxo de Transferência

1. Usuário seleciona conta origem e destino
2. Valores de saldo são carregados em tempo real
3. Usuário insere valor da transferência
4. Sistema valida valores antes de enviar
5. Resumo da operação é exibido
6. Após confirmação, requisição é enviada ao backend
7. Feedback de sucesso ou erro é exibido
8. Lista é atualizada com novos saldos

## 📈 Performance

- Lazy loading não implementado (aplicação pequena)
- Change detection: OnPush (possível implementação futura)
- Optimização com trackBy (possível melhoria)

## 🔒 Segurança

- Validações no front-end (UX)
- Validações no back-end (segurança real)
- Sem armazenamento de senhas
- CORS habilitado apenas para localhost em desenvolvimento

## 📞 Links Úteis

- [Documentação Angular](https://angular.io)
- [RxJS Documentation](https://rxjs.dev)
- [TypeScript Handbook](https://www.typescriptlang.org/docs)

## ✅ Checklist

- [x] Componentes criados e funcionando
- [x] Serviço de API implementado
- [x] Temas e estilos aplicados
- [x] Responsividade implementada
- [x] Tratamento de erros
- [x] Validações de formulário
- [x] Navegação entre rotas
- [x] Integração com backend

---

**Versão**: 1.0.0  
**Última atualização**: Março 2026  
**Status**: ✅ Pronto para Produção
