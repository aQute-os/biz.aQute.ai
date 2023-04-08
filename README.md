# biz.aQute.ai

We recently had a discussion here about Java, OSGi, and the meaning of life. This made me a bit melancholic. Clearly the Large Language Models (LLM) like for example OpenAI's gpt and Midjourney will make drastic changes in our world. One of the more debilitatingly frustrating  parts of my curmudgeon life today is that Java seems to have vanished from all the interesting parts of the software industry. Even Java libraries supplied for the cool stuff seem to be getting rarer. And of course the greatest insult is that Github co-pilot does not even run in Eclipse, or worse, the Eclipse people do not even seem to be aware or care.  I think the C++ guys must have felt similar at the end of the last century :frowning: 

Anyway, I am not yet ready to give up. One of the shortcomings is that gpt does not have access to real time or context information. All its knowledge is confined to its training data (which for gpt-4 is amazing, it can even tell you my bio!) and whatever happens in the session. I saw a video that showed how a guy (they are always guys?) created a pipeline. It looked up the query in a semantic database with documents, compressed the text and fed this as the prolog to the gpt session. This allowed him to discuss large documents with his AI friend. 

At the same time, we now have plugins on chat gpt! I initially registered multiple times hoping that would speed up my acceptance but they kept ignoring me. So I told myself that plugins cannot access local information very easily so I can do better. Pondering about this problem I got the idea to let gpt write a function that when executed could give the actual response to the user. The function could provide all the information its language had access. For domain functions, I could just add an API description.

Well, gpt is not a Java guru. Even though I fed back the code it generated with the errors found by the compiler so it could fix it, it got stuck most of the time. (A nice way to burn tokens!) A pity, because the Java interface are a real nice way to specify functionality. I then tried Javascript but gpt assumed it was running in a full blown Node or browser and all I got was a almost dead Rhino. I then tried to use JRuby and this worked quite well.

For example, in the prolog I say among other things:

    // return the current user time in the ISO_LOCAL_DATE_TIME format
    def currentTime() ... end

when I know ask gpt:

    $ aski what is the current time
    def execute(util)
      return util.currentTime();
    end
    2023-04-08T17:03:42.696177
    $ 

That was easy, but can it use the date?

        $ aski what week day is it today
        def execute(util)
          now = java.time.LocalDateTime.now()
          dayOfWeek = now.getDayOfWeek().toString()
          return "Today is #{dayOfWeek}."
        end
        Today is SATURDAY.
        $ 

        $ aski how many days between today and May 1
        def execute(util)
          require 'date'
          today = Date.today
          may_1 = Date.new(today.year, 5, 1)
          days_between = (may_1 - today).to_i
          return "There are #{days_between} days between today and May 1."
        end
        There are 23 days between today and May 1.
        $ 

But what about OSGi? So I added a function to return the Bundle Context:

    // returns the OSGi bundle context
    // notice that maps in OSGi are Java Dictionary objects so instead
    // of containsKey(k), use get(k) != null
    def bundlecontext()  ... end

So in theory, gpt knows about OSGi and its Java interface, and it can infer that JRuby will use Java objects under the hood. 

        $ aski show all bundles from apache
        To show all bundles from Apache, the response is:
        def execute(util)
          result = ""
          bundle_context = util.bundlecontext()
          bundles = bundle_context.getBundles()
          for bundle in bundles
            if bundle.getSymbolicName().start_with?("org.apache")
              result += "#{bundle.getSymbolicName()} - #{bundle.getVersion()}\n"
            end
          end
          return result
        end
        
        error: (SyntaxError) <script>:5: syntax error, unexpected tXSTRING_BEG
    
        def execute(util)
          result = ""
          bundle_context = util.bundlecontext()
          bundles = bundle_context.getBundles()
          for bundle in bundles
            if bundle.getSymbolicName().start_with?("org.apache")
              result += "#{bundle.getSymbolicName()} - #{bundle.getVersion()}\n"
            end
          end
          return result
        end
        
        org.apache.felix.framework - 7.0.5
        org.apache.commons.commons-fileupload - 1.5.0
        org.apache.commons.commons-io - 2.11.0
        org.apache.felix.configadmin - 1.9.26
        org.apache.felix.gogo.command - 1.1.2
        org.apache.felix.gogo.runtime - 1.1.6
        org.apache.felix.http.servlet-api - 1.2.0
        org.apache.felix.inventory - 1.1.0
        org.apache.felix.log - 1.2.6
        org.apache.felix.scr - 2.2.6
        org.apache.felix.webconsole - 4.8.8
        org.apache.felix.webconsole.plugins.ds - 2.2.0
        $ 

It had to fix the script because it was too verbose but it came up with a correct answer in the end. This is still  concern, a better prolog might help here.

        $ aski show all bundles that  have a major version larger than 5
        def execute(util)
          result = ""
          bundle_context = util.bundlecontext()
          bundles = bundle_context.getBundles()
          for bundle in bundles
            version = bundle.getVersion().toString()
            major_version = version.split(".")[0].to_i
            if major_version > 5
              result += "#{bundle.getSymbolicName()} - #{bundle.getVersion()}\n"
            end
          end
          return result
        end
        
        org.apache.felix.framework - 7.0.5
        aQute.libg - 6.4.0.202302281434
        org.jruby.jruby - 9.3.10.0
 
Anyway pretty cool! This is of course a playground and I've spent zero time in making the code clean. And the prolog needs work. This was based on GPT-3.5 I hope gpt-4 will be better becase it quite often needs to correct its code several times. But when I've time I will continue experiment with this. Then again, I just got accepted as a Chat GPT plugin developer ...

Primary goal is to make the plugins services and use the idea of a semantic search of the question to associate the needed services so that the prolog can only focus on the services that are actually needed.

You can find it at:

      https://github.com/aQute-os/biz.aQute.ai

If you add cool features, please submit a PR (no issues please, if you have a cool idea that will make is an AI millionaire mail is a better medium.) All my code is Apache 2.0.
