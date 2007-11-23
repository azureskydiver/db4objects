/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;

namespace Db4objects.Db4o.Instrumentation.Api
{
	[System.Serializable]
	public class InstrumentationException : Exception
	{
		public InstrumentationException(Exception cause) : base(cause.Message, cause)
		{
		}
	}
}
