package com.example.backend.service;

import com.example.backend.dto.TransferRequest;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
// @Transactional garante que todas as operações do serviço ocorram dentro de uma transação
// Se qualquer exceção ocorrer, toda a transação é feita rollback (desfazer alterações)
// Essencial para manter consistência de dados em operações críticas como transferências
@Transactional
public class BeneficioService {

    @Autowired
    private BeneficioRepository beneficioRepository;

    /**
     * Buscar todos os beneficiários
     * @return Lista com todos os beneficiários cadastrados
     */
    public List<Beneficio> findAll() {
        // Delega para o repositório que executa: SELECT * FROM beneficio
        return beneficioRepository.findAll();
    }

    /**
     * Buscar beneficiário ativo
     * @return Lista de beneficiários com status ativo = true
     */
    public List<Beneficio> findAllActive() {
        // Busca apenas beneficiários com ativo = true
        // Equivalente a: SELECT * FROM beneficio WHERE ativo = true
        return beneficioRepository.findByAtivo(true);
    }

    /**
     * Buscar beneficiário por ID
     * @param id ID do beneficiário
     * @return Optional contendo o beneficiário se encontrado
     * @throws IllegalArgumentException Se ID é nulo
     */
    public Optional<Beneficio> findById(Long id) {
        // Valida se ID é nulo para evitar erro no banco
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        // Busca e retorna Optional (pode estar vazio se não encontrado)
        return beneficioRepository.findById(id);
    }

    /**
     * Buscar beneficiário por nome com busca parcial (case-insensitive)
     * @param nome Parte ou nome completo do beneficiário
     * @return Lista de beneficiários que contém o nome informado
     */
    public List<Beneficio> findByNome(String nome) {
        // Se nome é nulo ou vazio, retorna todos os beneficiários
        if (nome == null || nome.trim().isEmpty()) {
            return beneficioRepository.findAll();
        }
        // Executa busca LIKE case-insensitive: nome ILIKE '%texto%'
        return beneficioRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Criar novo beneficiário com validações
     * @param beneficio Objeto com nome, descricao e valor
     * @return Beneficiário criado com ID gerado
     * @throws IllegalArgumentException Se dados forem inválidos
     */
    public Beneficio create(Beneficio beneficio) {
        // Valida se objeto não é nulo
        if (beneficio == null) {
            throw new IllegalArgumentException("Beneficiário não pode ser nulo");
        }
        
        // Valida se nome foi informado e não está vazio
        if (beneficio.getNome() == null || beneficio.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do beneficiário é obrigatório");
        }
        
        // Valida se valor é positivo e não nulo
        if (beneficio.getValor() == null || beneficio.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor deve ser maior ou igual a zero");
        }

        // Define status como ativo por padrão para novos beneficiários
        beneficio.setAtivo(true);
        
        // Persiste no banco de dados
        // O ID é gerado automaticamente (IDENTITY strategy)
        return beneficioRepository.save(beneficio);
    }

    /**
     * Atualizar beneficiário existente com validações
     * @param id ID do beneficiário a atualizar
     * @param beneficio Dados atualizados
     * @return Beneficiário atualizado
     * @throws IllegalArgumentException Se ID não existe ou dados são inválidos
     */
    public Beneficio update(Long id, Beneficio beneficio) {
        // Valida se ID foi informado
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        
        // Valida se objeto não é nulo
        if (beneficio == null) {
            throw new IllegalArgumentException("Beneficiário não pode ser nulo");
        }
        
        // Valida se nome foi informado
        if (beneficio.getNome() == null || beneficio.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do beneficiário é obrigatório");
        }
        
        // Valida se valor é positivo
        if (beneficio.getValor() == null || beneficio.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor deve ser maior ou igual a zero");
        }

        // Busca beneficiário existente no banco
        Optional<Beneficio> existing = beneficioRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Beneficiário com ID " + id + " não encontrado");
        }

        // Obtém a entidade gerenciada do JPA (com estado sincronizado com DB)
        Beneficio managed = existing.get();
        
        // Atualiza apenas os campos que vieram na requisição
        managed.setNome(beneficio.getNome());
        managed.setDescricao(beneficio.getDescricao());
        managed.setValor(beneficio.getValor());
        
        // Se ativo for nulo, mantém valor anterior; senão usa novo valor
        managed.setAtivo(beneficio.getAtivo() != null ? beneficio.getAtivo() : true);

        // Persiste mudanças no banco de dados
        // @Transactional garante commit, ou rollback em erro
        return beneficioRepository.save(managed);
    }

    /**
     * Deletar beneficiário
     * @param id ID do beneficiário a deletar
     * @throws IllegalArgumentException Se ID não existe
     */
    public void delete(Long id) {
        // Valida se ID foi informado
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }

        // Busca beneficiário existente antes de deletar
        Optional<Beneficio> existing = beneficioRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Beneficiário com ID " + id + " não encontrado");
        }

        // Remove do banco de dados
        // @Transactional garante que mudança é persistida
        beneficioRepository.deleteById(id);
    }

    /**
     * Realizar transferência entre beneficiários com pessimistic locking
     * 
     * MECANISMO DE SEGURANÇA:
     * 1. Pessimistic Locking (WRITE): Bloqueia registros no banco de dados
     * 2. Validações de entrada: Verifica parâmetros antes de prosseguir
     * 3. Validações de estado: Confirma que contas estão ativas
     * 4. Validação de saldo: Garante que conta de origem tem saldo suficiente
     * 5. @Transactional: Rollback automático em caso de erro
     * 
     * @param transferRequest Contém: fromId (origem), toId (destino), amount (valor)
     * @throws IllegalArgumentException Se validações falharem
     */
    public void transfer(TransferRequest transferRequest) {
        // ========== PASSO 1: VALIDAR REQUISIÇÃO ==========
        if (transferRequest == null) {
            throw new IllegalArgumentException("Solicitação de transferência não pode ser nula");
        }

        // Extrai parâmetros da requisição
        Long fromId = transferRequest.getFromId();
        Long toId = transferRequest.getToId();
        BigDecimal amount = transferRequest.getAmount();

        // ========== PASSO 2: VALIDAÇÕES DE ENTRADA ==========
        // Verifica se IDs foram informados (null check)
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("IDs de origem e destino devem ser informados");
        }

        // Verifica se amount é positivo (evita transferências zeradas ou negativas)
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }

        // Verifica se está tentando transferir para a mesma conta (lógica de negócio)
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta");
        }

        // ========== PASSO 3: ADQUIRIR LOCKS PESSIMISTAS ==========
        // PESSIMISTIC_WRITE: Bloqueia o registro para leitura E escrita
        // - Outros processos NÃO conseguem ler enquanto este estiver processando
        // - Evita race condition: dois processos alterarem saldo simultaneamente
        // - Lock é liberado ao final da transação (COMMITS ou ROLLBACK)
        Optional<Beneficio> fromOpt = beneficioRepository.findByIdWithLock(fromId);
        Optional<Beneficio> toOpt = beneficioRepository.findByIdWithLock(toId);

        // ========== PASSO 4: VALIDAR EXISTÊNCIA DAS CONTAS ==========
        // Verifica se conta de origem existe
        if (fromOpt.isEmpty()) {
            throw new IllegalArgumentException("Beneficiário de origem (ID: " + fromId + ") não encontrado");
        }

        // Verifica se conta de destino existe
        if (toOpt.isEmpty()) {
            throw new IllegalArgumentException("Beneficiário de destino (ID: " + toId + ") não encontrado");
        }

        // Extrai objetos do Optional (safe porque já validamos acima)
        Beneficio from = fromOpt.get();
        Beneficio to = toOpt.get();

        // ========== PASSO 5: VALIDAR ESTADO DAS CONTAS ==========
        // Conta de origem deve estar ativa (não pode transferir de conta inativa)
        if (!from.getAtivo()) {
            throw new IllegalArgumentException("Conta de origem está inativa");
        }

        // Conta de destino deve estar ativa (não pode transferir para conta inativa)
        if (!to.getAtivo()) {
            throw new IllegalArgumentException("Conta de destino está inativa");
        }

        // ========== PASSO 6: VALIDAR SALDO SUFICIENTE ==========
        // Compara saldo (from.getValor()) com valor solicitado (amount)
        // compareTo() retorna: -1 (menor), 0 (igual), 1 (maior)
        // Se compareTo < 0, significa saldo é menor que o valor solicitado
        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente. Saldo disponível: " + from.getValor() +
                    ", Valor solicitado: " + amount
            );
        }

        // ========== PASSO 7: EXECUTAR TRANSFERÊNCIA ==========
        // LOCK PESSIMISTA AINDA ATIVO: nenhuma outra transação consegue interferir
        
        // Subtrai valor da conta de origem
        // Cria novo BigDecimal com resultado da operação (BigDecimal é imutável)
        from.setValor(from.getValor().subtract(amount));
        
        // Adiciona valor à conta de destino
        to.setValor(to.getValor().add(amount));

        // ========== PASSO 8: PERSISTIR MUDANÇAS ==========
        // Save ocorre dentro da transação @Transactional
        // Qualquer erro aqui dispara exceção, causando rollback automático
        // Locks pessimistas são libertos ao final da transação
        beneficioRepository.save(from);
        beneficioRepository.save(to);
        
        // Se chegarmos aqui com sucesso:
        // - Transação commit ocorre automaticamente
        // - Mudanças são persistidas no banco de dados
        // - Locks são libertos
    }

    /**
     * Obter saldo de um beneficiário
     * @param id ID do beneficiário
     * @return Saldo atual (BigDecimal)
     * @throws IllegalArgumentException Se ID é nulo ou não encontrado
     */
    public BigDecimal getBalance(Long id) {
        // Valida se ID foi informado
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }

        // Busca beneficiário no banco
        Optional<Beneficio> beneficio = beneficioRepository.findById(id);
        
        // Verifica se existe
        if (beneficio.isEmpty()) {
            throw new IllegalArgumentException("Beneficiário com ID " + id + " não encontrado");
        }

        // Retorna apenas o saldo (valor) do beneficiário
        return beneficio.get().getValor();
    }
}
