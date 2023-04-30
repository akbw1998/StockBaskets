package edu.neu.esd.investmentapp.daos;
import edu.neu.esd.investmentapp.entities.Investor;
import jakarta.persistence.PersistenceException;
import org.hibernate.Hibernate;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
@Transactional(rollbackFor = Exception.class)
public class InvestorDAO extends DAO{
    public void save(Investor investor) throws ResponseStatusException{
        try {
            begin();
            getSession().persist(investor);
            commit();
            close();
        } catch (PersistenceException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error persisting investor.");
        }
    }

    public Investor findById(long investorId) {
        begin();
        Investor investor = getSession().get(Investor.class, investorId);
        Hibernate.initialize(investor.getInvestments()); // this along with removing cascade worked.
        Hibernate.initialize(investor.getSubscriptions());
        commit();
        close();
        return investor;
    }

    public List<Investor> findAll(){
        begin();
        Query<Investor> query = getSession().createQuery("FROM Investor", Investor.class);
        List<Investor> investors = query.list();
        for(Investor investor : investors){
            Hibernate.initialize(investor.getInvestments()); // this along with removing cascade worked.
            Hibernate.initialize(investor.getSubscriptions());
        }
        commit();
        close();
        return investors;
    }

    public Investor findByEmail(String email) {
        begin();
        Query<Investor> query = getSession().createQuery("FROM Investor WHERE email = :email", Investor.class);
        query.setParameter("email", email);
        Investor investor = query.uniqueResult();
        if(investor != null){
            Hibernate.initialize(investor.getInvestments());
            Hibernate.initialize(investor.getSubscriptions());
        }
        commit();
        close();
        return investor;
    }

    public void merge(Investor investor) {
        begin();
        getSession().merge(investor);
        commit();
        close();
    }

    public void remove(Investor investor) {
        begin();
        getSession().remove(investor);
        commit();
        close();
    }
}
