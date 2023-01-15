package hexlet.code.service;

import hexlet.code.dto.StatusDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final StatusService statusService;

    @Override
    public Task createNewTask(TaskDto dto) {
        final Task newTask = fromDto(dto);
        return taskRepository.save(newTask);
    }

    @Override
    public Task updateTask(final long id, final TaskDto dto) {
        final Task task = taskRepository.findById(id).get();
        merge(task, dto);
        return taskRepository.save(task);
    }

    private void merge(final Task task, final TaskDto dto) {
        final Task newTask = fromDto(dto);
        task.setExecutor(newTask.getExecutor());
        task.setTaskStatus(newTask.getTaskStatus());
        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
    }

    private Task fromDto(final TaskDto dto) {
        final User author = userService.getCurrentUser();
        final User executor = Optional.ofNullable(dto.getExecutorId())
                .map(User::new)
                .orElse(null);
        final Status status = Optional.ofNullable(dto.getTaskStatusId())
                .map(Status::new)
                .orElse(null);


        return Task.builder()
                .author(author)
                .executor(executor)
                .taskStatus(status)
                //.labels(labels)
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
