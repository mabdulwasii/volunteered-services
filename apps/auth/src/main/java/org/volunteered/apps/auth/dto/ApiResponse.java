package org.volunteered.apps.auth.dto;

public class ApiResponse {

	private final String message;

	public ApiResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "ApiResponse{" +
				"message='" + message + '\'' +
				'}';
	}
}
