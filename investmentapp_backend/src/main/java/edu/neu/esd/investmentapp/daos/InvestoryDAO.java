package edu.neu.esd.investmentapp.daos;
import edu.neu.esd.investmentapp.entities.Investory;
import jakarta.persistence.PersistenceException;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class InvestoryDAO extends DAO{
    public void save(Investory investory) throws ResponseStatusException {
        try {
            begin();
            getSession().persist(investory);
            commit();
            close();
        } catch (PersistenceException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error persisting investory");
        }
    }

    public Investory findById(long investoryId) {
        begin();
        Investory investory = getSession().get(Investory.class, investoryId);
        commit();
        close();
        return investory;
    }

    public List<Investory> findAll(){
        begin();
        Query<Investory> query = getSession().createQuery("FROM edu.neu.esd.investmentapp.entities.Investory", Investory.class);

        List<Investory> investories = query.list();
        commit();
        close();
        return investories;
    }

    public List<Investory> findAllByExpertId(long expertId){
        begin();
        Query<Investory> query = getSession().createQuery("FROM Investory i WHERE i.expert.id = :expertId", Investory.class);
        query.setParameter("expertId", expertId);
        List<Investory> investories = query.list();
        commit();
        close();

        return investories;
    }

    public void merge(Investory investory) {
        begin();
        getSession().merge(investory);
        commit();
        close();
    }

    public void remove(Investory investory) {
        begin();
        getSession().remove(investory);
        commit();
        close();
    }
}
