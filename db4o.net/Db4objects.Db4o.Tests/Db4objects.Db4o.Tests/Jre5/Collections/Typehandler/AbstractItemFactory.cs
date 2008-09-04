/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;

namespace Db4objects.Db4o.Tests.Jre5.Collections.Typehandler
{
	/// <decaf.ignore></decaf.ignore>
	public abstract class AbstractItemFactory
	{
		internal static readonly string MapFieldName = "_map";

		internal static readonly string ListFieldName = "_list";

		public abstract string FieldName();

		public abstract object NewItem();

		public abstract Type ItemClass();

		public abstract Type ContainerClass();
	}
}
