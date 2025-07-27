package com.redisd01.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.redisd01.dto.EmployeeDTO;
import com.redisd01.entity.Employee;
import com.redisd01.excepion.EmployeeAlreadyExistException;
import com.redisd01.excepion.EmployeeNotFoundException;
import com.redisd01.repository.EmployeeRepository;
import com.redisd01.service.EmployeeService;

// its using dto for redis

@Service
public class EmployeeServiceImplCacheAnnotation implements EmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImplCacheAnnotation.class);

	@Autowired
	private EmployeeRepository repo;

	@Override
	@CachePut(value = "employee", key = "#result.id", unless = "#result == null")
	public EmployeeDTO save(EmployeeDTO employeeDTO) {
		logger.debug("Attempting to save employee: {}", employeeDTO);

		if (employeeDTO.getId() != null && repo.existsById(employeeDTO.getId())) {
			logger.error("Employee already exists: {}", employeeDTO);
			throw new EmployeeAlreadyExistException("Employee already exists with ID: " + employeeDTO.getId());
		}

		Employee saved = repo.save(EmployeeDTO.mapToEntity(employeeDTO));
		logger.info("Employee saved successfully with ID: {}", saved.getId());
		return EmployeeDTO.mapToDTO(saved);
	}

	@Override
	public List<EmployeeDTO> getAll() {
		logger.debug("Fetching all employees from database.");
		List<Employee> employees = repo.findAll();
		logger.info("Fetched {} employees from database.", employees.size());

		if (employees.isEmpty()) {
			logger.warn("No employees found in the database.");
			throw new EmployeeNotFoundException("No employees found in the database");
		}

		return employees.stream()
				.map(EmployeeDTO::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Cacheable(value = "employee", key = "#id", unless = "#result == null")
	public EmployeeDTO getById(Integer id) {
		logger.debug("Fetching employee with ID: {}", id);

		Employee employee = repo.findById(id)
				.orElseThrow(() -> {
					logger.error("Employee not found with ID: {}", id);
					return new EmployeeNotFoundException("Employee not found with ID: " + id);
				});

		logger.info("Employee fetched from DB with ID: {}", id);
		return EmployeeDTO.mapToDTO(employee);
	}

	@Override
	@CachePut(value = "employee", key = "#id", unless = "#result == null")
	public EmployeeDTO update(Integer id, EmployeeDTO dto) {
		logger.debug("Attempting full update for employee ID: {} with data: {}", id, dto);

		Employee existing = repo.findById(id)
				.orElseThrow(() -> {
					logger.error("Cannot update, employee not found with ID: {}", id);
					return new EmployeeNotFoundException("Employee not found with ID: " + id);
				});

		existing.setName(dto.getName());
		existing.setAge(dto.getAge());
		existing.setGender(dto.getGender());

		Employee updated = repo.save(existing);
		logger.info("Employee updated successfully with ID: {}", updated.getId());
		return EmployeeDTO.mapToDTO(updated);
	}

	@Override
	@CachePut(value = "employee", key = "#id", unless = "#result == null")
	public EmployeeDTO updatePartial(Integer id, EmployeeDTO dto) {
		logger.debug("Attempting partial update for employee ID: {} with data: {}", id, dto);

		Employee existing = repo.findById(id)
				.orElseThrow(() -> {
					logger.error("Cannot partially update, employee not found with ID: {}", id);
					return new EmployeeNotFoundException("Employee not found with ID: " + id);
				});

		if (dto.getName() != null) {
			logger.debug("Updating name to: {}", dto.getName());
			existing.setName(dto.getName());
		}
		if (dto.getAge() != null) {
			logger.debug("Updating age to: {}", dto.getAge());
			existing.setAge(dto.getAge());
		}
		if (dto.getGender() != null) {
			logger.debug("Updating gender to: {}", dto.getGender());
			existing.setGender(dto.getGender());
		}

		Employee updated = repo.save(existing);
		logger.info("Employee partially updated with ID: {}", updated.getId());
		return EmployeeDTO.mapToDTO(updated);
	}

	@Override
	@CacheEvict(value = "employee", key = "#id")
	public void deleteById(Integer id) {
		logger.debug("Attempting to delete employee with ID: {}", id);

		repo.findById(id)
				.orElseThrow(() -> {
					logger.error("Cannot delete, employee not found with ID: {}", id);
					return new EmployeeNotFoundException("Employee not found with ID: " + id);
				});

		repo.deleteById(id);
		logger.info("Employee deleted from DB and cache evicted for ID: {}", id);
	}
}
