package org.httpsqs.client.ex;

import java.io.IOException;
import java.io.InputStream;

public class ContentLengthInputStream extends InputStream {

  /**
   * The maximum number of bytes that can be read from the stream. Subsequent
   * read operations will return -1.
   */
  private long contentLength;

  /**
   * The current position
   */
  private long pos = 0;

  /**
   * True if the stream is closed.
   */
  private boolean closed = false;

  /**
   * Wrapped input stream that all calls are delegated to.
   */
  private InputStream wrappedStream = null;

  /**
   * Creates a new length limited stream
   *
   * @param in            The stream to wrap
   * @param contentLength The maximum number of bytes that can be read from
   *                      the stream. Subsequent read operations will return -1.
   * @since 3.0
   */
  public ContentLengthInputStream(InputStream in, long contentLength) {
    super();
    this.wrappedStream = in;
    this.contentLength = contentLength;
  }

  /**
   * <p>Reads until the end of the known length of content.</p>
   * <p/>
   * <p>Does not close the underlying socket input, but instead leaves it
   * primed to parse the next response.</p>
   *
   * @throws IOException If an IO problem occurs.
   */
  public void close() throws IOException {
    if (!closed) {
      closed = true;
    }
  }


  /**
   * Read the next byte from the stream
   *
   * @return The next byte or -1 if the end of stream has been reached.
   * @throws IOException If an IO problem occurs
   * @see java.io.InputStream#read()
   */
  public int read() throws IOException {
    if (closed) {
      throw new IOException("Attempted read from closed stream.");
    }

    if (pos >= contentLength) {
      return -1;
    }
    pos++;
    return this.wrappedStream.read();
  }

  /**
   * Does standard {@link InputStream#read(byte[], int, int)} behavior, but
   * also notifies the watcher when the contents have been consumed.
   *
   * @param b   The byte array to fill.
   * @param off Start filling at this position.
   * @param len The number of bytes to attempt to read.
   * @return The number of bytes read, or -1 if the end of content has been
   *         reached.
   * @throws java.io.IOException Should an error occur on the wrapped stream.
   */
  public int read(byte[] b, int off, int len) throws java.io.IOException {
    if (closed) {
      throw new IOException("Attempted read from closed stream.");
    }

    if (pos >= contentLength) {
      return -1;
    }

    if (pos + len > contentLength) {
      len = (int) (contentLength - pos);
    }
    int count = this.wrappedStream.read(b, off, len);
    pos += count;
    return count;
  }


  /**
   * Read more bytes from the stream.
   *
   * @param b The byte array to put the new data in.
   * @return The number of bytes read into the buffer.
   * @throws IOException If an IO problem occurs
   * @see java.io.InputStream#read(byte[])
   */
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * Skips and discards a number of bytes from the input stream.
   *
   * @param n The number of bytes to skip.
   * @return The actual number of bytes skipped. <= 0 if no bytes
   *         are skipped.
   * @throws IOException If an error occurs while skipping bytes.
   * @see InputStream#skip(long)
   */
  public long skip(long n) throws IOException {
    // make sure we don't skip more bytes than are
    // still available
    long length = Math.min(n, contentLength - pos);
    // skip and keep track of the bytes actually skipped
    length = this.wrappedStream.skip(length);
    // only add the skipped bytes to the current position
    // if bytes were actually skipped
    if (length > 0) {
      pos += length;
    }
    return length;
  }

  public int available() throws IOException {
    if (this.closed) {
      return 0;
    }
    int avail = this.wrappedStream.available();
    if (this.pos + avail > this.contentLength) {
      avail = (int) (this.contentLength - this.pos);
    }
    return avail;
  }

}