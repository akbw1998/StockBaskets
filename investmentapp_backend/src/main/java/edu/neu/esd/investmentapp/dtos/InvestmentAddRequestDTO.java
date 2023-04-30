package edu.neu.esd.investmentapp.dtos;

public class InvestmentAddRequestDTO {
    private long investoryId;
    private int investorySharesToAdd;
    private double investmentAmountToAdd;

    public double getInvestmentAmountToAdd() {
        return investmentAmountToAdd;
    }

    public int getInvestorySharesToAdd() {
        return investorySharesToAdd;
    }

    public long getInvestoryId() {
        return investoryId;
    }

}
