package hexlet.code.controller;

import hexlet.code.dto.StatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Status;
import hexlet.code.model.User;
import hexlet.code.repository.StatusRepository;
import hexlet.code.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static hexlet.code.controller.StatusController.STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;



@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + STATUS_CONTROLLER_PATH)
public class StatusController {

    public static final String ID = "/{id}";

    public static final String STATUS_CONTROLLER_PATH = "/statuses";

    private final StatusService statusService;

    private final StatusRepository statusRepository;


    @Operation(summary = "Create new status")
    @ApiResponse(responseCode = "201", description = "Status created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Status createStatus(@RequestBody @Valid final StatusDto dto) {
        return statusService.createNewStatus(dto);
    }


    @ApiResponses(@ApiResponse(responseCode = "200", content =
            // Указываем тип содержимого ответа
    @Content(schema = @Schema(implementation = User.class))
    ))
    @GetMapping
    public List<Status> getAll() {
        return statusRepository.findAll()
                .stream()
                .toList();
    }


    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public Status getUserById(@PathVariable final Long id) {
        return statusRepository.findById(id).get();
    }


    @PutMapping(ID)
    public Status update(@PathVariable final long id, @RequestBody @Valid final StatusDto dto) {
        return statusService.updateStatus(id, dto);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final long id) {
        statusRepository.deleteById(id);
    }
}
