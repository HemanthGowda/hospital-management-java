package com.hospital.management.doctor.service;

import org.springframework.stereotype.Component;
import com.hospital.management.doctor.dto.DoctorResponse;
import com.hospital.management.doctor.model.DoctorDocument;
import com.hospital.management.doctor.dto.DoctorResponseForView;

@Component
public class DoctorMapper {

    public DoctorResponse toResponse(DoctorDocument document) {
        return new DoctorResponse(
                document.getId(),
                document.getfullName(),
                document.getlicenceNumber(),
                document.getSpecialty(),
                document.getDepartment()

        );

    }

    public DoctorResponseForView toResponseForSigned(DoctorDocument document) {
        return new DoctorResponseForView(
                document.getfullName(),
                document.getlicenceNumber(),
                document.getSpecialty(),
                document.getDepartment());
    }

}
