package org.httpsqs.client.ex;

import org.httpsqs.client.Base64;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class HttpsqsClientEx {
  private String _server;  //服务器IP地址
  private int _port;  //服务器端口号
  private String _charset;  //HTTP请求字符集
  private int _connectTimeout = 0;  //连接超时
  private int _readTimeout = 0;  //读超时

  private HttpClient _client = null;

  public static final String HTTPSQS_ERROR_PREFIX = "HTTPSQS_ERROR";

  /**
   * 建立HTTP Sqs Client
   *
   * @param server         服务器IP地址
   * @param port           服务器端口号
   * @param charset        HTTP请求字符集
   * @param connectTimeout 连接超时
   * @param readTimeout    读超时
   */
  public HttpsqsClientEx(String server, int port, String charset, int connectTimeout, int readTimeout) {
    this._server = server;
    this._port = port;
    this._charset = charset;
    this._connectTimeout = connectTimeout;
    this._readTimeout = readTimeout;
    _client = new HttpClient();
  }

  public void open() {
    try {
      _client.openConnection();
    } catch (IOException e) {
      //
    }
  }

  public void close() {
    _client.closeConnection();
  }

  /**
   * 更改指定队列的最大队列数量
   *
   * @param queue_name 队列名
   * @param num        最大队列数
   * @param user       用户名
   * @param pass       口令
   * @return 成功: 返回"HTTPSQS_MAXQUEUE_OK"
   *         <br>错误: "HTTPSQS_MAXQUEUE_CANCEL"-设置没有成功
   *         <br>其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String maxqueue(String queue_name, long num, String user, String pass) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=maxqueue&num=" + num;
      String result = _client.sendRequest(urlstr, null, user, pass);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }
//synctime

  /**
   * 修改定时刷新内存缓冲区内容到磁盘的间隔时间
   *
   * @param queue_name 队列名
   * @param num        间隔时间(秒)
   * @param user       用户名
   * @param pass       口令
   * @return 成功: 返回"HTTPSQS_SYNCTIME_OK"
   *         <br>错误: "HTTPSQS_SYNCTIME_CANCEL"-设置没有成功
   *         <br>其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String synctime(String queue_name, int num, String user, String pass) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=synctime&num=" + num;
      String result = _client.sendRequest(urlstr, null, user, pass);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 手动刷新内存缓冲区内容到磁盘
   *
   * @param queue_name 队列名
   * @param user       用户名
   * @param pass       口令
   * @return 成功: 返回"HTTPSQS_FLUSH_OK"
   *         <br>其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String flush(String queue_name, String user, String pass) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=flush";
      String result = _client.sendRequest(urlstr, null, user, pass);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 重置指定队列
   *
   * @param queue_name 队列名
   * @param user       用户名
   * @param pass       口令
   * @return 成功: 返回"HTTPSQS_RESET_OK"
   *         <br>错误: "HTTPSQS_RESET_ERROR"-设置没有成功
   *         <br>其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String reset(String queue_name, String user, String pass) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=reset";
      String result = _client.sendRequest(urlstr, null, user, pass);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 查看队列状态
   *
   * @param queue_name 队列名
   * @return 成功: 返回队列信息
   *         <br>错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String status(String queue_name) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=status";
      String result = _client.sendRequest(urlstr, null, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 已JSO格式,查看队列状态
   *
   * @param queue_name 队列名
   * @return 成功: {"name":"队列名","maxqueue":最大队列数,"putpos":队列写入点值,"putlap":队列写入点值圈数,"getpos":队列获取点值,"getlap":队列获取点值圈数,"unread":未读消息数}
   *         <br>错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String statusJson(String queue_name) {
    try {
      String urlstr = "/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=status_json";
      String result = _client.sendRequest(urlstr, null, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 查看指定队列位置点的内容
   *
   * @param queue_name 队列名
   * @param pos        位置
   * @param auth       Sqs4j的get,put,view的验证密码,当不需要验证时,设置为null
   * @return 成功: 返回指定位置的队列内容,错误返回已"HTTPSQS_ERROR"开头的字符串
   *         <br>错误: "HTTPSQS_ERROR_NOFOUND"-指定的消息不存在
   *         <br>验证错误: "HTTPSQS_AUTH_FAILED"
   *         <br>其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String view(String queue_name, String pos, String auth) {
    try {
      StringBuilder urlstr = new StringBuilder("/?charset=" + this._charset + "&name=" + URLEncoder.encode(queue_name, _charset) + "&opt=view&pos=" + pos);
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, _charset));
      }

      String result = _client.sendRequest(urlstr.toString(), null, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 出队列
   *
   * @param queue_name 队列名
   * @param auth       Sqs4j的get,put,view的验证密码,当不需要验证时,设置为null
   * @return 成功: 出队列的消息内容
   *         <br>错误: "HTTPSQS_GET_END"-队列为空
   *         <br>验证错误: "HTTPSQS_AUTH_FAILED"
   *         <br>其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String get(String queue_name, String auth) {
    try {
      StringBuilder urlstr = new StringBuilder("/?charset=" + this._charset + "&name=" + URLEncoder.encode(queue_name, _charset) + "&opt=get");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, _charset));
      }

      String result = _client.sendRequest(urlstr.toString(), null, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * 入队列
   *
   * @param queue_name 队列名
   * @param data       入队列的消息内容
   * @param auth       Sqs4j的get,put,view的验证密码,当不需要验证时,设置为null
   * @return 成功: 返回字符串"HTTPSQS_PUT_OK"
   *         <br>错误: "HTTPSQS_PUT_ERROR"-入队列错误; "HTTPSQS_PUT_END"-队列已满
   *         <br>验证错误: "HTTPSQS_AUTH_FAILED"
   *         <br>其他错误: 返回已"HTTPSQS_ERROR"开头的字符串
   */
  public String put(String queue_name, String data, String auth) {
    try {
      StringBuilder urlstr = new StringBuilder("/?name=" + URLEncoder.encode(queue_name, _charset) + "&opt=put");
      if (auth != null) {
        urlstr.append("&auth=" + URLEncoder.encode(auth, _charset));
      }

      String result = _client.sendRequest(urlstr.toString(), data, null, null);
      return result;
    } catch (Throwable ex) {
      return HTTPSQS_ERROR_PREFIX + ":" + ex.getMessage();
    }
  }

  /**
   * A replacement for java.net.URLConnection.
   */
  private class HttpClient {
    private String _hostAndPort;

    private Socket _socket = null;
    private BufferedWriter _writer;
    private InputStream _input;
    private byte[] _readBbuffer;
    private boolean _keepalive;


    public HttpClient() {
      _hostAndPort = _port == 80 ? _server : _server + ":" + _port;
    }

    protected void openConnection() throws IOException {
      if (_input == null) {
        InetSocketAddress socketAddress = new InetSocketAddress(_server, _port);
        _socket = new Socket();
        try {
          _socket.setSoTimeout(_readTimeout);
          _socket.setReuseAddress(true);
          _socket.setTcpNoDelay(true);
          _socket.setSoLinger(true, 0);
        } catch (Throwable thex) {
          //
        }

        _socket.connect(socketAddress, _connectTimeout);
        _writer = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream(), _charset));
        _input = _socket.getInputStream();
      }
    }

    protected void closeConnection() {
      if (_input != null) {
        try {
          _input.close();
        } catch (IOException ex1) {
        }
        _input = null;
      }

      if (_writer != null) {
        try {
          _writer.close();
        } catch (IOException ex) {
        }
        _writer = null;
      }

      if (_socket != null) {
        try {
          _socket.close();
        } catch (Exception ignore) {
        }
        _socket = null;
      }
    }

    /**
     * 从HTTP Header里找到字符集编码,没有发现返回null
     *
     * @param contentType
     * @return
     */
    private String getCharsetFromContentType(String contentType) {
      if (contentType == null) {
        return null;
      }
      int start = contentType.indexOf("charset=");
      if (start < 0) {
        return null;
      }
      String encoding = contentType.substring(start + 8);
      int end = encoding.indexOf(';');
      if (end >= 0) {
        encoding = encoding.substring(0, end);
      }
      encoding = encoding.trim();
      if ((encoding.length() > 2) && (encoding.startsWith("\"")) && (encoding.endsWith("\""))) {
        encoding = encoding.substring(1, encoding.length() - 1);
      }
      return (encoding.trim());
    }

    private static final String CR_NL = "\r\n";

    /**
     * 发送HTTP请求并读取服务器返回的数据,如果是GET请求就把request设置为null值,如果不需要BASIC验证,把user以及pass设置为null值
     *
     * @param pathAndQuery
     * @param request
     * @param user         用户名
     * @param pass         口令
     * @return 服务器的返回信息
     */
    public String sendRequest(String pathAndQuery, String request, String user, String pass) throws IOException {
      this.openConnection();
      try {
        if (request != null) {
          _writer.write("POST " + pathAndQuery + " HTTP/1.1" + CR_NL);
        } else {
          _writer.write("GET " + pathAndQuery + " HTTP/1.1" + CR_NL);
        }

        _writer.write("User-Agent: " + "wstone" + CR_NL);
        _writer.write("Host: " + _hostAndPort + CR_NL);

        _writer.write("Connection: Keep-Alive" + CR_NL);
        _writer.write("Content-Type: text/plan;charset=" + _charset + CR_NL);
        if (user != null && pass != null) {
          _writer.write("Authorization: Basic " + new String(Base64.encodeBytes((user + ":" + pass).getBytes(_charset))) + CR_NL);  //需要BASIC验证
        }

        if (request != null) {
          _writer.write("Content-Length: " + request.getBytes(_charset).length);  //加上2是为了加上多余的回车换行
        }

        _writer.write(CR_NL + CR_NL);
        if (request != null) {
          _writer.write(request);
        }
        _writer.flush();

        // start reading  server response headers
        String line = readLine(_input, _charset);
        int contentLength = -1;
        StringTokenizer tokens = new StringTokenizer(line);
        String httpversion = tokens.nextToken();
        String statusCode = tokens.nextToken();
        String statusMsg = tokens.nextToken("\n\r");
        _keepalive = "HTTP/1.1".equals(httpversion);
        if (!"200".equals(statusCode)) {
          throw new IOException("Unexpected Response from Server: " + statusMsg);
        }

        do {
          line = readLine(_input, _charset);
          if (line != null) {
            line = line.toLowerCase();
            if (line.startsWith("content-length:")) {
              contentLength = Integer.parseInt(line.substring(15).trim());
            }
            if (line.startsWith("connection:")) {
              _keepalive = line.indexOf("keep-alive") > -1;
            }
            if (line.startsWith("content-type")) {
              _charset = getCharsetFromContentType(line);
            }
          }
        } while (line != null && !line.equals(""));

        InputStream bodyInputStream;
        if (contentLength > 0) {
          bodyInputStream = new ContentLengthInputStream(_input, contentLength);
        } else {
          bodyInputStream = new ChunkedInputStream(_input);
        }

        BufferedReader bodyReader = new BufferedReader(new InputStreamReader(bodyInputStream, _charset));
        StringBuilder result = new StringBuilder();
        int i = 0;
        while ((line = bodyReader.readLine()) != null) {
          i++;
          if (i != 1) {
            result.append("\n");
          }
          result.append(line);
        }

        if (!_keepalive) {
          this.closeConnection();
        }

        return result.toString();
      } catch (IOException ioex) {
        this.closeConnection();
        throw ioex;
      }
    }

    /**
     * @throws Throwable
     */
    protected void finalize() throws Throwable {
      closeConnection();
    }

    private static final int defaultByteBufferSize = 8192;

    private String readLine(InputStream input, String charset) throws IOException {
      if (_readBbuffer == null) {
        _readBbuffer = new byte[defaultByteBufferSize];
      }
      int next;
      int count = 0;
      while (true) {
        next = input.read();
        if (next < 0 || next == '\n') {
          break;
        }
        if (next != '\r') {
          _readBbuffer[count++] = (byte) next;
        }
        if (count >= _readBbuffer.length) {
          throw new IOException("HTTP Line too long, more than:" + _readBbuffer.length);
        }
      }
      return new String(_readBbuffer, 0, count, charset);
    }

  } //<-end HttpClient


}
