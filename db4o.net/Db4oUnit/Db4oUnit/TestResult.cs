namespace Db4oUnit
{
	public class TestResult : Db4oUnit.Printable
	{
		private Db4oUnit.TestFailureCollection _failures = new Db4oUnit.TestFailureCollection
			();

		private int _testCount = 0;

		private readonly Db4oUnit.Util.StopWatch _watch = new Db4oUnit.Util.StopWatch();

		private readonly System.IO.TextWriter _stdout;

		public TestResult(bool printLabels)
		{
			_stdout = printLabels ? Db4oUnit.TestPlatform.GetStdOut() : null;
		}

		public TestResult() : this(false)
		{
		}

		public virtual void TestStarted(Db4oUnit.ITest test)
		{
			++_testCount;
			Print(test.GetLabel());
		}

		public virtual void TestFailed(Db4oUnit.ITest test, System.Exception failure)
		{
			PrintFailure(failure);
			_failures.Add(new Db4oUnit.TestFailure(test, failure));
		}

		private void PrintFailure(System.Exception failure)
		{
			if (failure == null)
			{
				Print("\t!");
			}
			else
			{
				Print("\t! " + failure.Message);
			}
		}

		public virtual bool Green()
		{
			return _failures.Size() == 0;
		}

		public virtual Db4oUnit.TestFailureCollection Failures()
		{
			return _failures;
		}

		public override void Print(System.IO.TextWriter writer)
		{
			if (Green())
			{
				writer.Write("GREEN (" + _testCount + " tests) - " + ElapsedString() + "\n");
				return;
			}
			writer.Write("RED (" + _failures.Size() + " out of " + _testCount + " tests failed) - "
				 + ElapsedString() + "\n");
			_failures.Print(writer);
		}

		private string ElapsedString()
		{
			return _watch.ToString();
		}

		public virtual int Assertions()
		{
			return 0;
		}

		public virtual void RunStarted()
		{
			_watch.Start();
		}

		public virtual void RunFinished()
		{
			_watch.Stop();
		}

		private void Print(string message)
		{
			if (null != _stdout)
			{
				try
				{
					_stdout.Write(message + "\n");
					_stdout.Flush();
				}
				catch (System.IO.IOException)
				{
				}
			}
		}
	}
}
