package com.hospital.management.patient.service;

import com.hospital.management.exception.BadRequestException;
import com.hospital.management.exception.ConflictException;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.patient.dto.PatientResponse;
import com.hospital.management.patient.dto.UpdatePatientRequest;
import com.hospital.management.patient.model.Gender;
import com.hospital.management.patient.model.PatientDocument;
import com.hospital.management.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    private PatientService patientService;

    @BeforeEach
    void setUp() {
        patientService = new PatientService(patientRepository, new PatientMapper());
    }

    @Test
    void updateReplacesMutableFields() {
        PatientDocument existing = new PatientDocument();
        existing.setId("patient-1");
        existing.setFullName("Old Name");
        existing.setPrimaryDoctorName("Old Doctor");
        existing.setMedicalRecordNumber("MRN-100001");
        existing.setGender(Gender.UNKNOWN);

        UpdatePatientRequest request = new UpdatePatientRequest(
                "Ada Lovelace",
                "Dr. Grace Hopper",
                "mrn-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "o+",
                "Updated notes"
        );

        when(patientRepository.findById("patient-1")).thenReturn(Optional.of(existing));
        when(patientRepository.existsByMedicalRecordNumberAndIdNot("MRN-100002", "patient-1")).thenReturn(false);
        when(patientRepository.save(any(PatientDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response = patientService.update("patient-1", request);

        assertThat(response.fullName()).isEqualTo("Ada Lovelace");
        assertThat(response.primaryDoctorName()).isEqualTo("Dr. Grace Hopper");
        assertThat(response.medicalRecordNumber()).isEqualTo("MRN-100002");
        assertThat(response.dateOfBirth()).isEqualTo(LocalDate.of(1815, 12, 10));
        assertThat(response.gender()).isEqualTo(Gender.FEMALE);
        assertThat(response.bloodGroup()).isEqualTo("O+");
    }

    @Test
    void updateFailsWhenPatientMissing() {
        when(patientRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patientService.update("missing", sampleRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateFailsWhenMedicalRecordNumberConflicts() {
        PatientDocument existing = new PatientDocument();
        existing.setId("patient-1");
        existing.setMedicalRecordNumber("MRN-100001");

        when(patientRepository.findById("patient-1")).thenReturn(Optional.of(existing));
        when(patientRepository.existsByMedicalRecordNumberAndIdNot("MRN-100002", "patient-1")).thenReturn(true);

        assertThatThrownBy(() -> patientService.update("patient-1", sampleRequest()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Medical record number");
    }

    @Test
    void updateFailsWhenDateOfBirthIsInTheFuture() {
        PatientDocument existing = new PatientDocument();
        existing.setId("patient-1");
        existing.setMedicalRecordNumber("MRN-100001");

        UpdatePatientRequest request = new UpdatePatientRequest(
                "Ada Lovelace",
                "Dr. Grace Hopper",
                "MRN-100002",
                LocalDate.now().plusDays(1),
                Gender.FEMALE,
                null,
                null,
                null
        );

        when(patientRepository.findById("patient-1")).thenReturn(Optional.of(existing));
        when(patientRepository.existsByMedicalRecordNumberAndIdNot("MRN-100002", "patient-1")).thenReturn(false);

        assertThatThrownBy(() -> patientService.update("patient-1", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("future");
    }

    private UpdatePatientRequest sampleRequest() {
        return new UpdatePatientRequest(
                "Ada Lovelace",
                "Dr. Grace Hopper",
                "MRN-100002",
                LocalDate.of(1815, 12, 10),
                Gender.FEMALE,
                "+15551230001",
                "O+",
                "Notes"
        );
    }
}
