package si.fri.rso.teamlj.bikes.entities;

import javax.persistence.*;

@Entity(name = "bikes")
@NamedQueries(value =
        {
                @NamedQuery(name = "Bike.getAll", query = "SELECT b FROM bikes b"),
                @NamedQuery(name = "Bike.findByLatAndLon", query = "SELECT b FROM bikes b WHERE b.latitude = :lat AND b.longitude = :lon")
        })
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private float latitude;

    private float longitude;

    private Integer mapId;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMapId() {
        return mapId;
    }

    public void setMapId(Integer mapId) {
        this.mapId = mapId;
    }
}
