package node.web;

//channel 이용해야 함

public class WebEvent {
	public final String key;
	public final String value;
	// 정보 X
	
	WebEvent(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
