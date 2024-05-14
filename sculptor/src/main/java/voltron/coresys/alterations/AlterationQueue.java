package voltron.coresys.alterations;

import com.immortalcrab.voltron.portage.ComIbmWsJcaJmsActivationSpecFactory;
import com.immortalcrab.voltron.portage.ComIbmWsMessagingRuntime;
import com.immortalcrab.voltron.portage.ComIbmWsSibQueueFactory;
import com.immortalcrab.voltron.portage.ObjectFactory;
import com.immortalcrab.voltron.portage.ServerType;
import java.util.Optional;
import javax.xml.bind.JAXBElement;
import voltron.coresys.SculptorException;
import voltron.coresys.tampering.XmlFormater;

public class AlterationQueue extends XmlFormater {

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
        ComIbmWsJcaJmsActivationSpecFactory aspec = obtainQueueActivationSpec(st);
        return getServer();
    }

    private ComIbmWsMessagingRuntime obtainRuntime(ServerType st) {
        Optional<ComIbmWsMessagingRuntime> messagingRuntime = Optional.ofNullable(findIncludeOrVariableOrWebApplication(st));
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

    private static <T> T findIncludeOrVariableOrWebApplication(ServerType st) {
        Class type = ((T) new Object()).getClass();
        for (Object element : st.getIncludeOrVariableOrWebApplication()) {
            if (type.getClass() == element.getClass()) {
                return (T) element;
            }
        }
        return null;
    }

    private ComIbmWsJcaJmsActivationSpecFactory obtainQueueActivationSpec(ServerType st) {
        ComIbmWsJcaJmsActivationSpecFactory aspec = new ObjectFactory().createComIbmWsJcaJmsActivationSpecFactory();
        aspec.setId(mName + "_" + "Act_Spec");
        st.getIncludeOrVariableOrWebApplication().add(aspec);
        return aspec;
    }
}
