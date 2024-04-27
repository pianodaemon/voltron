package voltron.coresys;

import com.immortalcrab.voltron.portage.ServerType;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerConfLoader {

    private static final String CTX_PATH = "com.immortalcrab.voltron.portage";

    private final JAXBElement<ServerType> server;

    public ServerConfLoader(String filePath) throws VoltronException {
        this(unmarshalling(filePath));
    }

    public ServerConfLoader(File shippingXML) throws VoltronException {
        this(unmarshalling(shippingXML));
    }

    private static <T> JAXBElement<T> unmarshalling(File shippingXML) throws VoltronException {
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(CTX_PATH).createUnmarshaller();
            return (JAXBElement<T>) unmarshaller.unmarshal(shippingXML);
        } catch (JAXBException ex) {
            throw new VoltronException(ex);
        }
    }

    private static <T> JAXBElement<T> unmarshalling(String filePath) throws VoltronException {
        return (JAXBElement<T>) unmarshalling(new File(filePath));
    }
}
