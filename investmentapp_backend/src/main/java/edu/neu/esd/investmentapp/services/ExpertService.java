package edu.neu.esd.investmentapp.services;

import edu.neu.esd.investmentapp.daos.ExpertDAO;
import edu.neu.esd.investmentapp.entities.Expert;
import edu.neu.esd.investmentapp.entities.Investor;
import edu.neu.esd.investmentapp.provider.PasswordCryptographyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpertService {

    private final ExpertDAO expertDAO;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    public ExpertService(ExpertDAO expertDAO) {
        this.expertDAO = expertDAO;
    }

    public List<Expert> getAllExpertsWithFilters(String ratingOrder) throws ResponseStatusException {

        if (ratingOrder != null && !ratingOrder.isEmpty()) {
            return expertDAO.findAllByRating(ratingOrder);
        }
        return expertDAO.findAll();
    }

    public Expert getExpert(long expertId){
        return expertDAO.findById(expertId);
    }

}
