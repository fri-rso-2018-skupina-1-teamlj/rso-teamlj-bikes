package si.fri.rso.teamlj.bikes.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import si.fri.rso.teamlj.bikes.MapEntity;
import si.fri.rso.teamlj.bikes.entities.Bike;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@ApplicationScoped
public class BikesBean {

    private Logger log = Logger.getLogger(BikesBean.class.getName());

    private Client httpClient;

    @Inject
    @DiscoverService("rso-bikes")
    private Optional<String> baseUrl;

    @Inject
    @DiscoverService("rso-map")
    private Optional<String> baseUrlMap;

    @Inject
    private EntityManager em;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        //baseUrl = "http://localhost:8084"; // bikes
    }

    public List<Bike> getBikes(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Bike.class, queryParameters);

    }

    public Bike getBike(Integer bikeId) {

        Bike bike = em.find(Bike.class, bikeId);

        if (bike == null) {
            throw new NotFoundException();
        }

        return bike;
    }

    public Bike createBike(Bike bike) {

        try {
            beginTx();
            em.persist(bike);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return bike;
    }

    public Bike putBike(Integer bikeId, Bike bike) {

        Bike b = em.find(Bike.class, bikeId);

        if (b == null) {
            return null;
        }

        try {
            beginTx();
            b.setId(b.getId());
            b = em.merge(bike);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return b;
    }
	
	/** posodobimo status kolesa na zaseden **/
    public Bike bikeTaken(Integer bikeId) {

        Bike bike = getBike(bikeId);

        MapEntity mapEntity = getMapEntity(bike.getMapId());

        try {
            beginTx();
            bike.setStatus("taken");
            bike.setMapId(null);
            bike.setLatitude(0);
            bike.setLongitude(0);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        removeBikeToMapEntity(mapEntity);

        return bike;
    }

    /** posodobimo status kolesa na prost **/
    public Bike bikeFree(Integer bikeId, Float latitude, Float longitude, Bike bike) {

        Bike b = em.find(Bike.class, bikeId);

        List<MapEntity> mapEntityList = getMapEntities();
        Integer mapId = null;
        MapEntity mapEntityTmp = null;
        for (MapEntity mapEntity : mapEntityList) {
            if (mapEntity.getLatitude() == latitude && mapEntity.getLongitude() == longitude) {
                mapId = mapEntity.getId();
                mapEntityTmp = mapEntity;
            }
        }

        if (b == null) {
            return null;
        }

        try {
            beginTx();
            b.setMapId(mapId);
            b.setId(b.getId());
            b.setLatitude(latitude);
            b.setLongitude(longitude);
            b.setStatus("free");
            b = em.merge(bike);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        addBikeToMapEntity(mapEntityTmp);

        return b;
    }

    public List<MapEntity> getMapEntities() {

        try {
            return httpClient
                    .target(baseUrlMap.get()  + "/v1/map")
//                    .target("http://localhost:8084/v1/map")
                    .request().get(new GenericType<List<MapEntity>>() {
                    });
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }

    }

    public MapEntity getMapEntity(Integer mapId) {

        try {
            return httpClient
                    .target(baseUrlMap.get()  + "/v1/map/" + mapId)
//                    .target("http://localhost:8084/v1/map/" + mapId)
                    .request().get(new GenericType<MapEntity>() {
                    });
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }

    }

    public void addBikeToMapEntity(MapEntity mapEntity) {

        mapEntity.setNumberOfAvailableBikes(mapEntity.getNumberOfAvailableBikes() + 1);

        try {
            httpClient
                    .target(baseUrlMap.get() + "/v1/map/"  + mapEntity.getId())
//                    .target("http://localhost:8084/v1/map/" + mapEntity.getId())
                    .request()
                    .build("PUT", Entity.json(mapEntity))
                    .invoke();
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public void removeBikeToMapEntity(MapEntity mapEntity) {

        mapEntity.setNumberOfAvailableBikes(mapEntity.getNumberOfAvailableBikes() - 1);

        try {
            httpClient
                    .target(baseUrlMap.get() + "/v1/map/" + mapEntity.getId())
//                    .target("http://localhost:8084/v1/map/" + mapEntity.getId())
                    .request()
                    .build("PUT", Entity.json(mapEntity))
                    .invoke();
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public boolean deleteBike(Integer bikeId) {

        Bike bike = em.find(Bike.class, bikeId);

        if (bike != null) {
            try {
                beginTx();
                em.remove(bike);
                commitTx();

                MapEntity mapEntity = getMapEntity(bike.getMapId());
                removeBikeToMapEntity(mapEntity);


            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
