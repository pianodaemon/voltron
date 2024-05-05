package voltron.coresys.wlp.mbeans;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

final class StatusApplicationHelper extends MBeanHelper {

    private static final String APPLICATION_BEAN_NAME = "WebSphere:service=com.ibm.websphere.application.ApplicationMBean";
    private static final String APPLICATION_BEAN_PREFIX = "{0},name={1}";

    public static Map<String, String> inquiry(Supplier<String> urlSupplier, Map<String, ?> environment) throws Exception {

        JMXServiceURL url = new JMXServiceURL(urlSupplier.get());
        try (JMXConnector jmxc = JMXConnectorFactory.connect(url, environment)) {
            MBeanServerConnection mbsConnection = jmxc.getMBeanServerConnection();

            Set<ObjectName> mbeans = mbsConnection.queryNames(new ObjectName(APPLICATION_BEAN_NAME + ",*"), null);

            List<String> applicationNames = mbeans.stream()
                    .map(mbean -> mbean.getKeyProperty("name"))
                    .collect(Collectors.toList());

            return applicationNames.stream()
                    .collect(Collectors.toMap(
                            appName -> appName,
                            appName -> inquiryStatus(mbsConnection, appName)));
        }

    }

    private static String inquiryStatus(MBeanServerConnection mbsConnection, final String appName) {

        final String objName = MessageFormat.format(
                APPLICATION_BEAN_PREFIX,
                new Object[]{APPLICATION_BEAN_NAME, appName});

        try {
            ObjectName applicationMBean = new ObjectName(objName);
            return (String) mbsConnection.getAttribute(applicationMBean, "State");
        } catch (IOException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | MalformedObjectNameException | ReflectionException ex) {
            return "Error retrieving status: " + ex.getMessage();
        }
    }

    private StatusApplicationHelper() {
    }
}
