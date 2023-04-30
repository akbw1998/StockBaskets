package edu.neu.esd.investmentapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class QuantifiedStock{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @OneToOne // unidirectional to a stock
        @JoinColumn(name = "stock_id")
        private Stock stock;
        private int qty; // stock qty
}

