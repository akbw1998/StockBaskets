package edu.neu.esd.investmentapp.daos;

import edu.neu.esd.investmentapp.entities.QuantifiedStock;
import edu.neu.esd.investmentapp.entities.Stock;
import jakarta.persistence.PersistenceException;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class QuantifiedStockDAO extends DAO{
    public void save(QuantifiedStock quantifiedStock) throws ResponseStatusException {
        try {
            begin();
            getSession().persist(quantifiedStock); // left off here.
            commit();
            close();
        } catch (PersistenceException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company already listed.");
        }
    }

    public QuantifiedStock findById(long quantifiedStockId) {
        begin();
        QuantifiedStock quantifiedStock = getSession().get(QuantifiedStock.class, quantifiedStockId);
        if(quantifiedStock != null)getSession().refresh(quantifiedStock);
        commit();
        close();
        return quantifiedStock;
    }

    public List<QuantifiedStock> findAll(){
        begin();
        Query<QuantifiedStock> query = getSession().createQuery("FROM edu.neu.esd.investmentapp.entities.QuantifiedStock", QuantifiedStock.class);

        List<QuantifiedStock> quantifiedStocks = query.list();
        for(QuantifiedStock quantifiedStock : quantifiedStocks)getSession().refresh(quantifiedStock);
        commit();
        close();
        return quantifiedStocks;
    }

    public void merge(QuantifiedStock quantifiedStock) {
        begin();
        getSession().merge(quantifiedStock);
        commit();
        close();
    }

    public void remove(QuantifiedStock quantifiedStock) {
        begin();
        getSession().remove(quantifiedStock);
        commit();
        close();
    }
}
