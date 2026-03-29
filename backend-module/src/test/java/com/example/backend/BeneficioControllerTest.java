package com.example.backend;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.backend.dto.TransferRequest;
import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de API de Beneficiários")
@SuppressWarnings("all")
public class BeneficioControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private BeneficioRepository beneficioRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private Beneficio beneficio1;
        private Beneficio beneficio2;

        @BeforeEach
        public void setUp() {
                beneficioRepository.deleteAll();

                beneficio1 = new Beneficio("Conta Alpha", "Conta de origem", new BigDecimal("1000.00"));
                beneficio2 = new Beneficio("Conta Beta", "Conta de destino", new BigDecimal("500.00"));

                beneficio1 = beneficioRepository.save(beneficio1);
                beneficio2 = beneficioRepository.save(beneficio2);
        }

        @Test
        @DisplayName("GET /api/v1/beneficios - Listar todos")
        public void testListarTodos() throws Exception {
                mockMvc.perform(get("/api/v1/beneficios"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].nome", is("Conta Alpha")))
                                .andExpect(jsonPath("$[1].nome", is("Conta Beta")));
        }

        @Test
        @DisplayName("GET /api/v1/beneficios?nome=A - Buscar por nome")
        public void testBuscarPorNome() throws Exception {
                mockMvc.perform(get("/api/v1/beneficios").param("nome", "Alpha"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].nome", is("Conta Alpha")));
        }

        @Test
        @DisplayName("GET /api/v1/beneficios/ativos - Listar ativos")
        public void testListarAtivos() throws Exception {
                mockMvc.perform(get("/api/v1/beneficios/ativos"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("GET /api/v1/beneficios/{id} - Buscar por ID")
        public void testBuscarPorId() throws Exception {
                mockMvc.perform(get("/api/v1/beneficios/" + beneficio1.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(beneficio1.getId().intValue())))
                                .andExpect(jsonPath("$.nome", is("Conta Alpha")));
        }

        @Test
        @DisplayName("GET /api/v1/beneficios/999 - ID inexistente")
        public void testBuscarPorIdInexistente() throws Exception {
                mockMvc.perform(get("/api/v1/beneficios/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/v1/beneficios - Criar novo")
        public void testCriar() throws Exception {
                Beneficio novo = new Beneficio("Conta C", "Descrição C", new BigDecimal("750.00"));

                mockMvc.perform(post("/api/v1/beneficios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(novo)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nome", is("Conta C")))
                                .andExpect(jsonPath("$.valor", is(750.00)))
                                .andExpect(jsonPath("$.ativo", is(true)));
        }

        @Test
        @DisplayName("POST /api/v1/beneficios - Falhar com dados inválidos")
        public void testCriarComDadosInvalidos() throws Exception {
                Beneficio novo = new Beneficio("", "Descrição", new BigDecimal("100.00"));

                mockMvc.perform(post("/api/v1/beneficios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(novo)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PUT /api/v1/beneficios/{id} - Atualizar")
        public void testAtualizar() throws Exception {
                beneficio1.setNome("Conta Alpha Modificada");
                beneficio1.setValor(new BigDecimal("2000.00"));

                mockMvc.perform(put("/api/v1/beneficios/" + beneficio1.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beneficio1)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nome", is("Conta Alpha Modificada")))
                                .andExpect(jsonPath("$.valor", is(2000.00)));
        }

        @Test
        @DisplayName("DELETE /api/v1/beneficios/{id} - Deletar")
        public void testDeletar() throws Exception {
                mockMvc.perform(delete("/api/v1/beneficios/" + beneficio1.getId()))
                                .andExpect(status().isNoContent());

                mockMvc.perform(get("/api/v1/beneficios/" + beneficio1.getId()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /api/v1/beneficios/transfer - Transferência com sucesso")
        public void testTransferComSucesso() throws Exception {
                TransferRequest request = new TransferRequest(beneficio1.getId(), beneficio2.getId(),
                                new BigDecimal("200.00"));

                mockMvc.perform(post("/api/v1/beneficios/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", containsString("sucesso")));

                // Verificar saldos atualizados
                mockMvc.perform(get("/api/v1/beneficios/" + beneficio1.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.valor", is(800.00)));

                mockMvc.perform(get("/api/v1/beneficios/" + beneficio2.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.valor", is(700.00)));
        }

        @Test
        @DisplayName("POST /api/v1/beneficios/transfer - Saldo insuficiente")
        public void testTransferSaldoInsuficiente() throws Exception {
                TransferRequest request = new TransferRequest(beneficio1.getId(), beneficio2.getId(),
                                new BigDecimal("2000.00"));

                mockMvc.perform(post("/api/v1/beneficios/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/v1/beneficios/transfer - Mesma conta")
        public void testTransferMesmaConta() throws Exception {
                TransferRequest request = new TransferRequest(beneficio1.getId(), beneficio1.getId(),
                                new BigDecimal("100.00"));

                mockMvc.perform(post("/api/v1/beneficios/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /api/v1/beneficios/{id}/saldo - Obter saldo")
        public void testObterSaldo() throws Exception {
                mockMvc.perform(get("/api/v1/beneficios/" + beneficio1.getId() + "/saldo"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.saldo", is(1000.00)));
        }
}
