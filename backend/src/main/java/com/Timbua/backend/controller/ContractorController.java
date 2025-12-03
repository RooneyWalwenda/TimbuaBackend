package com.Timbua.backend.controller;

import com.Timbua.backend.dto.ContractorRequestDTO;
import com.Timbua.backend.dto.ContractorResponseDTO;
import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.model.ContractorDocument;
import com.Timbua.backend.service.ContractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contractors")
@CrossOrigin(origins = "*")
@Tag(name = "Contractors", description = "APIs for contractor registration and management")
public class ContractorController {

    private final ContractorService contractorService;

    public ContractorController(ContractorService contractorService) {
        this.contractorService = contractorService;
    }

    // === Public Registration ===
    @PostMapping("/register")
    @Operation(summary = "Register a new contractor", description = "Self-registration for contractors")
    public ResponseEntity<ContractorResponseDTO> registerContractor(@RequestBody ContractorRequestDTO contractorRequest) {
        return ResponseEntity.ok(contractorService.registerContractor(contractorRequest));
    }

    // === Document Upload ===
    @PostMapping("/{contractorId}/documents")
    @Operation(summary = "Upload contractor document", description = "Upload verification documents")
    public ResponseEntity<ContractorDocument> uploadDocument(
            @PathVariable Long contractorId,
            @RequestBody ContractorDocument document) {
        return ResponseEntity.ok(contractorService.uploadDocument(contractorId, document));
    }

    // === Admin Endpoints ===
    @PutMapping("/{id}/verify")
    @Operation(summary = "Verify contractor", description = "Admin verification of contractor")
    public ResponseEntity<ContractorResponseDTO> verifyContractor(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remarks) {
        return ResponseEntity.ok(contractorService.verifyContractor(id, approved, remarks != null ? remarks : ""));
    }

    @PutMapping("/documents/{documentId}/status")
    @Operation(summary = "Update document status", description = "Admin review of contractor documents")
    public ResponseEntity<ContractorDocument> updateDocumentStatus(
            @PathVariable Long documentId,
            @RequestParam ContractorDocument.Status status) {
        return ResponseEntity.ok(contractorService.updateDocumentStatus(documentId, status));
    }

    // === Standard CRUD ===
    @GetMapping
    @Operation(summary = "Get all contractors", description = "Get list of all contractors")
    public ResponseEntity<List<ContractorResponseDTO>> getAllContractors() {
        return ResponseEntity.ok(contractorService.getAllContractors());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get contractors by status", description = "Get contractors filtered by verification status")
    public ResponseEntity<List<ContractorResponseDTO>> getContractorsByStatus(@PathVariable Contractor.Status status) {
        return ResponseEntity.ok(contractorService.getContractorsByStatus(status));
    }

    @GetMapping("/verified")
    @Operation(summary = "Get verified contractors", description = "Get list of verified contractors only")
    public ResponseEntity<List<ContractorResponseDTO>> getVerifiedContractors() {
        return ResponseEntity.ok(contractorService.getVerifiedContractors());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contractor by ID", description = "Get contractor details by ID")
    public ResponseEntity<ContractorResponseDTO> getContractorById(@PathVariable Long id) {
        return contractorService.getContractorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/documents")
    @Operation(summary = "Get contractor documents", description = "Get all documents for a contractor")
    public ResponseEntity<List<ContractorDocument>> getContractorDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(contractorService.getContractorDocuments(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update contractor", description = "Update contractor information")
    public ResponseEntity<ContractorResponseDTO> updateContractor(@PathVariable Long id, @RequestBody ContractorRequestDTO contractorRequest) {
        return ResponseEntity.ok(contractorService.updateContractor(id, contractorRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete contractor", description = "Delete contractor account")
    public ResponseEntity<Void> deleteContractor(@PathVariable Long id) {
        contractorService.deleteContractor(id);
        return ResponseEntity.noContent().build();
    }
}
