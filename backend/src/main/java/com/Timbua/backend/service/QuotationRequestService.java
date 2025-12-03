package com.Timbua.backend.service;

import com.Timbua.backend.model.Contractor;
import com.Timbua.backend.model.QuotationRequest;
import com.Timbua.backend.model.Supplier;
import com.Timbua.backend.repository.ContractorRepository;
import com.Timbua.backend.repository.QuotationRequestRepository;
import com.Timbua.backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class QuotationRequestService {

    private final QuotationRequestRepository repository;
    private final SupplierRepository supplierRepository;
    private final ContractorRepository contractorRepository;

    public QuotationRequestService(QuotationRequestRepository repository,
                                   SupplierRepository supplierRepository,
                                   ContractorRepository contractorRepository) {
        this.repository = repository;
        this.supplierRepository = supplierRepository;
        this.contractorRepository = contractorRepository;
    }

    /**
     * Create quotation request for specific suppliers
     */
    @Transactional
    public QuotationRequest createQuotationRequest(QuotationRequest request, List<Long> supplierIds) {
        // Set initial status
        request.setStatus(QuotationRequest.Status.PENDING);

        // Fetch and set the contractor entity
        if (request.getContractor() != null && request.getContractor().getId() != null) {
            Contractor contractor = contractorRepository.findById(request.getContractor().getId())
                    .orElseThrow(() -> new RuntimeException("Contractor not found with id: " + request.getContractor().getId()));
            request.setContractor(contractor);
        }

        // Fetch invited suppliers
        List<Supplier> invitedSuppliers = supplierRepository.findAllById(supplierIds);
        if (invitedSuppliers.isEmpty()) {
            throw new RuntimeException("No valid suppliers found with the provided IDs");
        }

        request.setInvitedSuppliers(invitedSuppliers);

        // TODO: Send notifications to suppliers (will implement later)
        // notifySuppliers(invitedSuppliers, request);

        return repository.save(request);
    }

    /**
     * Get all quotation requests (for admin)
     */
    public List<QuotationRequest> getAllRequests() {
        return repository.findAll();
    }

    /**
     * Get requests created by a specific contractor
     */
    public List<QuotationRequest> getRequestsByContractor(Long contractorId) {
        return repository.findByContractorId(contractorId);
    }

    /**
     * Get requests sent to a specific supplier
     */
    public List<QuotationRequest> getRequestsForSupplier(Long supplierId) {
        return repository.findByInvitedSuppliersId(supplierId);
    }

    /**
     * Get request by ID
     */
    public Optional<QuotationRequest> getRequestById(Long id) {
        return repository.findById(id);
    }

    /**
     * Update request status
     */
    public QuotationRequest updateStatus(Long id, QuotationRequest.Status status) {
        QuotationRequest request = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quotation Request not found"));
        request.setStatus(status);
        return repository.save(request);
    }

    /**
     * Add more suppliers to an existing request
     */
    @Transactional
    public QuotationRequest addSuppliersToRequest(Long requestId, List<Long> supplierIds) {
        QuotationRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Quotation Request not found"));

        List<Supplier> newSuppliers = supplierRepository.findAllById(supplierIds);

        for (Supplier supplier : newSuppliers) {
            if (!request.getInvitedSuppliers().contains(supplier)) {
                request.addInvitedSupplier(supplier);
                // TODO: Send notification to newly added supplier
            }
        }

        return repository.save(request);
    }

    /**
     * Remove supplier from request
     */
    @Transactional
    public QuotationRequest removeSupplierFromRequest(Long requestId, Long supplierId) {
        QuotationRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Quotation Request not found"));

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        request.removeInvitedSupplier(supplier);

        return repository.save(request);
    }

    /**
     * Cancel a quotation request
     */
    @Transactional
    public QuotationRequest cancelRequest(Long requestId, String reason) {
        QuotationRequest request = repository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Quotation Request not found"));

        request.setStatus(QuotationRequest.Status.CANCELLED);

        // TODO: Notify all invited suppliers about cancellation

        return repository.save(request);
    }
}
