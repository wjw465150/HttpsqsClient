package org.httpsqs.client;

public class SqsMsg {
  public final long pos;
  public final String msg;

  public SqsMsg(final long pos, final String msg) {
    this.pos = pos;
    this.msg = msg;
  }

  @Override
  public String toString() {
    return "SqsMsg [pos=" + pos + ", msg=" + msg + "]";
  }

}
