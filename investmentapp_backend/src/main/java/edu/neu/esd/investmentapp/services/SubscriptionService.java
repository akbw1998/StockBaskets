package edu.neu.esd.investmentapp.services;

import edu.neu.esd.investmentapp.daos.ExpertDAO;
import edu.neu.esd.investmentapp.daos.InvestorDAO;
import edu.neu.esd.investmentapp.daos.SubscriptionDAO;
import edu.neu.esd.investmentapp.dtos.SubscriptionRequestDTO;
import edu.neu.esd.investmentapp.entities.Expert;
import edu.neu.esd.investmentapp.entities.Investor;
import edu.neu.esd.investmentapp.entities.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscriptionService {

    private final InvestorDAO investorDAO;
    private final ExpertDAO expertDAO;
    private final SubscriptionDAO subscriptionDAO;

    @Autowired
    public SubscriptionService(InvestorDAO investorDAO, ExpertDAO expertDAO, SubscriptionDAO subscriptionDAO)
    {
        this.investorDAO = investorDAO;
        this.expertDAO = expertDAO;
        this.subscriptionDAO = subscriptionDAO;
    }
    public List<Subscription> getAllSubscriptionsWithFilters(Long expertId, Long investorId){
        if(expertId == null && investorId == null){return getAllSubscriptions();}
        else if(expertId != null && investorId == null){return getAllSubscriptionsByExpertId(expertId);}
        else if(investorId != null && expertId == null){return getAllSubscriptionsByInvestorId(investorId);}
        else{
            List<Subscription> resultList = new ArrayList<>();
            Subscription subscription= subscriptionDAO.findByInvestorIdAndExpertId(investorId,expertId);
            if (subscription != null)resultList.add(subscription);
            return resultList;
        }
    }
    private List<Subscription> getAllSubscriptions(){
        return subscriptionDAO.findAll();
    }

    private List<Subscription> getAllSubscriptionsByExpertId(long expertId){
        return subscriptionDAO.findAllByExpertId(expertId);
    }

    private List<Subscription> getAllSubscriptionsByInvestorId(long investorId){
        return subscriptionDAO.findAllByInvestorId(investorId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Subscription addSubscription(SubscriptionRequestDTO subscriptionRequestDTO) throws ResponseStatusException {
        Investor investor = investorDAO.findById(subscriptionRequestDTO.getInvestorId());
        if(investor == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with that id does not exist.");
        }
        Expert expert = expertDAO.findById(subscriptionRequestDTO.getExpertId());
        if(expert == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expert with that id does not exist.");
        }

        Subscription existingSubscription = subscriptionDAO.findByInvestorIdAndExpertId(subscriptionRequestDTO.getInvestorId(), subscriptionRequestDTO.getExpertId());
        if(existingSubscription != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription between investor and expert already exists.");
        }

        if(investor.getWallet() < subscriptionRequestDTO.getPrice()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds for subscription.");
        }

        Subscription subscription = Subscription
                                    .builder()
//                                    .investorId(subscriptionRequestDTO.getInvestorId())
//                                    .expertId(subscriptionRequestDTO.getExpertId())
                                    .investorUsername(investor.getUsername())
                                    .investorEmail(investor.getEmail())
                                    .expertUsername(expert.getUsername())
                                    .expertEmail(expert.getEmail())
                                    .price(subscriptionRequestDTO.getPrice())
                                    .type(subscriptionRequestDTO.getType())
                                    .build();

        investor.setWallet(investor.getWallet() - subscription.getPrice());
        investor.getSubscriptions().add(subscription);
        subscription.setInvestor(investor); // properly setting bidirectional relationship no investorid commented above
        expert.getSubscriptions().add(subscription);
        subscription.setExpert(expert); // properly setting bidirectional relationship no expertId commented above
        try {
            subscriptionDAO.save(subscription);
            investorDAO.merge(investor);
            expertDAO.merge(expert);
            return subscription;
        }catch(Exception e){
            //rollback transaction if an exception is thrown
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Add subscription transaction failed.");
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public void deleteSubscription(long subscriptionId) throws ResponseStatusException{
        Subscription subscription = subscriptionDAO.findById(subscriptionId);
        if(subscription == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription with this id does not exist");
        }
//        try{
            Investor investor = investorDAO.findById(subscription.getInvestor().getId()); // need to lazily load subscriptions, so need to get through dao method
            Expert expert = subscription.getExpert(); // change to bidrectional entity, before this was id
            investor.getSubscriptions().remove(subscription);
            expert.getSubscriptions().remove(subscription);
            investorDAO.merge(investor);
            expertDAO.merge(expert);
            subscriptionDAO.remove(subscription);

//        }catch(Exception e){
//            System.out.println("-----------------EXCEPTION MESSAGE : " + e.getMessage() + "-------------------");
//            //rollback transaction if an exception is thrown
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Delete subscription transaction failed.");
//        }
    }

}
