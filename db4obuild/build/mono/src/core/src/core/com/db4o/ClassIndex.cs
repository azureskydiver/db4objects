/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <summary>representation to collect and hold all IDs of one class</summary>
	internal class ClassIndex : com.db4o.YapMeta, com.db4o.ReadWriteable, com.db4o.UseSystemTransaction
	{
		/// <summary>contains TreeInt with object IDs</summary>
		internal com.db4o.Tree i_root;

		internal virtual void add(int a_id)
		{
			i_root = com.db4o.Tree.add(i_root, new com.db4o.TreeInt(a_id));
		}

		public int byteCount()
		{
			return com.db4o.YapConst.YAPINT_LENGTH * (com.db4o.Tree.size(i_root) + 1);
		}

		public void clear()
		{
			i_root = null;
		}

		internal com.db4o.Tree cloneForYapClass(com.db4o.Transaction a_trans, int a_yapClassID
			)
		{
			com.db4o.Tree[] tree = new com.db4o.Tree[] { com.db4o.Tree.deepClone(i_root, null
				) };
			a_trans.traverseAddedClassIDs(a_yapClassID, new _AnonymousInnerClass29(this, tree
				));
			a_trans.traverseRemovedClassIDs(a_yapClassID, new _AnonymousInnerClass34(this, tree
				));
			return tree[0];
		}

		private sealed class _AnonymousInnerClass29 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass29(ClassIndex _enclosing, com.db4o.Tree[] tree)
			{
				this._enclosing = _enclosing;
				this.tree = tree;
			}

			public void visit(object obj)
			{
				tree[0] = com.db4o.Tree.add(tree[0], new com.db4o.TreeInt(((com.db4o.TreeInt)obj)
					.i_key));
			}

			private readonly ClassIndex _enclosing;

			private readonly com.db4o.Tree[] tree;
		}

		private sealed class _AnonymousInnerClass34 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass34(ClassIndex _enclosing, com.db4o.Tree[] tree)
			{
				this._enclosing = _enclosing;
				this.tree = tree;
			}

			public void visit(object obj)
			{
				tree[0] = com.db4o.Tree.removeLike(tree[0], (com.db4o.TreeInt)obj);
			}

			private readonly ClassIndex _enclosing;

			private readonly com.db4o.Tree[] tree;
		}

		internal sealed override byte getIdentifier()
		{
			return com.db4o.YapConst.YAPINDEX;
		}

		internal virtual long[] getInternalIDs(com.db4o.Transaction a_trans, int a_yapClassID
			)
		{
			com.db4o.Tree tree = cloneForYapClass(a_trans, a_yapClassID);
			if (tree == null)
			{
				return new long[0];
			}
			long[] ids = new long[tree.size()];
			int[] i = new int[] { 0 };
			tree.traverse(new _AnonymousInnerClass54(this, ids, i));
			return ids;
		}

		private sealed class _AnonymousInnerClass54 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass54(ClassIndex _enclosing, long[] ids, int[] i)
			{
				this._enclosing = _enclosing;
				this.ids = ids;
				this.i = i;
			}

			public void visit(object obj)
			{
				ids[i[0]++] = ((com.db4o.TreeInt)obj).i_key;
			}

			private readonly ClassIndex _enclosing;

			private readonly long[] ids;

			private readonly int[] i;
		}

		internal sealed override int ownLength()
		{
			return com.db4o.YapConst.OBJECT_LENGTH + byteCount();
		}

		public object read(com.db4o.YapReader a_reader)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal sealed override void readThis(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_reader)
		{
			i_root = new com.db4o.TreeReader(a_reader, new com.db4o.TreeInt(0)).read();
		}

		internal virtual void remove(int a_id)
		{
			i_root = com.db4o.Tree.removeLike(i_root, new com.db4o.TreeInt(a_id));
		}

		internal virtual void setDirty(com.db4o.YapStream a_stream)
		{
			a_stream.setDirty(this);
		}

		public virtual void write(com.db4o.YapWriter a_writer)
		{
			writeThis(a_writer);
		}

		internal sealed override void writeThis(com.db4o.YapWriter a_writer)
		{
			com.db4o.Tree.write(a_writer, i_root);
		}
	}
}
