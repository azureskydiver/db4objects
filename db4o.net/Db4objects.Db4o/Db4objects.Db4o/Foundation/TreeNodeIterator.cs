using Db4objects.Db4o.Foundation;

namespace Db4objects.Db4o.Foundation
{
	/// <exclude></exclude>
	public class TreeNodeIterator : AbstractTreeIterator
	{
		public TreeNodeIterator(Tree tree) : base(tree)
		{
		}

		protected override object CurrentValue(Tree tree)
		{
			return tree.Root();
		}
	}
}
