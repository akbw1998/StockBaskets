package edu.neu.esd.investmentapp.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
public class Expert extends UserDTO{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email; // needed for auth
    @Column(nullable = false)
    private String username; // needed for auth
    @Column(nullable = false)
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String salt;//create salt of type String

    @OneToMany(mappedBy = "expert",fetch = FetchType.EAGER)
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToMany(mappedBy = "expert",fetch = FetchType.EAGER, cascade = CascadeType.ALL)// cascade persist/remove/update/merge on Investories from session.persist/remove/update/merge(expert)
    private Set<Investory> investories = new HashSet<>();
}
