package hexlet.code.controller;

import hexlet.code.dto.StatusDto;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import hexlet.code.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

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


    @Operation(summary = "Get all statuses")
    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = Status.class)),
            description = "Get all statuses"
    ))
    @GetMapping
    public List<Status> getAll() {
        return statusRepository.findAll()
                .stream()
                .toList();
    }


    @Operation(summary = "Retrieve status by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status is found", content =
            @Content(schema = @Schema(implementation = Status.class))),
            @ApiResponse(responseCode = "404", description = "Status with this id is not found")})
    @GetMapping(ID)
    public Status getStatusById(@PathVariable final Long id) {
        return statusRepository.findById(id).get();
    }


    @Operation(summary = "Update status by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status is updated", content =
            @Content(schema = @Schema(implementation = Status.class))),
            @ApiResponse(responseCode = "404", description = "Status with this id is not found")})
    @PutMapping(ID)
    public Status update(@PathVariable final long id, @RequestBody @Valid final StatusDto dto) {
        return statusService.updateStatus(id, dto);
    }

    @Operation(summary = "Delete status by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status is deleted", content =
            @Content(schema = @Schema(implementation = Status.class))),
            @ApiResponse(responseCode = "404", description = "Status with this id is not found")})
    @DeleteMapping(ID)
    public void delete(@PathVariable final long id) {
        statusRepository.deleteById(id);
    }
}
