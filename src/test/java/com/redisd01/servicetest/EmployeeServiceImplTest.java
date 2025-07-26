package com.redisd01.servicetest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.redisd01.dto.EmployeeDTO;
import com.redisd01.entity.Employee;
import com.redisd01.excepion.EmployeeAlreadyExistException;
import com.redisd01.repository.EmployeeRepository;
import com.redisd01.serviceimpl.EmployeeServiceImplCacheAnnotation;


@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

	@Mock
	private EmployeeRepository repo;
	
	@InjectMocks
	private EmployeeServiceImplCacheAnnotation service;
	
	private Employee emp;
	
	
	@BeforeEach
	public void resetEmployee() {
		emp = new Employee(1, "Tanmoy", 29, "Male");
	}
	
	@BeforeAll
	public static void testStart() {
		System.out.println("-------- test started ----------");
	}
	
	@AfterAll
	public static void testEnded() {
		System.out.println("-------- test ended ----------");
	}
	
	@Test
	@DisplayName("Employee save success")
	public void employeeSaveSuccess() {
		
		Mockito.when(repo.existsById(1)).thenReturn(false);
			
		// Give Error PotentialStubbingProblem 	
	//	Mockito.when(repo.save(emp)).thenReturn(emp);
		
		// Its returning Entity
		// Employee saved = repo.save(EmployeeDTO.mapToEntity(employeeDTO));
		Mockito.when(repo.save(Mockito.any(Employee.class))).thenReturn(emp);
//		or
//		Mockito.when(repo.save(Mockito.refEq(emp))).thenReturn(emp);

		
		//  When coming to service, From Controller Its DTO
		//  Controller Part:
		//  EmployeeDTO employeeDTO = service.save(dto);
		EmployeeDTO empDTO= service.save(EmployeeDTO.mapToDTO(emp));
		
		Assertions.assertEquals(emp.getId(),empDTO.getId());
		Assertions.assertEquals(emp.getName(), empDTO.getName());
	}
	
	@Test
    @DisplayName("Employee Save - Already Exists Exception")
    public void employeeSaveFailedExceptionThrow() {
		
        // Simulate that employee already exists
		Mockito.when(repo.existsById(1)).thenReturn(true);

		// With in Controller calling service
		// In Controller its giving DTO
		
        EmployeeAlreadyExistException thrown = Assertions.assertThrows(
            EmployeeAlreadyExistException.class,
            () -> service.save(EmployeeDTO.mapToDTO(emp))
        );

        Assertions.assertTrue(thrown.getMessage().contains("Employee already exists with ID: " + emp.getId()));
    }
	
}
