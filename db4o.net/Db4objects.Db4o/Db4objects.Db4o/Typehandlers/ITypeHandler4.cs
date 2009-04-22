/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Delete;
using Db4objects.Db4o.Marshall;
using Db4objects.Db4o.Reflect;

namespace Db4objects.Db4o.Typehandlers
{
	/// <summary>
	/// handles reading, writing, deleting, defragmenting and
	/// comparisons for types of objects.<br /><br />
	/// Custom Typehandlers can be implemented to alter the default
	/// behaviour of storing all non-transient fields of an object.<br /><br />
	/// </summary>
	/// <seealso>
	/// 
	/// <see cref="Db4objects.Db4o.Config.IConfiguration.RegisterTypeHandler">Db4objects.Db4o.Config.IConfiguration.RegisterTypeHandler
	/// 	</see>
	/// 
	/// </seealso>
	public interface ITypeHandler4
	{
		/// <summary>gets called when an object gets deleted.</summary>
		/// <remarks>gets called when an object gets deleted.</remarks>
		/// <param name="context"></param>
		/// <exception cref="Db4objects.Db4o.Ext.Db4oIOException">Db4objects.Db4o.Ext.Db4oIOException
		/// 	</exception>
		void Delete(IDeleteContext context);

		/// <summary>gets called when an object gets defragmented.</summary>
		/// <remarks>gets called when an object gets defragmented.</remarks>
		/// <param name="context"></param>
		void Defragment(IDefragmentContext context);

		/// <summary>gets called when an object is to be written to the database.</summary>
		/// <remarks>gets called when an object is to be written to the database.</remarks>
		/// <param name="context"></param>
		/// <param name="obj">the object</param>
		void Write(IWriteContext context, object obj);

		/// <summary>
		/// gets called to check whether a TypeHandler can hold
		/// a specific type
		/// </summary>
		/// <param name="type">the type</param>
		/// <returns>true, if this Typehandler can hold a type</returns>
		bool CanHold(IReflectClass type);
	}
}
