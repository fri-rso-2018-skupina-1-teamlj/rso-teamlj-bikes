package si.fri.rso.teamlj.bikes.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
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
import javax.ws.rs.core.UriInfo;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class BikesBean {

    private Logger log = Logger.getLogger(BikesBean.class.getName());

    private Client httpClient;

    private String baseUrl;

    @Inject
    private EntityManager em;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        baseUrl = "http://localhost:8081"; // only for demonstration
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

        Bike s = em.find(Bike.class, bikeId);

        if (s == null) {
            return null;
        }

        try {
            beginTx();
            s.setId(s.getId());
            s = em.merge(s);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return s;
    }
	
	/** TODO spremeni to metodo **/
    /*public Bike bikeDelivered(Integer bikeId) {

        Bike bike = getBike(bikeId);

        try {
            beginTx();
            bike.setStatus("delivered");
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        try {
            httpClient
                    .target(baseUrl + "/v1/orders/" + bike.getOrderId() + "/completed")
                    .request()
                    .build("PATCH", Entity.json(""))
                    .invoke();
        } catch (WebApplicationException | ProcessingException e) {
            log.severe(e.getMessage());
            throw new InternalServerErrorException(e);
        }

        return bike;
    }*/

    public boolean deleteBike(String bikeId) {

        Bike bike = em.find(Bike.class, bikeId);

        if (bike != null) {
            try {
                beginTx();
                em.remove(bike);
                commitTx();
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
