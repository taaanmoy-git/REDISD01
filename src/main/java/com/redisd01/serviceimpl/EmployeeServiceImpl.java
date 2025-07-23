package com.redisd01.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redisd01.dto.EmployeeDTO;
import com.redisd01.entity.Employee;
import com.redisd01.excepion.EmployeeAlreadyExistException;
import com.redisd01.excepion.EmployeeNotFoundException;
import com.redisd01.repository.EmployeeRepository;
import com.redisd01.service.EmployeeService;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	@Autowired
	private EmployeeRepository repo;

	@Override
	public EmployeeDTO save(EmployeeDTO employeeDTO) {
		Optional<Employee> empOptional = repo.findById(employeeDTO.getId());

		if (empOptional.isPresent()) {
			logger.error("Employee already exists: {}", employeeDTO);
			throw new EmployeeAlreadyExistException("Employee already exists with ID: " + employeeDTO.getId());
		}

		Employee saved = repo.save(EmployeeDTO.mapToEntity(employeeDTO));
		logger.info("Employee saved successfully with ID: {}", saved.getId());
		return EmployeeDTO.mapToDTO(saved);
	}

	@Override
	public List<EmployeeDTO> getAll() {
		List<Employee> employees = repo.findAll();
		logger.info("Fetched {} employees from database.", employees.size());
		if(employees.isEmpty()) {
			return employees.stream()
					.map(EmployeeDTO::mapToDTO)
					.collect(Collectors.toList());
		}else {
			return employees.stream()
					.map(EmployeeDTO::mapToDTO)
					.collect(Collectors.toList());
		}
		
	}

	@Override
	public EmployeeDTO getById(Integer id) {
		Employee employee = repo.findById(id)
				.orElseThrow(() -> {
					logger.error("Employee not found with ID: {}", id);
					return new EmployeeNotFoundException("Employee not found with ID: " + id);
				});

		logger.info("Fetched employee with ID: {}", id);
		return EmployeeDTO.mapToDTO(employee);
	}
}
