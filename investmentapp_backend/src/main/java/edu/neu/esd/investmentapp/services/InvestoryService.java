package edu.neu.esd.investmentapp.services;

import edu.neu.esd.investmentapp.daos.ExpertDAO;
import edu.neu.esd.investmentapp.daos.InvestoryDAO;
import edu.neu.esd.investmentapp.daos.QuantifiedStockDAO;
import edu.neu.esd.investmentapp.daos.SubscriptionDAO;
import edu.neu.esd.investmentapp.dtos.InvestoryRequestDTO;
import edu.neu.esd.investmentapp.entities.Expert;
import edu.neu.esd.investmentapp.entities.Investory;
import edu.neu.esd.investmentapp.entities.QuantifiedStock;
import edu.neu.esd.investmentapp.entities.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class InvestoryService {

    private final InvestoryDAO investoryDAO;
    private final ExpertDAO expertDAO;
    private final QuantifiedStockDAO quantifiedStockDAO;
    private final SubscriptionDAO subscriptionDAO;

    @Autowired
    public InvestoryService(InvestoryDAO investoryDAO, ExpertDAO expertDAO, QuantifiedStockDAO quantifiedStockDAO, SubscriptionDAO subscriptionDAO) {
        this.investoryDAO = investoryDAO;
        this.expertDAO = expertDAO;
        this.quantifiedStockDAO = quantifiedStockDAO;
        this.subscriptionDAO = subscriptionDAO;
    }

    public void updateInvestoryPortfolio() throws ResponseStatusException{
        try {
            List<Investory> investories = investoryDAO.findAll();
            for (Investory investory : investories) {
                investory.updateChangePercentage();
                investoryDAO.merge(investory);
            }
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating investories on market change");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Investory addInvestory(InvestoryRequestDTO investoryRequestDTO) throws ResponseStatusException{
        Expert expert = expertDAO.findById(investoryRequestDTO.getExpertId());
        if (expert == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expert with that id not found");
        }
        Investory investory = new Investory();
//        investory.setExpertId(investoryRequestDTO.getExpertId());  not good keeping id; use actual expert.
        investory.setInvestoryName(investoryRequestDTO.getInvestoryName());
        investory.setExpertName(expert.getUsername());
        investory.setExpertEmail(expert.getEmail());
        investory.setDescription(investoryRequestDTO.getDescription());
        investory.setQuantifiedStocks(investoryRequestDTO.getQuantifiedStocks()); // this should automatically persist the quantified stocks because of cascade, when investory saved
        investory.updateInitialPrice(investoryRequestDTO.getQuantifiedStocks());
        investory.updateChangePercentage();
        expert.getInvestories().add(investory); // actual expert entity connected
        investory.setExpert(expert); // connect populated expert to investory
        expertDAO.merge(expert);
        System.out.println("---------Created Investory Details---------------");
        System.out.println(investory.toString());

        return investory;
    }

    @Transactional(rollbackFor = Exception.class)
    public Investory updateInvestory(long investoryId, InvestoryRequestDTO investoryRequestDTO)throws ResponseStatusException{
        Investory investory = investoryDAO.findById(investoryId);
        if(investory == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Investory with that id not found.");
        }
        if(investory.getExpert().getId() != investoryRequestDTO.getExpertId()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized to resource");
        }
        investory.setDescription(investoryRequestDTO.getDescription());
        Set<QuantifiedStock> previousReferences = investory.getQuantifiedStocks();
        investory.setQuantifiedStocks(investoryRequestDTO.getQuantifiedStocks()); // update with new references, orphans previous reference for removal
        investory.updateInitialPrice(investoryRequestDTO.getQuantifiedStocks()); // update initial price
        investory.updateChangePercentage(); // update change = 0
        investoryDAO.merge(investory); // this should cascade save changes in expert's reference as well
        for(QuantifiedStock quantifiedStock : previousReferences)quantifiedStockDAO.remove(quantifiedStock);
        System.out.println("---------Updated Investory Details---------------");
        System.out.println(investory.toString());

        return investory;
    }

    public List<Investory> getAllInvestoriesWithFilters(long[] subscriptionIds, Long expertId) throws ResponseStatusException {
        if ((subscriptionIds != null && subscriptionIds.length != 0) && expertId != null) {
            List<Investory> investoryListResultBySubscriptions = new ArrayList<>();
            List<Investory> resultList = new ArrayList<>();
            for(long subscriptionId : subscriptionIds){
                Subscription subscription = subscriptionDAO.findById(subscriptionId);
                if(subscription != null){
                    List<Investory> intermediateResult = investoryDAO.findAllByExpertId(subscription.getExpert().getId());
                    if(intermediateResult != null){
                        investoryListResultBySubscriptions.addAll(intermediateResult);
                    }
                }
            }
            for(Investory investory : investoryListResultBySubscriptions){
                if(investory.getExpert().getId().equals(expertId)){
                    resultList.add(investory);
                }
            }
            return resultList;
        }
        else if ((subscriptionIds != null && subscriptionIds.length != 0)) {
            List<Investory> resultList = new ArrayList<>();
            for(long subscriptionId : subscriptionIds){
                Subscription subscription = subscriptionDAO.findById(subscriptionId);
                if(subscription != null){
                    List<Investory> intermediateResult = investoryDAO.findAllByExpertId(subscription.getExpert().getId());
                    if(intermediateResult != null){
                        resultList.addAll(intermediateResult);
                    }
                }
            }
            return resultList;
        }
        else if(expertId != null){
            return investoryDAO.findAllByExpertId(expertId);
        }
        else
            return investoryDAO.findAll();
    }
    public List<Investory> getAllInvestoriesByExpertId(long expertId){return investoryDAO.findAllByExpertId(expertId);}
    public List<Investory> getAllInvestories(){
        return investoryDAO.findAll();
    }
}
