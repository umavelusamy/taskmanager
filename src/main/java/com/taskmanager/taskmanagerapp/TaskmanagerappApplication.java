package com.taskmanager.taskmanagerapp;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.taskmanager.taskmanagerapp.entity.Role;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication (exclude = UserDetailsServiceAutoConfiguration.class)
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
@Slf4j
public class TaskmanagerappApplication implements CommandLineRunner {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerappApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN, Limit.of(1));
		if (admins.isEmpty()) {
			User admUser = User.builder()
					.firstname("admin")
					.lastname("admin")
					.email("admin@localhost")
					.password(passwordEncoder.encode("admin"))
					.role(Role.ROLE_ADMIN)
					.build();
			try {
				userRepository.save(admUser);
			} catch (Exception exception) {
				log.info("Error while creating default admin user, exception:", exception);
				System.exit(1);
			}
		}
	}

}
