package com.example.backend.config;

import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader {

    @Autowired
    private BeneficioRepository beneficioRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        // Verificar se já existem dados
        if (beneficioRepository.count() > 0) {
            return;
        }

        // Inserir dados iniciais
        Beneficio beneficio1 = new Beneficio("Beneficio A", "Descrição A", new BigDecimal("1000.00"));
        Beneficio beneficio2 = new Beneficio("Beneficio B", "Descrição B", new BigDecimal("500.00"));

        beneficioRepository.save(beneficio1);
        beneficioRepository.save(beneficio2);

        System.out.println("Dados iniciais carregados com sucesso!");
    }
}
