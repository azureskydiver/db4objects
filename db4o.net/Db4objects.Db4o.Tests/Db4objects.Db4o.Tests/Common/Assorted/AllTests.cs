using System;
using Db4oUnit.Extensions;
using Db4objects.Db4o.Tests.Common.Assorted;

namespace Db4objects.Db4o.Tests.Common.Assorted
{
	public class AllTests : Db4oTestSuite
	{
		public static void Main(string[] args)
		{
			new Db4objects.Db4o.Tests.Common.Assorted.AllTests().RunSolo();
		}

		protected override Type[] TestCases()
		{
			return new Type[] { typeof(AliasesTestCase), typeof(BackupStressTestCase), typeof(CanUpdateFalseRefreshTestCase)
				, typeof(CascadedDeleteReadTestCase), typeof(ChangeIdentity), typeof(ClassMetadataTestCase)
				, typeof(ClassRenameTestCase), typeof(CloseUnlocksFileTestCase), typeof(ComparatorSortTestCase)
				, typeof(DatabaseUnicityTest), typeof(DescendToNullFieldTestCase), typeof(FileSizeOnRollbackTestCase)
				, typeof(GetByUUIDTestCase), typeof(GetSingleSimpleArrayTestCase), typeof(HandlerRegistryTestCase)
				, typeof(IndexCreateDropTestCase), typeof(LazyObjectReferenceTestCase), typeof(LongLinkedListTestCase)
				, typeof(MaximumActivationDepthTestCase), typeof(MultiDeleteTestCase), typeof(NakedObjectTestCase)
				, typeof(ObjectMarshallerTestCase), typeof(ObjectVersionTest), typeof(PersistStaticFieldValuesTestCase)
				, typeof(PersistTypeTestCase), typeof(PreventMultipleOpenTestCase), typeof(ReAddCascadedDeleteTestCase)
				, typeof(ReferenceSystemTestCase), typeof(RollbackTestCase), typeof(SimplestPossibleTestCase)
				, typeof(SystemInfoTestCase) };
		}
	}
}
