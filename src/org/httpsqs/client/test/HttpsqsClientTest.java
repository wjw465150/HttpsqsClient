package org.httpsqs.client.test;

import junit.framework.TestCase;
import org.httpsqs.client.HttpsqsClient;

/**
 * 测试HttpSqsClient
 */
public class HttpsqsClientTest extends TestCase {
  String queue_name = "test_queue";
  HttpsqsClient instance = new HttpsqsClient("127.0.0.1", 1218, "GBK", 60 * 1000, 60 * 1000);
  //HttpsqsClient instance = new HttpsqsClient("192.168.0.80", 1218, "GBK", 60 * 1000, 60 * 1000);
  String user = "admin";
  String pass = "123456";

  public HttpsqsClientTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }


  public void testBenchMarkPut() {
    long currentTimeMillis = System.currentTimeMillis();
    System.out.println(currentTimeMillis);
    String data = "test(测试)Httpsqs:";
    for (int i = 1; i <= 10000; i++) {
      instance.put(queue_name, data + i, null);
      //System.out.println(".");
    }
    System.out.println("testBenchMarkPut(),10000次用时" + (System.currentTimeMillis() - currentTimeMillis) / 1000.00 + "秒");
  }

  /**
   * Test of put method, of class HttpsqsClient.
   */
  public void testPut() {
    System.out.println("put");
    String data = "test(测试)Httpsqs";
    String expResult = "HTTPSQS_PUT_OK";
    String result = instance.put(queue_name, data, null);
    System.out.println(result);
    assertTrue(expResult.equals(result));
  }

  /**
   * Test of maxqueue method, of class HttpsqsClient.
   */
  public void testMaxqueue() {
    System.out.println("maxqueue");
    long num = 1000000;
    String expResult = "HTTPSQS_MAXQUEUE_OK";
    String result = instance.maxqueue(queue_name, num, user , pass);
    System.out.println(result);
    assertTrue(expResult.equals(result));
  }

  /**
   * Test of synctime method, of class HttpsqsClient.
   */
  public void testSynctime() {
    System.out.println("synctime");
    int num = 5;
    String expResult = "HTTPSQS_SYNCTIME_OK";
    String result = instance.synctime(queue_name, num, user ,pass);
    System.out.println(result);
    assertTrue(expResult.equals(result));
  }

  /**
   * Test of flush method, of class HttpsqsClient.
   */
  public void testFlush() {
    System.out.println("flush");
    String expResult = "HTTPSQS_FLUSH_OK";
    String result = instance.flush(queue_name, user ,pass);
    System.out.println(result);
    assertTrue(expResult.equals(result));
  }

  /**
   * Test of reset method, of class HttpsqsClient.
   */
  public void testReset() {
    System.out.println("reset");
    String expResult = "HTTPSQS_RESET_OK";
    String result = instance.reset(queue_name,user ,pass);
    System.out.println(result);
    assertTrue(expResult.equals(result));
  }

  /**
   * Test of view method, of class HttpsqsClient.
   */
  public void testView() {
    System.out.println("view");
    long pos = -1;
    String expResult = "HTTPSQS_ERROR_NOFOUND";
    String result = instance.view(queue_name, pos, null);
    System.out.println(result);
    assertTrue(!expResult.equals(result));
  }

  /**
   * Test of status method, of class HttpsqsClient.
   */
  public void testStatus() {
    System.out.println("status");
    String expResult = "HTTPSQS_ERROR";
    String result = instance.status(queue_name);
    System.out.println(result);
    assertTrue(!expResult.equals(result));
  }

  /**
   * Test of statusJson method, of class HttpsqsClient.
   */
  public void testStatusJson() {
    System.out.println("statusJson");
    String expResult = "HTTPSQS_ERROR";
    String result = instance.statusJson(queue_name);
    System.out.println(result);
    assertTrue(!expResult.equals(result));
  }

  public void testBenchMarkGet() {
    long currentTimeMillis = System.currentTimeMillis();
    System.out.println(currentTimeMillis);
    for (int i = 1; i <= 10000; i++) {
      instance.get(queue_name,null);
      //System.out.println(".");
    }
    System.out.println("testBenchMarkPut(),10000次用时" + (System.currentTimeMillis() - currentTimeMillis) / 1000.00 + "秒");
  }

  /**
   * Test of get method, of class HttpsqsClient.
   */
  public void testGet() {
    System.out.println("get");
    String expResult = "HTTPSQS_GET_END";
    String result = instance.get(queue_name,null);
    System.out.println(result);
    assertTrue(!expResult.equals(result) && !result.startsWith(HttpsqsClient.HTTPSQS_ERROR_PREFIX));
  }

}
