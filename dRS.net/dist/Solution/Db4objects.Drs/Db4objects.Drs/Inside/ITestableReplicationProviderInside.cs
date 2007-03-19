namespace Db4objects.Drs.Inside
{
	public interface ITestableReplicationProviderInside : Db4objects.Drs.Inside.IReplicationProviderInside
		, Db4objects.Drs.Inside.ISimpleObjectContainer
	{
		bool SupportsMultiDimensionalArrays();

		bool SupportsHybridCollection();

		bool SupportsRollback();

		bool SupportsCascadeDelete();
	}
}
