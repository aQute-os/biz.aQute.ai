package biz.aQute.ai.openai.provider;

import java.time.Instant;

public class Appointment {
	public Instant time;
	public String description;
	public Instant getInstanceTime() {
		return time;
	}
}
