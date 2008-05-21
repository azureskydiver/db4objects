/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Drs.Inside.Traversal;

namespace Db4objects.Drs.Inside.Traversal
{
	public interface ICollectionFlattener : ICollectionTraverser
	{
		bool CanHandle(object obj);

		bool CanHandleClass(Type c);
	}
}
