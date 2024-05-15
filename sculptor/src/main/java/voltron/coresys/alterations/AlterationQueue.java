package voltron.coresys.alterations;

import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsActivationSpecFactory;
import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsActivationSpecPropertiesWasJmsJavaxJmsMessageListener;
import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsQueueFactory;
import com.immortalcrab.voltron.portage.ComIbmWsMessagingRuntime;
import com.immortalcrab.voltron.portage.ComIbmWsSibQueueFactory;
import com.immortalcrab.voltron.portage.ObjectFactory;
import com.immortalcrab.voltron.portage.ServerType;
import java.text.MessageFormat;
import java.util.Optional;
import javax.xml.bind.JAXBElement;
import voltron.coresys.SculptorException;
import voltron.coresys.tampering.XmlFormater;

public class AlterationQueue extends XmlFormater {

    private static final String JMS_PREFIX = "jms";
    private static final String ACT_SPEC_POSTFIX = "Act_Spec";
    private static final String ACT_SPEC_DEST_TYPE = "javax.jms.Queue";
    private static final String QUEUE_SESSION_JNDI_TPL = JMS_PREFIX + "/{0}";
    private static final String ACT_SPEC_ID_TPL = JMS_PREFIX + "/{0}_{1}";

    private final String mName;

    public AlterationQueue(String xmlFilePath, String name) throws SculptorException {
        super(xmlFilePath);
        mName = name;
    }

    @Override
    public JAXBElement<ServerType> tailorHandler() throws SculptorException {
        ServerType st = getServer().getValue();
        ComIbmWsMessagingRuntime messagingRuntime = obtainRuntime(st);
        ComIbmWsSibQueueFactory sqf = makeQueue();
        if (findQueueWithinRuntime(messagingRuntime, sqf)) {
            throw new SculptorException("Queue is already present");
        }
        addQueueIntoRuntime(messagingRuntime, sqf);
        setupQueueSession(st);
        setupQueueActivationSpec(st);
        return getServer();
    }

    private ComIbmWsMessagingRuntime obtainRuntime(ServerType st) {
        Optional<ComIbmWsMessagingRuntime> messagingRuntime = Optional.ofNullable(findMessagingRuntime(st));
        if (messagingRuntime.isPresent()) {
            return messagingRuntime.get();
        }
        ComIbmWsMessagingRuntime mrt = new ObjectFactory().createComIbmWsMessagingRuntime();
        st.getIncludeOrVariableOrWebApplication().add(mrt);
        return mrt;
    }

    private ComIbmWsSibQueueFactory makeQueue() {
        ComIbmWsSibQueueFactory sqf = new ObjectFactory().createComIbmWsSibQueueFactory();
        sqf.setId(mName);
        return sqf;
    }

    private boolean findQueueWithinRuntime(ComIbmWsMessagingRuntime messagingRuntime, ComIbmWsSibQueueFactory sqf) {
        for (Object element : messagingRuntime.getFileStoreOrQueueOrTopicSpace()) {
            if (element instanceof ComIbmWsSibQueueFactory) {
                return sqf.getId().equals(((ComIbmWsSibQueueFactory) element).getId());
            }
        }
        return false;
    }

    private void addQueueIntoRuntime(ComIbmWsMessagingRuntime messagingRuntime, ComIbmWsSibQueueFactory sqf) {
        messagingRuntime.getFileStoreOrQueueOrTopicSpace().add(sqf);
    }

    private static ComIbmWsMessagingRuntime findMessagingRuntime(ServerType st) {
        for (Object element : st.getIncludeOrVariableOrWebApplication()) {
            if (element instanceof ComIbmWsMessagingRuntime) {
                return (ComIbmWsMessagingRuntime) element;
            }
        }
        return null;
    }

    private void setupQueueActivationSpec(ServerType st) {
        ComIbmWsJcaJmsActivationSpecPropertiesWasJmsJavaxJmsMessageListener propertyWasJms = new ObjectFactory()
                .createComIbmWsJcaJmsActivationSpecPropertiesWasJmsJavaxJmsMessageListener();
        propertyWasJms.setDestinationType(ACT_SPEC_DEST_TYPE);
        propertyWasJms.setDestinationRef(mName);
        ComIbmWsJcaJmsActivationSpecFactory aspec = new ObjectFactory().createComIbmWsJcaJmsActivationSpecFactory();
        aspec.setId(MessageFormat.format(ACT_SPEC_ID_TPL, new Object[]{mName, ACT_SPEC_POSTFIX}));
        aspec.getAuthDataOrPropertiesWasJms().add(propertyWasJms);
        st.getIncludeOrVariableOrWebApplication().add(aspec);
    }

    private void setupQueueSession(ServerType st) {
        // Declare a queue resource to create a Producer/Consumer session to the queue
        ComIbmWsJcaJmsQueueFactory session = new ObjectFactory().createComIbmWsJcaJmsQueueFactory();
        session.setJndiName(MessageFormat.format(QUEUE_SESSION_JNDI_TPL, new Object[]{mName}));
        st.getIncludeOrVariableOrWebApplication().add(session);
    }
}
