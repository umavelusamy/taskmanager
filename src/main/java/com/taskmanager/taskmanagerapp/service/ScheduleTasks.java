package com.taskmanager.taskmanagerapp.service;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.model.EmailDetails;
import com.taskmanager.taskmanagerapp.repository.TaskRepository;
import com.taskmanager.taskmanagerapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleTasks {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final MailSenderService mailSenderService;

    // @Scheduled(cron = "*/10 * * * * *")
    @Scheduled(cron = "0 0 21 * * *")
    public void reminderSchedule() {
        Set<?> distinctAssignee = taskRepository.findDistinctAssignee();
        Iterator<?> iterator = distinctAssignee.iterator();
        while (iterator.hasNext()) {
            int user_id = (Integer) iterator.next();
            sendScheduledReminderEmail(user_id);
        }
    }

    private void sendScheduledReminderEmail(Integer user_id) {

        try {
        
        Optional<User> assigneeUserFromDb = userRepository.findById(user_id);

        if (assigneeUserFromDb.isPresent()) {
            List<Task> singleUserTasks = taskRepository.findByAssignee(assigneeUserFromDb.get());
            ListIterator<Task> task = singleUserTasks.listIterator();// 102. 152
            String msgBody = "Tasks with below Task ids are assigned to you."+"\n";
            while (task.hasNext()) {
                Task tempTask = new Task();
                tempTask = task.next();
                msgBody = msgBody + "ID: " + tempTask.getId().toString()
                        + " Task_Title: " + tempTask.getTitle()
                        + " Task_Description: " + tempTask.getDescription()
                        + "\n";
            }

            mailSenderService.sendMail(
                    EmailDetails
                            .builder()
                            .toAddress(assigneeUserFromDb.get().getEmail())
                            .subject("[Reminder] Tasks Assigned")
                            .mailBody(msgBody)
                            .build());
        }
    } catch (Exception exception) {
        log.error("Error while sending scheduled reminder email", exception);
    }
    }
}
