package edu.neu.esd.investmentapp.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Investory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL) // uni-directional One-To-many, cascade all operations since lifecycle of Quantified stock bound to this investory
    @JoinColumn(name = "investory_id")
    private Set<QuantifiedStock> quantifiedStocks = new HashSet<>();

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "expert_id")
    Expert expert;

    private String investoryName;
    private String expertName;
    private String expertEmail;
    private String description;

    private double initialPrice;
    private double changePercentage; // percentage to be used by UI in investory market, investmentDetails to calc value on user investment.

    public void updateInitialPrice(Set<QuantifiedStock> quantifiedStocks){
        double totalSum = 0;
        for (QuantifiedStock quantifiedStock : quantifiedStocks) {
            totalSum += quantifiedStock.getStock().getCurrentSharePrice() * quantifiedStock.getQty();
        }
        this.initialPrice = totalSum;
    }

    public void updateChangePercentage() {
        double currentPriceSum = quantifiedStocks.stream().mapToDouble(qs -> qs.getStock().getCurrentSharePrice() * qs.getQty()).sum();
        this.changePercentage = ((currentPriceSum - initialPrice) / initialPrice) * 100;
    }
}
