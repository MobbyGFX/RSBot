/*
 * @author - Jeremy Trifilo (Digistr).
*/

package com;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.BufferUnderflowException;

import java.lang.IllegalArgumentException;

public class FileBuilder 
{
	private byte[] buffer;
	private int readerIndex = 0;
	private int writerIndex = 0;

	public void skipBytes(int skipped) {
		writerIndex += skipped;
	}

	public void seek(int index)
	{
		readerIndex = index;
	}

	public int readableBytes() {
		return (buffer.length - readerIndex);
	}

	public int readerIndex() {
		return readerIndex;
	}

	public int writerIndex() {
		return writerIndex;
	}

	public byte[] array() {
		return buffer;
	}

	public FileBuilder(int length) {
		buffer = new byte[length];
	}

	public FileBuilder(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public int writeLength() {
		int size = writerIndex;
		writerIndex = 0;
		writeShort(size);
		return size;	
	}

	public void writeBoolean(boolean b)
	{
		buffer[writerIndex++] = (byte)(b ? 1 : 0);
	}

	public void writeByte(byte data) {
		buffer[writerIndex++] = data;
	}
	
	public void writeShort(int data) {
		buffer[writerIndex++] = (byte)(data >> 8);
		buffer[writerIndex++] = (byte)(data);
	}

	public void writeInt(int data) {
		buffer[writerIndex++] = (byte)(data >> 24);
		buffer[writerIndex++] = (byte)(data >> 16);
		buffer[writerIndex++] = (byte)(data >> 8);
		buffer[writerIndex++] = (byte)(data);
	}

	public void writeLong(long data) {
		buffer[writerIndex++] = (byte)(data >> 56);
		buffer[writerIndex++] = (byte)(data >> 48);
		buffer[writerIndex++] = (byte)(data >> 40);
		buffer[writerIndex++] = (byte)(data >> 32);
		buffer[writerIndex++] = (byte)(data >> 24);
		buffer[writerIndex++] = (byte)(data >> 16);
		buffer[writerIndex++] = (byte)(data >> 8);
		buffer[writerIndex++] = (byte)(data);
	}

	public void writeString(String s) {
		writeString(s.toCharArray());
	}

	public void writeString(char[] data) {
		for (int i = 0; i < data.length; i++)
			buffer[writerIndex++] = (byte)data[i];
		buffer[writerIndex++] = 0;
	}

	public boolean readBoolean()
	{
		return buffer[readerIndex++] == 1;
	}

	public int readByte() {
		return buffer[readerIndex++] & 255;
	}

	public byte readSignedByte() {
		return buffer[readerIndex++];
	}

	public int readShort() {
		return (readByte() << 8) | readByte();
	}

	public int readSignedShort() {
		return (readSignedByte() << 8) | readByte();
	}

	public int readInt() {
		return (readByte() << 24) | (readByte() << 16) | (readByte() << 8) | readByte();
	}

	public int readSignedInt() {
		return (readSignedByte() << 24) | (readSignedByte() << 16) | (readSignedByte() << 8) | readByte();
	}

	public long readLong() {
        	return ((0xffffffffL & (long)readInt()) << 32) + (0xffffffffL & (long)readInt());
	}

	public String readString() {
		StringBuilder sb = new StringBuilder();
		byte b;
		while((b = buffer[readerIndex++]) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	public String readJString() {
		StringBuilder sb = new StringBuilder();
		byte b;
		while((b = buffer[readerIndex++]) != 10) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	public String readRSString() {
		StringBuilder sb = new StringBuilder();
		byte b;
		while((b = buffer[readerIndex++]) != 10 && b != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	public static FileBuilder read(String path, int seek, int length) 
	{
		byte[] bytes = new byte[length];
		try {
			RandomAccessFile file = new RandomAccessFile(path, "r");
			file.seek(seek);
			file.read(bytes, 0, length);
			file.close();
		} catch (IOException io) {
			System.out.println("IO Error - File Doesn't exist: " + path);
		} catch (BufferUnderflowException bufe) {
			System.out.println("Read Out Bounds Exception: { Seek: " + seek + " , Length: " + length + " }");
		} catch (IllegalArgumentException iae) {
			System.out.println("Seek Out Bounds Exception: { Seek: " + seek + " , Length: " + length + " }");
		}
		return new FileBuilder(bytes);
	}

}