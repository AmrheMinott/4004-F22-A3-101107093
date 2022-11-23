package crazyeight.websocket.spring.model;

public class CrazyEightMessage {

	private String toWhom;
	private String fromWho;
	private String message;

	public String getToWhom() {
		return toWhom;
	}

	public void setToWhom(String toWhom) {
		this.toWhom = toWhom;
	}

	public String getFromWho() {
		return fromWho;
	}

	public void setFromWho(String fromWho) {
		this.fromWho = fromWho;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message " + message + " From: " + fromWho + " To: " + toWhom;
	}

}
