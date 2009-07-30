/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using Db4objects.Db4o.CS.Internal.Messages;

namespace Db4objects.Db4o.CS.Internal.Messages
{
	/// <exclude></exclude>
	public interface IMessageDispatcher
	{
		bool IsMessageDispatcherAlive();

		bool Write(Msg msg);

		bool Close();

		void SetDispatcherName(string name);
	}
}
