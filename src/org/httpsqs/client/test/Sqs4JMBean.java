package org.httpsqs.client.test;

public interface Sqs4JMBean {
  String version();

  boolean flush();

  String status(String httpsqs_input_name);

  String queueNames();
}