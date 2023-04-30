package edu.neu.esd.investmentapp.controllers;

import com.google.gson.Gson;
import edu.neu.esd.investmentapp.entities.Expert;
import edu.neu.esd.investmentapp.services.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/experts")
public class ExpertController {

    private static final Gson gson = new Gson();

    @Autowired
    ExpertService expertService;

    @GetMapping
    public ResponseEntity<?> getAllExperts(@RequestParam(value = "rating", required = false) String ratingOrder) {
        try{
            List<Expert> experts = expertService.getAllExpertsWithFilters(ratingOrder);
            return ResponseEntity.ok(experts);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExpert(@PathVariable("id") Long expertId){
        try{
            return ResponseEntity.ok(expertService.getExpert(expertId));
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }
}
