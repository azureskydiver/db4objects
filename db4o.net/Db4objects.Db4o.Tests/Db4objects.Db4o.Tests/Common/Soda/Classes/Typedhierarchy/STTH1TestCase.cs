namespace Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy
{
	/// <summary>TH: Typed Hierarchy</summary>
	public class STTH1TestCase : Db4objects.Db4o.Tests.Common.Soda.Util.SodaBaseTestCase
	{
		public Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2 h2;

		public string foo1;

		public STTH1TestCase()
		{
		}

		public STTH1TestCase(Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2
			 a2)
		{
			h2 = a2;
		}

		public STTH1TestCase(string str)
		{
			foo1 = str;
		}

		public STTH1TestCase(Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2
			 a2, string str)
		{
			h2 = a2;
			foo1 = str;
		}

		public override object[] CreateData()
		{
			return new object[] { new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				(), new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase("str1"
				), new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase(new 
				Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2()), new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2("str2")), new 
				Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2
				(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH3("str3"))), new 
				Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2
				(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH3("str3"), "str2"
				)) };
		}

		public virtual void TestStrNull()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				());
			q.Descend("foo1").Constrain(null);
			Expect(q, new int[] { 0, 2, 3, 4, 5 });
		}

		public virtual void TestBothNull()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				());
			q.Descend("foo1").Constrain(null);
			q.Descend("h2").Constrain(null);
			Db4objects.Db4o.Tests.Common.Soda.Util.SodaTestUtil.ExpectOne(q, _array[0]);
		}

		public virtual void TestDescendantNotNull()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				());
			q.Descend("h2").Constrain(null).Not();
			Expect(q, new int[] { 2, 3, 4, 5 });
		}

		public virtual void TestDescendantDescendantNotNull()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				());
			q.Descend("h2").Descend("h3").Constrain(null).Not();
			Expect(q, new int[] { 4, 5 });
		}

		public virtual void TestDescendantExists()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(_array[2]);
			Expect(q, new int[] { 2, 3, 4, 5 });
		}

		public virtual void TestDescendantValue()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(_array[3]);
			Expect(q, new int[] { 3, 5 });
		}

		public virtual void TestDescendantDescendantExists()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH3
				())));
			Expect(q, new int[] { 4, 5 });
		}

		public virtual void TestDescendantDescendantValue()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH3
				("str3"))));
			Expect(q, new int[] { 4, 5 });
		}

		public virtual void TestDescendantDescendantStringPath()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				());
			q.Descend("h2").Descend("h3").Descend("foo3").Constrain("str3");
			Expect(q, new int[] { 4, 5 });
		}

		public virtual void TestSequentialAddition()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				());
			Db4objects.Db4o.Query.IQuery cur = q.Descend("h2");
			cur.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH2(
				));
			cur.Descend("foo2").Constrain("str2");
			cur = cur.Descend("h3");
			cur.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH3(
				));
			cur.Descend("foo3").Constrain("str3");
			Db4objects.Db4o.Tests.Common.Soda.Util.SodaTestUtil.ExpectOne(q, _array[5]);
		}

		public virtual void TestTwoLevelOr()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				("str1"));
			q.Descend("foo1").Constraints().Or(q.Descend("h2").Descend("h3").Descend("foo3").
				Constrain("str3"));
			Expect(q, new int[] { 1, 4, 5 });
		}

		public virtual void TestThreeLevelOr()
		{
			Db4objects.Db4o.Query.IQuery q = NewQuery();
			q.Constrain(new Db4objects.Db4o.Tests.Common.Soda.Classes.Typedhierarchy.STTH1TestCase
				("str1"));
			q.Descend("foo1").Constraints().Or(q.Descend("h2").Descend("foo2").Constrain("str2"
				)).Or(q.Descend("h2").Descend("h3").Descend("foo3").Constrain("str3"));
			Expect(q, new int[] { 1, 3, 4, 5 });
		}
	}
}
