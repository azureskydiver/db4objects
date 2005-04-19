/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <exclude></exclude>
	internal class QResultClient : com.db4o.QResult
	{
		private object[] i_prefetched = new object[com.db4o.YapConst.PREFETCH_OBJECT_COUNT
			];

		private int i_remainingObjects;

		private int i_prefetchCount = com.db4o.YapConst.PREFETCH_OBJECT_COUNT;

		internal QResultClient(com.db4o.Transaction a_ta) : base(a_ta)
		{
		}

		internal QResultClient(com.db4o.Transaction a_ta, int initialSize) : base(a_ta, initialSize
			)
		{
		}

		public override bool hasNext()
		{
			lock (streamLock())
			{
				if (i_remainingObjects > 0)
				{
					return true;
				}
				return base.hasNext();
			}
		}

		public override object next()
		{
			lock (streamLock())
			{
				com.db4o.YapClient stream = (com.db4o.YapClient)i_trans.i_stream;
				stream.checkClosed();
				if (i_remainingObjects < 1)
				{
					if (base.hasNext())
					{
						i_remainingObjects = (stream).prefetchObjects(this, i_prefetched, i_prefetchCount
							);
					}
				}
				i_remainingObjects--;
				if (i_remainingObjects < 0)
				{
					return null;
				}
				if (i_prefetched[i_remainingObjects] == null)
				{
					return next();
				}
				return activate(i_prefetched[i_remainingObjects]);
			}
		}

		public override void reset()
		{
			lock (streamLock())
			{
				i_remainingObjects = 0;
				base.reset();
			}
		}
	}
}
