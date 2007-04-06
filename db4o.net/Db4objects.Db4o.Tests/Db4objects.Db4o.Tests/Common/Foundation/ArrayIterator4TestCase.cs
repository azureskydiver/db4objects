using System;
using Db4oUnit;
using Db4objects.Db4o.Foundation;

namespace Db4objects.Db4o.Tests.Common.Foundation
{
	public class ArrayIterator4TestCase : ITestCase
	{
		public virtual void TestEmptyArray()
		{
			AssertExhausted(new ArrayIterator4(new object[0]));
		}

		public virtual void TestArray()
		{
			ArrayIterator4 i = new ArrayIterator4(new object[] { "foo", "bar" });
			Assert.IsTrue(i.MoveNext());
			Assert.AreEqual("foo", i.Current);
			Assert.IsTrue(i.MoveNext());
			Assert.AreEqual("bar", i.Current);
			AssertExhausted(i);
		}

		private void AssertExhausted(ArrayIterator4 i)
		{
			Assert.IsFalse(i.MoveNext());
			Assert.Expect(typeof(IndexOutOfRangeException), new _AnonymousInnerClass29(this, 
				i));
		}

		private sealed class _AnonymousInnerClass29 : ICodeBlock
		{
			public _AnonymousInnerClass29(ArrayIterator4TestCase _enclosing, ArrayIterator4 i
				)
			{
				this._enclosing = _enclosing;
				this.i = i;
			}

			public void Run()
			{
				Sharpen.Runtime.Out.WriteLine(i.Current);
			}

			private readonly ArrayIterator4TestCase _enclosing;

			private readonly ArrayIterator4 i;
		}
	}
}
