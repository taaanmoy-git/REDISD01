package com.redisd01.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.redisd01.dto.EmployeeDTO;
import com.redisd01.service.EmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    @Qualifier("employeeServiceImplCacheAnnotation")
    private EmployeeService service;

    @PostMapping("/save")
    public ResponseEntity<EmployeeDTO> save(@RequestBody EmployeeDTO dto) {
        EmployeeDTO employeeDTO = service.save(dto);
        return new ResponseEntity<>(employeeDTO, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDTO>> getAll() {
        List<EmployeeDTO> employees = service.getAll();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getById(@PathVariable Integer id) {
        EmployeeDTO employeeDTO = service.getById(id);
        return new ResponseEntity<>(employeeDTO, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EmployeeDTO> update(@PathVariable Integer id, @RequestBody EmployeeDTO dto) {
        EmployeeDTO updated = service.update(id, dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PatchMapping("/updatePartial/{id}")
    public ResponseEntity<EmployeeDTO> updatePartial(@PathVariable Integer id, @RequestBody EmployeeDTO dto) {
        EmployeeDTO updated = service.updatePartial(id, dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        service.deleteById(id);
        return new ResponseEntity<>("Employee deleted successfully with ID: " + id, HttpStatus.OK);
    }
}
