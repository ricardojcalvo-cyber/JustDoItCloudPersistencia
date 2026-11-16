package teccr.justdoitcloud.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import teccr.justdoitcloud.data.Task;
import teccr.justdoitcloud.data.User;
import teccr.justdoitcloud.repository.TaskRepository;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasksForUser(User user) {
        return taskRepository.findByUserId(user.getId());
    }

    public void addTaskToUser(User user, Task task) {
        task.setUserId(user.getId());
        taskRepository.save(task);
    }

    @Transactional
    public void advanceTask(User user, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada: " + taskId));

        // Seguridad: la tarea debe pertenecer al usuario en sesión
        if (!user.getId().equals(task.getUserId())) {
            throw new IllegalArgumentException("La tarea no pertenece al usuario actual");
        }

        if (task.getStatus() != Task.Status.DONE) {
            taskRepository.save(task.advance());   // UPDATE en justdoitdb
        }
    }
}