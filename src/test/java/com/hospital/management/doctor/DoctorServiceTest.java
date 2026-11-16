package com.hospital.management.doctor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import com.hospital.management.doctor.model.DoctorDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.doctor.service.DoctorMapper;
import com.hospital.management.doctor.service.DoctorService;
import com.hospital.management.doctor.dto.DoctorRegister;
import com.hospital.management.doctor.dto.DoctorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    private DoctorService doctorService;
    private static Validator validator;

    @BeforeEach
    void setUp() {
        doctorService = new DoctorService(doctorRepository, new DoctorMapper());
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerCreatesDoctorWithNormalizedLicenceNumber() {
        DoctorRegister request = new DoctorRegister(

                "abc ",
                "abc",
                "dermatologist",
                "xyz");

        when(doctorRepository.existsByLicenceNumber("abc")).thenReturn(false);
        when(doctorRepository.save(any(DoctorDocument.class))).thenAnswer(invocation -> {
            DoctorDocument document = invocation.getArgument(0);
            document.setId("1");
            return document;
        });

        DoctorResponse response = doctorService.register(request);

        ArgumentCaptor<DoctorDocument> captor = ArgumentCaptor.forClass(DoctorDocument.class);
        verify(doctorRepository).save(captor.capture());

        DoctorDocument saved = captor.getValue();
        assertThat(saved.getlicenceNumber()).isEqualTo("abc");
        assertThat(saved.getfullName()).isEqualTo("abc");
        assertThat(saved.getSpecialty()).isEqualTo("dermatologist");
        assertThat(saved.getDepartment()).isEqualTo("xyz");

        assertThat(response.fullName()).isEqualTo("abc");
        assertThat(response.licenceNumber()).isEqualTo("abc");

    }

    @Test
    void registerRejectsDuplicateLicenceNumber() {
        when(doctorRepository.existsByLicenceNumber("hsghas")).thenReturn(true);

        assertThatThrownBy(() -> doctorService.register(
                new DoctorRegister("abc", "hsghas", "Alex Reader", "dhsgds")))

                .hasMessageContaining("already registered");
    }

    @Test
    void validateFindByLicenceNumber() {

        DoctorDocument doctor = doctorRepository.findByLicenceNumberIgnoreCase("abc").orElse(null);
        String expected = "Doctor not found";
        String actual = "";
        if (doctor == null) {
            actual = "Doctor not found";
        }
        assertEquals(expected, actual);
    }

    @Test
    void rejectsIfDoctorNameIsBlank() {
        DoctorRegister request = new DoctorRegister("   ", "dsjd", "jhd", "sjdn");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorLicenceNumberIsBlank() {
        DoctorRegister request = new DoctorRegister("doctor", " ", "jhd", "sjdn");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorSpecialityIsBlank() {
        DoctorRegister request = new DoctorRegister("doctor", "abc123 ", " ", "sjdn");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorDepartmentIsBlank() {
        DoctorRegister request = new DoctorRegister("doctor", "abc123 ", "xyz", " ");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorNameIsNull() {
        DoctorRegister request = new DoctorRegister(null, "abc123 ", "xyz", "abc");
        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorLicenceNumberIsNull() {
        DoctorRegister request = new DoctorRegister("abc", null, "xyz", "abc");
        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorSpceilaityIsNull() {
        DoctorRegister request = new DoctorRegister("abc", "abc123 ", null, "abc");
        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorDepartmentIsNull() {
        DoctorRegister request = new DoctorRegister("abc", "LC12", "xyz", null);
        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorNameIsAboveRange() {

        String fullName = "a".repeat(161);
        DoctorRegister request = new DoctorRegister(fullName, "abc", "accc", "xyz");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorLicenceNumberIsAboveRange() {

        String licenceNumber = "a1".repeat(21);
        DoctorRegister request = new DoctorRegister("abc", licenceNumber, "accc", "xyz");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorSpecialityIsAboveRange() {

        String speciality = "a2".repeat(41);
        DoctorRegister request = new DoctorRegister("abc", "ab12", speciality, "xyz");

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorDepartmentIsAboveRange() {

        String department = "xy".repeat(41);
        DoctorRegister request = new DoctorRegister("bsd", "abc", "accc", department);

        Set<ConstraintViolation<DoctorRegister>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void valiadteNormalizedLicenceNumber() {
        DoctorRegister request = new DoctorRegister("abc ", " Abc", "dermatologist", "xyz");

        when(doctorRepository.save(any(DoctorDocument.class))).thenAnswer(invocation -> {
            DoctorDocument document = invocation.getArgument(0);
            document.setId("1");
            return document;
        });

        DoctorResponse response = doctorService.register(request);

        ArgumentCaptor<DoctorDocument> captor = ArgumentCaptor.forClass(DoctorDocument.class);
        verify(doctorRepository).save(captor.capture());

        DoctorDocument saved = captor.getValue();
        assertThat(saved.getlicenceNumber()).isEqualTo("abc");
        assertThat(saved.getfullName()).isEqualTo("abc");
        assertThat(saved.getSpecialty()).isEqualTo("dermatologist");
        assertThat(saved.getDepartment()).isEqualTo("xyz");

        assertThat(response.licenceNumber()).isEqualTo("abc");
    }

}
