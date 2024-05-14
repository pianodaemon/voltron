package voltron.cli.helpers;

import java.util.Optional;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import voltron.coresys.SculptorException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import voltron.coresys.alterations.AlterationQueue;
import voltron.coresys.tampering.XmlFormater;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
final public class QueueParsingHelper {

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

    public static void tie(String[] args, CommandLineParser parser, Options options) throws SculptorException {
        QueueParsingHelper qpHelper = new QueueParsingHelper(parser, options);
        qpHelper.doParsing(args, (cmdLine) -> {
            if (cmdLine.hasOption('q')) {
                if (!(cmdLine.getArgs().length > 0)) {
                    throw new SculptorException("No arguments for the respective action were provided");
                }
                handleQueueSubCmd(cmdLine.getOptionValue('q'), cmdLine.getArgs()[ACTION_ARG_IDX]);
            }
        });
    }

    private static void handleQueueSubCmd(final String action, final String queueName) throws SculptorException {
        Optional<String> srvOriginal = Optional.ofNullable(System.getenv("SERVER_ORIGINAL"));
        Optional<String> srvMadeUp = Optional.ofNullable(System.getenv("SERVER_MADE_UP"));
        if (srvOriginal.isEmpty() || srvMadeUp.isEmpty()) {
            throw new SculptorException("Original and Made up server files has not been set");
        }
        if (action.equals("new")) {
            System.out.println("This is the newer queue:" + queueName);
            XmlFormater xmlFormater;
            xmlFormater = new AlterationQueue(srvOriginal.get(), queueName);
            xmlFormater.renderFeaturingSave(srvMadeUp.get());
        } else {
            throw new SculptorException("Such action has not been supported yet");
        }
    }

    private void doParsing(String[] args, SubCmdHandler sch) throws SculptorException {
        try {
            sch.bind(getCliParser().parse(getStemOptions(), args));
        } catch (ParseException ex) {
            throw new SculptorException(ex);
        }
    }

    @FunctionalInterface
    private interface SubCmdHandler {

        public void bind(CommandLine cmdLine) throws SculptorException;
    }
}
