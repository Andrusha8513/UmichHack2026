package com.example.profile_service.service;

import com.example.profile_service.dto.CreateEmployeeRequestDto;
import com.example.profile_service.dto.EmployeeShortDto;
import com.example.profile_service.entity.Employee;
import com.example.profile_service.entity.Pvz;
import com.example.profile_service.mapper.EmployeeMapper;
import com.example.profile_service.repository.EmployeeRepository;
import com.example.profile_service.repository.PvzRepository;
import com.example.profile_service.security.Owner_Pvz;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PvzRepository pvzRepository;
    private final Owner_Pvz owner_pvz;

    @Transactional
    public void createEmployee(CreateEmployeeRequestDto createEmployeeRequestDto, Long pvzId) {
        Pvz pvz = pvzRepository.findById(pvzId)
                .orElseThrow(() -> new IllegalArgumentException("Пвз не найдено"));


        owner_pvz.checkAccess(pvz);

        if (employeeRepository.existsByPhoneAndPvzId(createEmployeeRequestDto.phone(), pvzId)) {
            throw new IllegalArgumentException("Сотрудник с таким телефоном уже существует");
        }
        Employee employee = employeeMapper.toEntity(createEmployeeRequestDto);

        employee.setPvz(pvz);
        pvz.getEmployees().add(employee);
        employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));
        employeeRepository.delete(employee);
    }

    @Transactional
    public void addDescription(Long id, String description) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));

        owner_pvz.checkAccess(employee.getPvz());

        employee.setDescription(description);
        employeeRepository.save(employee);
    }

    @Transactional
    public void addBank(Long id, String bank) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));

        owner_pvz.checkAccess(employee.getPvz());

        employee.setBank(bank);
        employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeShortDto> searchEmployees(Long pvzId, String name, String secondName, String surName) {
        Pvz pvz = pvzRepository.findById(pvzId)
                .orElseThrow(() -> new IllegalArgumentException("ПВЗ не найдено"));

        owner_pvz.checkAccess(pvz);

        List<Employee> result = employeeRepository.searchEmployees(pvzId, name, secondName, surName);

        return result.stream()
                .map(employeeMapper::toShortDto)
                .toList();
    }


}
