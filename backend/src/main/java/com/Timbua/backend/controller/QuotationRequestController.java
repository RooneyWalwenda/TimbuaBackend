package com.Timbua.backend.controller;

import com.Timbua.backend.model.QuotationRequest;
import com.Timbua.backend.service.QuotationRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
@CrossOrigin(origins = "*")
@Tag(name = "Quotation Requests", description = "APIs for managing material quotation requests between contractors and suppliers")
public class QuotationRequestController {

    private final QuotationRequestService service;

    public QuotationRequestController(QuotationRequestService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(
        summary = "Create new quotation request",
        description = "Create a new material quotation request. Status is automatically set to PENDING. Contractors use this to request prices for construction materials from suppliers."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Quotation request created successfully",
            content = @Content(schema = @Schema(implementation = QuotationRequest.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data"
        )
    })
    public QuotationRequest createQuotation(@RequestBody QuotationRequest request) {
        return service.createQuotationRequest(request);
    }

    @GetMapping
    @Operation(
        summary = "Get all quotation requests",
        description = "Retrieve a list of all quotation requests in the system. Useful for admin overview and supplier browsing."
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of all quotation requests retrieved successfully",
        content = @Content(schema = @Schema(implementation = QuotationRequest.class))
    )
    public List<QuotationRequest> getAll() {
        return service.getAllRequests();
    }

    @GetMapping("/contractor/{contractorId}")
    @Operation(
        summary = "Get quotation requests by contractor",
        description = "Retrieve all quotation requests created by a specific contractor. Contractors can view their own request history and status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Contractor's quotation requests retrieved successfully",
            content = @Content(schema = @Schema(implementation = QuotationRequest.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Contractor not found"
        )
    })
    public List<QuotationRequest> getByContractor(@PathVariable Long contractorId) {
        return service.getRequestsByContractor(contractorId);
    }

    @PutMapping("/{id}/status")
    @Operation(
        summary = "Update quotation request status",
        description = "Update the status of a quotation request. Valid status transitions: PENDING → SENT → QUOTED → APPROVED/REJECTED → COMPLETED"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Quotation request status updated successfully",
            content = @Content(schema = @Schema(implementation = QuotationRequest.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Quotation request not found"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status transition"
        )
    })
    public QuotationRequest updateStatus(
            @PathVariable Long id,
            @RequestParam QuotationRequest.Status status) {
        return service.updateStatus(id, status);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get quotation request by ID",
        description = "Retrieve detailed information for a specific quotation request including material details, quantities, and current status."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Quotation request details retrieved successfully",
            content = @Content(schema = @Schema(implementation = QuotationRequest.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Quotation request not found"
        )
    })
    public QuotationRequest getById(@PathVariable Long id) {
        return service.getRequestById(id)
                .orElseThrow(() -> new RuntimeException("Quotation Request not found with id: " + id));
    }
}
