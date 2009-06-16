/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.CS.Internal.Messages;

namespace Db4objects.Db4o.CS.Internal
{
	public class MessageEventArgs : EventArgs
	{
		private Msg _message;

		public MessageEventArgs(Msg message)
		{
			_message = message;
		}

		public virtual Msg Message()
		{
			return _message;
		}
	}
}