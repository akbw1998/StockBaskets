package edu.neu.esd.investmentapp.dtos;

public class StockRequestDTO {
    private String company;
    private double currentSharePrice;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getCurrentSharePrice() {
        return currentSharePrice;
    }

    public void setCurrentSharePrice(double currentSharePrice) {
        this.currentSharePrice = currentSharePrice;
    }
}
