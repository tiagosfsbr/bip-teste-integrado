package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.backend.dto.TransferRequest;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;

@SpringBootTest
@DisplayName("Testes de Serviço de Beneficiários")
@SuppressWarnings("all")
public class BeneficioServiceTest {

    @Autowired
    private BeneficioService beneficioService;

    @Autowired
    private BeneficioRepository beneficioRepository;

    private Beneficio beneficio1;
    private Beneficio beneficio2;

    @BeforeEach
    public void setUp() {
        beneficioRepository.deleteAll();

        beneficio1 = new Beneficio("Conta A", "Conta de origem", new BigDecimal("1000.00"));
        beneficio2 = new Beneficio("Conta B", "Conta de destino", new BigDecimal("500.00"));

        beneficio1 = beneficioRepository.save(beneficio1);
        beneficio2 = beneficioRepository.save(beneficio2);
    }

    @Test
    @DisplayName("Criar novo beneficiário com sucesso")
    public void testCreateBeneficio() {
        Beneficio novo = new Beneficio("Conta C", "Conta nova", new BigDecimal("100.00"));
        Beneficio created = beneficioService.create(novo);

        assertNotNull(created.getId());
        assertEquals("Conta C", created.getNome());
        assertTrue(created.getAtivo());
    }

    @Test
    @DisplayName("Falhar ao criar beneficiário com nome vazio")
    public void testCreateBeneficioComNomeVazio() {
        Beneficio novo = new Beneficio("", "Descrição", new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> beneficioService.create(novo));
    }

    @Test
    @DisplayName("Falhar ao criar beneficiário com valor negativo")
    public void testCreateBeneficioComValorNegativo() {
        Beneficio novo = new Beneficio("Conta", "Descrição", new BigDecimal("-100.00"));

        assertThrows(IllegalArgumentException.class, () -> beneficioService.create(novo));
    }

    @Test
    @DisplayName("Buscar beneficiário por ID com sucesso")
    public void testFindById() {
        Optional<Beneficio> resultado = beneficioService.findById(beneficio1.getId());

        assertTrue(resultado.isPresent());
        assertEquals(beneficio1.getId(), resultado.get().getId());
        assertEquals("Conta A", resultado.get().getNome());
    }

    @Test
    @DisplayName("Retornar vazio ao buscar ID inexistente")
    public void testFindByIdNaoExistente() {
        Optional<Beneficio> resultado = beneficioService.findById(9999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Transferência com sucesso")
    public void testTransferComSucesso() {
        BigDecimal valorTransferido = new BigDecimal("200.00");
        BigDecimal saldoOriginalA = beneficio1.getValor();
        BigDecimal saldoOriginalB = beneficio2.getValor();

        TransferRequest request = new TransferRequest(beneficio1.getId(), beneficio2.getId(), valorTransferido);
        beneficioService.transfer(request);

        Optional<Beneficio> contaA = beneficioService.findById(beneficio1.getId());
        Optional<Beneficio> contaB = beneficioService.findById(beneficio2.getId());

        assertTrue(contaA.isPresent());
        assertTrue(contaB.isPresent());

        assertEquals(saldoOriginalA.subtract(valorTransferido), contaA.get().getValor());
        assertEquals(saldoOriginalB.add(valorTransferido), contaB.get().getValor());
    }

    @Test
    @DisplayName("Falhar transferência com saldo insuficiente")
    public void testTransferComSaldoInsuficiente() {
        BigDecimal valorTransferido = new BigDecimal("2000.00");

        TransferRequest request = new TransferRequest(beneficio1.getId(), beneficio2.getId(), valorTransferido);

        assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(request));
    }

    @Test
    @DisplayName("Falhar transferência para mesma conta")
    public void testTransferParaMesmaConta() {
        BigDecimal valorTransferido = new BigDecimal("100.00");

        TransferRequest request = new TransferRequest(beneficio1.getId(), beneficio1.getId(), valorTransferido);

        assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(request));
    }

    @Test
    @DisplayName("Falhar transferência com valor zero ou negativo")
    public void testTransferComValorInvalido() {
        TransferRequest request1 = new TransferRequest(beneficio1.getId(), beneficio2.getId(), BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(request1));

        TransferRequest request2 = new TransferRequest(beneficio1.getId(), beneficio2.getId(),
                new BigDecimal("-100.00"));
        assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(request2));
    }

    @Test
    @DisplayName("Falhar transferência com conta de origem inexistente")
    public void testTransferComContaOrigemInexistente() {
        BigDecimal valorTransferido = new BigDecimal("100.00");

        TransferRequest request = new TransferRequest(9999L, beneficio2.getId(), valorTransferido);

        assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(request));
    }

    @Test
    @DisplayName("Falhar transferência com conta de destino inexistente")
    public void testTransferComContaDestinoInexistente() {
        BigDecimal valorTransferido = new BigDecimal("100.00");

        TransferRequest request = new TransferRequest(beneficio1.getId(), 9999L, valorTransferido);

        assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(request));
    }

    @Test
    @DisplayName("Falhar transferência se conta de origem está inativa")
    public void testTransferComContaOrigemInativa() {
        beneficio1.setAtivo(false);
        beneficioRepository.save(beneficio1);

        BigDecimal valorTransferido = new BigDecimal("100.00");
        TransferRequest request = new TransferRequest(beneficio1.getId(), beneficio2.getId(), valorTransferido);

        assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(request));
    }

    @Test
    @DisplayName("Falhar transferência se conta de destino está inativa")
    public void testTransferComContaDestinoInativa() {
        beneficio2.setAtivo(false);
        beneficioRepository.save(beneficio2);

        BigDecimal valorTransferido = new BigDecimal("100.00");
        TransferRequest request = new TransferRequest(beneficio1.getId(), beneficio2.getId(), valorTransferido);

        assertThrows(IllegalArgumentException.class, () -> beneficioService.transfer(request));
    }

    @Test
    @DisplayName("Obter saldo de beneficiário com sucesso")
    public void testGetBalance() {
        java.math.BigDecimal saldo = beneficioService.getBalance(beneficio1.getId());

        assertEquals(new BigDecimal("1000.00"), saldo);
    }

    @Test
    @DisplayName("Atualizar beneficiário com sucesso")
    public void testUpdateBeneficio() {
        beneficio1.setNome("Conta A Atualizada");
        beneficio1.setValor(new BigDecimal("1500.00"));

        Beneficio updated = beneficioService.update(beneficio1.getId(), beneficio1);

        assertEquals("Conta A Atualizada", updated.getNome());
        assertEquals(new BigDecimal("1500.00"), updated.getValor());
    }

    @Test
    @DisplayName("Deletar beneficiário com sucesso")
    public void testDeleteBeneficio() {
        beneficioService.delete(beneficio1.getId());

        Optional<Beneficio> deletado = beneficioService.findById(beneficio1.getId());
        assertTrue(deletado.isEmpty());
    }

    /**
     * TESTE DE TRANSAÇÕES CONCORRENTES
     * 
     * Simula múltiplas transferências simultâneas para validar:
     * 1. Pessimistic Locking está funcionando
     * 2. Não há race conditions no cálculo de saldo
     * 3. Transações são isoladas corretamente
     * 4. Saldo final é consistente após todas as operações
     * 
     * Cenário: 10 threads fazem transferências simultâneas de um saldo único.
     * Esperado: Todos os 10 transferências bem-sucedidas com saldo consistente
     */
    @Test
    @DisplayName("Validar pessimistic locking com múltiplas transferências concorrentes")
    public void testTransferenciasConcorentesComPessimisticLocking() throws InterruptedException {
        // ===== SETUP: Preparar dados para teste de concorrência =====
        // Limpar dados anteriores
        beneficioRepository.deleteAll();
        
        // Criar conta de origem com R$ 2000 (suficiente para 10 transferências de R$ 100)
        Beneficio contaOrigem = new Beneficio("Conta Origem", "Origem", new BigDecimal("2000.00"));
        contaOrigem = beneficioRepository.save(contaOrigem);
        Long contaOrigemId = contaOrigem.getId();
        
        // Criar 10 contas de destino
        Beneficio[] contasDestino = new Beneficio[10];
        Long[] contasDestinoIds = new Long[10];
        for (int i = 0; i < 10; i++) {
            Beneficio conta = new Beneficio("Conta Destino " + i, "Destino " + i, BigDecimal.ZERO);
            contasDestino[i] = beneficioRepository.save(conta);
            contasDestinoIds[i] = contasDestino[i].getId();
        }
        
        // ===== EXECUÇÃO: Múltiplas threads fazendo transferências =====
        
        // ExecutorService: Pool de threads para executar operações em paralelo
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // CountDownLatch: Sincroniza o início de todas as threads
        // Força que todas as threads comecem no mesmo momento (concorrência real)
        CountDownLatch startLatch = new CountDownLatch(1);
        
        // CountDownLatch: Aguarda conclusão de todas as threads
        CountDownLatch endLatch = new CountDownLatch(10);
        
        // Contadores thread-safe para rastrear execução
        AtomicInteger transferenciasAcertadas = new AtomicInteger(0);
        AtomicInteger erros = new AtomicInteger(0);
        
        // ===== PASSO 1: Disparar 10 threads para transferências simultâneas =====
        for (int i = 0; i < 10; i++) {
            final int index = i;
            final Long destinyId = contasDestinoIds[i];
            
            executor.submit(() -> {
                try {
                    // Aguardar sinal de início
                    // Assim elas começam no mesmo momento (concorrência real)
                    startLatch.await();
                    
                    // Criar requisição de transferência
                    // Cada thread transfere R$ 100 da conta original para uma conta de destino diferente
                    TransferRequest request = new TransferRequest(
                        contaOrigemId,
                        destinyId,
                        new BigDecimal("100.00")
                    );
                    
                    // Executar transferência
                    // AQUI O PESSIMISTIC LOCKING ENTRA EM AÇÃO:
                    // - Primeira thread adquire lock na conta de origem (PESSIMISTIC_WRITE)
                    // - Outras 9 threads ficam aguardando na fila
                    // - Cada uma executa sequencialmente mesmo tendo começado juntas
                    beneficioService.transfer(request);
                    
                    // Registrar sucesso
                    transferenciasAcertadas.incrementAndGet();
                    
                } catch (Exception e) {
                    // Registrar erro
                    erros.incrementAndGet();
                    System.err.println("Erro na thread " + index + ": " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    // Sinalizar que esta thread terminou
                    endLatch.countDown();
                }
            });
        }
        
        // ===== PASSO 2: Dar o sinal de saída para todas as threads =====
        // Libera todas as 10 threads que estão aguardando em startLatch.await()
        startLatch.countDown();
        
        // ===== PASSO 3: Aguardar conclusão de todas as threads =====
        // Esperar até 30 segundos por todas as 10 threads terminarem
        boolean todasTerminaram = endLatch.await(30, TimeUnit.SECONDS);
        
        // Desligar o executor
        executor.shutdown();
        
        // ===== VALIDAÇÕES =====
        
        // Validação 1: Timeout
        assertTrue(todasTerminaram, 
            "Nem todas as threads terminaram a tempo (timeout de 30s)");
        
        // Validação 2: Todas as transferências bem-sucedidas
        assertEquals(10, transferenciasAcertadas.get(), 
            "Todas as 10 transferências devem ter sucesso");
        
        // Validação 3: Sem erros
        assertEquals(0, erros.get(), 
            "Não deve haver erros nas transferências concorrentes");
        
        // ===== PASSO 4: Validar saldo final (CONSISTÊNCIA) =====
        
        // Buscar saldo da conta de origem
        Optional<Beneficio> contaOrigemAtualizada = beneficioService.findById(contaOrigemId);
        assertTrue(contaOrigemAtualizada.isPresent(), 
            "Conta de origem deve existir após transferências");
        
        // Saldo esperado: 2000 - (10 × 100) = 1000
        BigDecimal saldoEsperado = new BigDecimal("1000.00");
        BigDecimal saldoAtual = contaOrigemAtualizada.get().getValor();
        
        assertEquals(saldoEsperado, saldoAtual,
            "Saldo final deve ser consistente após transferências concorrentes. " +
            "Esperado: " + saldoEsperado + ", Atual: " + saldoAtual);
        
        // ===== PASSO 5: Validar que cada conta recebeu exatamente R$ 100 =====
        BigDecimal totalRecebido = BigDecimal.ZERO;
        
        for (int i = 0; i < 10; i++) {
            Optional<Beneficio> contaDestino = beneficioService.findById(contasDestinoIds[i]);
            assertTrue(contaDestino.isPresent(), 
                "Conta destino " + i + " deve existir");
            
            BigDecimal saldoDestino = contaDestino.get().getValor();
            assertEquals(new BigDecimal("100.00"), saldoDestino,
                "Conta " + i + " deve ter recebido R$ 100.00, mas tem R$ " + saldoDestino);
            
            totalRecebido = totalRecebido.add(saldoDestino);
        }
        
        // Validação 6: Total transferido
        assertEquals(new BigDecimal("1000.00"), totalRecebido,
            "Total transferido deve ser R$ 1000.00 (10 × 100)");
        
        // ===== CONCLUSÃO =====
        // Se chegamos aqui, o teste provou que:
        // ✓ Pessimistic locking funcionou corretamente
        // ✓ Não houve race conditions
        // ✓ Transações foram isoladas
        // ✓ Saldo é consistente
    }
}
