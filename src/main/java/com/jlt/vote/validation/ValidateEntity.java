package com.jlt.vote.validation;

public class ValidateEntity {
	private boolean result;
	private String errorMessage;
	public ValidateEntity(boolean result,String errorMessage) {
		this.result = result;
		this.errorMessage = errorMessage;
	}
	
	public ValidateEntity(boolean result) {
		this.result = result;
	}
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
