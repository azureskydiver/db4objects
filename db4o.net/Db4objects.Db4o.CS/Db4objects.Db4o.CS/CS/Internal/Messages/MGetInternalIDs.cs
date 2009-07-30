/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.CS.Internal.Messages;
using Db4objects.Db4o.CS.Internal.Objectexchange;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;

namespace Db4objects.Db4o.CS.Internal.Messages
{
	public sealed class MGetInternalIDs : MsgD, IMessageWithResponse
	{
		public Msg ReplyFromServer()
		{
			ByteArrayBuffer bytes = GetByteLoad();
			int classMetadataID = bytes.ReadInt();
			int prefetchDepth = bytes.ReadInt();
			int prefetchCount = bytes.ReadInt();
			ByteArrayBuffer payload = MarshallIDsFor(classMetadataID, prefetchDepth, prefetchCount
				);
			MsgD message = Msg.IdList.GetWriterForLength(Transaction(), payload.Length());
			message.PayLoad().WriteBytes(payload._buffer);
			return message;
		}

		private ByteArrayBuffer MarshallIDsFor(int classMetadataID, int prefetchDepth, int
			 prefetchCount)
		{
			lock (StreamLock())
			{
				long[] ids = IdsFor(classMetadataID);
				return ObjectExchangeStrategyFactory.ForConfig(new ObjectExchangeConfiguration(prefetchDepth
					, prefetchCount)).Marshall((LocalTransaction)Transaction(), IntIterators.ForLongs
					(ids), ids.Length);
			}
		}

		private long[] IdsFor(int classMetadataID)
		{
			lock (StreamLock())
			{
				try
				{
					return Stream().ClassMetadataForID(classMetadataID).GetIDs(Transaction());
				}
				catch (Exception)
				{
				}
			}
			return new long[0];
		}
	}
}
