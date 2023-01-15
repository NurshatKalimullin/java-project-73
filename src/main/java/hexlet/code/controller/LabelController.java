package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.dto.StatusDto;
import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;


@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LabelController.LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String ID = "/{id}";

    public static final String LABEL_CONTROLLER_PATH = "/labels";

    private final LabelService labelService;

    private final LabelRepository labelRepository;


    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "Label created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createStatus(@RequestBody @Valid final LabelDto dto) {
        return labelService.createNewLabel(dto);
    }


    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = Label.class)),
            description = "Get all labels"
    ))
    @GetMapping
    public List<Label> getAll() {
        return labelRepository.findAll()
                .stream()
                .toList();
    }


    @ApiResponses(@ApiResponse(responseCode = "200", description = "Retrieve label by its id"))
    @GetMapping(ID)
    public Label getUserById(@PathVariable final Long id) {
        return labelRepository.findById(id).get();
    }


    @ApiResponse(responseCode = "200", description = "Update label by its id")
    @PutMapping(ID)
    public Label update(@PathVariable final long id, @RequestBody @Valid final LabelDto dto) {
        return labelService.updateLabel(id, dto);
    }

    @ApiResponse(responseCode = "200", description = "Delete label by its id")
    @DeleteMapping(ID)
    public void delete(@PathVariable final long id) {
        labelRepository.deleteById(id);
    }

}
