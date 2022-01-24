/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.utils;

public class ByteBuf {

  private final byte[] bytes;
  private int size = 0;

  public ByteBuf(int length) {
    bytes = new byte[length];
  }

  public void putByte(byte b) {
    bytes[size] = b;
    size += 1;
  }

  public void putUInt32LE(long value) {
    putByte((byte) (value & 0xFF));
    putByte((byte) ((value >> 8) & 0xFF));
    putByte((byte) ((value >> 16) & 0xFF));
    putByte((byte) ((value >> 24) & 0xFF));
  }

  public byte[] getBytes() {
    return this.bytes;
  }
}
