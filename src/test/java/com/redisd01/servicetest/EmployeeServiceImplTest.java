package com.redisd01.servicetest;

import java.nio.file.FileSystemNotFoundException;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
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
import com.redisd01.excepion.EmployeeNotFoundException;
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

		//  Controller Part:
		//  EmployeeDTO employeeDTO = service.save(dto);
		EmployeeDTO empDTO= service.save(EmployeeDTO.mapToDTO(emp));
		
		Assertions.assertEquals(emp.getId(),empDTO.getId());
		Assertions.assertEquals(emp.getName(), empDTO.getName());
	}
	
	@Test
    @DisplayName("Employee Save Failed - Already Exists Exception")
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
	
	@Test
	@DisplayName("Employee getAll Success")
	public void employeeGetALLSuccess(){
		//Return List Employee
		List<Employee> empList= List.of(new Employee(1, "Tanmoy", 29, "Male"),
				new Employee(2, "Soumi", 24, "Female"));
		
		Mockito.when(repo.findAll()).thenReturn(empList);
		//Controller 
		List<EmployeeDTO> empDTOList= service.getAll();
		
		Assertions.assertEquals(2, empDTOList.size());
		Assertions.assertEquals(empList.get(0).getName(),empDTOList.get(0).getName());
	}
	
	@Test
	@DisplayName("Employee getAll Failed")
	public void employeeGetALLFailed(){
		
		Mockito.when(repo.findAll()).thenReturn(List.of());
		
		//Controller 
		EmployeeNotFoundException exc= Assertions.assertThrows(EmployeeNotFoundException.class, 
				()-> service.getAll());
		
		Assertions.assertEquals("No employees found in the database",exc.getMessage());
		
	}
	
	@Test
	@DisplayName("GetById Success")
	public void employeeGetByIdSuccess() {
		Mockito.when(repo.findById(1)).thenReturn(Optional.of(emp));
		
		//Controller 
		EmployeeDTO empDTO= service.getById(1);
		
		Assertions.assertEquals(emp.getAge(), empDTO.getAge());
		
		Mockito.verify(repo, Mockito.times(1)).findById(1);
	}
	
	@Test
	@DisplayName("GetById Failed")
	public void employeeGetByIdFailed() {
		Mockito.when(repo.findById(111)).thenReturn(Optional.empty());
		
		//Controller 
		EmployeeNotFoundException exc= Assertions.assertThrows(EmployeeNotFoundException.class, 
				()-> service.getById(111));
		
		Assertions.assertEquals("Employee not found with ID: " + 111, exc.getMessage());
	
	    Mockito.verify(repo, Mockito.times(1)).findById(111);
	}
	
	@Test
	@DisplayName("Full Update Employee - Success")
	void updateEmployee_Success() {
		Integer id = 1;
	    EmployeeDTO inputDto = new EmployeeDTO(id, "UpdatedName", 35, "Male");
	    Employee existingEmp = new Employee(id, "OldName", 30, "Male");
	    Employee updatedEmp = new Employee(id, "UpdatedName", 35, "Male");
	    
	    Mockito.when(repo.findById(id)).thenReturn(Optional.of(existingEmp));
	    Mockito.when(repo.save(Mockito.any(Employee.class))).thenReturn(updatedEmp);
	    
	    EmployeeDTO result= service.update(id, inputDto);
	    
	    Assertions.assertEquals("UpdatedName", result.getName());
	    Assertions.assertEquals(35, result.getAge());
	    Assertions.assertEquals("Male", result.getGender());

	    Mockito.verify(repo).findById(id);
	    Mockito.verify(repo).save(Mockito.any(Employee.class));
	}

	@Test
	@DisplayName("Full Update Employee - Not Found")
	void updateEmployee_NotFound() {
		
		Integer id = 2;
		EmployeeDTO dto = new EmployeeDTO(id, "Name", 40, "Female");

		Mockito.when(repo.findById(id)).thenReturn(Optional.empty());

		EmployeeNotFoundException exc = Assertions.assertThrows(EmployeeNotFoundException.class,
				() -> service.update(id, dto));

		Assertions.assertEquals("Employee not found with ID: " + id, exc.getMessage());
		Mockito.verify(repo).findById(id);

	}

	
	//----------
	@Test
	@DisplayName("Partial Update Employee - Success")
	void updatePartialEmployee_Success() {
	    Integer id = 3;
	    EmployeeDTO dto = new EmployeeDTO(null, "PartialName", null, "Female");
	    Employee existing = new Employee(id, "ExistingName", 28, "Male");
	    Employee updated = new Employee(id, "PartialName", 28, "Female");

	    Mockito.when(repo.findById(id)).thenReturn(Optional.of(existing));
	    Mockito.when(repo.save(Mockito.any(Employee.class))).thenReturn(updated);

	    EmployeeDTO result = service.updatePartial(id, dto);

	    Assertions.assertEquals("PartialName", result.getName());
	    Assertions.assertEquals(28, result.getAge());
	    Assertions.assertEquals("Female", result.getGender());

	    Mockito.verify(repo).findById(id);
	    Mockito.verify(repo).save(Mockito.any(Employee.class));
	}

	@Test
	@DisplayName("Partial Update Employee - Not Found")
	void updatePartialEmployee_NotFound() {
	    Integer id = 4;
	    EmployeeDTO dto = new EmployeeDTO(null, "Name", 22, "Male");

	    Mockito.when(repo.findById(id)).thenReturn(Optional.empty());

	    EmployeeNotFoundException exc = Assertions.assertThrows(EmployeeNotFoundException.class,
	        () -> service.updatePartial(id, dto));

	    Assertions.assertEquals("Employee not found with ID: " + id, exc.getMessage());
	    Mockito.verify(repo).findById(id);
	}

	@Test
	@DisplayName("Delete Employee - Success")
	void deleteEmployee_Success() {
	    Integer id = 5;
	    Employee existing = new Employee(id, "ToDelete", 29, "Male");

	    Mockito.when(repo.findById(id)).thenReturn(Optional.of(existing));
	    Mockito.doNothing().when(repo).deleteById(id);

	    service.deleteById(id);

	    Mockito.verify(repo).findById(id);
	    Mockito.verify(repo).deleteById(id);
	}

	@Test
	@DisplayName("Delete Employee - Not Found")
	void deleteEmployee_NotFound() {
	    Integer id = 6;

	    Mockito.when(repo.findById(id)).thenReturn(Optional.empty());

	    EmployeeNotFoundException exc = Assertions.assertThrows(EmployeeNotFoundException.class,
	        () -> service.deleteById(id));

	    Assertions.assertEquals("Employee not found with ID: " + id, exc.getMessage());
	    Mockito.verify(repo).findById(id);
	}
	
}
