using System;

namespace Db4oUnit
{
	public delegate void CodeBlock();

	public partial class Assert
	{
		public static Exception Expect(System.Type exception, CodeBlock block)
		{
			return Assert.Expect(exception, new DelegateCodeBlock(block));
		}

		public static Exception Expect<TException>(CodeBlock block) where TException : Exception
		{
			return Assert.Expect(typeof(TException), block);
		}

		private class DelegateCodeBlock : ICodeBlock
		{
			private readonly CodeBlock _block;

			public DelegateCodeBlock(CodeBlock block)
			{
				_block = block;
			}

			public void Run()
			{
				_block();
			}
		}
	}
}
