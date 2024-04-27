package voltron.coresys;

import com.immortalcrab.voltron.portage.ComIbmWsAppManagement;
import com.immortalcrab.voltron.portage.ObjectFactory;
import com.immortalcrab.voltron.portage.ServerType;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class XmlFormater implements OutputFormater<ByteArrayOutputStream> {

    private static final String XML_TAILORED_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String CTX_PATH = "com.immortalcrab.voltron.portage";

    private final @NonNull
    String description;

    private final boolean autoExpand;

    private final @NonNull
    Map<String, Object> kvArgs;

    @Override
    public ByteArrayOutputStream render() throws VoltronException {
        ObjectFactory factory = new ObjectFactory();
        ServerType st = factory.createServerType();
        st.setDescription(description);
        ComIbmWsAppManagement applicationManager = factory.createComIbmWsAppManagement();
        applicationManager.setAutoExpand(Boolean.toString(autoExpand));
        st.getIncludeOrVariableOrWebApplication().add(applicationManager);
        JAXBElement<ServerType> server = factory.createServer(st);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshalling(server, os);
        return os;
    }

    @Override
    public void saveOnStorage(String filePath, ByteArrayOutputStream pData) throws VoltronException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(filePath))) {
            out.write(pData.toString());
        } catch (IOException ex) {
            throw new VoltronException(ex);
        }
    }

    private static void marshalling(JAXBElement<ServerType> server, ByteArrayOutputStream baos) throws VoltronException {
        Marshaller marshaller;
        try {
            JAXBContext context = JAXBContext.newInstance(CTX_PATH);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            baos.write(XML_TAILORED_HEADER.getBytes());
            marshaller.marshal(server, baos);
        } catch (IOException | JAXBException ex) {
            throw new VoltronException(ex);
        }
    }

    protected static class LegoAssembler {

        public static Map<String, Object> obtainMapFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {
            return LegoAssembler.obtainObjFromKey(m, k);
        }

        public static <T> T obtainObjFromKey(Map<String, Object> m, final String k) throws NoSuchElementException {
            return (T) Optional.ofNullable(m.get(k)).orElseThrow();
        }
    }
}
