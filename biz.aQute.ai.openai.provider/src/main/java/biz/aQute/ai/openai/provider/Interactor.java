package biz.aQute.ai.openai.provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jruby.embed.ScriptingContainer;

import biz.aQute.ai.chat.api.Chat;
import biz.aQute.ai.chat.api.Reply;

class Interactor {
	final static Pattern	BODY_P	= Pattern.compile("```(?<content>.*)```", Pattern.DOTALL);
	final Chat				chat;
	final Util				util;
    final ScriptingContainer container = new ScriptingContainer();


	final String			prolog	= """
			You are an assistant that answers with JRuby code that has
			a function like ` def execute(util) ... end` where the return value is the 
			real answer given to the user. 

			The parameter `util` can be used to extract specific
			information about this session, it has the following functions.

				// return the current user time in the ISO_LOCAL_DATE_TIME format
				def currentTime() ... end

				// return the current user name as a string
				def currentUser() ... end

				// returns an the current log, one log entry per line
				def log()  ... end

				// you can request another instance of you to process the string and give an answer
				def askgpt(string)  ... end

								// returns the OSGi bundle context
				// notice that maps are Java Dictionary objects so instead
				// of containsKey(k), use get(k) != null
				def bundlecontext()  ... end

                // returns a Java Optional<Bundle>
				def bundle(String bsn) ... end
				
                // execute a gogo command
                // scr:list gives you OSGi components
				def gogo(String commandLine) ... end

			   For example, to get the current time, the response must be:
			   ```
			   def execute(util)
			      return util.currentTime();
			   end
			   ```
			   Do not output anything but valid JRuby code. Do not explain
			   the script. Do not put anything around the script. If there is no
			   need to call any of the util functions, just return a string
			   with your answer. Prefer your own answers, only call the
			   util functions when you need the real time information.
			""";

	Interactor(Chat chat, Util util) {
		this.chat = chat;
		this.util = util;
		chat.system(prolog);

	}

	Object ask(String request) {
		String extra = "";
		int n = 4;
		String script = "";
		while (n-- > 0)
			try {
				Reply ask = chat.ask(extra + request);
				script = ask.reply;
				Matcher m = BODY_P.matcher(script);
				if (m.find()) {
					script = m.group("content");
				}

				System.out.println(script);
				return compile(script,util);
			} catch (Exception e) {
				System.out.println("error: " + e.getMessage() + "\n");
				extra = script + "\n" + e.getMessage();
			}

		return "failed";
	}

	public Object compile(String script, Object arg) {
        container.runScriptlet(script);
        Object result = container.callMethod(null, "execute",arg);
        return result;
	}

	public void clear() {
		chat.clear();
		chat.system(prolog);
	}

}
