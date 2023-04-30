package edu.neu.esd.investmentapp.controllers;

import com.google.gson.Gson;
import edu.neu.esd.investmentapp.dtos.AddToWalletRequestDTO;
import edu.neu.esd.investmentapp.dtos.InvestmentAddRequestDTO;
import edu.neu.esd.investmentapp.dtos.InvestmentUpdateRequestDTO;
import edu.neu.esd.investmentapp.entities.Investment;
import edu.neu.esd.investmentapp.entities.Investor;
import edu.neu.esd.investmentapp.services.InvestmentService;
import edu.neu.esd.investmentapp.services.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/investors")
public class InvestorController {

    private static final Gson gson = new Gson();

    @Autowired
    InvestorService investorService;

    @Autowired
    InvestmentService investmentService;

    @GetMapping
    public ResponseEntity<List<Investor>> getAllInvestors(){
        return ResponseEntity.ok(investorService.getAllInvestors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvestor(@PathVariable("id") Long investorId){
        try{
            return ResponseEntity.ok(investorService.getInvestor(investorId));
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @PutMapping("/{id}/wallet")
    public ResponseEntity<?> addToWallet(@PathVariable("id") long id, @RequestBody AddToWalletRequestDTO addToWalletRequestDTO) {
        try{
            Investor investor = investorService.addToWallet(id,addToWalletRequestDTO.getAmount());
            return ResponseEntity.ok(investor);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @PostMapping("/{id}/investments") // we are creating a new investment entity. Put would be modifying an existing subscription
    public ResponseEntity<?> addInvestment(@PathVariable("id") long investorId, @RequestBody InvestmentAddRequestDTO investmentAddRequestDTO){
        System.out.println("investment DTO : " + investmentAddRequestDTO.toString());
        try{
            Investment investment = investmentService.addInvestment(investorId, investmentAddRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(investment);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @GetMapping("/investments")
    public ResponseEntity<?> getAllInvestments() {
        try{
            List<Investment> investments = investmentService.getAllInvestments();
            return ResponseEntity.ok(investments);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @GetMapping("/{id}/investments")
    public ResponseEntity<?> getAllInvestmentsByInvestorId(@PathVariable("id") long investorId) {
        try{
            System.out.println("In get by investorhere");
            List<Investment> investments = investmentService.getAllInvestmentsByInvestorId(investorId);
            return ResponseEntity.ok(investments);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @DeleteMapping("/{investorId}/investments/{investmentId}")
    public ResponseEntity<?> deleteInvestment(@PathVariable("investorId") long investorId, @PathVariable("investmentId") long investmentId){
        try{
            investmentService.deleteInvestment(investorId, investmentId);
            return ResponseEntity.ok(gson.toJson("Investment with id = " + investmentId + " of investor with id = " + investorId + " deleted."));
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @PutMapping("/{investorId}/investments/{investmentId}")
    public ResponseEntity<?> updateInvestment(@PathVariable("investorId") long investorId,
                                              @PathVariable("investmentId") long investmentId,
                                              @RequestBody InvestmentUpdateRequestDTO investmentUpdateRequestDTO){
        try{
            Investment updatedInvestment = investmentService.updateInvestment(investorId, investmentId, investmentUpdateRequestDTO);
            return ResponseEntity.ok(updatedInvestment);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }
}
