package hexlet.code.dto;

import hexlet.code.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    @Size(min = 1)
    private String name;

    @Lob
    private String description;

    @NotNull
    private Long taskStatusId;

    private Long executorId;
}
