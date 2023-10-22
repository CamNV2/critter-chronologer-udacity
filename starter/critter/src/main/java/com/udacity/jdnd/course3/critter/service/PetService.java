package com.udacity.jdnd.course3.critter.service;


import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.exception.PetNotFoundException;
import com.udacity.jdnd.course3.critter.exception.ScheduleNotFoundException;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PetService {

    @Autowired
    PetRepository petRepository;

    public PetEntity savePet(PetEntity pet){
        return petRepository.save(pet);
    }

    public PetEntity getPetByPetId(Long petId){
        Optional<PetEntity> optionalPet = this.petRepository.findById(petId);
        PetEntity petEntity = new PetEntity();
        if(optionalPet.isPresent()){
            petEntity =  optionalPet.get();
        }else {
            throw new PetNotFoundException("Not found pet id: " + petId);
        }
        return petEntity;
    }

    public List<PetEntity> getPetsOfAnOwner(Long ownerId){
        return this.petRepository.findPetsByOwnerId(ownerId);

    }

    public List<PetEntity> findAllPets(){
        return this.petRepository.findAll();
    }
}
