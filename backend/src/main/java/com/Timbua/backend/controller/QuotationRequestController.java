package com.Timbua.backend.controller;

import com.Timbua.backend.model.QuotationRequest;
import com.Timbua.backend.service.QuotationRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotation-requests")
@CrossOrigin(origins = "*")
@Tag(name = "Quotation Requests", description = "APIs for managing material quotation requests from contractors to suppliers")
public class QuotationRequestController {

    private final QuotationRequestService quotationRequestService;

    public QuotationRequestController(QuotationRequestService quotationRequestService) {
        this.quotationRequestService = quotationRequestService;
    }

    // Create quotation request for specific suppliers
    @PostMapping
    @Operation(
            summary = "Create a new quotation request",
            description = """
            Creates a quotation request for specific materials and invites selected suppliers to quote.
            
            **Workflow:**
            1. Contractor needs materials for a construction site
            2. Creates quotation request with material details
            3. Selects which suppliers to invite (one or multiple)
            4. Invited suppliers receive notification to submit quotes
            
            **Business Rules:**
            - Contractor must be verified
            - At least one supplier must be invited
            - Request deadline must be in the future
            - Material quantity must be positive
            
            **Typical Use Case:**
            Contractor needs 100 bags of cement for Site A, invites 3 cement suppliers to quote.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quotation request created successfully",
                    content = @Content(schema = @Schema(implementation = QuotationRequest.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid input or supplier IDs"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contractor or supplier not found"
            )
    })
    public ResponseEntity<QuotationRequest> createRequest(
            @Parameter(
                    description = "Quotation request details",
                    required = true,
                    example = """
                    {
                      "contractor": {"id": 1},
                      "siteId": 1,
                      "material": "Portland Cement",
                      "quantity": 100.0,
                      "unit": "BAGS",
                      "deadline": "2024-01-30"
                    }
                    """
            )
            @RequestBody QuotationRequest request,

            @Parameter(
                    description = "List of supplier IDs to invite for quotation",
                    required = true,
                    example = "[1, 2, 3]"
            )
            @RequestParam List<Long> supplierIds) {
        return ResponseEntity.ok(quotationRequestService.createQuotationRequest(request, supplierIds));
    }

    // Get all requests (admin view)
    @GetMapping
    @Operation(
            summary = "Get all quotation requests",
            description = "Retrieves all quotation requests in the system (Admin view)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of all quotation requests",
            content = @Content(schema = @Schema(implementation = QuotationRequest.class))
    )
    public ResponseEntity<List<QuotationRequest>> getAllRequests() {
        return ResponseEntity.ok(quotationRequestService.getAllRequests());
    }

    // Get requests by contractor
    @GetMapping("/contractor/{contractorId}")
    @Operation(
            summary = "Get quotation requests by contractor",
            description = "Retrieves all quotation requests created by a specific contractor"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of quotation requests for the contractor"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contractor not found"
            )
    })
    public ResponseEntity<List<QuotationRequest>> getRequestsByContractor(
            @Parameter(description = "Contractor ID", example = "1", required = true)
            @PathVariable Long contractorId) {
        return ResponseEntity.ok(quotationRequestService.getRequestsByContractor(contractorId));
    }

    // Get requests for a supplier
    @GetMapping("/supplier/{supplierId}")
    @Operation(
            summary = "Get quotation requests for a supplier",
            description = """
            Retrieves all quotation requests where a specific supplier is invited.
            This is the supplier's dashboard view.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of quotation requests the supplier is invited to"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Supplier not found"
            )
    })
    public ResponseEntity<List<QuotationRequest>> getRequestsForSupplier(
            @Parameter(description = "Supplier ID", example = "1", required = true)
            @PathVariable Long supplierId) {
        return ResponseEntity.ok(quotationRequestService.getRequestsForSupplier(supplierId));
    }

    // Get single request by ID
    @GetMapping("/{id}")
    @Operation(
            summary = "Get quotation request by ID",
            description = "Retrieves a single quotation request with all details including invited suppliers and submitted quotes"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quotation request details"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quotation request not found"
            )
    })
    public ResponseEntity<QuotationRequest> getRequestById(
            @Parameter(description = "Quotation Request ID", example = "1", required = true)
            @PathVariable Long id) {
        return quotationRequestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update request status
    @PutMapping("/{id}/status")
    @Operation(
            summary = "Update quotation request status",
            description = """
            Updates the status of a quotation request.
            
            **Status Flow:**
            - PENDING → Request created, waiting for quotes
            - QUOTED → At least one quote received
            - ACCEPTED → A quote has been accepted
            - CANCELLED → Request cancelled
            
            **Note:** Some status transitions may trigger other actions (e.g., ACCEPTED creates an order)
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Status updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid status transition"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quotation request not found"
            )
    })
    public ResponseEntity<QuotationRequest> updateStatus(
            @Parameter(description = "Quotation Request ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(
                    description = "New status",
                    required = true,
                    schema = @Schema(allowableValues = {"PENDING", "QUOTED", "ACCEPTED", "CANCELLED"})
            )
            @RequestParam QuotationRequest.Status status) {
        return ResponseEntity.ok(quotationRequestService.updateStatus(id, status));
    }

    // Add more suppliers to request
    @PostMapping("/{id}/suppliers")
    @Operation(
            summary = "Add more suppliers to quotation request",
            description = "Adds additional suppliers to an existing quotation request"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Suppliers added successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid supplier IDs"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quotation request not found"
            )
    })
    public ResponseEntity<QuotationRequest> addSuppliers(
            @Parameter(description = "Quotation Request ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(
                    description = "List of additional supplier IDs to invite",
                    required = true,
                    example = "[4, 5]"
            )
            @RequestBody List<Long> supplierIds) {
        return ResponseEntity.ok(quotationRequestService.addSuppliersToRequest(id, supplierIds));
    }

    // Remove supplier from request
    @DeleteMapping("/{requestId}/suppliers/{supplierId}")
    @Operation(
            summary = "Remove supplier from quotation request",
            description = "Removes a supplier from the invited list of a quotation request"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Supplier removed successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quotation request or supplier not found"
            )
    })
    public ResponseEntity<QuotationRequest> removeSupplier(
            @Parameter(description = "Quotation Request ID", example = "1", required = true)
            @PathVariable Long requestId,

            @Parameter(description = "Supplier ID to remove", example = "2", required = true)
            @PathVariable Long supplierId) {
        return ResponseEntity.ok(quotationRequestService.removeSupplierFromRequest(requestId, supplierId));
    }

    // Cancel request
    @PutMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel a quotation request",
            description = """
            Cancels a quotation request and notifies all invited suppliers.
            
            **Note:** Once cancelled, no more quotes can be submitted.
            Existing quotes remain but are marked as cancelled.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Request cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot cancel request in current state"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quotation request not found"
            )
    })
    public ResponseEntity<QuotationRequest> cancelRequest(
            @Parameter(description = "Quotation Request ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "Reason for cancellation", example = "Project cancelled", required = false)
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(quotationRequestService.cancelRequest(id, reason));
    }
}
