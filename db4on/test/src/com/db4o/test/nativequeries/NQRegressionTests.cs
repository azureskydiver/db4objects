namespace com.db4o.test.nativequeries
{
	public class NQRegressionTests
	{
		private abstract class Base
		{
			internal int id;

			public Base(int id)
			{
				this.id = id;
			}

			public virtual int getId()
			{
				return id;
			}
		}

		private class Data : com.db4o.test.nativequeries.NQRegressionTests.Base
		{
			internal float value;

			internal string name;

			internal com.db4o.test.nativequeries.NQRegressionTests.Data prev;

			public Data(int id, float value, string name, com.db4o.test.nativequeries.NQRegressionTests.Data
				 prev) : base(id)
			{
				this.value = value;
				this.name = name;
				this.prev = prev;
			}

			public virtual float getValue()
			{
				return value;
			}

			public virtual string getName()
			{
				return name;
			}

			public virtual com.db4o.test.nativequeries.NQRegressionTests.Data getPrev()
			{
				return prev;
			}
		}

		public virtual void store()
		{
			com.db4o.test.nativequeries.NQRegressionTests.Data a = new com.db4o.test.nativequeries.NQRegressionTests.Data
				(1, 1.1f, "Aa", null);
			com.db4o.test.nativequeries.NQRegressionTests.Data b = new com.db4o.test.nativequeries.NQRegressionTests.Data
				(2, 1.1f, "Bb", a);
			com.db4o.test.nativequeries.NQRegressionTests.Data c = new com.db4o.test.nativequeries.NQRegressionTests.Data
				(3, 2.2f, "Cc", b);
			com.db4o.test.nativequeries.NQRegressionTests.Data cc = new com.db4o.test.nativequeries.NQRegressionTests.Data
				(3, 3.3f, "Cc", null);
			com.db4o.test.Tester.store(a);
			com.db4o.test.Tester.store(b);
			com.db4o.test.Tester.store(c);
			com.db4o.test.Tester.store(cc);
		}

		private abstract class ExpectingPredicate : com.db4o.query.Predicate
		{
			public abstract int expected();
		}

		private sealed class _AnonymousInnerClass72 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass72()
			{
			}

			public override int expected()
			{
				return 0;
			}

			public bool match(object candidate)
			{
				return true;
			}
		}

		private sealed class _AnonymousInnerClass79 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass79()
			{
			}

			public override int expected()
			{
				return 4;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return true;
			}
		}

		private sealed class _AnonymousInnerClass92 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass92()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id == 1;
			}
		}

		private sealed class _AnonymousInnerClass98 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass98()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id == 3;
			}
		}

		private sealed class _AnonymousInnerClass104 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass104()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.value == 1.1f;
			}
		}

		private sealed class _AnonymousInnerClass110 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass110()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.value == 3.3f;
			}
		}

		private sealed class _AnonymousInnerClass117 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass117()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.name=="Aa";
			}
		}

		private sealed class _AnonymousInnerClass123 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass123()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.name=="Cc";
			}
		}

		private sealed class _AnonymousInnerClass130 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass130()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id < 2;
			}
		}

		private sealed class _AnonymousInnerClass136 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass136()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id > 2;
			}
		}

		private sealed class _AnonymousInnerClass142 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass142()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id <= 2;
			}
		}

		private sealed class _AnonymousInnerClass148 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass148()
			{
			}

			public override int expected()
			{
				return 3;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id >= 2;
			}
		}

		private sealed class _AnonymousInnerClass155 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass155()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.value > 2.9f;
			}
		}

		private sealed class _AnonymousInnerClass162 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass162()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getPrev() != null && candidate.getPrev().getId() >= 1;
			}
		}

		private sealed class _AnonymousInnerClass168 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass168()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.getPrev() != null) && ("Bb"==candidate.getPrev().getName(
					));
			}
		}

		private sealed class _AnonymousInnerClass174 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass174()
			{
			}

			public override int expected()
			{
				return 0;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getPrev() != null && candidate.getPrev().getName()=="";
			}
		}

		private sealed class _AnonymousInnerClass181 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass181()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getId() == 2;
			}
		}

		private sealed class _AnonymousInnerClass187 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass187()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getId() < 2;
			}
		}

		private sealed class _AnonymousInnerClass193 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass193()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getId() > 2;
			}
		}

		private sealed class _AnonymousInnerClass199 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass199()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getId() <= 2;
			}
		}

		private sealed class _AnonymousInnerClass205 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass205()
			{
			}

			public override int expected()
			{
				return 3;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getId() >= 2;
			}
		}

		private sealed class _AnonymousInnerClass211 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass211()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getName()=="Cc";
			}
		}

		private sealed class _AnonymousInnerClass218 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass218()
			{
			}

			public override int expected()
			{
				return 3;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return !(candidate.id == 1);
			}
		}

		private sealed class _AnonymousInnerClass224 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass224()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return !(candidate.getId() > 2);
			}
		}

		private sealed class _AnonymousInnerClass230 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass230()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return !(candidate.getName()=="Cc");
			}
		}

		private sealed class _AnonymousInnerClass237 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass237()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id > 1) && candidate.getName()=="Cc";
			}
		}

		private sealed class _AnonymousInnerClass243 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass243()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id > 1) && (candidate.getId() <= 2);
			}
		}

		private sealed class _AnonymousInnerClass249 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass249()
			{
			}

			public override int expected()
			{
				return 0;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id > 1) && (candidate.getId() < 1);
			}
		}

		private sealed class _AnonymousInnerClass256 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass256()
			{
			}

			public override int expected()
			{
				return 3;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id == 1) || candidate.getName()=="Cc";
			}
		}

		private sealed class _AnonymousInnerClass262 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass262()
			{
			}

			public override int expected()
			{
				return 4;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id > 1) || (candidate.getId() <= 2);
			}
		}

		private sealed class _AnonymousInnerClass268 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass268()
			{
			}

			public override int expected()
			{
				return 3;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return (candidate.id <= 1) || (candidate.getId() >= 3);
			}
		}

		private sealed class _AnonymousInnerClass275 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass275()
			{
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return ((candidate.id >= 1) || candidate.getName()=="Cc") && candidate.getId
					() < 3;
			}
		}

		private sealed class _AnonymousInnerClass281 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass281()
			{
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return ((candidate.id == 2) || candidate.getId() <= 1) && !(candidate.getName()=="Bb");
			}
		}

		private sealed class _AnonymousInnerClass288 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass288()
			{
			}

			private int id = 2;

			public override int expected()
			{
				return 3;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id >= this.id;
			}
		}

		private sealed class _AnonymousInnerClass296 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass296()
			{
			}

			private string name = "Bb";

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getName()==this.name;
			}
		}

		private sealed class _AnonymousInnerClass305 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass305()
			{
			}

			private int id = 2;

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id >= this.id + 1;
			}
		}

		private sealed class _AnonymousInnerClass313 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass313()
			{
			}

			private int factor = 2;

			private int calc()
			{
				return this.factor + 1;
			}

			public override int expected()
			{
				return 2;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.id >= this.calc();
			}
		}

		private sealed class _AnonymousInnerClass325 : com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
		{
			public _AnonymousInnerClass325()
			{
			}

			private float predFactor = 2.0f;

			private float calc()
			{
				return this.predFactor * 1.1f;
			}

			public override int expected()
			{
				return 1;
			}

			public bool match(com.db4o.test.nativequeries.NQRegressionTests.Data candidate)
			{
				return candidate.getValue() == this.calc();
			}
		}

		private static com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate[] PREDICATES
			= { 
//				new _AnonymousInnerClass72(), // untyped/unconditional
//				new _AnonymousInnerClass79(), // unconditional
				new _AnonymousInnerClass92(),
				new _AnonymousInnerClass98(),
				new _AnonymousInnerClass104(), // float
				new _AnonymousInnerClass110(), // float
				new _AnonymousInnerClass117(),
				new _AnonymousInnerClass123(),
				new _AnonymousInnerClass130(),
				new _AnonymousInnerClass136(),
				new _AnonymousInnerClass142(),
				new _AnonymousInnerClass148(),
				new _AnonymousInnerClass155(), // float
				new _AnonymousInnerClass162(),
				new _AnonymousInnerClass168(),
				new _AnonymousInnerClass174(),
				new _AnonymousInnerClass181(),
				new _AnonymousInnerClass187(),
				new _AnonymousInnerClass193(),
				new _AnonymousInnerClass199(),
				new _AnonymousInnerClass205(),
				new _AnonymousInnerClass211(),
				new _AnonymousInnerClass218(),
				new _AnonymousInnerClass224(),
				new _AnonymousInnerClass230(),
				new _AnonymousInnerClass237(),
				new _AnonymousInnerClass243(),
				new _AnonymousInnerClass249(),
				new _AnonymousInnerClass256(),
				new _AnonymousInnerClass262(), // (candidate.id > 1) || (candidate.getId() <= 2)
				new _AnonymousInnerClass268(),
				new _AnonymousInnerClass275(),
				new _AnonymousInnerClass281(), // ((candidate.id == 2) || candidate.getId() <= 1) && !(candidate.getName()=="Bb")
				new _AnonymousInnerClass288(),
				new _AnonymousInnerClass296(),
//				new _AnonymousInnerClass305(), // arithmetics
//				new _AnonymousInnerClass313(), // arithmetics
//				new _AnonymousInnerClass325() // arithmetics/float
			};

		public virtual void testAll()
		{
			for (int predIdx = 0; predIdx < PREDICATES.Length; predIdx++)
			{
				com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate predicate = PREDICATES
					[predIdx];
				assertNQResult(predicate);
			}
		}

		private void assertNQResult(com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
			 filter)
		{
			com.db4o.ObjectContainer db = com.db4o.test.Tester.objectContainer();
			com.db4o.inside.query.Db4oQueryExecutionListener listener = new _AnonymousInnerClass348
				(this, filter);
			((com.db4o.YapStream)db).getNativeQueryHandler().addListener(listener);
			db.ext().configure().optimizeNativeQueries(false);
			com.db4o.ObjectSet raw = db.query(filter);
			db.ext().configure().optimizeNativeQueries(true);
			com.db4o.ObjectSet optimized = db.query(filter);
			com.db4o.test.Tester.ensureEquals(raw.size(),optimized.size());
			for(int resultIdx=0;resultIdx<raw.size();resultIdx++) 
			{
				com.db4o.test.Tester.ensureEquals(raw.ext().get(resultIdx),optimized.ext().get(resultIdx));
			}
			com.db4o.test.Tester.ensureEquals(filter.expected(), raw.size());
			((com.db4o.YapStream)db).getNativeQueryHandler().clearListeners();
		}

		private sealed class _AnonymousInnerClass348 : com.db4o.inside.query.Db4oQueryExecutionListener
		{
			public _AnonymousInnerClass348(NQRegressionTests _enclosing, com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate
				 filter)
			{
				this._enclosing = _enclosing;
				this.filter = filter;
			}

			private int run = 0;

			public void notifyQueryExecuted(object actualPredicate, string 
				msg)
			{
				com.db4o.test.Tester.ensureEquals(actualPredicate, filter);
				string expMsg = null;
				switch (this.run)
				{
					case 0:
					{
						expMsg = com.db4o.inside.query.NativeQueryHandler.UNOPTIMIZED;
						break;
					}

					case 1:
					{
						expMsg = com.db4o.inside.query.NativeQueryHandler.DYNOPTIMIZED;
						break;
					}
				}
				com.db4o.test.Tester.ensureEquals(expMsg, msg);
				this.run++;
			}

			private readonly NQRegressionTests _enclosing;

			private readonly com.db4o.test.nativequeries.NQRegressionTests.ExpectingPredicate filter;
		}
	}
}
