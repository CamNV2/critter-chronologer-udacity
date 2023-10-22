package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.entity.OwnerEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles web requests related to Pets.
 */
@RestController
@RequestMapping("/pet")
public class PetController {
    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @PostMapping
    public PetDTO savePet(@RequestBody PetDTO petDTO) {
        OwnerEntity owner = new OwnerEntity();
        if (petDTO.getOwnerId() != 0) {
            owner = userService.findOwnerById(petDTO.getOwnerId());
        }
        PetEntity pet = convertPetDTOToPet(petDTO);
        pet.setOwner(owner);
        PetEntity pet1 = petService.savePet(pet);
        if (owner != null) {
            owner.addPet(pet1);
        }
        return convertPetToPetDTO(pet1);
    }

    @GetMapping("/{petId}")
    public PetDTO getPet(@PathVariable long petId) {
        PetEntity pet = petService.getPetByPetId(petId);
        if(pet == null) {
            throw new UnsupportedOperationException("Can't not find pet");
        }
        return convertPetToPetDTO(pet);
    }

    @GetMapping
    public List<PetDTO> getPets() {
        List<PetEntity> petEntities = petService.findAllPets();
        List<PetDTO> lstPets = new ArrayList<>();
        for (PetEntity pet : petEntities) {
            lstPets.add(convertPetToPetDTO(pet));
        }
        return lstPets;
    }

    @GetMapping("/owner/{ownerId}")
    public List<PetDTO> getPetsByOwner(@PathVariable long ownerId) {
        List<PetDTO> lstPets = new ArrayList<>();
        List<PetEntity> petEntities = petService.getPetsOfAnOwner(ownerId);
        if(petEntities == null) {
            throw new UnsupportedOperationException("Can't not find pet");
        }
        for (PetEntity pet : petEntities) {
            lstPets.add(convertPetToPetDTO(pet));
        }
        return lstPets;
    }

    private PetDTO convertPetToPetDTO(PetEntity pet) {
        PetDTO petDTO = new PetDTO();
        // in order for copyProperties to work, properties of the DTO and normal object must match in name
        BeanUtils.copyProperties(pet, petDTO);
        if (pet.getOwner() != null) {
            petDTO.setOwnerId(pet.getOwner().getId());
        }
        return petDTO;
    }

    private PetEntity convertPetDTOToPet(PetDTO petDTO) {
        ModelMapper modelMapper = new ModelMapper();
        PetEntity pet = modelMapper.map(petDTO, PetEntity.class);
        return pet;
    }
}
