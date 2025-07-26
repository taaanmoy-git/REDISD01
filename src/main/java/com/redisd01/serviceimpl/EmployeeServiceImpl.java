package com.redisd01.serviceimpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.redisd01.dto.EmployeeDTO;
import com.redisd01.entity.Employee;
import com.redisd01.excepion.EmployeeAlreadyExistException;
import com.redisd01.excepion.EmployeeNotFoundException;
import com.redisd01.repository.EmployeeRepository;
import com.redisd01.service.EmployeeService;

// its using Entity for Redis


@Primary
@Service
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	private static final String HASH_KEY = "EMPLOYEE";

	@Autowired
	private EmployeeRepository repo;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public EmployeeDTO save(EmployeeDTO employeeDTO) {
		logger.debug("Attempting to save employee: {}", employeeDTO);

		if (employeeDTO.getId() != null && repo.existsById(employeeDTO.getId())) {
			logger.error("Employee already exists: {}", employeeDTO);
			throw new EmployeeAlreadyExistException("Employee already exists with ID: " + employeeDTO.getId());
		}

		Employee saved = repo.save(EmployeeDTO.mapToEntity(employeeDTO));
		logger.debug("Employee saved to DB: {}", saved);

		redisTemplate.opsForHash().put(HASH_KEY, String.valueOf(saved.getId()), saved);
		logger.debug("Employee cached in Redis with ID: {}", saved.getId());

		logger.info("Employee saved successfully with ID: {}", saved.getId());
		return EmployeeDTO.mapToDTO(saved);
	}

	@Override
	public List<EmployeeDTO> getAll() {
		logger.debug("Fetching all employees from database.");
		List<Employee> employees = repo.findAll();
		logger.info("Fetched {} employees from database.", employees.size());

		return employees.stream()
				.map(EmployeeDTO::mapToDTO)
				.collect(Collectors.toList());
	}

	@Override
	public EmployeeDTO getById(Integer id) {
		logger.debug("Fetching employee with ID: {}", id);

		Employee employee = (Employee) redisTemplate.opsForHash().get(HASH_KEY, String.valueOf(id));

		if (employee == null) {
			logger.warn("Employee not found in Redis cache for ID: {}, querying DB.", id);
			employee = repo.findById(id)
					.orElseThrow(() -> {
						logger.error("Employee not found with ID: {}", id);
						return new EmployeeNotFoundException("Employee not found with ID: " + id);
					});
			redisTemplate.opsForHash().put(HASH_KEY, String.valueOf(id), employee);
			logger.debug("Employee cached in Redis after DB fetch: {}", employee);
		} else {
			logger.info("Employee found in Redis cache for ID: {}", id);
		}

		return EmployeeDTO.mapToDTO(employee);
	}

	@Override
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
		logger.debug("Employee updated in DB: {}", updated);

		redisTemplate.opsForHash().put(HASH_KEY,String.valueOf(updated.getId()) , updated);
		logger.debug("Employee cache updated in Redis for ID: {}", updated.getId());

		logger.info("Employee updated successfully with ID: {}", updated.getId());
		return EmployeeDTO.mapToDTO(updated);
	}

	@Override
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
		logger.debug("Employee partially updated in DB: {}", updated);

		redisTemplate.opsForHash().put(HASH_KEY, String.valueOf(updated.getId()), updated);
		logger.debug("Employee cache partially updated in Redis for ID: {}", updated.getId());

		logger.info("Employee partially updated with ID: {}", updated.getId());
		return EmployeeDTO.mapToDTO(updated);
	}

	@Override
	public void deleteById(Integer id) {
		logger.debug("Attempting to delete employee with ID: {}", id);

		Employee existing = repo.findById(id)
				.orElseThrow(() -> {
					logger.error("Cannot delete, employee not found with ID: {}", id);
					return new EmployeeNotFoundException("Employee not found with ID: " + id);
				});

		repo.deleteById(id);
		logger.debug("Employee deleted from DB with ID: {}", id);

		redisTemplate.opsForHash().delete(HASH_KEY, String.valueOf(id));
		logger.debug("Employee removed from Redis cache with ID: {}", id);

		logger.info("Employee deleted with ID: {}", id);
	}
}
