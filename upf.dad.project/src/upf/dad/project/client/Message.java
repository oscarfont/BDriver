package upf.dad.project.client;

public class Message {
	
	// Variables
	private long chat_id;
	private String text;
		
	//Constructor
	public Message() {}
	public Message(long chat_id, String text) {
		this.chat_id = chat_id;
		this.text = text;
	}
		
	// Getters
	public long getChat_id() { return chat_id; }
	public String getText() { return text; }
		
	// Setters
	public void setChat_id(long chat_id) { this.chat_id = chat_id; }
	public void setText(String text) { this.text = text; }

	// toString
	public String toString() {
		return "Messages [chat_id=" + chat_id + ", text=" + text + "]";
	}
}
