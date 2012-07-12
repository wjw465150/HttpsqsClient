package org.httpsqs.client;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class HttpsqsClient {
  private String server; //服务器IP地址
  private int port; //服务器端口号
  private String charset; //HTTP请求字符集
  private int connectTimeout = 0; //连接超时
  private int readTimeout = 0; //读超时

  public static final String HTTPSQS_ERROR_PREFIX = "HTTPSQS_ERROR"; //Sqs4J其他错误前缀

  /**
   * 建立HTTP Sqs Client
   * 
   * @param server
   *          服务器IP地址
   * @param port
   *          服务器端口号
   * @param charset
   *          HTTP请求字符集
   * @param connectTimeout
   *          连接超时(毫秒)
   * @param readTimeout
   *          读超时(毫秒)
   */
  public HttpsqsClient(String server, int port, String charset, int connectTimeout, int readTimeout) {
    this.server = server;
    this.port = port;
    this.charset = charset;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
  }

  /**
   * 处理HTTP的GET请求,如果不需要BASIC验证,把user以及pass设置为null值
   * 
   * @param urlstr
   *          请求的URL
   * @param user
   *          用户名
   * @param pass
   *          口令
   * @return 服务器的返回信息
   */
  private String doGetProcess(String urlstr, String user, String pass) {
    URL url = null;
    try {
      url = new URL(urlstr);
    } catch (MalformedURLException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    }

    BufferedReader reader = null;
    try {
      URLConnection conn = url.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setUseCaches(false);
      conn.setDoOutput(false);
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "text/plain;charset=" + charset);
      if (user != null && pass != null) {
        conn.setRequestProperty("Authorization", "Basic "
            + new String(Base64.encodeBytes((user + ":" + pass).getBytes(charset)))); //需要BASIC验证
      }

      conn.connect();

      reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
      String line;
      StringBuilder result = new StringBuilder();

      int i = 0;
      while ((line = reader.readLine()) != null) {
        i++;
        if (i != 1) {
          result.append("\n");
        }
        result.append(line);
      }
      return result.toString();
    } catch (IOException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * 处理HTTP的GET请求,如果不需要BASIC验证,把user以及pass设置为null值
   * 
   * @param urlstr
   *          请求的URL
   * @param user
   *          用户名
   * @param pass
   *          口令
   * @return 服务器的返回信息
   */
  private SqsMsg doGetProcessEx(String urlstr, String user, String pass) {
    URL url = null;
    try {
      url = new URL(urlstr);
    } catch (MalformedURLException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    }

    BufferedReader reader = null;
    try {
      URLConnection conn = url.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setUseCaches(false);
      conn.setDoOutput(false);
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "text/plain;charset=" + charset);
      if (user != null && pass != null) {
        conn.setRequestProperty("Authorization", "Basic "
            + new String(Base64.encodeBytes((user + ":" + pass).getBytes(charset)))); //需要BASIC验证
      }

      conn.connect();

      reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
      String line;
      StringBuilder result = new StringBuilder();

      int i = 0;
      while ((line = reader.readLine()) != null) {
        i++;
        if (i != 1) {
          result.append("\n");
        }
        result.append(line);
      }

      long getPos = -1;
      try {
        getPos = Long.parseLong(conn.getHeaderField("Pos"));
      } catch (Throwable e) {
        getPos = -1;
      }

      return new SqsMsg(getPos, result.toString());
    } catch (IOException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * 更改指定队列的最大队列数量
   * 
   * @param queue_name
   *          队列名
   * @param num
   *          最大队列数
   * @param user
   *          用户名
   * @param pass
   *          口令
   * @return 成功: 返回"HTTPSQS_MAXQUEUE_OK" <br>
   *         错误: "HTTPSQS_MAXQUEUE_CANCEL"-设置没有成功 <br>
   *         其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String maxqueue(String queue_name, long num, String user, String pass) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=maxqueue&num=" + num;
      result = this.doGetProcess(urlstr, user, pass);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  //synctime

  /**
   * 修改定时刷新内存缓冲区内容到磁盘的间隔时间
   * 
   * @param queue_name
   *          队列名
   * @param num
   *          间隔时间(秒)
   * @param user
   *          用户名
   * @param pass
   *          口令
   * @return 成功: 返回"HTTPSQS_SYNCTIME_OK" <br>
   *         错误: "HTTPSQS_SYNCTIME_CANCEL"-设置没有成功 <br>
   *         其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String synctime(String queue_name, int num, String user, String pass) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=synctime&num=" + num;
      result = this.doGetProcess(urlstr, user, pass);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 手动刷新内存缓冲区内容到磁盘
   * 
   * @param queue_name
   *          队列名
   * @param user
   *          用户名
   * @param pass
   *          口令
   * @return 成功: 返回"HTTPSQS_FLUSH_OK" <br>
   *         其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String flush(String queue_name, String user, String pass) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=flush";
      result = this.doGetProcess(urlstr, user, pass);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 重置指定队列
   * 
   * @param queue_name
   *          队列名
   * @param user
   *          用户名
   * @param pass
   *          口令
   * @return 成功: 返回"HTTPSQS_RESET_OK" <br>
   *         错误: "HTTPSQS_RESET_ERROR"-设置没有成功 <br>
   *         其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String reset(String queue_name, String user, String pass) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=reset";

      result = this.doGetProcess(urlstr, user, pass);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 查看队列状态
   * 
   * @param queue_name
   *          队列名
   * @return 成功: 返回队列信息 <br>
   *         错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String status(String queue_name) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=status";

      result = this.doGetProcess(urlstr, null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 已JSO格式,查看队列状态
   * 
   * @param queue_name
   *          队列名
   * @return 成功:
   *         {"name":"队列名","maxqueue":最大队列数,"putpos":队列写入点值,"putlap":队列写入点值圈数
   *         ,"getpos":队列获取点值,"getlap":队列获取点值圈数,"unread":未读消息数} <br>
   *         错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String statusJson(String queue_name) {
    String result = null;
    try {
      String urlstr = "http://" + this.server + ":" + this.port + "/?name=" + URLEncoder.encode(queue_name, charset)
          + "&opt=status_json";

      result = this.doGetProcess(urlstr, null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 查看指定队列位置点的内容
   * 
   * @param queue_name
   *          队列名
   * @param pos
   *          位置
   * @param auth
   *          Sqs4j的get,put,view的验证密码,当不需要验证时,设置为null
   * @return 成功: 返回指定位置的队列内容,错误返回已"HTTPSQS_ERROR"开头的字符串 <br>
   *         错误: "HTTPSQS_ERROR_NOFOUND"-指定的消息不存在 <br>
   *         验证错误: "HTTPSQS_AUTH_FAILED" <br>
   *         其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String view(String queue_name, long pos, String auth) {
    String result = null;
    try {
      StringBuilder urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?charset=" + this.charset
          + "&name=" + URLEncoder.encode(queue_name, charset) + "&opt=view&pos=" + pos);
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      result = this.doGetProcess(urlstr.toString(), null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 出队列
   * 
   * @param queue_name
   *          队列名
   * @param auth
   *          Sqs4j的get,put,view的验证密码,当不需要验证时,设置为null
   * @return 成功: 出队列的消息内容 <br>
   *         错误: "HTTPSQS_GET_END"-队列为空 <br>
   *         验证错误: "HTTPSQS_AUTH_FAILED" <br>
   *         其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String get(String queue_name, String auth) {
    String result = null;
    try {
      StringBuilder urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?charset=" + this.charset
          + "&name=" + URLEncoder.encode(queue_name, charset) + "&opt=get");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      result = this.doGetProcess(urlstr.toString(), null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 出队列
   * 
   * @param queue_name
   *          队列名
   * @param auth
   *          Sqs4j的get,put,view的验证密码,当不需要验证时,设置为null
   * @return SqsMsg 成功: SqsMsg.pos=当前队列的读取位置点, SqsMsg.msg=出队列的消息内容 <br>
   *         错误: SqsMsg.pos=-1; SqsMsg.msg="HTTPSQS_GET_END"-队列为空 <br>
   *         验证错误: SqsMsg.pos=-1; SqsMsg.msg="HTTPSQS_AUTH_FAILED" <br>
   *         其他错误: SqsMsg.pos=-1; SqsMsg.msg=返回已"HTTPSQS_ERROR"开头的字符串
   */
  public SqsMsg getEx(String queue_name, String auth) {
    SqsMsg result = null;
    try {
      StringBuilder urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?charset=" + this.charset
          + "&name=" + URLEncoder.encode(queue_name, charset) + "&opt=get");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      result = this.doGetProcessEx(urlstr.toString(), null, null);
      return result;
    } catch (UnsupportedEncodingException ex) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage());
    }
  }

  /**
   * 入队列
   * 
   * @param queue_name
   *          队列名
   * @param data
   *          入队列的消息内容
   * @param auth
   *          Sqs4j的get,put,view的验证密码,当不需要验证时,设置为null
   * @return 成功: 返回字符串"HTTPSQS_PUT_OK" <br>
   *         错误: "HTTPSQS_PUT_ERROR"-入队列错误; "HTTPSQS_PUT_END"-队列已满 <br>
   *         验证错误: "HTTPSQS_AUTH_FAILED" <br>
   *         其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String put(String queue_name, String data, String auth) {
    StringBuilder urlstr;
    URL url;
    try {
      urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?name="
          + URLEncoder.encode(queue_name, charset) + "&opt=put");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      url = new URL(urlstr.toString());
    } catch (UnsupportedEncodingException ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    } catch (MalformedURLException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    }
    URLConnection conn;

    OutputStreamWriter writer = null;
    try {
      conn = url.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "text/plain;charset=" + charset);

      //conn.setRequestProperty("Authorization","Basic "+ new String(Base64.encodeBytes((user+":"+pass).getBytes(charset))));  //需要BASIC验证的可以加上

      conn.connect();

      writer = new OutputStreamWriter(conn.getOutputStream(), charset);
      writer.write(URLEncoder.encode(data, charset));
      writer.flush();
    } catch (IOException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ex) {
        }
      }
    }

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
      return reader.readLine();
    } catch (IOException e) {
      return HTTPSQS_ERROR_PREFIX + ":" + e.getMessage();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }

  /**
   * 入队列
   * 
   * @param queue_name
   *          队列名
   * @param data
   *          入队列的消息内容
   * @param auth
   *          Sqs4j的get,put,view的验证密码,当不需要验证时,设置为null
   * @return SqsMsg 成功: SqsMsg.pos=当前队列的插入位置点, SqsMsg.msg=返回字符串"HTTPSQS_PUT_OK" <br>
   *         错误: SqsMsg.pos=-1; SqsMsg.msg="HTTPSQS_PUT_ERROR"-入队列错误; SqsMsg.msg="HTTPSQS_PUT_END"-队列已满 <br>
   *         验证错误: SqsMsg.pos=-1; SqsMsg.msg="HTTPSQS_AUTH_FAILED" <br>
   *         其他错误: SqsMsg.pos=-1; SqsMsg.msg=返回已"HTTPSQS_ERROR"开头的字符串
   */
  public SqsMsg putEx(String queue_name, String data, String auth) {
    StringBuilder urlstr;
    URL url;
    try {
      urlstr = new StringBuilder("http://" + this.server + ":" + this.port + "/?name="
          + URLEncoder.encode(queue_name, charset) + "&opt=put");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, charset));
      }

      url = new URL(urlstr.toString());
    } catch (UnsupportedEncodingException ex) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage());
    } catch (MalformedURLException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    }
    URLConnection conn;

    OutputStreamWriter writer = null;
    try {
      conn = url.openConnection();
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);
      conn.setUseCaches(false);
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestProperty("Content-Type", "text/plain;charset=" + charset);

      //conn.setRequestProperty("Authorization","Basic "+ new String(Base64.encodeBytes((user+":"+pass).getBytes(charset))));  //需要BASIC验证的可以加上

      conn.connect();

      writer = new OutputStreamWriter(conn.getOutputStream(), charset);
      writer.write(URLEncoder.encode(data, charset));
      writer.flush();
    } catch (IOException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException ex) {
        }
      }
    }

    long putPos = -1;
    try {
      putPos = Long.parseLong(conn.getHeaderField("Pos"));
    } catch (Throwable e) {
      putPos = -1;
    }

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
      return new SqsMsg(putPos, reader.readLine());
    } catch (IOException e) {
      return new SqsMsg(-1, HTTPSQS_ERROR_PREFIX + ":" + e.getMessage());
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
        }
      }
    }
  }
}