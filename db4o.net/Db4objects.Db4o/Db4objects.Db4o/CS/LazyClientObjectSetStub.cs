namespace Db4objects.Db4o.CS
{
	/// <exclude></exclude>
	public class LazyClientObjectSetStub
	{
		private readonly Db4objects.Db4o.Inside.Query.AbstractQueryResult _queryResult;

		private Db4objects.Db4o.Foundation.IIntIterator4 _idIterator;

		public LazyClientObjectSetStub(Db4objects.Db4o.Inside.Query.AbstractQueryResult queryResult
			, Db4objects.Db4o.Foundation.IIntIterator4 idIterator)
		{
			_queryResult = queryResult;
			_idIterator = idIterator;
		}

		public virtual Db4objects.Db4o.Foundation.IIntIterator4 IdIterator()
		{
			return _idIterator;
		}

		public virtual Db4objects.Db4o.Inside.Query.AbstractQueryResult QueryResult()
		{
			return _queryResult;
		}

		public virtual void Reset()
		{
			_idIterator = _queryResult.IterateIDs();
		}
	}
}
