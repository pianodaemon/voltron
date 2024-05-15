package voltron.coresys.alterations;

import com.immortalcrab.voltron.portage.ComIbmWsJcaConnectionManagerFactory;
import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsActivationSpecFactory;
import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsActivationSpecPropertiesWasJmsJavaxJmsMessageListener;
import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsQueueConnectionFactoryFactory;
import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsQueueFactory;
import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsQueuePropertiesWasJmsJavaxJmsQueueComIbmWsSibApiJmsImplJmsQueueImpl;
import com.immortalcrab.voltron.portage.ComIbmWsKernelFeature;
import com.immortalcrab.voltron.portage.ComIbmWsMessagingRuntime;
import com.immortalcrab.voltron.portage.ComIbmWsSibQueueFactory;
import com.immortalcrab.voltron.portage.ObjectFactory;
import com.immortalcrab.voltron.portage.ServerType;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.xml.bind.JAXBElement;
import voltron.coresys.SculptorException;
import voltron.coresys.tampering.XmlFormater;

public class Point2PointEnabler extends XmlFormater {

    private static final Set<String> FEATURES_EXPECTED = new HashSet<>(Arrays.asList("wasJmsServer-1.0", "wasJmsClient-2.0", "jndi-1.0"));
    private static final String JMS_PREFIX = "jms";
    private static final String ACT_SPEC_POSTFIX = "Act_Spec";
    private static final String ACT_SPEC_DEST_TYPE = "javax.jms.Queue";
    private static final String QUEUE_SESSION_JNDI_TPL = JMS_PREFIX + "/{0}";
    private static final String ACT_SPEC_ID_TPL = JMS_PREFIX + "/{0}_{1}";
    private static final String DEFAULT_CONN_MGR_ID = "Default_ConnMgr";
    private static final String DEFAULT_CONN_MGR_MAX_POOL_SIZE = "2";
    private static final String DEFAULT_CONN_JNDI_TPL = JMS_PREFIX + "/Default_QCF";

    private final String mName;

    public Point2PointEnabler(String xmlFilePath, String name) throws SculptorException {
        super(xmlFilePath);
        mName = name;
    }

    @Override
    public JAXBElement<ServerType> tailorHandler() throws SculptorException {
        ServerType st = getServer().getValue();
        configurePointToPoint(st);
        return getServer();
    }

    /* Configuring point-to-point messaging for a single Liberty server
     * https://www.ibm.com/docs/en/was-liberty/base?topic=server-configuring-point-point-messaging-single-liberty
     */
    private void configurePointToPoint(ServerType st) throws SculptorException {
        verifyMandatoryFeatures(st);
        setupMessagingEngine(st);
        setupConnManager(st);
        setupQueueSession(st);
        setupQueueActivationSpec(st);
    }

    private void setupConnManager(ServerType st) {
        ComIbmWsJcaConnectionManagerFactory cmf = new ObjectFactory().createComIbmWsJcaConnectionManagerFactory();
        cmf.setId(DEFAULT_CONN_MGR_ID);
        cmf.setMaxPoolSize(DEFAULT_CONN_MGR_MAX_POOL_SIZE);
        st.getIncludeOrVariableOrWebApplication().add(cmf);
        ComIbmWsJcaJmsQueueConnectionFactoryFactory cff = new ObjectFactory().createComIbmWsJcaJmsQueueConnectionFactoryFactory();
        cff.setJndiName(DEFAULT_CONN_JNDI_TPL);
        cff.setConnectionManagerRef(DEFAULT_CONN_MGR_ID);
        st.getIncludeOrVariableOrWebApplication().add(cff);
    }

    private static void verifyMandatoryFeatures(ServerType st) throws SculptorException {
        Optional<ComIbmWsKernelFeature> messagingRuntime = Optional.ofNullable(findKernelFeature(st));
        if (messagingRuntime.isPresent()) {
            HashSet<String> availableOnes = new HashSet<>(messagingRuntime.get().getFeature());
            if (availableOnes.containsAll(FEATURES_EXPECTED)) {
                return;
            }
        }
        throw new SculptorException("Missing one or more server features");
    }

    private static ComIbmWsKernelFeature findKernelFeature(ServerType st) {
        for (Object element : st.getIncludeOrVariableOrWebApplication()) {
            if (element instanceof ComIbmWsKernelFeature) {
                return (ComIbmWsKernelFeature) element;
            }
        }
        return null;
    }

    private static ComIbmWsMessagingRuntime obtainRuntime(ServerType st) {
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

    private static boolean findQueueWithinRuntime(ComIbmWsMessagingRuntime messagingRuntime, ComIbmWsSibQueueFactory sqf) {
        for (Object element : messagingRuntime.getFileStoreOrQueueOrTopicSpace()) {
            if (element instanceof ComIbmWsSibQueueFactory) {
                return sqf.getId().equals(((ComIbmWsSibQueueFactory) element).getId());
            }
        }
        return false;
    }

    private static void addQueueIntoRuntime(ComIbmWsMessagingRuntime messagingRuntime, ComIbmWsSibQueueFactory sqf) {
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
        ComIbmWsJcaJmsQueuePropertiesWasJmsJavaxJmsQueueComIbmWsSibApiJmsImplJmsQueueImpl propertyWasJms = new ObjectFactory()
                .createComIbmWsJcaJmsQueuePropertiesWasJmsJavaxJmsQueueComIbmWsSibApiJmsImplJmsQueueImpl();
        propertyWasJms.setDeliveryMode("Application");
        propertyWasJms.setQueueName(mName);
        ComIbmWsJcaJmsQueueFactory session = new ObjectFactory().createComIbmWsJcaJmsQueueFactory();
        session.setJndiName(MessageFormat.format(QUEUE_SESSION_JNDI_TPL, new Object[]{mName}));
        session.getPropertiesWasJms().add(propertyWasJms);
        st.getIncludeOrVariableOrWebApplication().add(session);
    }

    private void setupMessagingEngine(ServerType st) throws SculptorException {
        ComIbmWsMessagingRuntime messagingRuntime = obtainRuntime(st);
        ComIbmWsSibQueueFactory sqf = makeQueue();
        if (findQueueWithinRuntime(messagingRuntime, sqf)) {
            throw new SculptorException("Queue is already present");
        }
        addQueueIntoRuntime(messagingRuntime, sqf);
    }
}
