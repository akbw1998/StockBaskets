package edu.neu.esd.investmentapp.daos;

import edu.neu.esd.investmentapp.entities.Expert;
import jakarta.persistence.PersistenceException;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class ExpertDAO extends DAO{
    public void save(Expert expert) throws ResponseStatusException{
        try {
            begin();
            getSession().persist(expert);
            commit();
            close();
        } catch (PersistenceException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists.");
        }
    }

    public Expert findById(long expertId) {
        begin();
        Expert expert = getSession().get(Expert.class, expertId);
        commit();
        close();
        return expert;
    }

    public List<Expert> findAll(){
        begin();
        List<Expert> experts = getSession().createQuery("FROM Expert",Expert.class).list();
        commit();
        close();
        return experts;
    }

    public List<Expert> findAllByRating(String ratingOrder) throws ResponseStatusException{
        if(!ratingOrder.equals("asc") && !ratingOrder.equals("desc")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating order not recognized.");
        }

        String hql = "FROM Expert e ORDER BY e.averageRating " + ratingOrder;
        begin();
        Query<Expert> query = getSession().createQuery(hql,Expert.class);
        List<Expert> experts = query.list();
        commit();
        close();
        return experts;
    }

    public Expert findByEmail(String email) {
        begin();
        Query<Expert> query = getSession().createQuery("FROM Expert WHERE email = :email", Expert.class);
        query.setParameter("email", email);
        Expert expert = query.uniqueResult();
        commit();
        close();
        return expert;
    }

    public void merge(Expert expert) {
        begin();
        getSession().merge(expert);
        commit();
        close();
    }

//    public void remove(Expert expert) {
//        begin();
//        getSession().remove(expert);
//        commit();
//    }
}
