namespace Db4objects.Db4o.Tests.Common.Querying
{
	public class IdListQueryResultTestCase : Db4objects.Db4o.Tests.Common.Querying.QueryResultTestCase
	{
		public static void Main(string[] args)
		{
			new Db4objects.Db4o.Tests.Common.Querying.IdListQueryResultTestCase().RunSolo();
		}

		protected override Db4objects.Db4o.Inside.Query.AbstractQueryResult NewQueryResult
			()
		{
			return new Db4objects.Db4o.Inside.Query.IdListQueryResult(Trans());
		}
	}
}
