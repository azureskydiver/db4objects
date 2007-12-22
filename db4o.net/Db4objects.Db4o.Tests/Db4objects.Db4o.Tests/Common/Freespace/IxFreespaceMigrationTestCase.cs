/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4oUnit;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Tests.Common.Freespace;
using Db4objects.Db4o.Tests.Common.Handlers;

namespace Db4objects.Db4o.Tests.Common.Freespace
{
	public class IxFreespaceMigrationTestCase : FormatMigrationTestCaseBase
	{
		protected override void ConfigureForStore(IConfiguration config)
		{
			config.Freespace().UseIndexSystem();
		}

		protected override void Store(IExtObjectContainer objectContainer)
		{
			IxFreespaceMigrationTestCase.Item nextItem = null;
			for (int i = 9; i >= 0; i--)
			{
				IxFreespaceMigrationTestCase.Item storedItem = new IxFreespaceMigrationTestCase.Item
					("item" + i, nextItem);
				objectContainer.Set(storedItem);
				nextItem = storedItem;
			}
			objectContainer.Commit();
			IxFreespaceMigrationTestCase.Item item = QueryForItem(objectContainer, 0);
			for (int i = 0; i < 5; i++)
			{
				objectContainer.Delete(item);
				item = item._next;
			}
			objectContainer.Commit();
		}

		private IxFreespaceMigrationTestCase.Item QueryForItem(IExtObjectContainer objectContainer
			, int n)
		{
			IQuery q = objectContainer.Query();
			q.Constrain(typeof(IxFreespaceMigrationTestCase.Item));
			q.Descend("_name").Constrain("item" + n);
			return (IxFreespaceMigrationTestCase.Item)q.Execute().Next();
		}

		protected override void AssertObjectsAreReadable(IExtObjectContainer objectContainer
			)
		{
			AssertItemCount(objectContainer, 5);
			IxFreespaceMigrationTestCase.Item item = QueryForItem(objectContainer, 5);
			for (int i = 5; i < 10; i++)
			{
				Assert.AreEqual("item" + i, item._name);
				item = item._next;
			}
		}

		private void AssertItemCount(IExtObjectContainer objectContainer, int i)
		{
			IQuery q = objectContainer.Query();
			q.Constrain(typeof(IxFreespaceMigrationTestCase.Item));
			Assert.AreEqual(i, q.Execute().Size());
		}

		public class Item
		{
			public string _name;

			public IxFreespaceMigrationTestCase.Item _next;

			public Item(string name)
			{
				_name = name;
			}

			public Item(string name, IxFreespaceMigrationTestCase.Item next_)
			{
				_name = name;
				_next = next_;
			}
		}

		protected override string FileNamePrefix()
		{
			return "migrate_freespace_ix_";
		}

		protected override string[] VersionNames()
		{
			return new string[] { Sharpen.Runtime.Substring(Db4oFactory.Version(), 5) };
		}
	}
}
