package com.taskmanager.taskmanagerapp.repository;

import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.taskmanagerapp.entity.Role;
import com.taskmanager.taskmanagerapp.entity.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findById(Integer id);

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role, Limit limit);
}
