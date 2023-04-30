package edu.neu.esd.investmentapp.services;

import edu.neu.esd.investmentapp.daos.ExpertDAO;
import edu.neu.esd.investmentapp.daos.InvestorDAO;
import edu.neu.esd.investmentapp.dtos.RegisterRequestDTO;
import edu.neu.esd.investmentapp.dtos.UserDTO;
import edu.neu.esd.investmentapp.entities.Expert;
import edu.neu.esd.investmentapp.entities.Investor;
import edu.neu.esd.investmentapp.provider.PasswordCryptographyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

@Service
public class AuthenticationService {

    private final InvestorDAO investorDAO;
    private final ExpertDAO expertDAO;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    public AuthenticationService(InvestorDAO investorDAO, ExpertDAO expertDAO) {
        this.investorDAO = investorDAO;
        this.expertDAO = expertDAO;
    }


    public UserDTO register(RegisterRequestDTO registerRequestDTO) throws ResponseStatusException{
        UserDTO registeredUser = null;
        final String role = registerRequestDTO.getRole();
        final String username = registerRequestDTO.getUsername();
        final String password = registerRequestDTO.getPassword();
        final String email = registerRequestDTO.getEmail();
        if(investorDAO.findByEmail(email) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists.");
        }
        if(role.equals("investor")){
            Investor newInvestor = Investor
                                    .builder()
                                    .username(username)
                                    .password(password)
                                    .email(email)
                                    .subscriptions(new HashSet<>())
                                    .investments(new HashSet<>())
                                    .build();
            encryptPassword(newInvestor);
            investorDAO.save(newInvestor);
            registeredUser = newInvestor;
        }else if(role.equals("expert")){
            Expert newExpert = Expert
                                .builder()
                                .username(username)
                                .password(password)
                                .email(email)
                                .investories(new HashSet<>())
                                .subscriptions(new HashSet<>())
//                                .ratings(new HashSet<>())
                                .build();
            System.out.println("Inside register expert (auth service):");
            encryptPassword(newExpert);
            System.out.println("After encryption:");
            System.out.println("Password : " + newExpert.getPassword());
            System.out.println("salt : " + newExpert.getSalt());
            System.out.println("Before save");
            expertDAO.save(newExpert);
            System.out.println("After save");
            registeredUser = newExpert;
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specified role does not exist.");
        }
        return registeredUser;
    }

    public UserDTO authenticate(final String email, final String password, final String role) throws ResponseStatusException {
        UserDTO authenticatedUser = null;
        if(role.equals("investor")){
            Investor investor = investorDAO.findByEmail(email);
            if (investor == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not registered.");

            final String encryptedPassword = passwordCryptographyProvider.encrypt(password, investor.getSalt());
            if (!investor.getPassword().equals(encryptedPassword)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
            }
            authenticatedUser = investor;
        }else if(role.equals("expert")){
            Expert expert = expertDAO.findByEmail(email);
            if (expert == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not registered.");

            final String encryptedPassword = passwordCryptographyProvider.encrypt(password, expert.getSalt());
            if (!expert.getPassword().equals(encryptedPassword)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials.");
            }
            authenticatedUser = expert;
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specified role does not exist.");
        }
        return authenticatedUser;
    }

    private void encryptPassword(final UserDTO userDTO) {
        String password = userDTO.getPassword();
        final String[] encryptedData = passwordCryptographyProvider.encrypt(password);
        if(userDTO instanceof Investor){
            Investor investor = (Investor) userDTO;
            investor.setSalt(encryptedData[0]);
            investor.setPassword(encryptedData[1]);
        }else{
            Expert expert = (Expert) userDTO;
            expert.setSalt(encryptedData[0]);
            expert.setPassword(encryptedData[1]);
        }
    }
}
