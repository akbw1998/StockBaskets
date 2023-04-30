package edu.neu.esd.investmentapp.daos;

import edu.neu.esd.investmentapp.entities.Subscription;
import jakarta.persistence.PersistenceException;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Repository
public class SubscriptionDAO extends DAO{
    public void save(Subscription subscription) throws ResponseStatusException {
        try {
            begin();
            getSession().persist(subscription);
            commit();
            close();
        } catch (PersistenceException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Subscription already exists.");
        }
    }

    public Subscription findById(long subscriptionId) {
        begin();
        Subscription subscription = getSession().get(Subscription.class, subscriptionId);
        commit();
        close();
        return subscription;
    }

    public List<Subscription> findAll(){
        begin();
        List<Subscription> subscriptions = getSession().createQuery("FROM Subscription ",Subscription.class).list();
        commit();
        close();
        return subscriptions;
    }
    public List<Subscription> findAllByExpertId(long expertId){
        String hql = "FROM Subscription s WHERE s.expert.id = :expertId";
        begin();
        Query<Subscription> query = getSession().createQuery(hql, Subscription.class);
        query.setParameter("expertId", expertId);
        List<Subscription> results = query.getResultList();
        commit();
        close();
        return results;
    }
    public List<Subscription> findAllByInvestorId(long investorId){
        String hql = "FROM Subscription s WHERE s.investor.id = :investorId";
        begin();
        Query<Subscription> query = getSession().createQuery(hql, Subscription.class);
        query.setParameter("investorId", investorId);
        List<Subscription> results = query.getResultList();
        commit();
        close();
        return results;
    }
    public Subscription findByInvestorIdAndExpertId(long investorId, long expertId){
        String hql = "FROM Subscription s WHERE s.investor.id = :investorId AND s.expert.id = :expertId";
        begin();
        Query<Subscription> query = getSession().createQuery(hql, Subscription.class);
        query.setParameter("investorId", investorId);
        query.setParameter("expertId", expertId);
        Subscription subscription = query.uniqueResult();
        commit();
        close();
        return subscription;
    }

    public void merge(Subscription subscription) {
        begin();
        getSession().merge(subscription);
        commit();
        close();
    }

    public void remove(Subscription subscription) {
        System.out.println("-----Entered subscriptionDAO remove method---------------");
        begin();
        getSession().remove(subscription);
        commit();
        close();
    }
}
