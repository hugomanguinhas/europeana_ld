package pt.ist.oai.harvester.cmd;

import java.util.*;

import static pt.ist.oai.harvester.cmd.HarvesterConfigs.*;

public class HarvesterCmd
{
    protected static Map<String,VerbCmd> _map = new HashMap<String,VerbCmd>();

    protected static void registerVerb(VerbCmd v) { _map.put(v.getVerb(), v); }

    static
    {
        registerVerb(new GetRecordCmd());
        registerVerb(new IdentifyCmd());
        registerVerb(new ListIdentifiersCmd());
        registerVerb(new ListMetadataFormatsCmd());
        registerVerb(new ListSetsCmd());
        registerVerb(new ListRecordsCmd());
    }


    public static void main(String[] args)
    {
        if (args.length == 0)           { printMainUsage(System.out); return; }

        List<String> argsList = Arrays.asList(args);
        if (argsList.contains("-help")) { printMainUsage(System.out); return; }

        String verbStr = args[0];
        VerbCmd verb = _map.get(verbStr);
        if(verb != null) {
            verb.process(Arrays.copyOfRange(args, 1, args.length));
        }
        else {
            printMainUsage(System.out, "Illegal Verb '" + verbStr + "'");
        }
    }
}