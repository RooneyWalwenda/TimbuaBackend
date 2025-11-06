package com.Timbua.backend.controller;

import com.Timbua.backend.model.QuotationRequest;
import com.Timbua.backend.service.QuotationRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "*")
public class QuotationRequestController {

    private final QuotationRequestService service;

    public QuotationRequestController(QuotationRequestService service) {
        this.service = service;
    }

    @PostMapping
    public QuotationRequest createQuotation(@RequestBody QuotationRequest request) {
        return service.createQuotationRequest(request);
    }

    @GetMapping
    public List<QuotationRequest> getAll() {
        return service.getAllRequests();
    }

    @GetMapping("/contractor/{contractorId}")
    public List<QuotationRequest> getByContractor(@PathVariable Long contractorId) {
        return service.getRequestsByContractor(contractorId);
    }

    @PutMapping("/{id}/status")
    public QuotationRequest updateStatus(@PathVariable Long id, @RequestParam QuotationRequest.Status status) {
        return service.updateStatus(id, status);
    }
}
