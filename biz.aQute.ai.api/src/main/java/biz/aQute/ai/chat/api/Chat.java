package biz.aQute.ai.chat.api;

public interface Chat {

	Reply ask(String question);

	void system(String command);
	
	void model(String model);

	void clear();
}
