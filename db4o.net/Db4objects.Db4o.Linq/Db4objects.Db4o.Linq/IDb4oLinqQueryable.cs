﻿/* Copyright (C) 2007 - 2008  db4objects Inc.  http://www.db4o.com */

using System.Linq;

namespace Db4objects.Db4o.Linq
{
	/// <summary>
	/// IDb4oLinqQueryable is the query type of Linq to db4o when working with an API requiring
	/// a LINQ provider implementing <see cref="System.Linq.IQueryable">IQueryable</see>.
	/// <typeparam name="T">The type of the objects that are queried from the database.</typeparam>
	public interface IDb4oLinqQueryable<T> : IDb4oLinqQueryable, IOrderedQueryable<T>
	{
	}

	public interface IDb4oLinqQueryable : IOrderedQueryable
	{
		IDb4oLinqQuery GetQuery();
	}
}
