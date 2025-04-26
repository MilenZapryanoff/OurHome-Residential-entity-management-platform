package com.example.OurHome.init;

import com.example.OurHome.model.Entity.Language;
import com.example.OurHome.model.Entity.Role;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.repo.LanguageRepository;
import com.example.OurHome.repo.RoleRepository;
import com.example.OurHome.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminInit implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleInit roleInit;
    private final LanguageInit languageInit;
    private final LanguageRepository languageRepository;

    public AdminInit(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RoleInit roleInit, LanguageInit languageInit, LanguageRepository languageRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleInit = roleInit;
        this.languageInit = languageInit;
        this.languageRepository = languageRepository;
    }

    /**
     * Creating an Admin Role in DB if not present.
     * <p>
     * To be able to create admin role in DB you should first create environment variable 'admin_pass'
     * in your configuration. The value of this variable will be your administrator password (encrypted).
     */

    @Override
    public void run(String... args) throws Exception {

        //Role initialization
        roleInit.roleInitialization();
        Role role = roleRepository.findRoleByName("ADMIN");

        //Language initialization
        languageInit.languageInitialization();
        Language language = languageRepository.findLanguageByDescription("bulgarian");

        if (userRepository.countAllByRole(role) == 0) {
            UserEntity admin = new UserEntity();

            // Administrator user properties
            admin.setEmail("admin@ourhome.bg");
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setRegistrationDateTime(LocalDateTime.now());
            //TODO: to run this code on your local machine you should first create a password for your admin user. You can do this by adding environment variable 'admin_pass' in your IDE.
            admin.setPassword(passwordEncoder.encode("${OurHome.remember.me.key}"));
            admin.setValidated(true);
            admin.setRole(role);
            admin.setLanguage(language);
            userRepository.save(admin);
        }
    }
}

