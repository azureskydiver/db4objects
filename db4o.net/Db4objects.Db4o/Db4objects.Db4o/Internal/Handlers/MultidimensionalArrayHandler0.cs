/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Marshall;

namespace Db4objects.Db4o.Internal.Handlers
{
	/// <exclude></exclude>
	public class MultidimensionalArrayHandler0 : MultidimensionalArrayHandler
	{
		public MultidimensionalArrayHandler0(ITypeHandler4 template) : base(template)
		{
		}

		public override object Read(IReadContext context)
		{
			throw new NotImplementedException();
		}
	}
}
