namespace Db4objects.Drs.Test
{
	public class ReplicationAfterDeletionTest : Db4objects.Drs.Test.DrsTestCase
	{
		public virtual void Test()
		{
			Replicate();
			Clean();
			Replicate();
			Clean();
		}

		protected override void Clean()
		{
			Delete(new System.Type[] { typeof(Db4objects.Drs.Test.SPCChild), typeof(Db4objects.Drs.Test.SPCParent)
				 });
		}

		private void Replicate()
		{
			Db4objects.Drs.Test.SPCChild child = new Db4objects.Drs.Test.SPCChild("c1");
			Db4objects.Drs.Test.SPCParent parent = new Db4objects.Drs.Test.SPCParent(child, "p1"
				);
			A().Provider().StoreNew(parent);
			A().Provider().Commit();
			ReplicateAll(A().Provider(), B().Provider());
		}
	}
}
