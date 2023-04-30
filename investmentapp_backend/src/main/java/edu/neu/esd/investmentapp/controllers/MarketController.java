package edu.neu.esd.investmentapp.controllers;

import com.google.gson.Gson;
import edu.neu.esd.investmentapp.dtos.StockRequestDTO;
import edu.neu.esd.investmentapp.entities.Stock;
import edu.neu.esd.investmentapp.services.InvestmentService;
import edu.neu.esd.investmentapp.services.InvestoryService;
import edu.neu.esd.investmentapp.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/market")
public class MarketController {

    private static final Gson gson = new Gson();

    @Autowired
    StockService stockService;

    @Autowired
    InvestoryService investoryService;

    @Autowired
    InvestmentService investmentService;

    @PostMapping("/stocks")
    public ResponseEntity<?> createStock(@RequestBody StockRequestDTO stockRequestDTO){
        System.out.println("stock DTO : " + stockRequestDTO.toString());
        try{
            Stock stock = stockService.createStock(stockRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(stock);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @PutMapping()
    public ResponseEntity<String> updateMarket(){
        try{
            stockService.updateStockPrices();
            investoryService.updateInvestoryPortfolio();
            investmentService.updateInvestmentPortfolio();
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson("Market updated"));
        }catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }

    }

    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getAllStocks(){
        return ResponseEntity.ok(stockService.getAllStocks());
    }

}
