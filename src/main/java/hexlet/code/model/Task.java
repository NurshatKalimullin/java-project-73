package hexlet.code.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.Temporal;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.Set;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Builder
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    //    name - обязательное. Минимум 1 символ. Названия задач могут быть любыми
    @NotBlank
    private String name;

    //    description - необязательное. Описание задачи, может быть любым
    private String description;

    //    taskStatus - обязательное. Связано с сущностью статуса
    @ManyToOne
    @JoinColumn(name = "task_status_id")
    @NotNull
    private Status taskStatus;

    //    author - обязательное. Создатель задачи, связан с сущностью пользователя
    @ManyToOne
    @JoinColumn(name = "author_id")
    @NotNull
    private User author;

    //    executor - необязательное. Исполнитель задачи, связан с сущностью пользователя
    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    //    createdAt - заполняется автоматически. Дата создания задачи
    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt;

    @ManyToMany
    private Set<Label> labels;

    public Task(final Long id) {
        this.id = id;
    }

}
