/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Foundation;

namespace Db4objects.Db4o.Foundation
{
	public class AutoStopWatch : StopWatch
	{
		public AutoStopWatch()
		{
			Start();
		}
	}
}
