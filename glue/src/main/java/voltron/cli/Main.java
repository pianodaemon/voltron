package voltron.cli;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionGroup;
import voltron.coresys.SculptorException;
import voltron.coresys.RestClientException;
import voltron.cli.helpers.DemoParsingHelper;
import voltron.cli.helpers.QueueParsingHelper;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.err.println("No arguments provided.");
                System.exit(1);
            } else {
                takeInputFromCli(args);
            }
        } catch (SculptorException | RestClientException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    private static void takeInputFromCli(String[] args) throws SculptorException, RestClientException {
        CommandLineParser parser = new DefaultParser();
        OptionGroup subCmdGroup = new OptionGroup();
        subCmdGroup
                .addOption(DemoParsingHelper.OPTION_SUB_CMD)
                .addOption(QueueParsingHelper.OPTION_SUB_CMD);

        Options options = new Options();
        options.addOptionGroup(subCmdGroup);

        DemoParsingHelper.tie(args, parser, options);
        QueueParsingHelper.tie(args, parser, options);
    }
}
