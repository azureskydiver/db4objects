/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Query;

namespace Db4objects.Db4o.Query
{
	/// <summary>
	/// set of
	/// <see cref="IConstraint">IConstraint</see>
	/// objects.
	/// &lt;br&gt;&lt;br&gt;This extension of the
	/// <see cref="IConstraint">IConstraint</see>
	/// interface allows
	/// setting the evaluation mode of all contained
	/// <see cref="IConstraint">IConstraint</see>
	/// objects with single calls.
	/// &lt;br&gt;&lt;br&gt;
	/// See also
	/// <see cref="IQuery.Constraints">IQuery.Constraints</see>
	/// .
	/// </summary>
	public interface IConstraints : IConstraint
	{
		/// <summary>
		/// returns an array of the contained
		/// <see cref="IConstraint">IConstraint</see>
		/// objects.
		/// </summary>
		/// <returns>
		/// an array of the contained
		/// <see cref="IConstraint">IConstraint</see>
		/// objects.
		/// </returns>
		IConstraint[] ToArray();
	}
}
