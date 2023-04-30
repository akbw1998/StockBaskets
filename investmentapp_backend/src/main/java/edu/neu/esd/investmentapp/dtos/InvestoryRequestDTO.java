package edu.neu.esd.investmentapp.dtos;

import edu.neu.esd.investmentapp.entities.QuantifiedStock;

import java.util.HashSet;
import java.util.Set;

public class InvestoryRequestDTO {
    private final Set<QuantifiedStock> quantifiedStocks = new HashSet<>();

    private long expertId;
    private String investoryName;
    private String description;

    public String getInvestoryName() {
        return investoryName;
    }

    public String getDescription() {
        return description;
    }

    public Set<QuantifiedStock> getQuantifiedStocks() {
        return quantifiedStocks;
    }

    public long getExpertId() {
        return expertId;
    }
}
