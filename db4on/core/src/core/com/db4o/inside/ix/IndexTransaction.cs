namespace com.db4o.inside.ix
{
	/// <summary>Index root holder for a field and a transaction.</summary>
	/// <remarks>Index root holder for a field and a transaction.</remarks>
	/// <exclude></exclude>
	public class IndexTransaction : com.db4o.foundation.Visitor4
	{
		internal readonly com.db4o.inside.ix.Index4 i_index;

		internal readonly com.db4o.Transaction i_trans;

		internal int i_version;

		private com.db4o.Tree i_root;

		internal IndexTransaction(com.db4o.Transaction a_trans, com.db4o.inside.ix.Index4
			 a_index)
		{
			i_trans = a_trans;
			i_index = a_index;
		}

		public override bool Equals(object obj)
		{
			return i_trans == ((com.db4o.inside.ix.IndexTransaction)obj).i_trans;
		}

		public virtual void add(int id, object value)
		{
			patch(new com.db4o.inside.ix.IxAdd(this, id, value));
		}

		public virtual void remove(int id, object value)
		{
			patch(new com.db4o.inside.ix.IxRemove(this, id, value));
		}

		private void patch(com.db4o.inside.ix.IxPatch patch)
		{
			i_root = com.db4o.Tree.add(i_root, patch);
		}

		public virtual com.db4o.Tree getRoot()
		{
			return i_root;
		}

		public virtual void commit()
		{
			i_index.commit(this);
		}

		public virtual void rollback()
		{
			i_index.rollback(this);
		}

		internal virtual void merge(com.db4o.inside.ix.IndexTransaction a_ft)
		{
			com.db4o.Tree otherRoot = a_ft.getRoot();
			if (otherRoot != null)
			{
				otherRoot.traverseFromLeaves(this);
			}
		}

		/// <summary>
		/// Visitor functionality for merge:<br />
		/// Add
		/// </summary>
		public virtual void visit(object obj)
		{
			if (obj is com.db4o.inside.ix.IxPatch)
			{
				com.db4o.inside.ix.IxPatch tree = (com.db4o.inside.ix.IxPatch)obj;
				if (tree.hasQueue())
				{
					com.db4o.foundation.Queue4 queue = tree.detachQueue();
					while ((tree = (com.db4o.inside.ix.IxPatch)queue.next()) != null)
					{
						tree.detachQueue();
						addPatchToRoot(tree);
					}
				}
				else
				{
					addPatchToRoot(tree);
				}
			}
		}

		private void addPatchToRoot(com.db4o.inside.ix.IxPatch tree)
		{
			if (tree._version != i_version)
			{
				tree.beginMerge();
				tree.handler().prepareComparison(tree.handler().comparableObject(i_trans, tree._value
					));
				if (i_root == null)
				{
					i_root = tree;
				}
				else
				{
					i_root = i_root.add(tree);
				}
			}
		}

		internal virtual int countLeaves()
		{
			if (i_root == null)
			{
				return 0;
			}
			int[] leaves = { 0 };
			i_root.traverse(new _AnonymousInnerClass102(this, leaves));
			return leaves[0];
		}

		private sealed class _AnonymousInnerClass102 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass102(IndexTransaction _enclosing, int[] leaves)
			{
				this._enclosing = _enclosing;
				this.leaves = leaves;
			}

			public void visit(object a_object)
			{
				leaves[0]++;
			}

			private readonly IndexTransaction _enclosing;

			private readonly int[] leaves;
		}

		public virtual void setRoot(com.db4o.Tree a_tree)
		{
			i_root = a_tree;
		}

		public override string ToString()
		{
			return base.ToString();
			j4o.lang.StringBuffer sb = new j4o.lang.StringBuffer();
			sb.append("IxFieldTransaction ");
			sb.append(j4o.lang.JavaSystem.identityHashCode(this));
			if (i_root == null)
			{
				sb.append("\n    Empty");
			}
			else
			{
				i_root.traverse(new _AnonymousInnerClass124(this, sb));
			}
			return sb.ToString();
		}

		private sealed class _AnonymousInnerClass124 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass124(IndexTransaction _enclosing, j4o.lang.StringBuffer
				 sb)
			{
				this._enclosing = _enclosing;
				this.sb = sb;
			}

			public void visit(object a_object)
			{
				sb.append("\n");
				sb.append(a_object.ToString());
			}

			private readonly IndexTransaction _enclosing;

			private readonly j4o.lang.StringBuffer sb;
		}
	}
}
