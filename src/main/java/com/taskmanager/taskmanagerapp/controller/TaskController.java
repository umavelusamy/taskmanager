package com.taskmanager.taskmanagerapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.exception.ResourceNotFoundException;
import com.taskmanager.taskmanagerapp.model.EmailDetails;
import com.taskmanager.taskmanagerapp.repository.TaskRepository;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import com.taskmanager.taskmanagerapp.service.MailSenderService;
import com.taskmanager.taskmanagerapp.utils.AppUtils;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RolesAllowed({ "USER", "ADMIN" })
@RequestMapping(path = "/api/task")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final MailSenderService mailSenderService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Task> tasks = taskRepository.findAll();
        return AppUtils.httpResponseOk(tasks);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Task task) {
        try {
            Task taskCreated = taskRepository.save(task);

            // Async email for task created
            buildAndSendEmail(taskCreated, "created");

            return AppUtils.httpResponseOk(taskCreated);
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        try {
            Optional<Task> task = taskRepository.findById(id);
            if (!task.isPresent()) {
                AppUtils.httpResponseBadRequest("task not found");
            }
            return AppUtils.httpResponseOk(task);
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable(value = "id") Long id,
            @Valid @RequestBody Task taskUpdated) {
        try {
            Optional<Task> taskOrg = taskRepository.findById(id);

            if (!taskOrg.isPresent()) {
                return AppUtils.httpResponseBadRequest("task not found");
            }

            Task task = taskOrg.get();
            // update user updated fields
            if (taskUpdated.getTitle() != null) {
                task.setTitle(taskUpdated.getTitle());
            }
            if (taskUpdated.getDescription() != null) {
                task.setDescription(taskUpdated.getDescription());
            }
            if (taskUpdated.getStatus() != null) {
                task.setStatus(taskUpdated.getStatus());
            }
            if (taskUpdated.getAssignee() != null) {
                task.setAssignee(taskUpdated.getAssignee());
            }
            if (taskUpdated.getDueDate() != null) {
                task.setDueDate(taskUpdated.getDueDate());
            }
            final Task taskPostUpdation = taskRepository.save(task);

            // async email for task updated
            buildAndSendEmail(taskPostUpdation, "updated");

            return AppUtils.httpResponseOk(taskPostUpdation);
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(Task task) {
        try {
            taskRepository.deleteById(task.getId());
            return AppUtils.httpResponseOk("deleted");
        } catch (Exception exception) {
            return AppUtils.httpResponseBadRequest(exception.getMessage(), exception);
        }
    }

    private void buildAndSendEmail(Task task, String status) {

        // retrieve user again from db to get assignee email id.
        // email should be sent to this id.
        Optional<User> assigneeUserFromDb = userRepository.findById(task.getAssignee().getId());

        if (assigneeUserFromDb.isPresent()) {
            mailSenderService.sendMail(
                    EmailDetails
                            .builder()
                            .toAddress(assigneeUserFromDb.get().getEmail())
                            .subject("Task has been " + status)
                            .mailBody("A task with Title: " + task.getTitle()
                                    + " Description: " + task.getDescription()
                                    + " Due date: " + task.getDueDate()
                                    + " Status: " + task.getStatus()
                                    + " is " + status + " and assigned to you")
                            .build());
            log.info("Task create/update email sent successfully");
        }

    }
}
