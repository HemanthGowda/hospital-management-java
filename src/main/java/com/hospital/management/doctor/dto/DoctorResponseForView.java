package com.hospital.management.doctor.dto;

public record DoctorResponseForView(
        String fullName,
        String licenceNumber,
        String speciality,
        String department

) {
}