package edu.neu.esd.investmentapp.controllers;

import com.google.gson.Gson;
import edu.neu.esd.investmentapp.dtos.SubscriptionRequestDTO;
import edu.neu.esd.investmentapp.entities.Subscription;
import edu.neu.esd.investmentapp.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private static final Gson gson = new Gson();

    @Autowired
    SubscriptionService subscriptionService;

    @PostMapping // we are creating a new subscription entity. Put would be modifying an existing subscription
    public ResponseEntity<?> addSubscription(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO){
        System.out.println("subscription DTO : " + subscriptionRequestDTO.toString());
        try{
            Subscription subscription = subscriptionService.addSubscription(subscriptionRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(subscription);
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSubscriptions(@RequestParam(required = false) Long expertId,
                                                 @RequestParam(required = false) Long investorId) {
            List<Subscription> subscriptions = subscriptionService.getAllSubscriptionsWithFilters(expertId,investorId);
            return ResponseEntity.ok(subscriptions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubscription(@PathVariable("id") long subscriptionId){
        try{
            subscriptionService.deleteSubscription(subscriptionId);
            return ResponseEntity.ok(gson.toJson("Subscription with id = " + subscriptionId + " successfully deleted."));
        }catch(ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

}
