package edu.neu.esd.investmentapp.dtos;

import edu.neu.esd.investmentapp.enums.SubscriptionType;

public class SubscriptionRequestDTO {
    private long investorId;
    private long expertId;
    private double price;
    private SubscriptionType type;

    public long getExpertId() {
        return expertId;
    }

    public long getInvestorId() {
        return investorId;
    }

    public void setExpertId(long expertId) {
        this.expertId = expertId;
    }

    public void setInvestorId(long investorId) {
        this.investorId = investorId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public SubscriptionType getType() {
        return type;
    }

    public void setType(SubscriptionType type) {
        this.type = type;
    }
}
