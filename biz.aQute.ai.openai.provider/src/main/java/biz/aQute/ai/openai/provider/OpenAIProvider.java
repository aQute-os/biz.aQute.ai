package biz.aQute.ai.openai.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.util.List;

import org.osgi.dto.DTO;
import org.osgi.service.component.annotations.Component;

import aQute.bnd.exceptions.Exceptions;
import aQute.lib.json.JSONCodec;
import biz.aQute.ai.chat.api.Chat;
import biz.aQute.ai.chat.api.OpenAI;

@Component
public class OpenAIProvider implements OpenAI {
	final String			apiKey			= System.getProperty("OPENAI_KEY");
	final String			openaiApiUrl	= "https://api.openai.com/v1/chat/completions";
	final String			models			= "https://api.openai.com/v1/models";

	final static JSONCodec	codec			= new JSONCodec();
	static {
		codec.setIgnorenull(true);
	}
	final HttpClient client = HttpClient.newHttpClient();

	@Override
	public Chat createChat() {
		return new ChatImpl(this);
	}

	<M extends DTO, R extends DTO> R get(String url, M msg, Class<R> replyType) {
		try {
			String payload = codec.enc().writeDefaults().put(msg).toString();
			System.out.println(payload);
			
			Builder requestBuilder = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer " + apiKey);

			if ( msg != null)
				requestBuilder=requestBuilder.POST(HttpRequest.BodyPublishers.ofString(payload));
			else 
				requestBuilder=requestBuilder.GET();
				
			HttpRequest request = requestBuilder.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			String body = response.body();
			if (response.statusCode() >= 300) {
				throw new RuntimeException("Error response: " + body);
			} else {
				return codec.dec().from(body).get(replyType);
			}
		} catch (Exception e) {
			throw Exceptions.duck(e);
		}
	}

	public static class Model extends DTO {
		public String		id;
		public String		object;
		public String		owned_by;
		public List<Object>	permission;
	};

	public static class Models extends DTO {
		public List<Model>	data;
		public String		object;
	};

	@Override
	public List<String> models() {
		return get(models, null, Models.class).data.stream().map(m -> m.id).toList();
	}

}
