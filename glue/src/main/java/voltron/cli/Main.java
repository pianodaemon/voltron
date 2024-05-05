package voltron.cli;

import voltron.coresys.VoltronException;
import voltron.coresys.RestClientException;
import voltron.coresys.tampering.XmlFormater;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import java.util.Map;
import voltron.coresys.alterations.Alteration00;
import voltron.coresys.wlp.mbeans.LibertyConnREST;

public class Main {

    public static void main(String[] args) {
        try {
            takeInputFromCli(args);
        } catch (VoltronException | RestClientException ex) {
            ex.printStackTrace();
        }
    }

    private static void takeInputFromCli(String[] args) throws VoltronException, RestClientException {
        Options options = new Options()
                .addOption(Option.builder("d")
                        .longOpt("description")
                        .required(true)
                        .hasArg(true)
                        .desc("This is a demo option upon cli")
                        .build());

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine;
        String desc;
        try {
            cmdLine = parser.parse(options, args);
            desc = cmdLine.getOptionValue('d');
        } catch (ParseException ex) {
            final String emsg = "Parser cli went mad";
            throw new VoltronException(emsg, ex);
        }


        XmlFormater xmlFormater;
        xmlFormater = new Alteration00("server_original.xml", desc, false);
        xmlFormater.renderFeaturingSave("server.xml");
        LibertyConnREST lc = new LibertyConnREST("127.0.0.1", 9443);
        Map<String, String> m = lc.inquiryAllApplicationStatus();
        m.forEach((key, value) -> System.out.println(key + "->" + value));
    }
}
