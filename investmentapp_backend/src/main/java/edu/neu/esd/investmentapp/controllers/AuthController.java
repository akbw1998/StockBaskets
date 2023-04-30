package edu.neu.esd.investmentapp.controllers;

import com.google.gson.Gson;
import edu.neu.esd.investmentapp.dtos.LoginRequestDTO;
import edu.neu.esd.investmentapp.dtos.RegisterRequestDTO;
import edu.neu.esd.investmentapp.dtos.UserDTO;
import edu.neu.esd.investmentapp.entities.Expert;
import edu.neu.esd.investmentapp.entities.Investor;
import edu.neu.esd.investmentapp.services.AuthenticationService;
import edu.neu.esd.investmentapp.utils.BasicAuthDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Gson gson = new Gson();

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(path = "/register",consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody final RegisterRequestDTO registerRequestDTO){
        System.out.println("Inside registerInvestor endpoint : " + "/register/investor");
        System.out.println("Passed in object : ");
        System.out.println(registerRequestDTO.toString());
        try{
            final UserDTO registeredUser = authenticationService.register(registerRequestDTO);
            if (registeredUser instanceof Investor) {
                Investor registeredInvestor = (Investor) registeredUser;
                return ResponseEntity.status(HttpStatus.OK).body(registeredInvestor);
            } else{
                Expert registeredExpert = (Expert) registeredUser;
                return ResponseEntity.status(HttpStatus.CREATED).body(registeredExpert);
            }
        }catch (ResponseStatusException e) {
            System.out.println("In controller catch");
            System.out.println("e details : " + e.toString());
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestHeader final String authorization, @RequestBody final LoginRequestDTO loginRequestDTO){
        final String role = loginRequestDTO.getRole();
        final BasicAuthDecoder basicAuthDecoder = new BasicAuthDecoder(authorization);
        try{
            final UserDTO authenticatedUser = authenticationService.authenticate(basicAuthDecoder.getEmail(), basicAuthDecoder.getPassword(), role);
            if (authenticatedUser instanceof Investor) {
                Investor authenticatedInvestor = (Investor) authenticatedUser;
                return ResponseEntity.status(HttpStatus.OK).body(authenticatedInvestor);
            } else{
                Expert authenticatedExpert = (Expert) authenticatedUser;
                return ResponseEntity.status(HttpStatus.OK).body(authenticatedExpert);
            }
        }catch(ResponseStatusException e){
            System.out.println("In controller catch");
            System.out.println("e details : " + e.toString());
            return ResponseEntity.status(e.getStatusCode().value()).body((gson.toJson(e.getMessage())));
        }
    }
}
