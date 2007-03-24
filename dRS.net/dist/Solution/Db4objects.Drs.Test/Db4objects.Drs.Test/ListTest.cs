namespace Db4objects.Drs.Test
{
	public class ListTest : Db4objects.Drs.Test.DrsTestCase
	{
		public virtual void Test()
		{
			ActualTest();
		}

		protected virtual void ActualTest()
		{
			StoreListToProviderA();
			ReplicateAllToProviderBFirstTime();
			ModifyInProviderB();
			ReplicateAllStep2();
			AddElementInProviderA();
			ReplicateHolderStep3();
		}

		private void StoreListToProviderA()
		{
			Db4objects.Drs.Test.ListHolder lh = CreateHolder();
			Db4objects.Drs.Test.ListContent lc1 = new Db4objects.Drs.Test.ListContent("c1");
			Db4objects.Drs.Test.ListContent lc2 = new Db4objects.Drs.Test.ListContent("c2");
			lh.Add(lc1);
			lh.Add(lc2);
			A().Provider().StoreNew(lh);
			A().Provider().Commit();
			EnsureContent(A(), new string[] { "h1" }, new string[] { "c1", "c2" });
		}

		protected virtual Db4objects.Drs.Test.ListHolder CreateHolder()
		{
			Db4objects.Drs.Test.ListHolder lh = new Db4objects.Drs.Test.ListHolder("h1");
			lh.SetList(new System.Collections.ArrayList());
			return lh;
		}

		private void ReplicateAllToProviderBFirstTime()
		{
			ReplicateAll(A().Provider(), B().Provider());
			EnsureContent(A(), new string[] { "h1" }, new string[] { "c1", "c2" });
			EnsureContent(B(), new string[] { "h1" }, new string[] { "c1", "c2" });
		}

		private void ModifyInProviderB()
		{
			Db4objects.Drs.Test.ListHolder lh = (Db4objects.Drs.Test.ListHolder)GetOneInstance
				(B(), typeof(Db4objects.Drs.Test.ListHolder));
			lh.SetName("h2");
			System.Collections.IEnumerator itor = lh.GetList().GetEnumerator();
			Db4objects.Drs.Test.ListContent lc1 = (Db4objects.Drs.Test.ListContent)itor.Current;
			Db4objects.Drs.Test.ListContent lc2 = (Db4objects.Drs.Test.ListContent)itor.Current;
			lc1.SetName("co1");
			lc2.SetName("co2");
			B().Provider().Update(lc1);
			B().Provider().Update(lc2);
			B().Provider().Update(lh.GetList());
			B().Provider().Update(lh);
			B().Provider().Commit();
			EnsureContent(B(), new string[] { "h2" }, new string[] { "co1", "co2" });
		}

		private void ReplicateAllStep2()
		{
			ReplicateAll(B().Provider(), A().Provider());
			EnsureContent(B(), new string[] { "h2" }, new string[] { "co1", "co2" });
			EnsureContent(A(), new string[] { "h2" }, new string[] { "co1", "co2" });
		}

		private void AddElementInProviderA()
		{
			Db4objects.Drs.Test.ListHolder lh = (Db4objects.Drs.Test.ListHolder)GetOneInstance
				(A(), typeof(Db4objects.Drs.Test.ListHolder));
			lh.SetName("h3");
			Db4objects.Drs.Test.ListContent lc3 = new Db4objects.Drs.Test.ListContent("co3");
			A().Provider().StoreNew(lc3);
			lh.GetList().Add(lc3);
			A().Provider().Update(lh.GetList());
			A().Provider().Update(lh);
			A().Provider().Commit();
			EnsureContent(A(), new string[] { "h3" }, new string[] { "co1", "co2", "co3" });
		}

		private void ReplicateHolderStep3()
		{
			ReplicateClass(A().Provider(), B().Provider(), typeof(Db4objects.Drs.Test.ListHolder)
				);
			EnsureContent(A(), new string[] { "h3" }, new string[] { "co1", "co2", "co3" });
			EnsureContent(B(), new string[] { "h3" }, new string[] { "co1", "co2", "co3" });
		}

		private void EnsureContent(Db4objects.Drs.Test.IDrsFixture fixture, string[] holderNames
			, string[] contentNames)
		{
			int holderCount = holderNames.Length;
			EnsureInstanceCount(fixture, typeof(Db4objects.Drs.Test.ListHolder), holderCount);
			int i = 0;
			System.Collections.IEnumerator objectSet = fixture.Provider().GetStoredObjects(typeof(Db4objects.Drs.Test.ListHolder)
				).GetEnumerator();
			while (objectSet.MoveNext())
			{
				Db4objects.Drs.Test.ListHolder lh = (Db4objects.Drs.Test.ListHolder)objectSet.Current;
				Db4oUnit.Assert.AreEqual(holderNames[i], lh.GetName());
				System.Collections.IEnumerator itor = lh.GetList().GetEnumerator();
				int idx = 0;
				while (itor.MoveNext())
				{
					Db4oUnit.Assert.AreEqual(contentNames[idx], ((Db4objects.Drs.Test.ListContent)itor
						.Current).GetName());
					idx++;
				}
			}
		}
	}
}
