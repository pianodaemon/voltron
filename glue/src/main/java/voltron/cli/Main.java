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

    public static final int EXIT_FAILURE = 1;

    public static void main(String[] args) {
        try {
            if (args.length == 0 || !args[0].startsWith("-")) {
                System.err.println("No subcommand provided.");
                System.exit(EXIT_FAILURE);
            }
            takeInputFromCli(args);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(EXIT_FAILURE);
        }
    }

    private static void takeInputFromCli(String[] args) throws SculptorException, RestClientException, Exception {
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
