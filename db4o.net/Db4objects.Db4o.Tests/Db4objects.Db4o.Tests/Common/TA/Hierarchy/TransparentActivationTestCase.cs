/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using Db4oUnit;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Tests.Common.TA;
using Db4objects.Db4o.Tests.Common.TA.Collections;
using Db4objects.Db4o.Tests.Common.TA.Hierarchy;

namespace Db4objects.Db4o.Tests.Common.TA.Hierarchy
{
	/// <decaf.ignore.jdk11></decaf.ignore.jdk11>
	public class TransparentActivationTestCase : TransparentActivationTestCaseBase
	{
		public static void Main(string[] args)
		{
			new TransparentActivationTestCase().RunAll();
		}

		private const int Priority = 42;

		protected override void Configure(IConfiguration config)
		{
			base.Configure(config);
			config.Add(new PagedListSupport());
		}

		/// <exception cref="Exception"></exception>
		protected override void Store()
		{
			Project project = new PrioritizedProject("db4o", Priority);
			project.LogWorkDone(new UnitOfWork("ta kick-off", new DateTime(1000), new DateTime
				(2000)));
			Store(project);
		}

		public virtual void Test()
		{
			PrioritizedProject project = (PrioritizedProject)RetrieveOnlyInstance(typeof(Project
				));
			Assert.AreEqual(Priority, project.GetPriority());
			// Project.totalTimeSpent needs the UnitOfWork objects to be activated
			Assert.AreEqual(1000, project.TotalTimeSpent());
		}
	}
}
