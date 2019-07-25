package upf.dad.project.data;

import java.util.Arrays;

public class User {

	private String phoneNumber;
	private long chatID;
	private int[] subscribedStations;

	// Constructor
	public User() {}
	public User(String newphoneNumber, long newchatID, int[] newsubscribedStations) {
		this.phoneNumber = newphoneNumber;
		this.chatID = newchatID;
		this.subscribedStations = newsubscribedStations;
	}
	
	// Getters
	public String getPhoneNumber() { return phoneNumber; }
	public long getChatId(){
		return this.chatID;
	}
	public int[] getSubscribedStations() { return subscribedStations; }
	
	// Setters
	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
	public void setSubscribedStations(int[] subscribedStations) { this.subscribedStations = subscribedStations; }
	
	// toString
	@Override
	public String toString() {
		return "User [phoneNumber=" + phoneNumber + ", chatID=" + chatID 
				+ ", subscribedStations=" + Arrays.toString(subscribedStations) + "]";
	}
}
