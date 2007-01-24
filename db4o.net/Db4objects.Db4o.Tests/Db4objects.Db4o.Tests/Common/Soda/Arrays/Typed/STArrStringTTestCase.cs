namespace Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed
{
	public class STArrStringTTestCase : Db4objects.Db4o.Tests.Common.Soda.Util.SodaBaseTestCase
	{
		public string[] strArr;

		public STArrStringTTestCase()
		{
		}

		public STArrStringTTestCase(string[] arr)
		{
			strArr = arr;
		}

		public override object[] CreateData()
		{
			return new object[] { new Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase
				(), new Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase(new 
				string[] { null }), new Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase
				(new string[] { null, null }), new Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase
				(new string[] { "foo", "bar", "fly" }), new Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase
				(new string[] { null, "bar", "wohay", "johy" }) };
		}

		public virtual void TestDefaultContainsOne()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase
				(new string[] { "bar" }));
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDefaultContainsTwo()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase
				(new string[] { "foo", "bar" }));
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendOne()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(typeof(Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase)
				);
			q.Descend("strArr").Constrain("bar");
			Expect(q, new int[] { 3, 4 });
		}

		public virtual void TestDescendTwo()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(typeof(Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase)
				);
			Db4objects.Db4o.Query.IQuery qElements = q.Descend("strArr");
			qElements.Constrain("foo");
			qElements.Constrain("bar");
			Expect(q, new int[] { 3 });
		}

		public virtual void TestDescendOneNot()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(typeof(Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase)
				);
			q.Descend("strArr").Constrain("bar").Not();
			Expect(q, new int[] { 0, 1, 2 });
		}

		public virtual void TestDescendTwoNot()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(typeof(Db4objects.Db4o.Tests.Common.Soda.Arrays.Typed.STArrStringTTestCase)
				);
			Db4objects.Db4o.Query.IQuery qElements = q.Descend("strArr");
			qElements.Constrain("foo").Not();
			qElements.Constrain("bar").Not();
			Expect(q, new int[] { 0, 1, 2 });
		}
	}
}
