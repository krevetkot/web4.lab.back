package labs.web4_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users", schema = "s409577")
@Data
@NoArgsConstructor
@Getter
@Setter
public class User {
    private String login;
    private String password;
    @Id
    @GeneratedValue
    private Integer user_id;

    public User(String login, String password){
        this.login = login;
        this.password = password;
    }
}
