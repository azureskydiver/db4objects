/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

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
			return new Type[] { typeof(AliasesTestCase), typeof(BackupStressTestCase), typeof(
				CallbackTestCase), typeof(CanUpdateFalseRefreshTestCase), typeof(CascadeDeleteDeletedTestCase
				), typeof(CascadedDeleteReadTestCase), typeof(ChangeIdentity), typeof(CloseUnlocksFileTestCase
				), typeof(ComparatorSortTestCase), typeof(DatabaseGrowthSizeTestCase), typeof(DatabaseUnicityTest
				), typeof(DbPathDoesNotExistTestCase), typeof(DeleteUpdateTestCase), typeof(DescendToNullFieldTestCase
				), typeof(DualDeleteTestCase), typeof(ExceptionsOnNotStorableFalseTestCase), typeof(
				ExceptionsOnNotStorableIsDefaultTestCase), typeof(GetSingleSimpleArrayTestCase), 
				typeof(HandlerRegistryTestCase), typeof(IndexCreateDropTestCase), typeof(IndexedBlockSizeQueryTestCase
				), typeof(InMemoryObjectContainerTestCase), typeof(InvalidOffsetInDeleteTestCase
				), typeof(KnownClassesTestCase), typeof(LazyObjectReferenceTestCase), typeof(LockedTreeTestCase
				), typeof(LongLinkedListTestCase), typeof(MultiDeleteTestCase), typeof(ObjectConstructorTestCase
				), typeof(PlainObjectTestCase), typeof(PeekPersistedTestCase), typeof(PersistentIntegerArrayTestCase
				), typeof(PersistStaticFieldValuesTestCase), typeof(PersistTypeTestCase), typeof(
				PreventMultipleOpenTestCase), typeof(QueryByInterface), typeof(ReAddCascadedDeleteTestCase
				), typeof(RepeatDeleteReaddTestCase), typeof(RollbackDeleteTestCase), typeof(RollbackTestCase
				), typeof(RollbackUpdateTestCase), typeof(RollbackUpdateCascadeTestCase), typeof(
				SimplestPossibleNullMemberTestCase), typeof(SimplestPossibleTestCase), typeof(SimplestPossibleParentChildTestCase
				), typeof(SystemInfoTestCase), typeof(UpdateDepthTestCase) };
		}
		// FIXME: COR-1060
		//            DeleteSetTestCase.class,
	}
}
