package com.Timbua.backend.controller;

import com.Timbua.backend.ResponseModel;
import com.Timbua.backend.model.Material;
import com.Timbua.backend.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "*")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @PostMapping("/supplier/{supplierId}")
    public ResponseEntity<ResponseModel<Material>> addMaterial(@PathVariable Long supplierId,
                                                               @RequestBody Material material) {
        try {
            Material saved = materialService.addMaterial(supplierId, material);
            return ResponseEntity.ok(new ResponseModel<>(saved, "MATERIAL_CREATED", "Material added"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "NOT_ALLOWED", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<Material>>> allMaterials() {
        List<Material> list = materialService.getAllMaterials();
        return ResponseEntity.ok(new ResponseModel<>(list, "OK", "All materials"));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ResponseModel<List<Material>>> materialsBySupplier(@PathVariable Long supplierId) {
        List<Material> list = materialService.getMaterialsBySupplier(supplierId);
        return ResponseEntity.ok(new ResponseModel<>(list, "OK", "Supplier materials"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<Material>> updateMaterial(@PathVariable Long id, @RequestBody Material material) {
        try {
            Material updated = materialService.updateMaterial(id, material);
            return ResponseEntity.ok(new ResponseModel<>(updated, "UPDATED", "Material updated"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(null, "NOT_FOUND", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteMaterial(@PathVariable Long id) {
        try {
            materialService.deleteMaterial(id);
            return ResponseEntity.ok(new ResponseModel<>("OK", "DELETED", "Material deleted"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseModel<>(null, "ERROR", ex.getMessage()));
        }
    }
}
