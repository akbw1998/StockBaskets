package edu.neu.esd.investmentapp.daos;

import edu.neu.esd.investmentapp.entities.Expert;
import edu.neu.esd.investmentapp.entities.Investment;
import jakarta.persistence.PersistenceException;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class InvestmentDAO extends DAO{
    public void save(Investment investment) throws ResponseStatusException {
        try {
            begin();
            getSession().persist(investment);
            commit();
            close();
        } catch (PersistenceException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Investment already exists.");
        }
    }

    public Investment findById(long investmentId) {
        begin();
        Investment investment = getSession().get(Investment.class, investmentId);
        commit();
        close();
        return investment;
    }

    public Investment findByInvestorIdAndInvestoryId(long investorId, long investoryId) {
        begin();
        Query<Investment> query = getSession().createQuery(
                "FROM Investment WHERE investor.id = :investorId AND investory.id = :investoryId",
                Investment.class
        );
        query.setParameter("investorId", investorId);
        query.setParameter("investoryId", investoryId);
        Investment investment = query.uniqueResult();
        commit();
        close();
        return investment;
    }

    public List<Investment> findAll(){
        begin();
        List<Investment> investments = getSession().createQuery("FROM Investment ",Investment.class).list();
        commit();
        close();
        return investments;
    }

    public List<Investment> findAllByInvestorId(long investorId){
        begin();
        Query<Investment> query = getSession().createQuery("FROM Investment WHERE investor.id = :investorId",Investment.class);
        query.setParameter("investorId", investorId);
        List<Investment> investments = query.list();
        commit();
        close();
        return investments;
    }

    public void merge(Investment investment) {
        begin();
        getSession().merge(investment);
        commit();
        close();
    }

    public void remove(Investment investment) {
        begin();
        getSession().remove(investment);
        commit();
        close();
    }
}
