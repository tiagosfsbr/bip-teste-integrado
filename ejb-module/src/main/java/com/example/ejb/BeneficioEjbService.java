package com.example.ejb;

import java.math.BigDecimal;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Stateless
@Transactional
@SuppressWarnings("all")
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Transferência entre beneficiários com validações completas e locking
     * otimista.
     *
     * @param fromId ID do beneficiário de origem
     * @param toId   ID do beneficiário de destino
     * @param amount Valor da transferência
     * @throws IllegalArgumentException se validações falhem
     * @throws OptimisticLockException  se houver conflito de versão (lost update)
     */
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // Validações de entrada
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("IDs de origem e destino devem ser informados");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }

        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta");
        }

        // Buscar registros com PESSIMISTIC_WRITE lock para evitar lost update
        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        Beneficio to = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);

        // Validações de existência
        if (from == null) {
            throw new IllegalArgumentException("Beneficiário de origem (ID: " + fromId + ") não encontrado");
        }

        if (to == null) {
            throw new IllegalArgumentException("Beneficiário de destino (ID: " + toId + ") não encontrado");
        }

        // Validações de estado
        if (!from.getAtivo()) {
            throw new IllegalArgumentException("Conta de origem está inativa");
        }

        if (!to.getAtivo()) {
            throw new IllegalArgumentException("Conta de destino está inativa");
        }

        // Validação de saldo
        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente. Saldo disponível: " + from.getValor() +
                            ", Valor solicitado: " + amount);
        }

        // Realizar a transferência
        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        // Merge com versionamento otimista (JPA detectará conflitos)
        em.merge(from);
        em.merge(to);

        // Flush para garantir que as mudanças serão persistidas antes do commit
        em.flush();
    }

    /**
     * Obter beneficiário pelo ID
     */
    public Beneficio find(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        Beneficio beneficio = em.find(Beneficio.class, id);
        if (beneficio == null) {
            throw new IllegalArgumentException("Beneficiário com ID " + id + " não encontrado");
        }
        return beneficio;
    }

    /**
     * Criar novo beneficiário
     */
    public Beneficio create(Beneficio beneficio) {
        if (beneficio == null) {
            throw new IllegalArgumentException("Beneficiário não pode ser nulo");
        }
        if (beneficio.getNome() == null || beneficio.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do beneficiário é obrigatório");
        }
        if (beneficio.getValor() == null || beneficio.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor deve ser maior ou igual a zero");
        }

        beneficio.setAtivo(true);
        beneficio.setVersion(0L);
        em.persist(beneficio);
        em.flush();
        return beneficio;
    }

    /**
     * Atualizar beneficiário existente
     */
    public Beneficio update(Beneficio beneficio) {
        if (beneficio == null || beneficio.getId() == null) {
            throw new IllegalArgumentException("Beneficiário com ID é obrigatório");
        }
        if (beneficio.getNome() == null || beneficio.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do beneficiário é obrigatório");
        }
        if (beneficio.getValor() == null || beneficio.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor deve ser maior ou igual a zero");
        }

        Beneficio managed = em.merge(beneficio);
        em.flush();
        return managed;
    }

    /**
     * Deletar beneficiário
     */
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo");
        }
        Beneficio beneficio = em.find(Beneficio.class, id);
        if (beneficio != null) {
            em.remove(beneficio);
            em.flush();
        }
    }
}
