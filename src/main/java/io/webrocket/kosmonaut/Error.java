package io.webrocket.kosmonaut;

public class Error {
	private int code;
	private String message;
	
	public Error(int code){
		this.code = code;
		this.message = getErrorString(code);
	}
	
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getErrorString(int index){
		switch (index) {
		case 400:
			return "Bad request";
		case 402:
			return "Unauthorized";
		case 403:
			return "Forbidden";
		case 451:
			return "Invalid Channel Name";
		case 454:
			return "Channel not found";
		case 597:
			return "Internal error";
		case 598:
			return "End Of File error";
		default:
			return null;
		}
	}
	
	public String toString(){
		return code + " - " + message;
	}
	
}
