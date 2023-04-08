package biz.aQute.ai.openai.provider;
import org.jruby.embed.ScriptingContainer;

public class JRubyTest {
    public static void main(String[] args) throws Exception {
        ScriptingContainer container = new ScriptingContainer();
        
        // Evaluate the JRuby code
        String code = "def my_function(x)\n  return x+1\nend";
        container.runScriptlet(code);

        // Call the JRuby function from Java
        long param = 42;
        Object result = container.callMethod(null, "my_function",param);
        System.out.println(result);
    }
}