package voltron.makeup;

import voltron.coresys.VoltronException;
import voltron.coresys.XmlFormater;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        Map m = new HashMap<>();
        m.put("xxxx", "ddddd");
        m.put("sdsdsd", "true");

        XmlFormater xmlFormater = new XmlFormater("hola", true, m);
        try {
            xmlFormater.renderFeaturingSave("hola.xml");
        } catch (VoltronException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
