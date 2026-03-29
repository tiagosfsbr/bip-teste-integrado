package com.example.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.OptimisticLockException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.TransferRequest;
import com.example.backend.model.Beneficio;
import com.example.backend.service.BeneficioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Beneficiários", description = "API para gerenciar beneficiários")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BeneficioController {

    @Autowired
    private BeneficioService beneficioService;

    /**
     * Listar todos os beneficiários
     */
    @GetMapping
    @Operation(summary = "Listar todos os beneficiários", description = "Retorna uma lista de todos os beneficiários registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de beneficiários obtida com sucesso")
    })
    public ResponseEntity<List<Beneficio>> list(@RequestParam(required = false) String nome) {
        List<Beneficio> beneficios;
        if (nome != null && !nome.trim().isEmpty()) {
            beneficios = beneficioService.findByNome(nome);
        } else {
            beneficios = beneficioService.findAll();
        }
        return ResponseEntity.ok(beneficios);
    }

    /**
     * Listar apenas beneficiários ativos
     */
    @GetMapping("/ativos")
    @Operation(summary = "Listar beneficiários ativos", description = "Retorna uma lista apenas de beneficiários ativos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de beneficiários ativos obtida com sucesso")
    })
    public ResponseEntity<List<Beneficio>> listActive() {
        List<Beneficio> beneficios = beneficioService.findAllActive();
        return ResponseEntity.ok(beneficios);
    }

    /**
     * Buscar beneficiário por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar beneficiário por ID", description = "Retorna um beneficiário específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Beneficiário encontrado"),
            @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado")
    })
    public ResponseEntity<Beneficio> getById(@PathVariable Long id) {
        Optional<Beneficio> beneficio = beneficioService.findById(id);
        if (beneficio.isPresent()) {
            return ResponseEntity.ok(beneficio.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Criar novo beneficiário
     */
    @PostMapping
    @Operation(summary = "Criar novo beneficiário", description = "Cria um novo beneficiário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Beneficiário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<Beneficio> create(@RequestBody Beneficio beneficio) {
        try {
            Beneficio created = beneficioService.create(beneficio);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Atualizar beneficiário existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar beneficiário", description = "Atualiza os dados de um beneficiário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Beneficiário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado")
    })
    public ResponseEntity<Beneficio> update(
            @PathVariable Long id,
            @RequestBody Beneficio beneficio) {
        try {
            Beneficio updated = beneficioService.update(id, beneficio);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deletar beneficiário
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar beneficiário", description = "Remove um beneficiário do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Beneficiário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            beneficioService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Realizar transferência entre beneficiários
     */
    @PostMapping("/transfer")
    @Operation(summary = "Realizar transferência", description = "Transfere saldo de um beneficiário para outro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou saldo insuficiente"),
            @ApiResponse(responseCode = "409", description = "Erro de concorrência (lost update)")
    })
    public ResponseEntity<String> transfer(@RequestBody TransferRequest transferRequest) {
        try {
            beneficioService.transfer(transferRequest);
            return ResponseEntity.ok("Transferência realizada com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof OptimisticLockException) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Obter saldo de um beneficiário
     */
    @GetMapping("/{id}/saldo")
    @Operation(summary = "Obter saldo", description = "Retorna o saldo atual de um beneficiário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo obtido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado")
    })
    public ResponseEntity<?> getBalance(@PathVariable Long id) {
        try {
            var balance = beneficioService.getBalance(id);
            return ResponseEntity.ok().body(new HashMap<String, Object>() {
                {
                    put("saldo", balance);
                }
            });
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
