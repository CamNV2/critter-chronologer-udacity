package com.udacity.jdnd.course3.critter.repository;


import com.udacity.jdnd.course3.critter.entity.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PetRepository extends JpaRepository<PetEntity, Long> {

    List<PetEntity> findPetsByOwnerId(Long owner_id);

}
