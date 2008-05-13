/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Internal;

namespace Db4objects.Db4o.Internal.Handlers
{
	/// <summary>
	/// marker interface for TypeHandlers where the slot
	/// length can change, depending on the object stored
	/// </summary>
	/// <exclude></exclude>
	public interface IVariableLengthTypeHandler : ITypeHandler4
	{
	}
}
