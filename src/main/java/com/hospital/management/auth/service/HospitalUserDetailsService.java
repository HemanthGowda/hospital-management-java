package com.hospital.management.auth.service;

import com.hospital.management.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class HospitalUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public HospitalUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(username)
                .map(HospitalUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
