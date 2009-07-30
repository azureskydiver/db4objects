/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using Db4objects.Db4o.CS.Internal.Messages;
using Db4objects.Db4o.Internal;

namespace Db4objects.Db4o.CS.Internal.Messages
{
	/// <exclude></exclude>
	public class MVersion : Msg, IMessageWithResponse
	{
		public virtual Msg ReplyFromServer()
		{
			long ver = 0;
			ObjectContainerBase stream = Stream();
			lock (StreamLock())
			{
				ver = stream.CurrentVersion();
			}
			return Msg.IdList.GetWriterForLong(Transaction(), ver);
		}
	}
}
