package com.redisd01.excepion;

public class EmployeeAlreadyExistException extends RuntimeException{


	private static final long serialVersionUID = 1L;

	public EmployeeAlreadyExistException(String message) {
		super(message);
	}
}
