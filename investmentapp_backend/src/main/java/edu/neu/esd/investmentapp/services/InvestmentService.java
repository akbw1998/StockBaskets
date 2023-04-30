package edu.neu.esd.investmentapp.services;

import edu.neu.esd.investmentapp.daos.*;
import edu.neu.esd.investmentapp.dtos.InvestmentAddRequestDTO;
import edu.neu.esd.investmentapp.dtos.InvestmentUpdateRequestDTO;
import edu.neu.esd.investmentapp.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InvestmentService {
    private final InvestorDAO investorDAO;
    private final InvestoryDAO investoryDAO;
    private final InvestmentDAO investmentDAO;
    private final SubscriptionDAO subscriptionDAO;

    @Autowired
    public InvestmentService(InvestorDAO investorDAO, ExpertDAO expertDAO, InvestoryDAO investoryDAO, InvestmentDAO investmentDAO, SubscriptionDAO subscriptionDAO)
    {
        this.investorDAO = investorDAO;
        this.investoryDAO = investoryDAO;
        this.investmentDAO = investmentDAO;
        this.subscriptionDAO = subscriptionDAO;
    }
    @Transactional(rollbackFor = Exception.class)
    public void updateInvestmentPortfolio() throws ResponseStatusException{
        try{
            List<Investment> investments = investmentDAO.findAll();
            double totalInvestorPortfolio = 0;
            for(Investment investment : investments){
                Investor investor = investment.getInvestor();
                investor.setPortfolio(investor.getPortfolio() - investment.getTotalInvestmentValue()); // delete previous investmentValue from investor portfolio
                investment.updateTotalInvestmentValue();
                investor.setPortfolio(investor.getPortfolio() + investment.getTotalInvestmentValue()); // add the new updated value to investor portfolio
                investmentDAO.merge(investment); // merge changes to investment
                investorDAO.merge(investor); // merge changes to investor
            }
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating investments on market change");
        }

    }

    @Transactional(propagation=Propagation.REQUIRED, rollbackFor = Exception.class)
    public Investment addInvestment(long investorId, InvestmentAddRequestDTO investmentAddRequestDTO) throws ResponseStatusException {
        Investor investor = investorDAO.findById(investorId);
        if (investor == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Investor with id " + investorId + " not found");
        }

        Investory investory = investoryDAO.findById(investmentAddRequestDTO.getInvestoryId());
        if(investory == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Investory with that id not found.");
        }
        if(investmentDAO.findByInvestorIdAndInvestoryId(investorId, investory.getId()) != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Investment already exists for investor in this investory");
        }
        if(subscriptionDAO.findByInvestorIdAndExpertId(investorId,investory.getExpert().getId()) == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Investor must subscribe before making an investment.");
        }
        if(investor.getWallet() < investmentAddRequestDTO.getInvestmentAmountToAdd()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        Investment investment = Investment
                                .builder()
                                .investory(investory)
//                                .investorId(investorId) // we are connecting actual investor below.
                                .totalAmountInvested(investmentAddRequestDTO.getInvestmentAmountToAdd())
                                .totalInvestoryShares(investmentAddRequestDTO.getInvestorySharesToAdd())
                                .build();
        investment.updateTotalInvestmentValue();
        investor.setWallet(investor.getWallet() - investmentAddRequestDTO.getInvestmentAmountToAdd());
        investor.setAmountInvested(investor.getAmountInvested() + investmentAddRequestDTO.getInvestmentAmountToAdd());
        investor.setPortfolio(investor.getPortfolio() + investmentAddRequestDTO.getInvestmentAmountToAdd());


        investor.getInvestments().add(investment);
        investment.setInvestor(investor);
        investmentDAO.save(investment);
        investorDAO.merge(investor); // should cascade save the investment
        System.out.println("---------Created Investment Details---------------");
        System.out.println(investment.toString());
        return investment;
    }

    @Transactional(rollbackFor = Exception.class)
    public Investment updateInvestment(long investorId, long investmentId, InvestmentUpdateRequestDTO investmentUpdateRequestDTO) throws ResponseStatusException {
        Investor investor = investorDAO.findById(investorId);
        if (investor == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Investor with that id not found");
        }
        Investment investment = investmentDAO.findById(investmentId);
        if(investment.getInvestor().getId() != investorId){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Investor unauthorized to investment resource");
        }
        if(investor.getWallet() < investmentUpdateRequestDTO.getInvestmentAmountToAdd()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        investor.setWallet(investor.getWallet() - investmentUpdateRequestDTO.getInvestmentAmountToAdd()); // deduct amount from investor wallet
        investment.setTotalAmountInvested(investment.getTotalAmountInvested() + investmentUpdateRequestDTO.getInvestmentAmountToAdd()); // set total amount invested in investment so far
        investor.setPortfolio(investor.getPortfolio() + investmentUpdateRequestDTO.getInvestmentAmountToAdd()); // add investment to user overall portfolio as well, this needs to be done through investments on market update too
        investor.setAmountInvested(investor.getAmountInvested() + investmentUpdateRequestDTO.getInvestmentAmountToAdd()); // update user global amount invested
        investment.setTotalInvestoryShares(investment.getTotalInvestoryShares() + investmentUpdateRequestDTO.getInvestorySharesToAdd()); // set total investory shares
        investment.updateTotalInvestmentValue(); // calling update to update investmentValue (total portfolio value for this investment) , we need to update this in updateMarket function while updating each investmentValue in investments
        investor.getInvestments().add(investment); // add investment to investor collection of investments
        investorDAO.merge(investor);
        investmentDAO.merge(investment); // no longer cascading due to error doing that in investment add merge of investor
        System.out.println("---------Created Investment Details---------------");
        System.out.println(investment.toString());
        return investment;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteInvestment(long investorId, long investmentId) throws ResponseStatusException{
        Investment investment = investmentDAO.findById(investmentId);
        if(investment == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Investment with this id does not exist");
        }
        if(investorId != investment.getInvestor().getId()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized resource");
        }
        try{
            Investor investor = investorDAO.findById(investment.getInvestor().getId());
            investor.setAmountInvested(investor.getAmountInvested() - investment.getTotalAmountInvested()); // update global amount invested value withdrawn from investment amount put in
            investor.setWallet(investor.getWallet() + investment.getTotalInvestmentValue()); //update investor wallet with total investment value withdrawn from investment
            investor.setPortfolio(investor.getPortfolio() - investment.getTotalInvestmentValue()); // reduce user global portfolio with total investment value of investory
            investor.getInvestments().remove(investment); // remove the investment from investor set
            investorDAO.merge(investor); // update user
            investmentDAO.remove(investment); // remove the dangling investment entity belonging to no investor
        }catch(Exception e){
            //rollback transaction if an exception is thrown
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete investment transaction failed.");
        }
    }
    public List<Investment> getAllInvestments(){
        return investmentDAO.findAll();
    }
    public List<Investment> getAllInvestmentsByInvestorId(long investorId){ return investmentDAO.findAllByInvestorId(investorId);}
}
