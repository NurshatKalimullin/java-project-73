package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;


@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";

    private static final String ONLY_TASK_OWNER = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;

    private final TaskRepository taskRepository;
    private final TaskService taskService;


    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Task createNewPost(@RequestBody @Valid final TaskDto dto) {
        System.out.println("Creation has started");
        return taskService.createNewTask(dto);
    }


    @Operation(summary = "Get all tasks by Predicate")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content (schema =
    @Schema (implementation = Task.class))
    ))
    @GetMapping
    public Iterable<Task> getAllTasks(@QuerydslPredicate final Predicate predicate) {
        return predicate == null ? taskRepository.findAll() : taskRepository.findAll(predicate);
    }

    @Operation(summary = "Get a task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task is found", content =
            @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task with this id is not found")})
    @GetMapping(ID)
    public Task getTask(@PathVariable long id) {
        return taskRepository.findById(id).get();
    }


    @Operation(summary = "Update task by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task is updated", content =
            @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task with this id is not found")})
    @PutMapping(ID)
    @PreAuthorize(ONLY_TASK_OWNER)
    public Task update(@PathVariable final long id, @RequestBody @Valid final TaskDto dto) {
        return taskService.updateTask(id, dto);
    }


    @Operation(summary = "Delete task by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task is deleted", content =
            @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "404", description = "Task with this id is not found")})
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_TASK_OWNER)
    public void delete(@PathVariable final long id) {
        taskRepository.deleteById(id);
    }

}
