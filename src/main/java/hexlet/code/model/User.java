package hexlet.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id; //уникальный идентификатор пользователя, генерируется автоматически
    @NotBlank
    private String firstName; //имя пользователя
    @NotBlank
    private String lastName; //фамилия пользователя
    @Column(unique = true)
    private String email; //адрес электронной почты
    @NotBlank
    @JsonIgnore
    private String password; //пароль
    @CreationTimestamp
    @Temporal(TIMESTAMP)
    private Date createdAt; //дата создания (регистрации) пользователя

}
