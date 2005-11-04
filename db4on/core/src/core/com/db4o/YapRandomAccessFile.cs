namespace com.db4o
{
	/// <exclude></exclude>
	public class YapRandomAccessFile : com.db4o.YapFile
	{
		private com.db4o.Session i_session;

		private com.db4o.io.IoAdapter i_file;

		private com.db4o.io.IoAdapter i_timerFile;

		private volatile com.db4o.io.IoAdapter i_backupFile;

		private byte[] i_timerBytes = new byte[com.db4o.YapConst.LONG_BYTES];

		private object i_fileLock;

		internal YapRandomAccessFile(com.db4o.Session a_session) : base(null)
		{
			lock (i_lock)
			{
				i_fileLock = new object();
				i_session = a_session;
				try
				{
					open();
				}
				catch (com.db4o.ext.DatabaseFileLockedException e)
				{
					stopSession();
					throw e;
				}
				initialize3();
			}
		}

		public override void backup(string path)
		{
			lock (i_lock)
			{
				checkClosed();
				if (i_backupFile != null)
				{
					com.db4o.inside.Exceptions4.throwRuntimeException(61);
				}
				try
				{
					i_backupFile = i_config.i_ioAdapter.open(path, true, i_file.getLength());
				}
				catch (System.Exception e)
				{
					i_backupFile = null;
					com.db4o.inside.Exceptions4.throwRuntimeException(12, path);
				}
			}
			long pos = 0;
			int bufferlength = 8192;
			byte[] buffer = new byte[bufferlength];
			do
			{
				lock (i_lock)
				{
					i_file.seek(pos);
					int read = i_file.read(buffer);
					i_backupFile.seek(pos);
					i_backupFile.write(buffer, read);
					pos += read;
					j4o.lang.JavaSystem.notify(i_lock);
				}
			}
			while (pos < i_file.getLength());
			lock (i_lock)
			{
				i_backupFile.close();
				i_backupFile = null;
			}
		}

		internal override void blockSize(int blockSize)
		{
			i_file.blockSize(blockSize);
			if (i_timerFile != null)
			{
				i_timerFile.blockSize(blockSize);
			}
		}

		public override byte blockSize()
		{
			return (byte)i_file.blockSize();
		}

		internal override bool close2()
		{
			bool stopSession = true;
			lock (com.db4o.Db4o.Lock)
			{
				stopSession = i_session.closeInstance();
				if (stopSession)
				{
					freePrefetchedPointers();
					i_entryCounter++;
					try
					{
						write(true);
					}
					catch (System.Exception t)
					{
						fatalException(t);
					}
					base.close2();
					i_entryCounter--;
					com.db4o.Db4o.sessionStopped(i_session);
					lock (i_fileLock)
					{
						try
						{
							i_file.close();
							i_file = null;
							if (needsLockFileThread() && com.db4o.Debug.lockFile)
							{
								com.db4o.YapWriter lockBytes = new com.db4o.YapWriter(i_systemTrans, com.db4o.YapConst
									.YAPLONG_LENGTH);
								com.db4o.YLong.writeLong(0, lockBytes);
								i_timerFile.blockSeek(_configBlock._address, com.db4o.YapConfigBlock.ACCESS_TIME_OFFSET
									);
								i_timerFile.write(lockBytes._buffer);
								i_timerFile.close();
							}
						}
						catch (System.Exception e)
						{
							i_file = null;
							com.db4o.inside.Exceptions4.throwRuntimeException(11, e);
						}
						i_file = null;
					}
				}
			}
			return stopSession;
		}

		internal override void commit1()
		{
			ensureLastSlotWritten();
			base.commit1();
		}

		public override void copy(int oldAddress, int oldAddressOffset, int newAddress, int
			 newAddressOffset, int length)
		{
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				checkXBytes(newAddress, newAddressOffset, length);
			}
			try
			{
				if (i_backupFile == null)
				{
					i_file.blockCopy(oldAddress, oldAddressOffset, newAddress, newAddressOffset, length
						);
					return;
				}
				byte[] copyBytes = new byte[length];
				i_file.blockSeek(oldAddress, oldAddressOffset);
				i_file.read(copyBytes);
				i_file.blockSeek(newAddress, newAddressOffset);
				i_file.write(copyBytes);
				if (i_backupFile != null)
				{
					i_backupFile.blockSeek(newAddress, newAddressOffset);
					i_backupFile.write(copyBytes);
				}
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.throwRuntimeException(16, e);
			}
		}

		private void checkXBytes(int a_newAddress, int newAddressOffset, int a_length)
		{
			if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
			{
				try
				{
					byte[] checkXBytes = new byte[a_length];
					i_file.blockSeek(a_newAddress, newAddressOffset);
					i_file.read(checkXBytes);
					for (int i = 0; i < checkXBytes.Length; i++)
					{
						if (checkXBytes[i] != com.db4o.YapConst.XBYTE)
						{
							string msg = "XByte corruption adress:" + a_newAddress + " length:" + a_length;
							throw new j4o.lang.RuntimeException(msg);
						}
					}
				}
				catch (System.Exception e)
				{
					j4o.lang.JavaSystem.printStackTrace(e);
				}
			}
		}

		internal override void emergencyClose()
		{
			base.emergencyClose();
			try
			{
				i_file.close();
			}
			catch (System.Exception e)
			{
			}
			try
			{
				com.db4o.Db4o.sessionStopped(i_session);
			}
			catch (System.Exception e)
			{
			}
			i_file = null;
		}

		internal override long fileLength()
		{
			try
			{
				return i_file.getLength();
			}
			catch (System.Exception e)
			{
				throw new j4o.lang.RuntimeException();
			}
		}

		internal override string fileName()
		{
			return i_session.fileName();
		}

		private void open()
		{
			bool isNew = false;
			try
			{
				if (j4o.lang.JavaSystem.getLengthOf(fileName()) > 0)
				{
					com.db4o.io.IoAdapter ioAdapter = i_config.i_ioAdapter;
					if (!ioAdapter.exists(fileName()))
					{
						isNew = true;
						logMsg(14, fileName());
					}
					try
					{
						bool lockFile = com.db4o.Debug.lockFile && i_config.i_lockFile && (!i_config.i_readonly
							);
						i_file = ioAdapter.open(fileName(), lockFile, 0);
						if (needsLockFileThread() && com.db4o.Debug.lockFile)
						{
							i_timerFile = ioAdapter.open(fileName(), false, 0);
						}
					}
					catch (com.db4o.ext.DatabaseFileLockedException de)
					{
						throw de;
					}
					catch (System.Exception e)
					{
						com.db4o.inside.Exceptions4.throwRuntimeException(12, fileName(), e);
					}
					if (isNew)
					{
						configureNewFile();
						if (i_config.i_reservedStorageSpace > 0)
						{
							reserve(i_config.i_reservedStorageSpace);
						}
						write(false);
						writeHeader(false);
					}
					else
					{
						readThis();
					}
				}
				else
				{
					com.db4o.inside.Exceptions4.throwRuntimeException(21);
				}
			}
			catch (System.Exception exc)
			{
				if (i_references != null)
				{
					i_references.stopTimer();
				}
				throw exc;
			}
		}

		internal override void readBytes(byte[] bytes, int address, int length)
		{
			readBytes(bytes, address, 0, length);
		}

		internal override void readBytes(byte[] bytes, int address, int addressOffset, int
			 length)
		{
			try
			{
				i_file.blockSeek(address, addressOffset);
				i_file.read(bytes, length);
			}
			catch (System.IO.IOException ioex)
			{
				throw new j4o.lang.RuntimeException();
			}
		}

		internal override void reserve(int byteCount)
		{
			lock (i_lock)
			{
				int address = getSlot(byteCount);
				com.db4o.YapWriter yb = new com.db4o.YapWriter(i_systemTrans, address, byteCount);
				writeBytes(yb);
				free(address, byteCount);
			}
		}

		internal override void syncFiles()
		{
			try
			{
				i_file.sync();
				if (needsLockFileThread() && com.db4o.Debug.lockFile)
				{
					i_timerFile.sync();
				}
			}
			catch (System.Exception e)
			{
			}
		}

		internal override bool writeAccessTime()
		{
			if (!needsLockFileThread())
			{
				return true;
			}
			lock (i_fileLock)
			{
				if (i_file == null)
				{
					return false;
				}
				long lockTime = j4o.lang.JavaSystem.currentTimeMillis();
				com.db4o.YLong.writeLong(lockTime, i_timerBytes);
				i_timerFile.blockSeek(_configBlock._address, com.db4o.YapConfigBlock.ACCESS_TIME_OFFSET
					);
				i_timerFile.write(i_timerBytes);
			}
			return true;
		}

		internal override void writeBytes(com.db4o.YapWriter a_bytes)
		{
			if (i_config.i_readonly)
			{
				return;
			}
			if (com.db4o.Deploy.debug && !com.db4o.Deploy.flush)
			{
				return;
			}
			try
			{
				if (com.db4o.Debug.xbytes && com.db4o.Deploy.overwrite)
				{
					if (a_bytes.getID() != com.db4o.YapConst.IGNORE_ID)
					{
						checkXBytes(a_bytes.getAddress(), a_bytes.addressOffset(), a_bytes.getLength());
					}
				}
				i_file.blockSeek(a_bytes.getAddress(), a_bytes.addressOffset());
				i_file.write(a_bytes._buffer, a_bytes.getLength());
				if (i_backupFile != null)
				{
					i_backupFile.blockSeek(a_bytes.getAddress(), a_bytes.addressOffset());
					i_backupFile.write(a_bytes._buffer, a_bytes.getLength());
				}
			}
			catch (System.Exception e)
			{
				com.db4o.inside.Exceptions4.throwRuntimeException(16, e);
			}
		}

		public override void writeXBytes(int a_address, int a_length)
		{
		}
	}
}
