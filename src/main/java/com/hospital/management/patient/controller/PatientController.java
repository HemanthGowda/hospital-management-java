package com.hospital.management.patient.controller;

import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.dto.UpdatePatientRequest;
import com.hospital.management.patient.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/patients")
@Tag(name = "Patients", description = "Patient chart APIs. Only update is implemented as a reference slice.")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(
            summary = "Update a patient",
            description = "Fully replaces mutable patient fields. Requires ADMIN or DOCTOR role."
    )
    @ApiResponse(responseCode = "200", description = "Patient updated",
            content = @Content(schema = @Schema(implementation = PatientResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content)
    @ApiResponse(responseCode = "401", description = "Missing or invalid token", content = @Content)
    @ApiResponse(responseCode = "403", description = "Insufficient role", content = @Content)
    @ApiResponse(responseCode = "404", description = "Patient not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Medical record number conflict", content = @Content)
    public PatientResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePatientRequest request) {
        return patientService.update(id, request);
    }
}
