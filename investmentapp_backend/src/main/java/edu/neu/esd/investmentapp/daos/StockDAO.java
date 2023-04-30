package edu.neu.esd.investmentapp.daos;

import edu.neu.esd.investmentapp.entities.Stock;
import jakarta.persistence.PersistenceException;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class StockDAO extends DAO{

    public void save(Stock stock) throws ResponseStatusException {
        try {
            begin();
            getSession().persist(stock);
            commit();
            close();
        } catch (PersistenceException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Company already listed.");
        }
    }

    public Stock findById(long stockId) {
        begin();
        Stock stock = getSession().get(Stock.class, stockId);
        commit();
        close();
        return stock;
    }

    public List<Stock> findAll(){
        begin();
        Query<Stock> query = getSession().createQuery("FROM edu.neu.esd.investmentapp.entities.Stock", Stock.class);
        List<Stock> stocks = query.list();
        commit();
        close();
        return stocks;
    }

    public void merge(Stock stock) {
        begin();
        getSession().merge(stock);
        commit();
        close();
    }

    public void remove(Stock stock) {
        begin();
        getSession().remove(stock);
        commit();
        close();
    }
}
