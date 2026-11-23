package com.hospital.management.doctor.dto;

import jakarta.validation.constraints.Size;

public record DoctorUpdateRequest(
        @Size(min = 1, max = 160, message = "name must be at most 160 characters") String fullName,

        @Size(min = 1, max = 40, message = "licence number must be between 1 and 40 characters") String licenceNumber,

        @Size(min = 1, max = 80, message = "specilaity must be between 1 and 80 characters") String speciality,

        @Size(min = 1, max = 80, message = "department must be between 1 and 80 characters") String department) {
}
