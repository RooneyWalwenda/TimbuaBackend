package com.Timbua.backend.controller;

import com.Timbua.backend.model.Quote;
import com.Timbua.backend.service.QuoteService;
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
@RequestMapping("/api/quotes")
@CrossOrigin(origins = "*")
@Tag(name = "Quotes", description = "APIs for managing supplier quotes for quotation requests")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    // Submit a quote for a request
    @PostMapping("/request/{requestId}/supplier/{supplierId}")
    @Operation(
            summary = "Submit a quote for a quotation request",
            description = """
            Allows a supplier to submit a price quote for a specific quotation request.
            
            **Business Rules:**
            - Supplier must be invited to the quotation request
            - Supplier can only submit one quote per request
            - Request must be in PENDING status
            - Quote amount must be positive
            - Delivery time must be specified
            
            **Workflow:**
            1. Supplier receives quotation request invitation
            2. Supplier reviews material requirements
            3. Supplier submits competitive quote
            4. Contractor reviews all quotes
            5. Contractor accepts best quote
            
            **Note:** First quote submitted changes request status from PENDING to QUOTED
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quote submitted successfully",
                    content = @Content(schema = @Schema(implementation = Quote.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                Bad Request - Possible reasons:
                - Supplier not invited to request
                - Supplier already submitted quote
                - Invalid quote parameters
                - Request not in PENDING status
                """
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quotation request or supplier not found"
            )
    })
    public ResponseEntity<Quote> submitQuote(
            @Parameter(description = "Quotation Request ID", example = "1", required = true)
            @PathVariable Long requestId,

            @Parameter(description = "Supplier ID submitting the quote", example = "2", required = true)
            @PathVariable Long supplierId,

            @Parameter(
                    description = "Quote details",
                    required = true,
                    example = """
                    {
                      "totalAmount": 85000.00,
                      "deliveryTime": "3 days",
                      "terms": "Free delivery within Nairobi"
                    }
                    """
            )
            @RequestBody Quote quote) {
        return ResponseEntity.ok(quoteService.submitQuote(requestId, supplierId, quote));
    }

    // Get all quotes for a request
    @GetMapping("/request/{requestId}")
    @Operation(
            summary = "Get all quotes for a quotation request",
            description = """
            Retrieves all quotes submitted for a specific quotation request.
            Used by contractor to compare quotes from different suppliers.
            
            **Typical Response Includes:**
            - Supplier details (company, rating, etc.)
            - Quote amount and terms
            - Delivery timeline
            - Quote status (PENDING/ACCEPTED/REJECTED)
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of quotes for the request"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quotation request not found"
            )
    })
    public ResponseEntity<List<Quote>> getQuotesByRequest(
            @Parameter(description = "Quotation Request ID", example = "1", required = true)
            @PathVariable Long requestId) {
        return ResponseEntity.ok(quoteService.getQuotesByRequest(requestId));
    }

    // Get all quotes by a supplier
    @GetMapping("/supplier/{supplierId}")
    @Operation(
            summary = "Get all quotes by a supplier",
            description = "Retrieves all quotes submitted by a specific supplier (Supplier's quote history)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of quotes submitted by the supplier"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Supplier not found"
            )
    })
    public ResponseEntity<List<Quote>> getQuotesBySupplier(
            @Parameter(description = "Supplier ID", example = "1", required = true)
            @PathVariable Long supplierId) {
        return ResponseEntity.ok(quoteService.getQuotesBySupplier(supplierId));
    }

    // Get single quote
    @GetMapping("/{id}")
    @Operation(
            summary = "Get quote by ID",
            description = "Retrieves detailed information about a specific quote"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quote details"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quote not found"
            )
    })
    public ResponseEntity<Quote> getQuoteById(
            @Parameter(description = "Quote ID", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(quoteService.getQuoteById(id));
    }

    // Accept a quote
    @PutMapping("/{id}/accept")
    @Operation(
            summary = "Accept a quote",
            description = """
            Accepts a submitted quote. This triggers:
            1. Quote status changes to ACCEPTED
            2. Quotation request status changes to ACCEPTED
            3. All other quotes for the same request are automatically REJECTED
            4. An order is automatically created from the accepted quote
            
            **Note:** This is a critical action that initiates the ordering process.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quote accepted successfully and order created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Cannot accept quote (already accepted/cancelled, etc.)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quote not found"
            )
    })
    public ResponseEntity<Quote> acceptQuote(
            @Parameter(description = "Quote ID to accept", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(quoteService.acceptQuote(id));
    }

    // Reject a quote
    @PutMapping("/{id}/reject")
    @Operation(
            summary = "Reject a quote",
            description = """
            Rejects a submitted quote. 
            
            **Note:** Typically used when contractor wants to manually reject a quote 
            without accepting another one (e.g., quote is too high, terms unacceptable).
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quote rejected successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Quote not found"
            )
    })
    public ResponseEntity<Quote> rejectQuote(
            @Parameter(description = "Quote ID to reject", example = "2", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(quoteService.rejectQuote(id));
    }
}
