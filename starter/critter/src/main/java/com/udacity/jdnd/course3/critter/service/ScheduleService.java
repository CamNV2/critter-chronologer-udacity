package com.udacity.jdnd.course3.critter.service;


import com.udacity.jdnd.course3.critter.entity.OwnerEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.entity.ScheduleEntity;
import com.udacity.jdnd.course3.critter.exception.ScheduleNotFoundException;
import com.udacity.jdnd.course3.critter.repository.OwnerRepository;
import com.udacity.jdnd.course3.critter.repository.PetRepository;
import com.udacity.jdnd.course3.critter.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ScheduleService {

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    OwnerRepository ownerRepository;

    public ScheduleEntity saveSchedule(ScheduleEntity schedule) {
        return this.scheduleRepository.save(schedule);
    }

    public List<ScheduleEntity> getAllSchedules() {
        return this.scheduleRepository.findAll();
    }

    public List<ScheduleEntity> getSchedulesByPetId(Long petId) {
        List<ScheduleEntity> schedules = this.scheduleRepository.getScheduleByPetId(petId);
        if (schedules.size() < 0) {
            throw new ScheduleNotFoundException("Not found Schedule by Pet id: " + petId);
        }
        return schedules;
    }

    // finding the customer with the given id, and if it's not already recorded in DB throw proper exception
    public List<ScheduleEntity> getScheduleByOwner(Long ownerId) {
        Optional<OwnerEntity> optionalOwner = this.ownerRepository.findById(ownerId);
        List<ScheduleEntity> schedules = new ArrayList<>();
        OwnerEntity customer = optionalOwner.get();
        List<PetEntity> pets = customer.getPets();
        if (optionalOwner.isPresent()) {
            for (PetEntity pet : pets) {
                schedules.addAll(scheduleRepository.getScheduleByPetId(pet.getId()));
            }
        } else {
            throw new ScheduleNotFoundException("Not found Schedule by owner id: " + ownerId);
        }
        return schedules;
    }

    public List<ScheduleEntity> getScheduleByEmployee(Long employeeId) {
        List<ScheduleEntity> schedules = scheduleRepository.getScheduleByEmployeeId(employeeId);
        if (schedules.size() < 0) {
            throw new ScheduleNotFoundException("Not found Schedule by Employee id: " + employeeId);
        }
        return schedules;
    }
}
