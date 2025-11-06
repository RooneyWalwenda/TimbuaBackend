package com.Timbua.backend.controller;

import com.Timbua.backend.ResponseModel;
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
    public ResponseEntity<ResponseModel<Supplier>> register(@RequestBody Supplier supplier) {
        try {
            Supplier saved = supplierService.registerSupplier(supplier);
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
    public ResponseEntity<ResponseModel<Supplier>> registerWithMaterials(@RequestBody Supplier supplier) {
        try {
            Supplier saved = supplierService.registerSupplierWithMaterials(supplier);
            return ResponseEntity.ok(new ResponseModel<>(saved, "SUPPLIER_CREATED_WITH_MATERIALS",
                    "Supplier registered successfully with " + saved.getMaterials().size() + " materials."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "VALIDATION_ERROR", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<Supplier>>> allSuppliers() {
        List<Supplier> list = supplierService.getAllSuppliers();
        return ResponseEntity.ok(new ResponseModel<>(list, "OK", "All suppliers"));
    }

    @GetMapping("/verified")
    public ResponseEntity<ResponseModel<List<Supplier>>> verifiedSuppliers() {
        List<Supplier> list = supplierService.getVerifiedSuppliers();
        return ResponseEntity.ok(new ResponseModel<>(list, "OK", "Verified suppliers"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<Supplier>> getSupplier(@PathVariable Long id) {
        try {
            Supplier s = supplierService.getSupplier(id);
            return ResponseEntity.ok(new ResponseModel<>(s, "OK", "Supplier found"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(null, "NOT_FOUND", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<Supplier>> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        try {
            Supplier updated = supplierService.updateSupplier(id, supplier);
            return ResponseEntity.ok(new ResponseModel<>(updated, "UPDATED", "Supplier updated"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<ResponseModel<Supplier>> verifySupplier(@PathVariable Long id,
                                                                  @RequestParam(name = "approve", defaultValue = "true") boolean approve) {
        try {
            Supplier s = supplierService.verifySupplier(id, approve);
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
            Supplier supplier = supplierService.getSupplier(supplierId);

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
            supplierService.getSupplier(supplierId); // validate exists
            List<SupplierDocument> docs = supplierService.getDocumentsForSupplier(supplierId);
            return ResponseEntity.ok(new ResponseModel<>(docs, "OK", "Documents list"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(null, "NOT_FOUND", ex.getMessage()));
        }
    }
}