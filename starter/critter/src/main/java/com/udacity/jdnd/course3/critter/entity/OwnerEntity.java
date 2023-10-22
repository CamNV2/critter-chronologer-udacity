package com.udacity.jdnd.course3.critter.entity;


import com.udacity.jdnd.course3.critter.entity.PetEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="tb_owner")
@Setter @Getter @NoArgsConstructor @EqualsAndHashCode
public class OwnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Nationalized
    String name;

    private String phoneNumber;
    private String notes;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner", cascade = CascadeType.ALL, targetEntity = PetEntity.class)
    private List<PetEntity> pets;

    public void addPet(PetEntity pet) {
        pets.add(pet);
    }

}
