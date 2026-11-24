package com.hospital.management.doctor.service;

import com.hospital.management.doctor.dto.DoctorRegister;
import com.hospital.management.doctor.dto.DoctorResponse;
import com.hospital.management.doctor.dto.DoctorUpdateRequest;
import com.hospital.management.doctor.model.DoctorDocument;
import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.patient.repository.PatientRepository;
import com.hospital.management.exception.ConflictException;
import com.hospital.management.exception.ResourceNotFoundException;

import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service

public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final PatientRepository patientRepository;

    public DoctorService(DoctorRepository doctorRepository, DoctorMapper doctorMapper,
            PatientRepository patientRepository) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
        this.patientRepository = patientRepository;
    }

    public DoctorResponse register(DoctorRegister request) {

        String normalizedLicenceNumber = normalizeLicence(request.licenceNumber());

        if (doctorRepository.existsByLicenceNumber(normalizedLicenceNumber)) {
            throw new ConflictException("number is already registered");
        }

        DoctorDocument doctor = new DoctorDocument();

        doctor.setfullName(request.fullName().trim());
        doctor.setLicencenumber(normalizedLicenceNumber);
        doctor.setSpecialty(request.speciality());
        doctor.setDepartment(request.department());

        DoctorDocument saved = doctorRepository.save(doctor);
        System.out.println("registered");
        return doctorMapper.toResponse(saved);
    }

    public DoctorResponse viewDoctor(String id) {

        DoctorDocument doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
        return doctorMapper.toResponse(doctor);
    }

    public Page<DoctorResponse> searchDoctorsByName(String fullName, Pageable page) {

        return doctorRepository.findByFullNameContainingOrderByFullNameAsc(fullName, page)
                .map(doctorMapper::toResponse);
    }

    public DoctorResponse updateDoctorDetails(String id, DoctorUpdateRequest request) {

        DoctorDocument doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));

        if (request.licenceNumber() != null) {
            String licenceNumber = normalizeLicence(request.licenceNumber());

            if (doctorRepository.existsByLicenceNumberAndIdNot(licenceNumber, id)) {
                throw new ConflictException("Licence number is already assigned to another doctor");
            }
            doctor.setLicencenumber(licenceNumber);
        }

        if (request.fullName() != null) {

            doctor.setfullName(request.fullName().trim());
        }
        if (request.speciality() != null) {
            doctor.setSpecialty(request.speciality().trim());
        }
        if (request.department() != null) {
            doctor.setDepartment(request.department().trim());
        }
        DoctorDocument saved = doctorRepository.save(doctor);
        return doctorMapper.toResponse(saved);
    }

    public String deleteADoctor(String id) {
        DoctorDocument doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));

        String doctorname = doctor.getfullName();
        if (patientRepository.existsByPrimaryDoctorName(doctorname)) {
            throw new ConflictException("doctor has been assigned to a patient");
        }

        doctorRepository.deleteById(id);
        return doctor.getfullName();
    }

    public String normalizeLicence(String licenceNumber) {
        return licenceNumber.trim().toLowerCase(Locale.ROOT);
    }

}
