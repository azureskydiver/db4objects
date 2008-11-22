/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.io;

import com.db4o.*;
import com.db4o.ext.*;

public class BlockAwareIo {

	private static final int COPY_SIZE = 4096;

	private int _blockSize;

	private final StorageFactory.Bin _storage;

	public BlockAwareIo(StorageFactory.Bin storage) {
		_storage = storage;
    }

	/**
	 * converts address and address offset to an absolute address
	 */
	protected final long regularAddress(int blockAddress, int blockAddressOffset) {
		if (0 == _blockSize) {
			throw new IllegalStateException();
		}
		return (long) blockAddress * _blockSize + blockAddressOffset;
	}

	/**
	 * copies a block within a file in block mode
	 */
	public void blockCopy(int oldAddress, int oldAddressOffset, int newAddress,
			int newAddressOffset, int length) throws Db4oIOException {
		copy(
			regularAddress(oldAddress, oldAddressOffset),
			regularAddress(newAddress, newAddressOffset),
			length);
	}
	
	/**
	 * outside call to set the block size of this adapter
	 */
	public void blockSize(int blockSize) {
		if (blockSize < 1) {
			throw new IllegalArgumentException();
		}
		_blockSize = blockSize;
	}

	/**
	 * implement to close the adapter
	 */
	public void close() throws Db4oIOException {
		_storage.close();
	}

	/**
	 * copies a block within a file in absolute mode
	 */
	public void copy(long oldAddress, long newAddress, int length)
			throws Db4oIOException {

		if (DTrace.enabled) {
			DTrace.IO_COPY.logLength(newAddress, length);
		}

		if (length > COPY_SIZE) {
			byte[] buffer = new byte[COPY_SIZE];
			int pos = 0;
			while (pos + COPY_SIZE < length) {
				copy(buffer, oldAddress + pos, newAddress + pos);
				pos += COPY_SIZE;
			}
			oldAddress += pos;
			newAddress += pos;
			length -= pos;
		}

		copy(new byte[length], oldAddress, newAddress);
	}

	private void copy(byte[] buffer, long oldAddress, long newAddress)
			throws Db4oIOException {
		read(oldAddress, buffer);
		write(oldAddress, buffer);
	}
	
	public long length() throws Db4oIOException {
		return _storage.length();
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public int blockRead(int address, int offset, byte[] buffer) throws Db4oIOException {
		return blockRead(address, offset, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public int blockRead(int address, int offset, byte[] bytes, int length) throws Db4oIOException {
		return read(regularAddress(address, offset), bytes, length);
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public int blockRead(int address, byte[] buffer) throws Db4oIOException {
		return blockRead(address, 0, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public int blockRead(int address, byte[] bytes, int length) throws Db4oIOException {
		return blockRead(address, 0, bytes, length);
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public int read(long pos, byte[] buffer) throws Db4oIOException {
		return read(pos, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
		return _storage.read(pos, bytes, length);
	}

	public void sync() throws Db4oIOException {
		_storage.sync();
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public void blockWrite(int address, int offset, byte[] buffer) throws Db4oIOException {
		blockWrite(address, offset, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public void blockWrite(int address, int offset, byte[] bytes, int length) throws Db4oIOException {
		write(regularAddress(address, offset), bytes, length);
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public void blockWrite(int address, byte[] buffer) throws Db4oIOException {
		blockWrite(address, 0, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public void blockWrite(int address, byte[] bytes, int length) throws Db4oIOException {
		blockWrite(address, 0, bytes, length);
	}

	/**
	 * writes a buffer to the seeked address
	 */
	public void write(long pos, byte[] bytes) throws Db4oIOException {
		write(pos, bytes, bytes.length);
	}

	/**
	 * implement to write a buffer at the seeked address
	 */
	public void write(long pos, byte[] buffer, int length)
			throws Db4oIOException {
		_storage.write(pos, buffer, length);
	}

	/**
	 * returns the block size currently used
	 */
	public int blockSize() {
		return _blockSize;
	}
}