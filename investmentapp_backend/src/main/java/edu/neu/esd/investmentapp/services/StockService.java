package edu.neu.esd.investmentapp.services;

import edu.neu.esd.investmentapp.daos.StockDAO;
import edu.neu.esd.investmentapp.dtos.StockRequestDTO;
import edu.neu.esd.investmentapp.entities.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@Service
public class StockService {

    private final StockDAO stockDAO;
    private final Random random = new Random();

    @Autowired
    public StockService(StockDAO stockDAO) {
        this.stockDAO = stockDAO;
    }

    public Stock createStock(StockRequestDTO stockRequestDTO) throws ResponseStatusException {
        Stock stock = Stock
                        .builder()
                        .company(stockRequestDTO.getCompany())
                        .currentSharePrice(stockRequestDTO.getCurrentSharePrice())
                        .build();
        stockDAO.save(stock);
        return stock;
    }

    public List<Stock> getAllStocks(){
        return stockDAO.findAll();
    }

    public void updateStockPrices() throws ResponseStatusException{
        try{
            List<Stock> stocks = stockDAO.findAll();
            Random random = new Random();
            for (Stock stock : stocks) {
                double fluctuation = (random.nextDouble() - 0.5) * 0.1; // generate a random number between -5% and 5%
                double newPrice = stock.getCurrentSharePrice() * (1 + fluctuation); // calculate the new price
                stock.setCurrentSharePrice(newPrice); // update the currentSharePrice of the stock
                stockDAO.merge(stock); // save the updated stock to the database
            }
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating stock prices on market change");
        }

    }

}
