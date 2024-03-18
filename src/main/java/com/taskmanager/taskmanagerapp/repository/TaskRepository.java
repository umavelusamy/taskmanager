package com.taskmanager.taskmanagerapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.entity.User;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = "SELECT DISTINCT ap.user_id FROM app_tasks ap", nativeQuery = true)
    Set<?> findDistinctAssignee();

    List<Task> findByAssignee(User assignee);
    
}
