package biz.aQute.ai.chat.api;

import java.util.List;

public interface OpenAI {
	Chat createChat();

	List<String> models();
}
