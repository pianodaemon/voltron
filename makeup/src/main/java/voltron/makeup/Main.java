package voltron.makeup;

import voltron.coresys.VoltronException;

import voltron.coresys.tampering.XmlFormater;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import voltron.coresys.alterations.Alteration00;

public class Main {

    public static void main(String[] args) throws VoltronException {
        //ServerConfLoader serverConfLoader = new ServerConfLoader("hola.xml");
        Map m = new HashMap<>();
        m.put("xxxx", "ddddd");
        m.put("sdsdsd", "true");
        try {
            XmlFormater xmlFormater;
            xmlFormater = new Alteration00("hola.xml", "hola mundo", false);
            xmlFormater.renderFeaturingSave("hola.xml");
        } catch (VoltronException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
