package biz.aQute.ai.openai.provider;

import java.util.List;

import org.apache.felix.service.command.annotations.GogoCommand;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.lib.strings.Strings;
import biz.aQute.ai.chat.api.Chat;
import biz.aQute.ai.chat.api.OpenAI;

@GogoCommand(function = { "system", "ask", "aski","clear", "model", "models", "cap" }, scope = "chatgpt")
@Component(service = ChatGogo.class, immediate = true)
public class ChatGogo {

	final OpenAI	openai;

	Chat			state;
	Interactor interactor;
	
	@Activate
	public ChatGogo(@Reference OpenAI openai, @Reference Util util) {
		this.openai = openai;
		this.state = openai.createChat();
		this.interactor = new Interactor(openai.createChat(), util);
	}

	public void system(String... system) {
		state.system(join(system));
	}

	public String ask(String... request) {
		return state.ask(join(request)).reply;
	}

	public Object aski(String... request) {
		return interactor.ask(join(request));
	}

	public void clear() {
		state = openai.createChat();
		interactor.clear();
	}

	public List<String> models() {
		return openai.models();
	}

	public void model(String model) {
		state.model(model);
	}

	private String join(String... strings) {
		return Strings.join(" ", strings);
	}

}
