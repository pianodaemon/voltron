package voltron.coresys.tampering;

import com.immortalcrab.voltron.portage.ServerType;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.Getter;
import voltron.coresys.VoltronException;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class XmlFormater implements OutputFormater<ByteArrayOutputStream> {

    private static final String XML_TAILORED_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String CTX_PATH = "com.immortalcrab.voltron.portage";

    private final JAXBElement<ServerType> server;

    public XmlFormater(String xmlFilePath) throws VoltronException {
        this(unmarshalling(xmlFilePath));
    }

    public abstract JAXBElement<ServerType> tailorHandler() throws VoltronException;

    @Override
    public ByteArrayOutputStream render() throws VoltronException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshalling(tailorHandler(), os);
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
