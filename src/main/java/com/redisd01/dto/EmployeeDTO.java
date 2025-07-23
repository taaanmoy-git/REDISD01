package com.redisd01.dto;

import com.redisd01.entity.Employee;

public class EmployeeDTO {

	private Integer id;
    private String name;
    private Integer age;
    private String gender; // male/female
    
    
    
	public EmployeeDTO() {
		super();
	}
	public EmployeeDTO(Integer id, String name, Integer age, String gender) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
		this.gender = gender;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	@Override
	public String toString() {
		return "EmployeeDTO [id=" + id + ", name=" + name + ", age=" + age + ", gender=" + gender + "]";
	}
    
    public static EmployeeDTO mapToDTO(Employee emp) {
        return new EmployeeDTO(emp.getId(), emp.getName(), emp.getAge(), emp.getGender());
    }
    

    public static Employee mapToEntity(EmployeeDTO dto) {
        return new Employee(dto.getId(), dto.getName(), dto.getAge(), dto.getGender());
    }

	
}
