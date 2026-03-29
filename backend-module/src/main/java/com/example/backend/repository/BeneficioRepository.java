package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.backend.model.Beneficio;
import jakarta.persistence.LockModeType;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long> {
    List<Beneficio> findByAtivo(Boolean ativo);

    List<Beneficio> findByNomeContainingIgnoreCase(String nome);

    /**
     * Buscar beneficiário com lock pessimista para transferências
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Beneficio b WHERE b.id = ?1")
    Optional<Beneficio> findByIdWithLock(Long id);
}
