package si.fri.rso.teamlj.bikes.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "bikes")
@NamedQueries(value =
        {
                @NamedQuery(name = "Bike.getAll", query = "SELECT b FROM bikes b")
        })
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String location;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
