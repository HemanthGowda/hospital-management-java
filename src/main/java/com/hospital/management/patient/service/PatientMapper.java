package com.hospital.management.patient.service;

import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.model.PatientDocument;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientResponse toResponse(PatientDocument document) {
        return new PatientResponse(
                document.getId(),
                document.getFullName(),
                document.getPrimaryDoctorName(),
                document.getMedicalRecordNumber(),
                document.getDateOfBirth(),
                document.getGender(),
                document.getPhone(),
                document.getBloodGroup(),
                document.getNotes(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
