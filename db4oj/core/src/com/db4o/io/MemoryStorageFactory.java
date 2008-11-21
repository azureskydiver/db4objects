package com.db4o.io;

import java.util.*;

import com.db4o.ext.*;

public class MemoryStorageFactory implements StorageFactory {

	private final Map<String, Storage> _storages = new HashMap<String, Storage>();

	public boolean exists(String uri) {
		return _storages.containsKey(uri);
	}

	public Storage open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
		final Storage storage = produceStorage(uri, initialLength);
		return readOnly ? new ReadOnlyStorage(storage) : storage;
	}

	private Storage produceStorage(String uri, long initialLength) {
	    final Storage storage = _storages.get(uri);
		if (null != storage) {
			return storage;
		}
		final MemoryStorage newStorage = new MemoryStorage(new byte[(int)initialLength]);
		_storages.put(uri, newStorage);
		return newStorage;
    }
	
	private static class MemoryStorage implements Storage {
		
		private static final int GROW_BY = 10000;
		private byte[] _bytes;
		private int _length;

		public MemoryStorage(byte[] bytes) {
			_bytes = bytes;
			_length = bytes.length;
        }
		
		public long length() {
			return _length;
		}
		
		public int read(long pos, byte[] bytes, int length) throws Db4oIOException {
			final long avail = _length - pos;
			if (avail <= 0) {
				return - 1;
			}
			final int read = Math.min((int)avail, length);
			System.arraycopy(_bytes, (int)pos, bytes, 0, read);
			return read;
		}

		public void sync() throws Db4oIOException {
		}
		
		public void close() {
		}

		/**
		 * for internal processing only.
		 */
		public void write(long pos, byte[] buffer, int length) throws Db4oIOException {
			if (pos + length > _bytes.length) {
				long growBy = GROW_BY;
				if (pos + length > growBy) {
					growBy = pos + length;
				}
				byte[] temp = new byte[(int)(_bytes.length + growBy)];
				System.arraycopy(_bytes, 0, temp, 0, _length);
				_bytes = temp;
			}
			System.arraycopy(buffer, 0, _bytes, (int)pos, length);
			pos += length;
			if (pos > _length) {
				_length = (int)pos;
			}
		}
		
	}

}
