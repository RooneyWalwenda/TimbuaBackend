package com.Timbua.backend.service;

import com.Timbua.backend.model.QuotationRequest;
import com.Timbua.backend.repository.QuotationRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuotationRequestService {

    private final QuotationRequestRepository repository;

    public QuotationRequestService(QuotationRequestRepository repository) {
        this.repository = repository;
    }

    public QuotationRequest createQuotationRequest(QuotationRequest request) {
        request.setStatus(QuotationRequest.Status.PENDING);
        return repository.save(request);
    }

    public List<QuotationRequest> getAllRequests() {
        return repository.findAll();
    }

    public List<QuotationRequest> getRequestsByContractor(Long contractorId) {
        return repository.findByContractorId(contractorId);
    }

    public Optional<QuotationRequest> getRequestById(Long id) {
        return repository.findById(id);
    }

    public QuotationRequest updateStatus(Long id, QuotationRequest.Status status) {
        QuotationRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation Request not found"));
        request.setStatus(status);
        return repository.save(request);
    }
}
