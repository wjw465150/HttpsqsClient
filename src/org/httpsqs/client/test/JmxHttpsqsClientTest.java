package org.httpsqs.client.test;

import java.util.HashMap;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import junit.framework.TestCase;

public class JmxHttpsqsClientTest extends TestCase {
	String queue_name = "SALT_QUEUE";
	String psqHost = "192.168.1.22";
	int jmxPort = 1219;
	String adminUser = "admin";
	String adminPass = "admin123456";
	String sqsJmxName = "org.sqs4j:type=Sqs4J";

	JMXConnector jmxc;
	Sqs4JMBean proxy;

	public JMXConnector createJMXConnector(String host, int port, String user, String pass) throws Exception {
		Map<String, Object> environment = new HashMap<String, Object>();
		String[] credentials = new String[] { user, pass };
		environment.put("jmx.remote.credentials", credentials);
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url, environment);

		return jmxc;
	}

	public Sqs4JMBean getAppMXBean(JMXConnector jmxc) throws Exception {
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

		ObjectName name = new ObjectName(sqsJmxName);
		return JMX.newMXBeanProxy(mbsc, name, Sqs4JMBean.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		jmxc = createJMXConnector(psqHost, jmxPort, adminUser, adminPass);
		proxy = getAppMXBean(jmxc);
	}

	@Override
	protected void tearDown() throws Exception {
		jmxc.close();

		super.tearDown();
	}

	public void testQueueNames() {
		String ss = proxy.queueNames();
		System.out.println(ss);
	}

	public void testStatus() {
		String ss = proxy.status(queue_name);
		System.out.println(ss);
	}
}
