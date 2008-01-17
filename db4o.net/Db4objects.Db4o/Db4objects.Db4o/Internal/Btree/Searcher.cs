/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.Internal.Btree;

namespace Db4objects.Db4o.Internal.Btree
{
	/// <exclude></exclude>
	public class Searcher
	{
		private int _lower;

		private int _upper;

		private int _cursor;

		private int _cmp;

		private readonly SearchTarget _target;

		private readonly int _count;

		public Searcher(SearchTarget target, int count)
		{
			if (count < 0)
			{
				throw new ArgumentException();
			}
			_target = target;
			_count = count;
			_cmp = -1;
			if (count == 0)
			{
				Complete();
				return;
			}
			_cursor = -1;
			_upper = count - 1;
			AdjustCursor();
		}

		private void AdjustBounds()
		{
			if (_cmp > 0)
			{
				_upper = _cursor - 1;
				if (_upper < _lower)
				{
					_upper = _lower;
				}
				return;
			}
			if (_cmp < 0)
			{
				if (_lower == _cursor && _lower < _upper)
				{
					_lower++;
				}
				else
				{
					_lower = _cursor;
				}
				return;
			}
			if (_target == SearchTarget.Any)
			{
				_lower = _cursor;
				_upper = _cursor;
			}
			else
			{
				if (_target == SearchTarget.Highest)
				{
					_lower = _cursor;
				}
				else
				{
					if (_target == SearchTarget.Lowest)
					{
						_upper = _cursor;
					}
					else
					{
						throw new InvalidOperationException("Unknown target");
					}
				}
			}
		}

		private void AdjustCursor()
		{
			int oldCursor = _cursor;
			if (_upper - _lower <= 1)
			{
				if ((_target == SearchTarget.Lowest) && (_cmp == 0))
				{
					_cursor = _lower;
				}
				else
				{
					_cursor = _upper;
				}
			}
			else
			{
				_cursor = _lower + ((_upper - _lower) / 2);
			}
			if (_cursor == oldCursor)
			{
				Complete();
			}
		}

		public virtual bool AfterLast()
		{
			if (_count == 0)
			{
				return false;
			}
			// _cursor is 0: not after last
			return (_cursor == _count - 1) && _cmp < 0;
		}

		public virtual bool BeforeFirst()
		{
			return (_cursor == 0) && (_cmp > 0);
		}

		private void Complete()
		{
			_upper = -2;
		}

		public virtual int Count()
		{
			return _count;
		}

		public virtual int Cursor()
		{
			return _cursor;
		}

		public virtual bool FoundMatch()
		{
			return _cmp == 0;
		}

		public virtual bool Incomplete()
		{
			return _upper >= _lower;
		}

		public virtual void MoveForward()
		{
			_cursor++;
		}

		public virtual void ResultIs(int cmp)
		{
			_cmp = cmp;
			AdjustBounds();
			AdjustCursor();
		}

		public virtual bool IsGreater()
		{
			return _cmp < 0;
		}
	}
}
