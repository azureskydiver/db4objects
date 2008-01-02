/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Activation;
using Db4objects.Db4o.Tests.Common.TA;

namespace Db4objects.Db4o.Tests.Common.TA.TA
{
	public class TAArrayItem : ActivatableImpl
	{
		public int[] value;

		public object obj;

		public LinkedList[] lists;

		public object listsObject;

		public TAArrayItem()
		{
		}

		public virtual int[] Value()
		{
			Activate(ActivationPurpose.READ);
			return value;
		}

		public virtual object Object()
		{
			Activate(ActivationPurpose.READ);
			return obj;
		}

		public virtual LinkedList[] Lists()
		{
			Activate(ActivationPurpose.READ);
			return lists;
		}

		public virtual object ListsObject()
		{
			Activate(ActivationPurpose.READ);
			return listsObject;
		}
	}
}
