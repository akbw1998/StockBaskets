package edu.neu.esd.investmentapp.services;

import edu.neu.esd.investmentapp.daos.ExpertDAO;
import edu.neu.esd.investmentapp.daos.InvestorDAO;
import edu.neu.esd.investmentapp.daos.SubscriptionDAO;
import edu.neu.esd.investmentapp.entities.Investor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InvestorService {

    private final InvestorDAO investorDAO;
    private final ExpertDAO expertDAO;
    private final SubscriptionDAO subscriptionDAO;

    @Autowired
    public InvestorService(InvestorDAO investorDAO, ExpertDAO expertDAO, SubscriptionDAO subscriptionDAO)
    {
        this.investorDAO = investorDAO;
        this.expertDAO = expertDAO;
        this.subscriptionDAO = subscriptionDAO;
    }

    public Investor getInvestor(Long investorId){
        return investorDAO.findById(investorId);
    }

    public List<Investor> getAllInvestors(){return investorDAO.findAll();}

    public Investor addToWallet(final Long id, final double amount) throws ResponseStatusException{
        Investor investor = investorDAO.findById(id);
        if (investor == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Investor not found");
        }
        System.out.println("---BEFORE UPDATE---");
        System.out.println("Investor wallet : " + investor.getWallet());
        investor.setWallet(investor.getWallet() + amount);
        System.out.println("--------AFTER UPDATE-------");
        System.out.println("Investor wallet : " + investor.getWallet());
        System.out.println("-----------Investor details------");
        investorDAO.merge(investor);
        return investor;
    }

}
