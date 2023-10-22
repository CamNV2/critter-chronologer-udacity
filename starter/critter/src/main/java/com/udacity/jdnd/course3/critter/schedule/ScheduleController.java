package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.entity.EmployeeEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.entity.ScheduleEntity;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.ScheduleService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Handles web requests related to Schedules.
 */
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @PostMapping
    public ScheduleDTO createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ScheduleEntity schedule = scheduleService.saveSchedule(convertScheduleDTOToScheduleEntity(scheduleDTO));
        return convertScheduleEntityToScheduleDTO(schedule);
    }

    @GetMapping
    public List<ScheduleDTO> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        List<ScheduleEntity> scheduleEntities = scheduleService.getAllSchedules();

        for (ScheduleEntity schedule : scheduleEntities){
            scheduleDTOS.add(convertScheduleEntityToScheduleDTO(schedule));
        }

        return scheduleDTOS;
    }

    @GetMapping("/pet/{petId}")
    public List<ScheduleDTO> getScheduleForPet(@PathVariable long petId) {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        List<ScheduleEntity> lstSchedules = scheduleService.getSchedulesByPetId(petId);
        if(lstSchedules == null) {
            throw new UnsupportedOperationException("Can't not find employee");
        }
        for (ScheduleEntity  schedule: lstSchedules){
            scheduleDTOS.add(convertScheduleEntityToScheduleDTO(schedule));
        }
        return scheduleDTOS;
    }

    @GetMapping("/employee/{employeeId}")
    public List<ScheduleDTO> getScheduleForEmployee(@PathVariable long employeeId) {
        List<ScheduleEntity> lstSchedule = scheduleService.getScheduleByEmployee(employeeId);
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();

        for (ScheduleEntity  schedule: lstSchedule){
            scheduleDTOS.add(convertScheduleEntityToScheduleDTO(schedule));
        }
        return scheduleDTOS;
    }

    @GetMapping("/customer/{customerId}")
    public List<ScheduleDTO> getScheduleForCustomer(@PathVariable long customerId) {
        List<ScheduleDTO> scheduleDTOS = new ArrayList<>();
        List<ScheduleEntity> lstSchedule = scheduleService.getScheduleByOwner(customerId);
        for (ScheduleEntity  schedule: lstSchedule){
            scheduleDTOS.add(convertScheduleEntityToScheduleDTO(schedule));
        }
        return scheduleDTOS;
    }


    private ScheduleDTO convertScheduleEntityToScheduleDTO(ScheduleEntity schedule) {

        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);

        scheduleDTO.setActivities(schedule.getActivities());

        List<PetEntity> pets = schedule.getPets();
        List<Long> petId = new ArrayList<>();
        for (PetEntity pet : pets) {
            petId.add(pet.getId());
        }
        scheduleDTO.setPetIds(petId);
        List<EmployeeEntity> employees = schedule.getEmployees();
        List<Long> employeeId = new ArrayList<>();
        for (EmployeeEntity employee : employees) {
            employeeId.add(employee.getId());
        }
        scheduleDTO.setEmployeeIds(employeeId);
        return scheduleDTO;

    }

    private ScheduleEntity convertScheduleDTOToScheduleEntity(ScheduleDTO scheduleDTO){
        ModelMapper modelMapper = new ModelMapper();
        ScheduleEntity schedule = modelMapper.map(scheduleDTO, ScheduleEntity.class);

        schedule.setActivities(scheduleDTO.getActivities());
        HashMap<Long, EmployeeEntity> employeeMap = new HashMap<>();
        for (Long employeeId : scheduleDTO.getEmployeeIds()) {
            Optional<EmployeeEntity> optionalEmployee = Optional.ofNullable(userService.findEmployeeById(employeeId));
            if (optionalEmployee.isPresent()) {
                employeeMap.put(employeeId, optionalEmployee.get());
            }
        }
        schedule.setEmployees(new ArrayList<EmployeeEntity>(employeeMap.values()));
        HashMap<Long, PetEntity> petMap = new HashMap<>();
        for (Long petId : scheduleDTO.getPetIds()) {
            Optional<PetEntity> optionalPet = Optional.ofNullable(petService.getPetByPetId(petId));
            if (optionalPet.isPresent()) {
                petMap.put(petId, optionalPet.get());
            }
        }
        schedule.setPets(new ArrayList<PetEntity>(petMap.values()));
        return schedule;
    }
}
