package com.Timbua.backend.controller;

import com.Timbua.backend.ResponseModel;
import com.Timbua.backend.dto.SupplierRequestDTO;
import com.Timbua.backend.dto.SupplierResponseDTO;
import com.Timbua.backend.model.Material;
import com.Timbua.backend.model.Supplier;
import com.Timbua.backend.model.SupplierDocument;
import com.Timbua.backend.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
public class SupplierController {

    private final SupplierService supplierService;
    private final String uploadDir = "uploads"; // not yet existing

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
        try {
            Path p = Paths.get(uploadDir);
            if (!Files.exists(p)) Files.createDirectories(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseModel<SupplierResponseDTO>> register(@RequestBody SupplierRequestDTO supplierRequest) {
        try {
            SupplierResponseDTO saved = supplierService.registerSupplier(supplierRequest);
            return ResponseEntity.ok(new ResponseModel<>(saved, "SUPPLIER_CREATED",
                    "Supplier added successfully and awaiting verification."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "SUPPLIER_EXISTS", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    // NEW ENDPOINT: Register supplier with materials
    @PostMapping("/register-with-materials")
    public ResponseEntity<ResponseModel<SupplierResponseDTO>> registerWithMaterials(
            @RequestBody RegisterWithMaterialsRequest request) {
        try {
            SupplierResponseDTO saved = supplierService.registerSupplierWithMaterials(
                    request.getSupplierRequest(),
                    request.getMaterials());
            return ResponseEntity.ok(new ResponseModel<>(saved, "SUPPLIER_CREATED_WITH_MATERIALS",
                    "Supplier registered successfully with " + request.getMaterials().size() + " materials."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "VALIDATION_ERROR", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<SupplierResponseDTO>>> allSuppliers() {
        List<SupplierResponseDTO> list = supplierService.getAllSuppliers();
        return ResponseEntity.ok(new ResponseModel<>(list, "OK", "All suppliers"));
    }

    @GetMapping("/verified")
    public ResponseEntity<ResponseModel<List<SupplierResponseDTO>>> verifiedSuppliers() {
        List<SupplierResponseDTO> list = supplierService.getVerifiedSuppliers();
        return ResponseEntity.ok(new ResponseModel<>(list, "OK", "Verified suppliers"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SupplierResponseDTO>> getSupplier(@PathVariable Long id) {
        try {
            SupplierResponseDTO s = supplierService.getSupplier(id);
            return ResponseEntity.ok(new ResponseModel<>(s, "OK", "Supplier found"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(null, "NOT_FOUND", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SupplierResponseDTO>> updateSupplier(@PathVariable Long id,
                                                                             @RequestBody SupplierRequestDTO supplierRequest) {
        try {
            SupplierResponseDTO updated = supplierService.updateSupplier(id, supplierRequest);
            return ResponseEntity.ok(new ResponseModel<>(updated, "UPDATED", "Supplier updated"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<ResponseModel<SupplierResponseDTO>> verifySupplier(@PathVariable Long id,
                                                                             @RequestParam(name = "approve", defaultValue = "true") boolean approve) {
        try {
            SupplierResponseDTO s = supplierService.verifySupplier(id, approve);
            String code = approve ? "VERIFIED" : "REJECTED";
            String msg = approve ? "Supplier verified" : "Supplier rejected";
            return ResponseEntity.ok(new ResponseModel<>(s, code, msg));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    @PostMapping("/{supplierId}/documents")
    public ResponseEntity<ResponseModel<SupplierDocument>> uploadDocument(@PathVariable Long supplierId,
                                                                          @RequestParam("file") MultipartFile file) {
        try {
            // Get supplier entity (not DTO) for document upload
            Supplier supplier = supplierService.getSupplierEntityById(supplierId)
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));

            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseModel<>(null, "NO_FILE", "Uploaded file is empty"));
            }

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID().toString() + ext;
            Path filepath = Paths.get(uploadDir, filename);
            Files.copy(file.getInputStream(), filepath);

            SupplierDocument doc = new SupplierDocument();
            doc.setFileName(original != null ? original : filename);
            doc.setFileType(file.getContentType());
            doc.setUrl("/" + uploadDir + "/" + filename); // for MVP serve static files from this path
            doc.setSupplier(supplier);

            SupplierDocument saved = supplierService.saveDocument(doc);

            return ResponseEntity.ok(new ResponseModel<>(saved, "UPLOADED", "Document uploaded successfully"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(null, "ERROR", "Failed to store file"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    @GetMapping("/{supplierId}/documents")
    public ResponseEntity<ResponseModel<List<SupplierDocument>>> getSupplierDocs(@PathVariable Long supplierId) {
        try {
            // Validate supplier exists
            supplierService.getSupplier(supplierId);
            List<SupplierDocument> docs = supplierService.getDocumentsForSupplier(supplierId);
            return ResponseEntity.ok(new ResponseModel<>(docs, "OK", "Documents list"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(null, "NOT_FOUND", ex.getMessage()));
        }
    }

    // === Additional Endpoints for Materials ===
    @PostMapping("/{supplierId}/materials")
    public ResponseEntity<ResponseModel<Material>> addMaterialToSupplier(@PathVariable Long supplierId,
                                                                         @RequestBody Material material) {
        try {
            Material savedMaterial = supplierService.addMaterialToSupplier(supplierId, material);
            return ResponseEntity.ok(new ResponseModel<>(savedMaterial, "MATERIAL_ADDED", "Material added successfully"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    @GetMapping("/{supplierId}/materials")
    public ResponseEntity<ResponseModel<List<Material>>> getSupplierMaterials(@PathVariable Long supplierId) {
        try {
            List<Material> materials = supplierService.getSupplierMaterials(supplierId);
            return ResponseEntity.ok(new ResponseModel<>(materials, "OK", "Supplier materials"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(null, "NOT_FOUND", ex.getMessage()));
        }
    }

    // === Request class for register with materials ===
    public static class RegisterWithMaterialsRequest {
        private SupplierRequestDTO supplierRequest;
        private List<Material> materials;

        // Getters and setters
        public SupplierRequestDTO getSupplierRequest() {
            return supplierRequest;
        }

        public void setSupplierRequest(SupplierRequestDTO supplierRequest) {
            this.supplierRequest = supplierRequest;
        }

        public List<Material> getMaterials() {
            return materials;
        }

        public void setMaterials(List<Material> materials) {
            this.materials = materials;
        }
    }
}
