package voltron.coresys.wlp.mbeans;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXConnector;
import voltron.coresys.RestClientException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LibertyConnREST {

    private static final int CRED_ELEMENT_NUMBER = 2;
    private static final String CONNECTOR_TYPE_REST = "rest";
    private static final String CONNECTOR_TEMPLATE_URL = "service:jmx:{0}://{1}:{2}/IBMJMXConnectorREST";

    private final String mHostName;
    private final Integer mPort;
    private final HashMap<String, Object> environment;

    public LibertyConnREST(String hostName, Integer port) throws RestClientException {
        this(hostName, port, new HashMap<>());
        setupEnvForLiberty();
    }

    private static String shapeServiceURL(final String hostName, final String port) {
        return MessageFormat.format(CONNECTOR_TEMPLATE_URL, new Object[]{CONNECTOR_TYPE_REST, hostName, port});
    }

    private void setupCredentials() throws Exception {
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

    private void setupEnvForLiberty() throws RestClientException {
        environment.put("com.ibm.ws.jmx.connector.client.disableURLHostnameVerification", Boolean.TRUE);
        environment.put("jmx.remote.protocol.provider.pkgs", "com.ibm.ws.jmx.connector.client");
        try {
            setupCredentials();
        } catch (Exception ex) {
            throw new RestClientException(ex);
        }
    }

    public Map<String, String> inquiryAllApplicationStatus() throws RestClientException {
        try {
            return StatusApplicationHelper.inquiryAll(() -> shapeServiceURL(mHostName, mPort.toString()), environment);
        } catch (Exception ex) {
            throw new RestClientException(ex);
        }
    }
}
