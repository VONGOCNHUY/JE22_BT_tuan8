package com.example.demo.config;

import com.example.demo.model.Account;
import com.example.demo.model.Role;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadData(AccountRepository accountRepository,
                                      RoleRepository roleRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if data already exists
            if (accountRepository.count() == 0) {
                // Create roles with ROLE_ prefix
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);

                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);

                // Create admin account
                Account adminAccount = new Account();
                adminAccount.setLogin_name("admin");
                adminAccount.setPassword(passwordEncoder.encode("admin123"));
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(adminRole);
                adminAccount.setRoles(adminRoles);
                accountRepository.save(adminAccount);

                // Create user account
                Account userAccount = new Account();
                userAccount.setLogin_name("user");
                userAccount.setPassword(passwordEncoder.encode("user123"));
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(userRole);
                userAccount.setRoles(userRoles);
                accountRepository.save(userAccount);

                System.out.println("Data loaded successfully!");
                System.out.println("Admin account: admin / admin123");
                System.out.println("User account: user / user123");
            }
        };
    }
}
