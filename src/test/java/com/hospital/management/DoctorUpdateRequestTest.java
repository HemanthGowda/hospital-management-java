package com.hospital.management;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.hospital.management.doctor.dto.DoctorUpdateRequest;

public class DoctorUpdateRequestTest {
    private static Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void rejectsIfDoctorNameIsBlank() {
        DoctorUpdateRequest request = new DoctorUpdateRequest("", "dsjd", "jhd", "sjdn");

        Set<ConstraintViolation<DoctorUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorLicenceNumberIsBlank() {
        DoctorUpdateRequest request = new DoctorUpdateRequest("doctor", " ", "jhd", "sjdn");

        Set<ConstraintViolation<DoctorUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorSpecialityIsBlank() {
        DoctorUpdateRequest request = new DoctorUpdateRequest("doctor", "abc123 ", " ", "sjdn");

        Set<ConstraintViolation<DoctorUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorDepartmentIsBlank() {
        DoctorUpdateRequest request = new DoctorUpdateRequest("doctor", "abc123 ", "xyz", " ");

        Set<ConstraintViolation<DoctorUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorNameIsAboveRange() {

        String fullName = "a".repeat(161);
        DoctorUpdateRequest request = new DoctorUpdateRequest(fullName, "abc", "accc", "xyz");

        Set<ConstraintViolation<DoctorUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorLicenceNumberIsAboveRange() {

        String licenceNumber = "a1".repeat(21);
        DoctorUpdateRequest request = new DoctorUpdateRequest("abc", licenceNumber, "accc", "xyz");

        Set<ConstraintViolation<DoctorUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorSpecialityIsAboveRange() {

        String speciality = "a2".repeat(41);
        DoctorUpdateRequest request = new DoctorUpdateRequest("abc", "ab12", speciality, "xyz");

        Set<ConstraintViolation<DoctorUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

    @Test
    void rejectsIfDoctorDepartmentIsAboveRange() {

        String department = "xy".repeat(41);
        DoctorUpdateRequest request = new DoctorUpdateRequest("bsd", "abc", "accc", department);

        Set<ConstraintViolation<DoctorUpdateRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
    }

}
