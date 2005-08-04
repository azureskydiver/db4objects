namespace com.db4o
{
	/// <exclude></exclude>
	public class DTrace
	{
		public const bool enabled = false;

		private static void breakPoint()
		{
			int placeBreakPointHere = 1;
		}

		private static object init()
		{
			return null;
		}

		private DTrace(bool enabled_, bool break_, string tag_, bool log_)
		{
		}

		private bool _enabled;

		private bool _break;

		private bool _log;

		private string _tag;

		private static long[] _rangeStart;

		private static long[] _rangeEnd;

		private static int _rangeCount;

		private static long _eventNr;

		private static long[] _breakEventNrs;

		private static int _breakEventCount;

		public static com.db4o.DTrace ADD_TO_CLASS_INDEX;

		public static com.db4o.DTrace BIND;

		public static com.db4o.DTrace CANDIDATE_READ;

		public static com.db4o.DTrace CLOSE;

		public static com.db4o.DTrace COLLECT_CHILDREN;

		public static com.db4o.DTrace COMMIT;

		public static com.db4o.DTrace CONTINUESET;

		public static com.db4o.DTrace CREATE_CANDIDATE;

		public static com.db4o.DTrace DELETE;

		public static com.db4o.DTrace DONOTINCLUDE;

		public static com.db4o.DTrace EVALUATE_SELF;

		public static com.db4o.DTrace FREE;

		public static com.db4o.DTrace FREE_ON_COMMIT;

		public static com.db4o.DTrace FREE_ON_ROLLBACK;

		public static com.db4o.DTrace GET_SLOT;

		public static com.db4o.DTrace GET_YAPOBJECT;

		public static com.db4o.DTrace ID_TREE_ADD;

		public static com.db4o.DTrace ID_TREE_REMOVE;

		public static com.db4o.DTrace JUST_SET;

		public static com.db4o.DTrace NEW_INSTANCE;

		public static com.db4o.DTrace READ_ARRAY_WRAPPER;

		public static com.db4o.DTrace READ_BYTES;

		public static com.db4o.DTrace READ_ID;

		public static com.db4o.DTrace READ_SLOT;

		public static com.db4o.DTrace REFERENCE_REMOVED;

		public static com.db4o.DTrace REGULAR_SEEK;

		public static com.db4o.DTrace REMOVE_FROM_CLASS_INDEX;

		public static com.db4o.DTrace TRANS_COMMIT;

		public static com.db4o.DTrace TRANS_DONT_DELETE;

		public static com.db4o.DTrace TRANS_DELETE;

		public static com.db4o.DTrace YAPCLASS_BY_ID;

		public static com.db4o.DTrace YAPCLASS_INIT;

		public static com.db4o.DTrace WRITE_BYTES;

		public static com.db4o.DTrace WRITE_UPDATE_DELETE_MEMBERS;

		private static readonly object forInit = init();

		private static com.db4o.DTrace all;

		private static int current;

		public virtual void log()
		{
		}

		public virtual void log(long p)
		{
		}

		public virtual void logInfo(string info)
		{
		}

		public virtual void log(long p, string info)
		{
		}

		public virtual void logLength(long start, long length)
		{
		}

		public virtual void logEnd(long start, long end)
		{
		}

		public virtual void logEnd(long start, long end, string info)
		{
		}

		public static void addRange(long pos)
		{
		}

		public static void addRangeWithLength(long start, long length)
		{
		}

		public static void addRangeWithEnd(long start, long end)
		{
		}

		private static void breakOnEvent(long eventNr)
		{
		}

		private string formatInt(long i, int len)
		{
			return null;
		}

		private string formatInt(long i)
		{
			return null;
		}

		private static void turnAllOffExceptFor(com.db4o.DTrace[] these)
		{
		}
	}
}
