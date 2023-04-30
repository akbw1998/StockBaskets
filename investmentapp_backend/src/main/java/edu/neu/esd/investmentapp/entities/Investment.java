package edu.neu.esd.investmentapp.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Investment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @OneToOne // uni-directional reference to investory to render user investment details on a particular investory
    @JoinColumn(name = "investory_id") // custom name for fk
    private Investory investory;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "investor_id")
    Investor investor;

    private double totalAmountInvested; // update using setter on add/update of investment
    private double totalInvestmentValue; // call at end of add/update , and also on market update.
    private int totalInvestoryShares; // update using setter on add/update of investment

    public void updateTotalInvestmentValue(){
        double investoryUnitShareValue = 0.0;
        for(QuantifiedStock qs : investory.getQuantifiedStocks()){
            investoryUnitShareValue += (qs.getStock().getCurrentSharePrice())*qs.getQty();
        }
        totalInvestmentValue = investoryUnitShareValue*totalInvestoryShares;
    }
}

