package com.testsigma.agent.mobile.ios;

import com.dd.plist.*;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Map;

@Log4j2
@Data
public class UsbMuxSocket {
  protected SocketAddress address;
  protected Socket socket;
  protected int tag;
  protected Boolean first = Boolean.TRUE;

  public UsbMuxSocket(int tag) {
    this.tag = tag;
  }

  public static UsbMuxSocket getSocketInstance(int tag) {
    String property = System.getProperty("os.name");
    if (property.startsWith("Window")) {
      return new UsbMuxWindowsSocket(tag);
    } else {
      return new UsbMuxPosixSocket(tag);
    }
  }

  public static NSDictionary getNsDictionary(InputStream input, int size) throws IOException, PropertyListFormatException, ParseException, ParserConfigurationException, SAXException {
    byte[] body = new byte[size];
    input.read(body);
    NSObject parse = PropertyListParser.parse(body);
    return (NSDictionary) parse;
  }

  public static int getSize(InputStream input) throws IOException {
    byte[] header = new byte[16];
    input.read(header);
    ByteBuffer buffer = ByteBuffer.allocate(16);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    buffer.put(header);
    return buffer.getInt(0) - 16;
  }

  public static ResultType retrieveMsgType(NSDictionary dico) {
    NSString messageType = (NSString) dico.get("MessageType");
    return ResultType.valueOf(messageType.getContent());
  }

  public NSDictionary sendRecvPacket(Map<String, Object> payload) throws UsbMuxReplyException, UsbMuxException {
    sendPacket(payload);
    return recvPacket();
  }

  public void sendPacket(Map<String, Object> payload) throws UsbMuxException {
    try {
      this.socket.getOutputStream().write(buildPacket(payloadBytes(payload)).array());
    } catch (Exception e) {
      throw new UsbMuxException(e.getMessage(), e);
    }
  }

  protected byte[] payloadBytes(Map<String, Object> payload) {
    NSDictionary root = new NSDictionary();
    for (Map.Entry<String, Object> entry : payload.entrySet()) {
      root.put(entry.getKey(), entry.getValue());
    }
    root.put("ClientVersionString", "testsigma-usbmux-driver");
    root.put("ProgName", "testsigma-usbmux-driver");
    log.info("Sending payload - " + root.toXMLPropertyList());
    return root.toXMLPropertyList().getBytes(StandardCharsets.UTF_8);
  }

  protected ByteBuffer buildPacket(byte[] bytes) throws UsbMuxException {
    try {
      int headerSize = 16;
      ByteBuffer buffer;
      if (this.first) {
        int len = (headerSize + bytes.length);
        int version = 1;
        int request = 8;
        int tag = this.tag;
        buffer = ByteBuffer.allocate(len);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(0, len);
        buffer.putInt(4, version);
        buffer.putInt(8, request);
        buffer.putInt(12, tag);
      } else {
        headerSize = 4;
        buffer = ByteBuffer.allocate(headerSize + bytes.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(0, bytes.length);
      }
      for (byte aByte : bytes) {
        buffer.put(headerSize++, aByte);
      }
      return buffer;
    } catch (Exception e) {
      throw new UsbMuxException(e.getMessage(), e);
    }
  }

  public NSDictionary recvPacket() throws UsbMuxReplyException, UsbMuxException {
    try {
      int bodyLength;
      if (this.first) {
        byte[] header = new byte[16];
        ByteBuffer buffer = ByteBuffer.allocate(16);
        this.socket.getInputStream().read(header);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(header);
        bodyLength = buffer.getInt(0) - 16;
        this.first = Boolean.FALSE;
      } else {
        byte[] header = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(4);
        this.socket.getInputStream().read(header);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.put(header);
        bodyLength = buffer.getInt(0);
      }
      byte[] body = new byte[bodyLength];
      this.socket.getInputStream().read(body);
      NSObject parse = PropertyListParser.parse(body);
      NSDictionary responseData = (NSDictionary) parse;
      checkResponseData(responseData);
      return (NSDictionary) parse;
    } catch (UsbMuxReplyException e) {
      throw e;
    } catch (Exception e) {
      throw new UsbMuxException(e.getMessage(), e);
    }
  }

  public void checkResponseData(NSDictionary response) throws UsbMuxReplyException {
    if ((response != null) && (response.get("Number") != null) && (Integer.parseInt(response.get("Number").toString()) != 0)) {
      throw new UsbMuxReplyException(Integer.parseInt(response.get("Number").toString()));
    }
  }

  public void close() {
    try {
      this.socket.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public enum ResultType {
    Attached, Detached, Paired, Result, Error
  }

}
