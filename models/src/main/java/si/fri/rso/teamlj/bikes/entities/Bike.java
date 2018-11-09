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

    private String longitude;

    private String latitude;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
