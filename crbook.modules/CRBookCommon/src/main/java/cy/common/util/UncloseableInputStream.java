package cy.common.util;

import java.io.IOException;
import java.io.InputStream;

public class UncloseableInputStream extends InputStream {
  private final InputStream input;

  public UncloseableInputStream(InputStream input) {
    this.input = input;
  }

  @Override
  public void close() throws IOException {} // do not close the wrapped stream

  @Override
  public int read() throws IOException {
    return input.read();
  }

  // delegate all other InputStream methods as with read above
}