/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	internal class YapClientBlobThread : j4o.lang.Thread
	{
		private com.db4o.YapClient stream;

		private com.db4o.Queue4 queue = new com.db4o.Queue4();

		private bool terminated = false;

		internal YapClientBlobThread(com.db4o.YapClient aStream)
		{
			stream = aStream;
			setPriority(MIN_PRIORITY);
		}

		internal virtual void add(com.db4o.MsgBlob msg)
		{
			lock (queue)
			{
				queue.add(msg);
			}
		}

		internal virtual bool isTerminated()
		{
			lock (this)
			{
				return terminated;
			}
		}

		public override void run()
		{
			try
			{
				com.db4o.YapSocket socket = stream.createParalellSocket();
				com.db4o.MsgBlob msg = null;
				lock (queue)
				{
					msg = (com.db4o.MsgBlob)queue.next();
				}
				while (msg != null)
				{
					msg.write(stream, socket);
					msg.processClient(socket);
					lock (stream.blobLock)
					{
						lock (queue)
						{
							msg = (com.db4o.MsgBlob)queue.next();
						}
						if (msg == null)
						{
							terminated = true;
							com.db4o.Msg.CLOSE.write(stream, socket);
							try
							{
								socket.close();
							}
							catch (System.Exception e)
							{
							}
						}
					}
				}
			}
			catch (System.Exception e)
			{
				j4o.lang.JavaSystem.printStackTrace(e);
			}
		}
	}
}
