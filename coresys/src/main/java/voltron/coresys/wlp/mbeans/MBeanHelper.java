package package voltron.coresys.wlp.mbeans;

import java.text.MessageFormat;
import java.util.Map;
import javax.management.remote.JMXConnector;

class MBeanHelper {

    protected static final String CONNECTOR_TYPE_REST = "rest";
    protected static final String CONNECTOR_TEMPLATE_URL = "service:jmx:{0}://{1}:{2}/IBMJMXConnectorREST";
    private static final int CRED_ELEMENT_NUMBER = 2;

    public static String shapeServiceURL(final String hostName, final String port) {
        return MessageFormat.format(CONNECTOR_TEMPLATE_URL, new Object[]{CONNECTOR_TYPE_REST, hostName, port});
    }

    public static void setupCredentials(Map<String, Object> environment) throws Exception {
        String credentialsProperty = System.getProperty("jmx.remote.credentials");

        if (credentialsProperty != null) {
            String[] credentials = credentialsProperty.split(",");
            if (credentials.length == CRED_ELEMENT_NUMBER) {
                environment.put(JMXConnector.CREDENTIALS, credentials);
            } else {
                throw new Exception("Invalid jmx.remote.credentials format. Expected 'username,password'.");
            }
        } else {
            throw new Exception("jmx.remote.credentials property not set.");
        }
    }
}
