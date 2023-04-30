package edu.neu.esd.investmentapp.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.neu.esd.investmentapp.dtos.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Investor extends UserDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email; //needed for auth
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    @JsonIgnore
    private String password; //needed for auth
    @JsonIgnore
    private String salt;//create salt of type String

    private double wallet; // invest/withdraw should change this
    private double portfolio; // total amount growth
    private double amountInvested; // total investment amounts

    @OneToMany(mappedBy = "investor") // removed cascade, handle it
    private Set<Investment> investments = new HashSet<>();

    @OneToMany(mappedBy = "investor") // create and cascade persist/remove subscriptions from Investor side: "/investor/subscriptions"
    private Set<Subscription> subscriptions = new HashSet<>();

}
