namespace com.db4o
{
	/// <exclude></exclude>
	public class YapObject : com.db4o.YapMeta, com.db4o.ext.ObjectInfo
	{
		private com.db4o.YapClass i_yapClass;

		internal object i_object;

		internal com.db4o.VirtualAttributes i_virtualAttributes;

		protected com.db4o.YapObject id_preceding;

		private com.db4o.YapObject id_subsequent;

		private int id_size;

		private com.db4o.YapObject hc_preceding;

		private com.db4o.YapObject hc_subsequent;

		private int hc_size;

		private int hc_code;

		public YapObject()
		{
		}

		public YapObject(int a_id)
		{
			i_id = a_id;
		}

		internal YapObject(com.db4o.YapClass a_yapClass, int a_id)
		{
			i_yapClass = a_yapClass;
			i_id = a_id;
		}

		internal virtual void Activate(com.db4o.Transaction ta, object a_object, int a_depth
			, bool a_refresh)
		{
			Activate1(ta, a_object, a_depth, a_refresh);
			ta.Stream().Activate3CheckStill(ta);
		}

		internal virtual void Activate1(com.db4o.Transaction ta, object a_object, int a_depth
			, bool a_refresh)
		{
			if (a_object is com.db4o.Db4oTypeImpl)
			{
				a_depth = ((com.db4o.Db4oTypeImpl)a_object).AdjustReadDepth(a_depth);
			}
			if (a_depth > 0)
			{
				com.db4o.YapStream stream = ta.Stream();
				if (a_refresh)
				{
					LogActivation(stream, "refresh");
				}
				else
				{
					if (IsActive())
					{
						if (a_object != null)
						{
							if (a_depth > 1)
							{
								if (i_yapClass.i_config != null)
								{
									a_depth = i_yapClass.i_config.AdjustActivationDepth(a_depth);
								}
								i_yapClass.ActivateFields(ta, a_object, a_depth);
							}
							return;
						}
					}
					LogActivation(stream, "activate");
				}
				Read(ta, null, a_object, a_depth, com.db4o.YapConst.ADD_MEMBERS_TO_ID_TREE_ONLY, 
					false);
			}
		}

		private void LogActivation(com.db4o.YapStream stream, string @event)
		{
			LogEvent(stream, @event, com.db4o.YapConst.ACTIVATION);
		}

		private void LogEvent(com.db4o.YapStream stream, string @event, int level)
		{
			if (stream.ConfigImpl().MessageLevel() > level)
			{
				stream.Message(string.Empty + GetID() + " " + @event + " " + i_yapClass.GetName()
					);
			}
		}

		internal void AddToIDTree(com.db4o.YapStream a_stream)
		{
			if (!(i_yapClass is com.db4o.YapClassPrimitive))
			{
				a_stream.IdTreeAdd(this);
			}
		}

		/// <summary>return false if class not completely initialized, otherwise true *</summary>
		internal virtual bool ContinueSet(com.db4o.Transaction a_trans, int a_updateDepth
			)
		{
			if (BitIsTrue(com.db4o.YapConst.CONTINUE))
			{
				if (!i_yapClass.StateOKAndAncestors())
				{
					return false;
				}
				BitFalse(com.db4o.YapConst.CONTINUE);
				com.db4o.YapWriter writer = com.db4o.inside.marshall.MarshallerFamily.Current()._object
					.MarshallNew(a_trans, this, a_updateDepth);
				com.db4o.YapStream stream = a_trans.Stream();
				stream.WriteNew(i_yapClass, writer);
				object obj = GetObject();
				ObjectOnNew(stream, obj);
				if (!i_yapClass.IsPrimitive())
				{
					i_object = stream.i_references.CreateYapRef(this, obj);
				}
				SetStateClean();
				EndProcessing();
			}
			return true;
		}

		private void ObjectOnNew(com.db4o.YapStream stream, object obj)
		{
			stream.Callbacks().ObjectOnNew(obj);
			i_yapClass.DispatchEvent(stream, obj, com.db4o.EventDispatcher.NEW);
		}

		internal virtual void Deactivate(com.db4o.Transaction a_trans, int a_depth)
		{
			if (a_depth > 0)
			{
				object obj = GetObject();
				if (obj != null)
				{
					if (obj is com.db4o.Db4oTypeImpl)
					{
						((com.db4o.Db4oTypeImpl)obj).PreDeactivate();
					}
					com.db4o.YapStream stream = a_trans.Stream();
					LogActivation(stream, "deactivate");
					SetStateDeactivated();
					i_yapClass.Deactivate(a_trans, obj, a_depth);
				}
			}
		}

		public override byte GetIdentifier()
		{
			return com.db4o.YapConst.YAPOBJECT;
		}

		public virtual object GetObject()
		{
			if (com.db4o.Platform4.HasWeakReferences())
			{
				return com.db4o.Platform4.GetYapRefObject(i_object);
			}
			return i_object;
		}

		public virtual com.db4o.YapStream GetStream()
		{
			if (i_yapClass == null)
			{
				return null;
			}
			return i_yapClass.GetStream();
		}

		public virtual com.db4o.Transaction GetTrans()
		{
			com.db4o.YapStream stream = GetStream();
			if (stream != null)
			{
				return stream.GetTransaction();
			}
			return null;
		}

		public virtual com.db4o.ext.Db4oUUID GetUUID()
		{
			com.db4o.VirtualAttributes va = VirtualAttributes(GetTrans());
			if (va != null && va.i_database != null)
			{
				return new com.db4o.ext.Db4oUUID(va.i_uuid, va.i_database.i_signature);
			}
			return null;
		}

		public virtual long GetVersion()
		{
			com.db4o.VirtualAttributes va = VirtualAttributes(GetTrans());
			if (va == null)
			{
				return 0;
			}
			return va.i_version;
		}

		public virtual com.db4o.YapClass GetYapClass()
		{
			return i_yapClass;
		}

		public override int OwnLength()
		{
			throw com.db4o.inside.Exceptions4.ShouldNeverBeCalled();
		}

		internal object Read(com.db4o.Transaction ta, com.db4o.YapWriter a_reader, object
			 a_object, int a_instantiationDepth, int addToIDTree, bool checkIDTree)
		{
			if (BeginProcessing())
			{
				com.db4o.YapStream stream = ta.Stream();
				if (a_reader == null)
				{
					a_reader = stream.ReadWriterByID(ta, GetID());
				}
				if (a_reader != null)
				{
					com.db4o.inside.marshall.ObjectHeader header = new com.db4o.inside.marshall.ObjectHeader
						(stream, a_reader);
					i_yapClass = header.YapClass();
					if (i_yapClass == null)
					{
						return null;
					}
					if (checkIDTree)
					{
						object objectInCacheFromClassCreation = stream.ObjectForIDFromCache(GetID());
						if (objectInCacheFromClassCreation != null)
						{
							return objectInCacheFromClassCreation;
						}
					}
					a_reader.SetInstantiationDepth(a_instantiationDepth);
					a_reader.SetUpdateDepth(addToIDTree);
					if (addToIDTree == com.db4o.YapConst.TRANSIENT)
					{
						a_object = i_yapClass.InstantiateTransient(this, a_object, header._marshallerFamily
							, header._headerAttributes, a_reader);
					}
					else
					{
						a_object = i_yapClass.Instantiate(this, a_object, header._marshallerFamily, header
							._headerAttributes, a_reader, addToIDTree == com.db4o.YapConst.ADD_TO_ID_TREE);
					}
				}
				EndProcessing();
			}
			return a_object;
		}

		public object ReadPrefetch(com.db4o.YapStream a_stream, com.db4o.YapWriter a_reader
			)
		{
			object readObject = null;
			if (BeginProcessing())
			{
				com.db4o.inside.marshall.ObjectHeader header = new com.db4o.inside.marshall.ObjectHeader
					(a_stream, a_reader);
				i_yapClass = header.YapClass();
				if (i_yapClass == null)
				{
					return null;
				}
				a_reader.SetInstantiationDepth(i_yapClass.ConfigOrAncestorConfig() == null ? 1 : 
					0);
				readObject = i_yapClass.Instantiate(this, GetObject(), header._marshallerFamily, 
					header._headerAttributes, a_reader, true);
				EndProcessing();
			}
			return readObject;
		}

		public sealed override void ReadThis(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_bytes)
		{
		}

		internal virtual void SetObjectWeak(com.db4o.YapStream a_stream, object a_object)
		{
			if (a_stream.i_references._weak)
			{
				if (i_object != null)
				{
					com.db4o.Platform4.KillYapRef(i_object);
				}
				i_object = com.db4o.Platform4.CreateYapRef(a_stream.i_references._queue, this, a_object
					);
			}
			else
			{
				i_object = a_object;
			}
		}

		public virtual void SetObject(object a_object)
		{
			i_object = a_object;
		}

		/// <summary>
		/// return true for complex objects to instruct YapStream to add to lookup trees
		/// and to perform delayed storage through call to continueset further up the stack.
		/// </summary>
		/// <remarks>
		/// return true for complex objects to instruct YapStream to add to lookup trees
		/// and to perform delayed storage through call to continueset further up the stack.
		/// </remarks>
		internal virtual bool Store(com.db4o.Transaction a_trans, com.db4o.YapClass a_yapClass
			, object a_object)
		{
			i_object = a_object;
			WriteObjectBegin();
			com.db4o.YapStream stream = a_trans.Stream();
			i_yapClass = a_yapClass;
			SetID(stream.NewUserObject());
			BeginProcessing();
			BitTrue(com.db4o.YapConst.CONTINUE);
			return true;
		}

		public virtual com.db4o.VirtualAttributes VirtualAttributes(com.db4o.Transaction 
			a_trans)
		{
			if (a_trans == null)
			{
				return i_virtualAttributes;
			}
			if (i_virtualAttributes == null)
			{
				if (i_yapClass.HasVirtualAttributes())
				{
					i_virtualAttributes = new com.db4o.VirtualAttributes();
					i_yapClass.ReadVirtualAttributes(a_trans, this);
				}
			}
			else
			{
				if (!i_virtualAttributes.SuppliesUUID())
				{
					if (i_yapClass.HasVirtualAttributes())
					{
						i_yapClass.ReadVirtualAttributes(a_trans, this);
					}
				}
			}
			return i_virtualAttributes;
		}

		public virtual void SetVirtualAttributes(com.db4o.VirtualAttributes at)
		{
			i_virtualAttributes = at;
		}

		public override void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			)
		{
		}

		internal virtual void WriteUpdate(com.db4o.Transaction a_trans, int a_updatedepth
			)
		{
			ContinueSet(a_trans, a_updatedepth);
			if (BeginProcessing())
			{
				object obj = GetObject();
				if (ObjectCanUpdate(a_trans.Stream(), obj))
				{
					if ((!IsActive()) || obj == null)
					{
						EndProcessing();
						return;
					}
					LogEvent(a_trans.Stream(), "update", com.db4o.YapConst.STATE);
					SetStateClean();
					a_trans.WriteUpdateDeleteMembers(GetID(), i_yapClass, a_trans.Stream().i_handlers
						.ArrayType(obj), 0);
					com.db4o.inside.marshall.MarshallerFamily.Current()._object.MarshallUpdate(a_trans
						, a_updatedepth, this, obj);
				}
				else
				{
					EndProcessing();
				}
			}
		}

		private bool ObjectCanUpdate(com.db4o.YapStream stream, object obj)
		{
			return stream.Callbacks().ObjectCanUpdate(obj) && i_yapClass.DispatchEvent(stream
				, obj, com.db4o.EventDispatcher.CAN_UPDATE);
		}

		/// <summary>HCTREE ****</summary>
		public virtual com.db4o.YapObject Hc_add(com.db4o.YapObject a_add)
		{
			object obj = a_add.GetObject();
			if (obj != null)
			{
				a_add.Hc_init(obj);
				return Hc_add1(a_add);
			}
			return this;
		}

		public virtual void Hc_init(object obj)
		{
			hc_preceding = null;
			hc_subsequent = null;
			hc_size = 1;
			hc_code = Hc_getCode(obj);
		}

		private com.db4o.YapObject Hc_add1(com.db4o.YapObject a_new)
		{
			int cmp = Hc_compare(a_new);
			if (cmp < 0)
			{
				if (hc_preceding == null)
				{
					hc_preceding = a_new;
					hc_size++;
				}
				else
				{
					hc_preceding = hc_preceding.Hc_add1(a_new);
					if (hc_subsequent == null)
					{
						return Hc_rotateRight();
					}
					return Hc_balance();
				}
			}
			else
			{
				if (hc_subsequent == null)
				{
					hc_subsequent = a_new;
					hc_size++;
				}
				else
				{
					hc_subsequent = hc_subsequent.Hc_add1(a_new);
					if (hc_preceding == null)
					{
						return Hc_rotateLeft();
					}
					return Hc_balance();
				}
			}
			return this;
		}

		private com.db4o.YapObject Hc_balance()
		{
			int cmp = hc_subsequent.hc_size - hc_preceding.hc_size;
			if (cmp < -2)
			{
				return Hc_rotateRight();
			}
			else
			{
				if (cmp > 2)
				{
					return Hc_rotateLeft();
				}
				else
				{
					hc_size = hc_preceding.hc_size + hc_subsequent.hc_size + 1;
					return this;
				}
			}
		}

		private void Hc_calculateSize()
		{
			if (hc_preceding == null)
			{
				if (hc_subsequent == null)
				{
					hc_size = 1;
				}
				else
				{
					hc_size = hc_subsequent.hc_size + 1;
				}
			}
			else
			{
				if (hc_subsequent == null)
				{
					hc_size = hc_preceding.hc_size + 1;
				}
				else
				{
					hc_size = hc_preceding.hc_size + hc_subsequent.hc_size + 1;
				}
			}
		}

		private int Hc_compare(com.db4o.YapObject a_to)
		{
			int cmp = a_to.hc_code - hc_code;
			if (cmp == 0)
			{
				cmp = a_to.i_id - i_id;
			}
			return cmp;
		}

		public virtual com.db4o.YapObject Hc_find(object obj)
		{
			return Hc_find(Hc_getCode(obj), obj);
		}

		private com.db4o.YapObject Hc_find(int a_id, object obj)
		{
			int cmp = a_id - hc_code;
			if (cmp < 0)
			{
				if (hc_preceding != null)
				{
					return hc_preceding.Hc_find(a_id, obj);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (hc_subsequent != null)
					{
						return hc_subsequent.Hc_find(a_id, obj);
					}
				}
				else
				{
					if (obj == GetObject())
					{
						return this;
					}
					if (hc_preceding != null)
					{
						com.db4o.YapObject inPreceding = hc_preceding.Hc_find(a_id, obj);
						if (inPreceding != null)
						{
							return inPreceding;
						}
					}
					if (hc_subsequent != null)
					{
						return hc_subsequent.Hc_find(a_id, obj);
					}
				}
			}
			return null;
		}

		private int Hc_getCode(object obj)
		{
			int hcode = j4o.lang.JavaSystem.IdentityHashCode(obj);
			if (hcode < 0)
			{
				hcode = ~hcode;
			}
			return hcode;
		}

		private com.db4o.YapObject Hc_rotateLeft()
		{
			com.db4o.YapObject tree = hc_subsequent;
			hc_subsequent = tree.hc_preceding;
			Hc_calculateSize();
			tree.hc_preceding = this;
			if (tree.hc_subsequent == null)
			{
				tree.hc_size = 1 + hc_size;
			}
			else
			{
				tree.hc_size = 1 + hc_size + tree.hc_subsequent.hc_size;
			}
			return tree;
		}

		private com.db4o.YapObject Hc_rotateRight()
		{
			com.db4o.YapObject tree = hc_preceding;
			hc_preceding = tree.hc_subsequent;
			Hc_calculateSize();
			tree.hc_subsequent = this;
			if (tree.hc_preceding == null)
			{
				tree.hc_size = 1 + hc_size;
			}
			else
			{
				tree.hc_size = 1 + hc_size + tree.hc_preceding.hc_size;
			}
			return tree;
		}

		private com.db4o.YapObject Hc_rotateSmallestUp()
		{
			if (hc_preceding != null)
			{
				hc_preceding = hc_preceding.Hc_rotateSmallestUp();
				return Hc_rotateRight();
			}
			return this;
		}

		internal virtual com.db4o.YapObject Hc_remove(com.db4o.YapObject a_find)
		{
			if (this == a_find)
			{
				return Hc_remove();
			}
			int cmp = Hc_compare(a_find);
			if (cmp <= 0)
			{
				if (hc_preceding != null)
				{
					hc_preceding = hc_preceding.Hc_remove(a_find);
				}
			}
			if (cmp >= 0)
			{
				if (hc_subsequent != null)
				{
					hc_subsequent = hc_subsequent.Hc_remove(a_find);
				}
			}
			Hc_calculateSize();
			return this;
		}

		public virtual void Hc_traverse(com.db4o.foundation.Visitor4 visitor)
		{
			if (hc_preceding != null)
			{
				hc_preceding.Hc_traverse(visitor);
			}
			visitor.Visit(this);
			if (hc_subsequent != null)
			{
				hc_subsequent.Hc_traverse(visitor);
			}
		}

		private com.db4o.YapObject Hc_remove()
		{
			if (hc_subsequent != null && hc_preceding != null)
			{
				hc_subsequent = hc_subsequent.Hc_rotateSmallestUp();
				hc_subsequent.hc_preceding = hc_preceding;
				hc_subsequent.Hc_calculateSize();
				return hc_subsequent;
			}
			if (hc_subsequent != null)
			{
				return hc_subsequent;
			}
			return hc_preceding;
		}

		/// <summary>IDTREE ****</summary>
		internal virtual com.db4o.YapObject Id_add(com.db4o.YapObject a_add)
		{
			a_add.id_preceding = null;
			a_add.id_subsequent = null;
			a_add.id_size = 1;
			return Id_add1(a_add);
		}

		private com.db4o.YapObject Id_add1(com.db4o.YapObject a_new)
		{
			int cmp = a_new.i_id - i_id;
			if (cmp < 0)
			{
				if (id_preceding == null)
				{
					id_preceding = a_new;
					id_size++;
				}
				else
				{
					id_preceding = id_preceding.Id_add1(a_new);
					if (id_subsequent == null)
					{
						return Id_rotateRight();
					}
					return Id_balance();
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (id_subsequent == null)
					{
						id_subsequent = a_new;
						id_size++;
					}
					else
					{
						id_subsequent = id_subsequent.Id_add1(a_new);
						if (id_preceding == null)
						{
							return Id_rotateLeft();
						}
						return Id_balance();
					}
				}
			}
			return this;
		}

		private com.db4o.YapObject Id_balance()
		{
			int cmp = id_subsequent.id_size - id_preceding.id_size;
			if (cmp < -2)
			{
				return Id_rotateRight();
			}
			else
			{
				if (cmp > 2)
				{
					return Id_rotateLeft();
				}
				else
				{
					id_size = id_preceding.id_size + id_subsequent.id_size + 1;
					return this;
				}
			}
		}

		private void Id_calculateSize()
		{
			if (id_preceding == null)
			{
				if (id_subsequent == null)
				{
					id_size = 1;
				}
				else
				{
					id_size = id_subsequent.id_size + 1;
				}
			}
			else
			{
				if (id_subsequent == null)
				{
					id_size = id_preceding.id_size + 1;
				}
				else
				{
					id_size = id_preceding.id_size + id_subsequent.id_size + 1;
				}
			}
		}

		internal virtual com.db4o.YapObject Id_find(int a_id)
		{
			int cmp = a_id - i_id;
			if (cmp > 0)
			{
				if (id_subsequent != null)
				{
					return id_subsequent.Id_find(a_id);
				}
			}
			else
			{
				if (cmp < 0)
				{
					if (id_preceding != null)
					{
						return id_preceding.Id_find(a_id);
					}
				}
				else
				{
					return this;
				}
			}
			return null;
		}

		private com.db4o.YapObject Id_rotateLeft()
		{
			com.db4o.YapObject tree = id_subsequent;
			id_subsequent = tree.id_preceding;
			Id_calculateSize();
			tree.id_preceding = this;
			if (tree.id_subsequent == null)
			{
				tree.id_size = id_size + 1;
			}
			else
			{
				tree.id_size = id_size + 1 + tree.id_subsequent.id_size;
			}
			return tree;
		}

		private com.db4o.YapObject Id_rotateRight()
		{
			com.db4o.YapObject tree = id_preceding;
			id_preceding = tree.id_subsequent;
			Id_calculateSize();
			tree.id_subsequent = this;
			if (tree.id_preceding == null)
			{
				tree.id_size = id_size + 1;
			}
			else
			{
				tree.id_size = id_size + 1 + tree.id_preceding.id_size;
			}
			return tree;
		}

		private com.db4o.YapObject Id_rotateSmallestUp()
		{
			if (id_preceding != null)
			{
				id_preceding = id_preceding.Id_rotateSmallestUp();
				return Id_rotateRight();
			}
			return this;
		}

		internal virtual com.db4o.YapObject Id_remove(int a_id)
		{
			int cmp = a_id - i_id;
			if (cmp < 0)
			{
				if (id_preceding != null)
				{
					id_preceding = id_preceding.Id_remove(a_id);
				}
			}
			else
			{
				if (cmp > 0)
				{
					if (id_subsequent != null)
					{
						id_subsequent = id_subsequent.Id_remove(a_id);
					}
				}
				else
				{
					return Id_remove();
				}
			}
			Id_calculateSize();
			return this;
		}

		private com.db4o.YapObject Id_remove()
		{
			if (id_subsequent != null && id_preceding != null)
			{
				id_subsequent = id_subsequent.Id_rotateSmallestUp();
				id_subsequent.id_preceding = id_preceding;
				id_subsequent.Id_calculateSize();
				return id_subsequent;
			}
			if (id_subsequent != null)
			{
				return id_subsequent;
			}
			return id_preceding;
		}

		public override string ToString()
		{
			return base.ToString();
			try
			{
				int id = GetID();
				string str = "YapObject\nID=" + id;
				if (i_yapClass != null)
				{
					com.db4o.YapStream stream = i_yapClass.GetStream();
					if (stream != null && id > 0)
					{
						com.db4o.YapWriter writer = stream.ReadWriterByID(stream.GetTransaction(), id);
						if (writer != null)
						{
							str += "\nAddress=" + writer.GetAddress();
						}
						com.db4o.inside.marshall.ObjectHeader oh = new com.db4o.inside.marshall.ObjectHeader
							(stream, writer);
						com.db4o.YapClass yc = oh.YapClass();
						if (yc != i_yapClass)
						{
							str += "\nYapClass corruption";
						}
						else
						{
							str += yc.ToString(oh._marshallerFamily, writer, this, 0, 5);
						}
					}
				}
				object obj = GetObject();
				if (obj == null)
				{
					str += "\nfor [null]";
				}
				else
				{
					string objToString = string.Empty;
					try
					{
						objToString = obj.ToString();
					}
					catch
					{
					}
					com.db4o.reflect.ReflectClass claxx = GetYapClass().Reflector().ForObject(obj);
					str += "\n" + claxx.GetName() + "\n" + objToString;
				}
				return str;
			}
			catch
			{
			}
			return "Exception in YapObject analyzer";
		}
	}
}
