package com.redisd01.service;

import com.redisd01.dto.EmployeeDTO;
import java.util.List;

public interface EmployeeService {
    EmployeeDTO save(EmployeeDTO dto);
    List<EmployeeDTO> getAll();
    EmployeeDTO getById(Integer id);
}
