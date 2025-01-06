package labs.web4_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "points", schema = "s409577")
@Data
@NoArgsConstructor
@Getter
@Setter
public class Point {
    private float x;
    private float y;
    private float r;
    private boolean isHit;
    @Id
    @GeneratedValue
    private Long id;
    public Point(float x, float y, float r, boolean isHit){
        this.x = x;
        this.y = y;
        this.r = r;
        this.isHit = isHit;
    }
    public boolean getIsHit(){
        return isHit;
    }
}
