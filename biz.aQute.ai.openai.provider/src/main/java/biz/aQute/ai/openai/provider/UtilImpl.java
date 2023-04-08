package biz.aQute.ai.openai.provider;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.LogReaderService;

import biz.aQute.ai.chat.api.Chat;
import biz.aQute.ai.chat.api.OpenAI;
import biz.aQute.ai.chat.api.Reply;

@Component
public class UtilImpl implements Util {

	@Reference
	LogReaderService	log;
	@Reference
	OpenAI				openAI;
	@Reference
	CommandProcessor	gogo;

	BundleContext		context;

	void activate(BundleContext context) {
		this.context = context;
	}

	public String currentTime() {
		return LocalDateTime.now().toString();
	}

	public String currentUser() {
		return "peter";
	}

	public String log() {
		return Collections.list(log.getLog()).stream()
				.map(Object::toString)
				.collect(Collectors.joining("\n"));
	}

	public BundleContext bundlecontext() {
		return context;
	}

	public Optional<Bundle> bundle(String x) {
		return Stream.of(context.getBundles()).filter(s -> s.getSymbolicName().equals(x)).findAny();
	}

	public Object gogo(String cmdline) throws Exception {

		try (CommandSession session = gogo.createSession(System.in, System.out, System.err);) {
			return session.execute(cmdline);
		}
	}

	public String askgpt(String cmdline) throws Exception {
		Chat chat = openAI.createChat();
		Reply ask = chat.ask(cmdline);
		return ask.reply;
		
	}
}
