package edu.neu.esd.investmentapp.dtos;

public class InvestmentUpdateRequestDTO {
    private int investorySharesToAdd;
    private double investmentAmountToAdd;
    public double getInvestmentAmountToAdd() {
        return investmentAmountToAdd;
    }
    public int getInvestorySharesToAdd() {
        return investorySharesToAdd;
    }
}
