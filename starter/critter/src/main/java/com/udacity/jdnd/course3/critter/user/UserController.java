package com.udacity.jdnd.course3.critter.user;

import com.udacity.jdnd.course3.critter.entity.EmployeeEntity;
import com.udacity.jdnd.course3.critter.entity.OwnerEntity;
import com.udacity.jdnd.course3.critter.entity.PetEntity;
import com.udacity.jdnd.course3.critter.service.PetService;
import com.udacity.jdnd.course3.critter.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Handles web requests related to Users.
 *
 * Includes requests for both customers and employees. Splitting this into separate user and customer controllers
 * would be fine too, though that is not part of the required scope for this class.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PetService petService;

    @Autowired
    private UserService userService;

    @PostMapping("/customer")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){

        List<Long> petIds = customerDTO.getPetIds();
        List<PetEntity> pets = new ArrayList<>();
        if (petIds != null) {
            for (Long petId : petIds) {
                pets.add(petService.getPetByPetId(petId));
            }
        }
        OwnerEntity owner = convertCustomerDTOToOwnerEntity(customerDTO);
        owner.setPets(pets);
        OwnerEntity savedOwner = userService.saveCustomer(owner);
        return convertOwnerOwnerEntityToCustomerDTO(savedOwner);
    }

    @GetMapping("/customer")
    public List<CustomerDTO> getAllCustomers(){
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        List<OwnerEntity> lstOwners =  userService.findAllOwners();
        for (OwnerEntity owner: lstOwners){
            customerDTOS.add(convertOwnerOwnerEntityToCustomerDTO(owner));
        }
        return customerDTOS;
    }

    @GetMapping("/customer/pet/{petId}")
    public CustomerDTO getOwnerByPet(@PathVariable long petId){
        PetEntity pet = petService.getPetByPetId(petId);
        OwnerEntity owner = userService.findOwnerById(pet.getOwner().getId());
        if (owner == null){
            throw new UnsupportedOperationException("The pet has no owner yet");
        }
        return convertOwnerOwnerEntityToCustomerDTO(owner);
    }

    @PostMapping("/employee")
    public EmployeeDTO saveEmployee(@RequestBody EmployeeDTO employeeDTO) {
        EmployeeEntity entity = convertEmployeeDTOToEmployeeEntity(employeeDTO);
        EmployeeEntity employee  = userService.saveEmployee(entity);
        employeeDTO.setId(employee.getId());
        return employeeDTO;
    }

    @GetMapping("/employee/{employeeId}")
    public EmployeeDTO getEmployee(@PathVariable long employeeId) {
        EmployeeEntity entity = userService.findEmployeeById(employeeId);
        if (entity == null) {
            throw new UnsupportedOperationException("Can't not find employee");
        }
        return convertEmployeeEntityToEmployeeDTO(entity);
    }

    @PutMapping("/employee/{employeeId}")
    public void setAvailability(@RequestBody Set<DayOfWeek> daysAvailable, @PathVariable long employeeId) {
        userService.updateDaysAvailable(daysAvailable, employeeId);
    }

    @GetMapping("/employee/availability")
    public List<EmployeeDTO> findEmployeesForService(@RequestBody EmployeeRequestDTO employeeDTO) {
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        List<EmployeeEntity> lstEmployees = userService.findEmployeesForService(employeeDTO.getSkills(),employeeDTO.getDate());
        if (lstEmployees == null) {
            throw new UnsupportedOperationException("Can't not find employee");
        }
        for (EmployeeEntity employee:lstEmployees ){
            employeeDTOS.add(convertEmployeeEntityToEmployeeDTO(employee));
        }
        return employeeDTOS;
    }

    private CustomerDTO convertOwnerOwnerEntityToCustomerDTO(OwnerEntity owner){
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(owner, customerDTO);
        List<PetEntity> pets = owner.getPets();

        if (pets != null) {
            List<Long> petIds = new ArrayList<>();

            for (PetEntity pet : pets) {
                petIds.add(pet.getId());
            }
            customerDTO.setPetIds(petIds);
        }
        return customerDTO;
    }

    private OwnerEntity convertCustomerDTOToOwnerEntity(CustomerDTO customerDTO){
        ModelMapper modelMapper = new ModelMapper();
        OwnerEntity owner = modelMapper.map(customerDTO, OwnerEntity.class);
        List<Long> petIds = customerDTO.getPetIds();

        if (petIds != null) {
            List<PetEntity> pets = new ArrayList<PetEntity>();

            for (Long petId : petIds) {
                pets.add(petService.getPetByPetId(petId));
            }
            owner.setPets(pets);
        }
        return owner;
    }

    private EmployeeDTO convertEmployeeEntityToEmployeeDTO(EmployeeEntity employee){
        EmployeeDTO employeeDTO = new EmployeeDTO();
        BeanUtils.copyProperties(employee, employeeDTO);
        return employeeDTO;
    }

    private EmployeeEntity convertEmployeeDTOToEmployeeEntity(EmployeeDTO employeeDTO){
        ModelMapper modelMapper = new ModelMapper();
        EmployeeEntity employee = modelMapper.map(employeeDTO, EmployeeEntity.class);
        return employee;
    }

}
