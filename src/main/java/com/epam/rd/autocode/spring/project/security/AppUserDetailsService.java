package com.epam.rd.autocode.spring.project.security;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.springframework.security.core.GrantedAuthority;
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
        return employeeRepository.findByEmail(username)
            .<UserDetails>map(e -> toDetails(
                e, List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))))
            .orElseGet(() -> clientRepository.findByEmail(username)
                .map(c -> toDetails(
                    c, List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));
    }

    private boolean enabledOrDefault(Boolean enabled) {
        return enabled == null || enabled;
    }

    private UserDetails toDetails(Employee e, List<? extends GrantedAuthority> auth) {
        boolean enabled = enabledOrDefault(e.getEnabled());
        return new AppUserDetails(e.getId(), e.getEmail(), e.getPassword(), auth, enabled);
    }

    private UserDetails toDetails(Client c, List<? extends GrantedAuthority> auth) {
        boolean enabled = enabledOrDefault(c.getEnabled());
        return new AppUserDetails(c.getId(), c.getEmail(), c.getPassword(), auth, enabled);
    }
}
