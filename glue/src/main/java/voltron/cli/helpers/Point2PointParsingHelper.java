package voltron.cli.helpers;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import voltron.coresys.SculptorException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import voltron.cli.GlobalConfig;
import voltron.coresys.alterations.Point2PointEnabler;
import voltron.coresys.tampering.XmlFormater;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
final public class Point2PointParsingHelper {

    private static final int ACTION_ARG_IDX = 0;
    public static final Option OPTION_SUB_CMD = Option.builder("q")
            .longOpt("queue")
            .required(false)
            .hasArg(true)
            .desc("Specify subcommand for queue")
            .build();

    private @NonNull
    CommandLineParser cliParser;

    private @NonNull
    Options stemOptions;

    public static void tie(String[] args, CommandLineParser parser, Options options) throws Exception, SculptorException {
        Point2PointParsingHelper qpHelper = new Point2PointParsingHelper(parser, options);
        qpHelper.doParsing(args, (cmdLine) -> {
            if (cmdLine.hasOption('q')) {
                if (!(cmdLine.getArgs().length > 0)) {
                    throw new SculptorException("No arguments for the respective action were provided");
                }
                handleQueueSubCmd(cmdLine.getOptionValue('q'), cmdLine.getArgs()[ACTION_ARG_IDX]);
            }
        });
    }

    private static void handleQueueSubCmd(final String action, final String queueName) throws Exception, SculptorException {

        if (action.equals("new")) {
            System.out.println("This is the newer queue:" + queueName);
            XmlFormater xmlFormater;
            xmlFormater = new Point2PointEnabler(GlobalConfig.getInstance().getSrvOriginal(), queueName);
            xmlFormater.renderFeaturingSave(GlobalConfig.getInstance().getSrvMadeUp());
        } else {
            throw new SculptorException("Such action has not been supported yet");
        }
    }

    private void doParsing(String[] args, SubCmdHandler sch) throws Exception {
        sch.bind(getCliParser().parse(getStemOptions(), args));
    }

    @FunctionalInterface
    private interface SubCmdHandler {

        public void bind(CommandLine cmdLine) throws Exception;
    }
}
