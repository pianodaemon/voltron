package voltron.coresys.alterations;

import com.immortalcrab.voltron.portage.ComIbmWsAppManagement;
import com.immortalcrab.voltron.portage.ObjectFactory;
import com.immortalcrab.voltron.portage.ServerType;
import javax.xml.bind.JAXBElement;
import voltron.coresys.VoltronException;
import voltron.coresys.tampering.XmlFormater;

public class Alteration00 extends XmlFormater {

    private final String description;
    private final boolean autoExpand;

    public Alteration00(String xmlFilePath, String desc, boolean expand) throws VoltronException {
        super(xmlFilePath);
        description = desc;
        autoExpand = expand;
    }

    @Override
    public JAXBElement<ServerType> tailorHandler() throws VoltronException {
        ServerType st = getServer().getValue();
        st.setDescription(description);
        for (Object element : st.getIncludeOrVariableOrWebApplication()) {
            if (element instanceof ComIbmWsAppManagement) {
                ((ComIbmWsAppManagement)element).setAutoExpand(Boolean.toString(autoExpand));
                break;
            }
        }
        return getServer();
    }
}
