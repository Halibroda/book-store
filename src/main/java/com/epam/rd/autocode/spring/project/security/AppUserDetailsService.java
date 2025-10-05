package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;

    public AppUserDetailsService(EmployeeRepository employeeRepository,
                                 ClientRepository clientRepository) {
        this.employeeRepository = employeeRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee e = employeeRepository.findByEmail(username).orElse(null);
        if (e != null) {
            return new AppUserDetails(e.getId(), e.getEmail(), e.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE")), true);
        }
        Client c = clientRepository.findByEmail(username).orElse(null);
        if (c != null) {
            return new AppUserDetails(c.getId(), c.getEmail(), c.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_CLIENT")), true);
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
