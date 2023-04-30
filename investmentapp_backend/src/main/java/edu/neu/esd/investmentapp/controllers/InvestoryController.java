package edu.neu.esd.investmentapp.controllers;

import com.google.gson.Gson;
import edu.neu.esd.investmentapp.dtos.InvestoryRequestDTO;
import edu.neu.esd.investmentapp.entities.Investory;
import edu.neu.esd.investmentapp.services.InvestoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/investories")
public class InvestoryController {

    private static final Gson gson = new Gson();

    @Autowired
    InvestoryService investoryService;

    @PostMapping // we are creating a new investory tightly coupled to an expert
    public ResponseEntity<?> addInvestory(@RequestBody InvestoryRequestDTO investoryRequestDTO){
        System.out.println("investory DTO : " + investoryRequestDTO.toString());
        try{
            Investory investory = investoryService.addInvestory(investoryRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(investory);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @PutMapping("/{id}") // we are updating an existing investory tightly coupled to an expert
    public ResponseEntity<?> updateInvestory(@PathVariable("id") long investoryId, @RequestBody InvestoryRequestDTO investoryRequestDTO){
        System.out.println("Investory id : " + investoryId);
        System.out.println("investory DTO : " + investoryRequestDTO.toString());
        try{
            Investory updatedInvestory = investoryService.updateInvestory(investoryId, investoryRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updatedInvestory);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllInvestories(@RequestParam(required = false) long[] subscriptionIds,
                                               @RequestParam(required = false) Long expertId) {
        try {
            List<Investory> investories = investoryService.getAllInvestoriesWithFilters(subscriptionIds,expertId);
            return ResponseEntity.ok(investories);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

}
