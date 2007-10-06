/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4oUnit;
using Db4oUnit.Extensions;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.TA;
using Db4objects.Db4o.TA.Tests;
using Db4objects.Db4o.TA.Tests.Collections;

namespace Db4objects.Db4o.TA.Tests
{
	public class TransparentActivationTestCase : AbstractDb4oTestCase
	{
		private const int PRIORITY = 42;

		protected override void Configure(IConfiguration config)
		{
			config.Add(new PagedListSupport());
			config.Add(new TransparentActivationSupport());
		}

		/// <exception cref="Exception"></exception>
		protected override void Store()
		{
			Project project = new PrioritizedProject("db4o", PRIORITY);
			project.LogWorkDone(new UnitOfWork("ta kick-off", new DateTime(1000), new DateTime
				(2000)));
			Store(project);
		}

		public virtual void Test()
		{
			PrioritizedProject project = (PrioritizedProject)RetrieveOnlyInstance(typeof(Project)
				);
			Assert.AreEqual(PRIORITY, project.GetPriority());
			Assert.AreEqual(1000, project.TotalTimeSpent());
		}
	}
}
