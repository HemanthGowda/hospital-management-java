package com.hospital.management.doctor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.hospital.management.doctor.model.DoctorDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hospital.management.doctor.repository.DoctorRepository;
import com.hospital.management.doctor.service.DoctorMapper;
import com.hospital.management.doctor.service.DoctorService;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.doctor.dto.DoctorRegister;
import com.hospital.management.doctor.dto.DoctorResponse;
import com.hospital.management.doctor.dto.DoctorResponseForView;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        doctorService = new DoctorService(doctorRepository, new DoctorMapper());

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
    void ViewInvalidDoctorId() {
        when(doctorRepository.findById("invalidId")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> doctorService.viewDoctor("invalidId"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void ViewRegisteredDoctorById() {

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
        doctorService.register(request);

        ArgumentCaptor<DoctorDocument> captor = ArgumentCaptor.forClass(DoctorDocument.class);
        verify(doctorRepository).save(captor.capture());

        DoctorDocument saved = captor.getValue();
        when(doctorRepository.findById("1")).thenReturn(Optional.of(saved));

        DoctorResponseForView view = doctorService.viewDoctor("1");
        assertThat(view.fullName()).isEqualTo("abc");
        assertThat(view.licenceNumber()).isEqualTo("abc");
        assertThat(view.speciality()).isEqualTo("dermatologist");
        assertThat(view.department()).isEqualTo("xyz");

    }

}