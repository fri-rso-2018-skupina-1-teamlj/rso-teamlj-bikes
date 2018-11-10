package si.fri.rso.teamlj.bikes.services.beans;

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

    @Inject
    private EntityManager em;


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

        try {
            beginTx();
            bike.setStatus("taken");
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return bike;
    }

    /** posodobimo status kolesa na prost **/
    public Bike bikeFee(Integer bikeId ,Bike bike) {

        Bike b = em.find(Bike.class, bikeId);

        if (b == null) {
            return null;
        }

        try {
            beginTx();
            b.setId(b.getId());
            b.setStatus("free");
            b = em.merge(bike);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return b;
    }

    public boolean deleteBike(Integer bikeId) {

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
