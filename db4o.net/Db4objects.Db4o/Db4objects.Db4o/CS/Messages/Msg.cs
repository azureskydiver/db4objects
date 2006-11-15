namespace Db4objects.Db4o.CS.Messages
{
	/// <summary>Messages for Client/Server Communication</summary>
	public class Msg : Sharpen.Lang.ICloneable
	{
		internal static int _idGenerator = 1;

		private static Db4objects.Db4o.CS.Messages.Msg[] _messages = new Db4objects.Db4o.CS.Messages.Msg
			[60];

		internal int _msgID;

		internal string _name;

		internal Db4objects.Db4o.Transaction _trans;

		public static readonly Db4objects.Db4o.CS.Messages.MsgD CLASS_NAME_FOR_ID = new Db4objects.Db4o.CS.Messages.MClassNameForID
			();

		public static readonly Db4objects.Db4o.CS.Messages.Msg CLOSE = new Db4objects.Db4o.CS.Messages.Msg
			("CLOSE");

		public static readonly Db4objects.Db4o.CS.Messages.Msg COMMIT = new Db4objects.Db4o.CS.Messages.MCommit
			();

		public static readonly Db4objects.Db4o.CS.Messages.Msg COMMIT_OK = new Db4objects.Db4o.CS.Messages.MCommitOK
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD CREATE_CLASS = new Db4objects.Db4o.CS.Messages.MCreateClass
			();

		public static readonly Db4objects.Db4o.CS.Messages.Msg CURRENT_VERSION = new Db4objects.Db4o.CS.Messages.Msg
			("VERSION");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD DELETE = new Db4objects.Db4o.CS.Messages.MDelete
			();

		public static readonly Db4objects.Db4o.CS.Messages.Msg ERROR = new Db4objects.Db4o.CS.Messages.Msg
			("ERROR");

		public static readonly Db4objects.Db4o.CS.Messages.Msg FAILED = new Db4objects.Db4o.CS.Messages.Msg
			("FAILED");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD GET_ALL = new Db4objects.Db4o.CS.Messages.MGetAll
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD GET_CLASSES = new Db4objects.Db4o.CS.Messages.MGetClasses
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD GET_INTERNAL_IDS = new Db4objects.Db4o.CS.Messages.MGetInternalIDs
			();

		public static readonly Db4objects.Db4o.CS.Messages.Msg GET_THREAD_ID = new Db4objects.Db4o.CS.Messages.Msg
			("GET_THREAD_ID");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD ID_LIST = new Db4objects.Db4o.CS.Messages.MsgD
			("ID_LIST");

		public static readonly Db4objects.Db4o.CS.Messages.Msg IDENTITY = new Db4objects.Db4o.CS.Messages.Msg
			("IDENTITY");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD LENGTH = new Db4objects.Db4o.CS.Messages.MsgD
			("LENGTH");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD LOGIN = new Db4objects.Db4o.CS.Messages.MsgD
			("LOGIN");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD LOGIN_OK = new Db4objects.Db4o.CS.Messages.MsgD
			("LOGIN_OK");

		public static readonly Db4objects.Db4o.CS.Messages.Msg NULL = new Db4objects.Db4o.CS.Messages.Msg
			("NULL");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD OBJECT_BY_UUID = new Db4objects.Db4o.CS.Messages.MObjectByUuid
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgObject OBJECT_TO_CLIENT = new 
			Db4objects.Db4o.CS.Messages.MsgObject();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD OBJECTSET_FETCH = new Db4objects.Db4o.CS.Messages.MObjectSetFetch
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD OBJECTSET_FINALIZED = new 
			Db4objects.Db4o.CS.Messages.MsgD("OBJECTSET_FINALIZED");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD OBJECTSET_GET_ID = new Db4objects.Db4o.CS.Messages.MObjectSetGetId
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD OBJECTSET_INDEXOF = new Db4objects.Db4o.CS.Messages.MObjectSetIndexOf
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD OBJECTSET_RESET = new Db4objects.Db4o.CS.Messages.MObjectSetReset
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD OBJECTSET_SIZE = new Db4objects.Db4o.CS.Messages.MObjectSetSize
			();

		public static readonly Db4objects.Db4o.CS.Messages.Msg OK = new Db4objects.Db4o.CS.Messages.Msg
			("OK");

		public static readonly Db4objects.Db4o.CS.Messages.Msg PING = new Db4objects.Db4o.CS.Messages.Msg
			("PING");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD PREFETCH_IDS = new Db4objects.Db4o.CS.Messages.MPrefetchIDs
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgObject QUERY_EXECUTE = new 
			Db4objects.Db4o.CS.Messages.MQueryExecute();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD QUERY_RESULT = new Db4objects.Db4o.CS.Messages.MsgD
			("QUERY_RESULT");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD RAISE_VERSION = new Db4objects.Db4o.CS.Messages.MsgD
			("RAISE_VERSION");

		public static readonly Db4objects.Db4o.CS.Messages.MsgBlob READ_BLOB = new Db4objects.Db4o.CS.Messages.MReadBlob
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD READ_BYTES = new Db4objects.Db4o.CS.Messages.MReadBytes
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD READ_MULTIPLE_OBJECTS = new 
			Db4objects.Db4o.CS.Messages.MReadMultipleObjects();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD READ_OBJECT = new Db4objects.Db4o.CS.Messages.MReadObject
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD RELEASE_SEMAPHORE = new Db4objects.Db4o.CS.Messages.MReleaseSemaphore
			();

		public static readonly Db4objects.Db4o.CS.Messages.Msg ROLLBACK = new Db4objects.Db4o.CS.Messages.MRollback
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD SET_SEMAPHORE = new Db4objects.Db4o.CS.Messages.MSetSemaphore
			();

		public static readonly Db4objects.Db4o.CS.Messages.Msg SUCCESS = new Db4objects.Db4o.CS.Messages.Msg
			("SUCCESS");

		public static readonly Db4objects.Db4o.CS.Messages.MsgD SWITCH_TO_FILE = new Db4objects.Db4o.CS.Messages.MsgD
			("SWITCH_F");

		public static readonly Db4objects.Db4o.CS.Messages.Msg SWITCH_TO_MAIN_FILE = new 
			Db4objects.Db4o.CS.Messages.Msg("SWITCH_M");

		public static readonly Db4objects.Db4o.CS.Messages.Msg TA_BEGIN_END_SET = new Db4objects.Db4o.CS.Messages.MTaBeginEndSet
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD TA_DELETE = new Db4objects.Db4o.CS.Messages.MTaDelete
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD TA_DONT_DELETE = new Db4objects.Db4o.CS.Messages.MTaDontDelete
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD TA_IS_DELETED = new Db4objects.Db4o.CS.Messages.MTaIsDeleted
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD USER_MESSAGE = new Db4objects.Db4o.CS.Messages.MUserMessage
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD USE_TRANSACTION = new Db4objects.Db4o.CS.Messages.MUseTransaction
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgBlob WRITE_BLOB = new Db4objects.Db4o.CS.Messages.MWriteBlob
			();

		public static readonly Db4objects.Db4o.CS.Messages.MWriteNew WRITE_NEW = new Db4objects.Db4o.CS.Messages.MWriteNew
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgObject WRITE_UPDATE = new Db4objects.Db4o.CS.Messages.MWriteUpdate
			();

		public static readonly Db4objects.Db4o.CS.Messages.MsgD WRITE_UPDATE_DELETE_MEMBERS
			 = new Db4objects.Db4o.CS.Messages.MWriteUpdateDeleteMembers();

		internal Msg()
		{
			_msgID = _idGenerator++;
			_messages[_msgID] = this;
		}

		internal Msg(string aName) : this()
		{
			_name = aName;
		}

		public Db4objects.Db4o.CS.Messages.Msg Clone(Db4objects.Db4o.Transaction a_trans)
		{
			Db4objects.Db4o.CS.Messages.Msg msg = null;
			try
			{
				msg = (Db4objects.Db4o.CS.Messages.Msg)MemberwiseClone();
				msg._trans = a_trans;
			}
			catch (Sharpen.Lang.CloneNotSupportedException)
			{
			}
			return msg;
		}

		public sealed override bool Equals(object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null || obj.GetType() != this.GetType())
			{
				return false;
			}
			return _msgID == ((Db4objects.Db4o.CS.Messages.Msg)obj)._msgID;
		}

		internal virtual void FakePayLoad(Db4objects.Db4o.Transaction a_trans)
		{
			_trans = a_trans;
		}

		/// <summary>
		/// dummy method to allow clean override handling
		/// without casting
		/// </summary>
		public virtual Db4objects.Db4o.YapReader GetByteLoad()
		{
			return null;
		}

		internal string GetName()
		{
			if (_name == null)
			{
				return GetType().FullName;
			}
			return _name;
		}

		protected virtual Db4objects.Db4o.Transaction Transaction()
		{
			return _trans;
		}

		protected virtual Db4objects.Db4o.YapFile File()
		{
			return (Db4objects.Db4o.YapFile)Stream();
		}

		protected virtual Db4objects.Db4o.YapStream Stream()
		{
			return Transaction().Stream();
		}

		protected virtual object StreamLock()
		{
			return Stream().Lock();
		}

		protected virtual Db4objects.Db4o.Config4Impl Config()
		{
			return Stream().Config();
		}

		/// <summary>server side execution</summary>
		/// <param name="serverThread">TODO</param>
		public virtual bool ProcessAtServer(Db4objects.Db4o.CS.YapServerThread serverThread
			)
		{
			return false;
		}

		public static Db4objects.Db4o.CS.Messages.Msg ReadMessage(Db4objects.Db4o.Transaction
			 a_trans, Db4objects.Db4o.Foundation.Network.IYapSocket sock)
		{
			Db4objects.Db4o.YapWriter reader = new Db4objects.Db4o.YapWriter(a_trans, Db4objects.Db4o.YapConst
				.MESSAGE_LENGTH);
			if (!reader.Read(sock))
			{
				return null;
			}
			Db4objects.Db4o.CS.Messages.Msg message = _messages[reader.ReadInt()].ReadPayLoad
				(a_trans, sock, reader);
			return message;
		}

		internal virtual Db4objects.Db4o.CS.Messages.Msg ReadPayLoad(Db4objects.Db4o.Transaction
			 a_trans, Db4objects.Db4o.Foundation.Network.IYapSocket sock, Db4objects.Db4o.YapReader
			 reader)
		{
			a_trans = CheckParentTransaction(a_trans, reader);
			return Clone(a_trans);
		}

		protected virtual Db4objects.Db4o.Transaction CheckParentTransaction(Db4objects.Db4o.Transaction
			 a_trans, Db4objects.Db4o.YapReader reader)
		{
			if (reader.ReadByte() == Db4objects.Db4o.YapConst.SYSTEM_TRANS && a_trans.ParentTransaction
				() != null)
			{
				return a_trans.ParentTransaction();
			}
			return a_trans;
		}

		internal void SetTransaction(Db4objects.Db4o.Transaction aTrans)
		{
			_trans = aTrans;
		}

		public sealed override string ToString()
		{
			return GetName();
		}

		public void Write(Db4objects.Db4o.YapStream stream, Db4objects.Db4o.Foundation.Network.IYapSocket
			 sock)
		{
			lock (sock)
			{
				try
				{
					sock.Write(PayLoad()._buffer);
					sock.Flush();
				}
				catch
				{
				}
			}
		}

		public virtual Db4objects.Db4o.YapWriter PayLoad()
		{
			Db4objects.Db4o.YapWriter writer = new Db4objects.Db4o.YapWriter(Transaction(), Db4objects.Db4o.YapConst
				.MESSAGE_LENGTH);
			writer.WriteInt(_msgID);
			return writer;
		}
	}
}
