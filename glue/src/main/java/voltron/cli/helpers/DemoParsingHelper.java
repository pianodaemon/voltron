package voltron.cli.helpers;

import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import voltron.cli.GlobalConfig;
import voltron.coresys.RestClientException;
import voltron.coresys.SculptorException;
import voltron.coresys.alterations.Alteration00;
import voltron.coresys.tampering.XmlFormater;
import voltron.coresys.wlp.mbeans.LibertyConnREST;

public class DemoParsingHelper {

    public static final Option OPTION_SUB_CMD = Option.builder("d")
            .longOpt("description")
            .required(false)
            .hasArg(true)
            .desc("This is a demo option upon cli")
            .build();

    public static void tie(String[] args, CommandLineParser parser, Options options) throws SculptorException, RestClientException, Exception {
        CommandLine cmdLine;
        String desc;
        try {
            cmdLine = parser.parse(options, args);
            if (!cmdLine.hasOption('d')) {
                return;
            }
            desc = cmdLine.getOptionValue('d');
        } catch (ParseException ex) {
            throw new SculptorException(ex);
        }

        System.out.println("xxx: " + desc);
        XmlFormater xmlFormater;
        xmlFormater = new Alteration00(GlobalConfig.getInstance().getSrvOriginal(), desc, false);
        xmlFormater.renderFeaturingSave(GlobalConfig.getInstance().getSrvMadeUp());
        LibertyConnREST lc = new LibertyConnREST("127.0.0.1", 9443);
        Map<String, String> m = lc.inquiryAllApplicationStatus();
        m.forEach((key, value) -> System.out.println(key + "->" + value));
    }
}
