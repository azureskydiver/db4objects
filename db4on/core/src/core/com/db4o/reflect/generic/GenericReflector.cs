namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericReflector : com.db4o.reflect.Reflector, com.db4o.foundation.DeepClone
	{
		private com.db4o.reflect.Reflector _delegate;

		private com.db4o.reflect.generic.GenericArrayReflector _array;

		private readonly com.db4o.foundation.Hashtable4 _classByName = new com.db4o.foundation.Hashtable4
			();

		private readonly com.db4o.foundation.Hashtable4 _classByClass = new com.db4o.foundation.Hashtable4
			();

		private readonly com.db4o.foundation.Collection4 _classes = new com.db4o.foundation.Collection4
			();

		private readonly com.db4o.foundation.Hashtable4 _classByID = new com.db4o.foundation.Hashtable4
			();

		private com.db4o.foundation.Collection4 _collectionPredicates = new com.db4o.foundation.Collection4
			();

		private com.db4o.foundation.Collection4 _collectionUpdateDepths = new com.db4o.foundation.Collection4
			();

		private com.db4o.foundation.Collection4 _pendingClasses = new com.db4o.foundation.Collection4
			();

		private com.db4o.Transaction _trans;

		private com.db4o.YapStream _stream;

		public GenericReflector(com.db4o.Transaction trans, com.db4o.reflect.Reflector delegateReflector
			)
		{
			SetTransaction(trans);
			_delegate = delegateReflector;
			if (_delegate != null)
			{
				_delegate.SetParent(this);
			}
		}

		public virtual object DeepClone(object obj)
		{
			com.db4o.reflect.generic.GenericReflector myClone = new com.db4o.reflect.generic.GenericReflector
				(null, (com.db4o.reflect.Reflector)_delegate.DeepClone(this));
			myClone._collectionPredicates = (com.db4o.foundation.Collection4)_collectionPredicates
				.DeepClone(myClone);
			myClone._collectionUpdateDepths = (com.db4o.foundation.Collection4)_collectionUpdateDepths
				.DeepClone(myClone);
			return myClone;
		}

		internal virtual com.db4o.YapStream GetStream()
		{
			return _stream;
		}

		public virtual bool HasTransaction()
		{
			return _trans != null;
		}

		public virtual void SetTransaction(com.db4o.Transaction trans)
		{
			if (trans != null)
			{
				_trans = trans;
				_stream = trans.Stream();
			}
		}

		public virtual com.db4o.reflect.ReflectArray Array()
		{
			if (_array == null)
			{
				_array = new com.db4o.reflect.generic.GenericArrayReflector(this);
			}
			return _array;
		}

		public virtual int CollectionUpdateDepth(com.db4o.reflect.ReflectClass candidate)
		{
			System.Collections.IEnumerator i = _collectionUpdateDepths.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.reflect.generic.CollectionUpdateDepthEntry entry = (com.db4o.reflect.generic.CollectionUpdateDepthEntry
					)i.Current;
				if (entry._predicate.Match(candidate))
				{
					return entry._depth;
				}
			}
			return 2;
		}

		public virtual bool ConstructorCallsSupported()
		{
			return _delegate.ConstructorCallsSupported();
		}

		internal virtual com.db4o.reflect.generic.GenericClass EnsureDelegate(com.db4o.reflect.ReflectClass
			 clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.generic.GenericClass claxx = (com.db4o.reflect.generic.GenericClass
				)_classByName.Get(clazz.GetName());
			if (claxx == null)
			{
				string name = clazz.GetName();
				claxx = new com.db4o.reflect.generic.GenericClass(this, clazz, name, null);
				_classes.Add(claxx);
				_classByName.Put(name, claxx);
			}
			return claxx;
		}

		public virtual com.db4o.reflect.ReflectClass ForClass(j4o.lang.Class clazz)
		{
			if (clazz == null)
			{
				return null;
			}
			com.db4o.reflect.ReflectClass claxx = (com.db4o.reflect.ReflectClass)_classByClass
				.Get(clazz);
			if (claxx != null)
			{
				return claxx;
			}
			claxx = ForName(clazz.GetName());
			if (claxx != null)
			{
				_classByClass.Put(clazz, claxx);
				return claxx;
			}
			claxx = _delegate.ForClass(clazz);
			if (claxx == null)
			{
				return null;
			}
			claxx = EnsureDelegate(claxx);
			_classByClass.Put(clazz, claxx);
			return claxx;
		}

		public virtual com.db4o.reflect.ReflectClass ForName(string className)
		{
			com.db4o.reflect.ReflectClass clazz = (com.db4o.reflect.ReflectClass)_classByName
				.Get(className);
			if (clazz != null)
			{
				return clazz;
			}
			clazz = _delegate.ForName(className);
			if (clazz != null)
			{
				return EnsureDelegate(clazz);
			}
			if (_stream == null)
			{
				return null;
			}
			if (_stream.ClassCollection() != null)
			{
				int classID = _stream.ClassCollection().GetYapClassID(className);
				if (classID > 0)
				{
					clazz = EnsureClassInitialised(classID);
					_classByName.Put(className, clazz);
					return clazz;
				}
			}
			return null;
		}

		public virtual com.db4o.reflect.ReflectClass ForObject(object obj)
		{
			if (obj is com.db4o.reflect.generic.GenericObject)
			{
				return ((com.db4o.reflect.generic.GenericObject)obj)._class;
			}
			return _delegate.ForObject(obj);
		}

		public virtual com.db4o.reflect.Reflector GetDelegate()
		{
			return _delegate;
		}

		public virtual bool IsCollection(com.db4o.reflect.ReflectClass candidate)
		{
			System.Collections.IEnumerator i = _collectionPredicates.GetEnumerator();
			while (i.MoveNext())
			{
				if (((com.db4o.reflect.ReflectClassPredicate)i.Current).Match(candidate))
				{
					return true;
				}
			}
			return _delegate.IsCollection(candidate.GetDelegate());
		}

		public virtual void RegisterCollection(j4o.lang.Class clazz)
		{
			RegisterCollection(ClassPredicate(clazz));
		}

		public virtual void RegisterCollection(com.db4o.reflect.ReflectClassPredicate predicate
			)
		{
			_collectionPredicates.Add(predicate);
		}

		private com.db4o.reflect.ReflectClassPredicate ClassPredicate(j4o.lang.Class clazz
			)
		{
			com.db4o.reflect.ReflectClass collectionClass = ForClass(clazz);
			com.db4o.reflect.ReflectClassPredicate predicate = new _AnonymousInnerClass199(this
				, collectionClass);
			return predicate;
		}

		private sealed class _AnonymousInnerClass199 : com.db4o.reflect.ReflectClassPredicate
		{
			public _AnonymousInnerClass199(GenericReflector _enclosing, com.db4o.reflect.ReflectClass
				 collectionClass)
			{
				this._enclosing = _enclosing;
				this.collectionClass = collectionClass;
			}

			public bool Match(com.db4o.reflect.ReflectClass candidate)
			{
				return collectionClass.IsAssignableFrom(candidate);
			}

			private readonly GenericReflector _enclosing;

			private readonly com.db4o.reflect.ReflectClass collectionClass;
		}

		public virtual void RegisterCollectionUpdateDepth(j4o.lang.Class clazz, int depth
			)
		{
			RegisterCollectionUpdateDepth(ClassPredicate(clazz), depth);
		}

		public virtual void RegisterCollectionUpdateDepth(com.db4o.reflect.ReflectClassPredicate
			 predicate, int depth)
		{
			_collectionUpdateDepths.Add(new com.db4o.reflect.generic.CollectionUpdateDepthEntry
				(predicate, depth));
		}

		public virtual void Register(com.db4o.reflect.generic.GenericClass clazz)
		{
			string name = clazz.GetName();
			if (_classByName.Get(name) == null)
			{
				_classByName.Put(name, clazz);
				_classes.Add(clazz);
			}
		}

		public virtual com.db4o.reflect.ReflectClass[] KnownClasses()
		{
			ReadAll();
			com.db4o.foundation.Collection4 classes = new com.db4o.foundation.Collection4();
			System.Collections.IEnumerator i = _classes.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.reflect.generic.GenericClass clazz = (com.db4o.reflect.generic.GenericClass
					)i.Current;
				if (!_stream.i_handlers.ICLASS_INTERNAL.IsAssignableFrom(clazz))
				{
					if (!clazz.IsSecondClass())
					{
						if (!clazz.IsArray())
						{
							classes.Add(clazz);
						}
					}
				}
			}
			com.db4o.reflect.ReflectClass[] ret = new com.db4o.reflect.ReflectClass[classes.Size
				()];
			int j = 0;
			i = classes.GetEnumerator();
			while (i.MoveNext())
			{
				ret[j++] = (com.db4o.reflect.ReflectClass)i.Current;
			}
			return ret;
		}

		private void ReadAll()
		{
			for (System.Collections.IEnumerator idIter = _stream.ClassCollection().Ids(); idIter
				.MoveNext(); )
			{
				EnsureClassAvailability(((int)idIter.Current));
			}
			for (System.Collections.IEnumerator idIter = _stream.ClassCollection().Ids(); idIter
				.MoveNext(); )
			{
				EnsureClassRead(((int)idIter.Current));
			}
		}

		private com.db4o.reflect.generic.GenericClass EnsureClassInitialised(int id)
		{
			com.db4o.reflect.generic.GenericClass ret = EnsureClassAvailability(id);
			while (_pendingClasses.Size() > 0)
			{
				com.db4o.foundation.Collection4 pending = _pendingClasses;
				_pendingClasses = new com.db4o.foundation.Collection4();
				System.Collections.IEnumerator i = pending.GetEnumerator();
				while (i.MoveNext())
				{
					EnsureClassRead(((int)i.Current));
				}
			}
			return ret;
		}

		private com.db4o.reflect.generic.GenericClass EnsureClassAvailability(int id)
		{
			if (id == 0)
			{
				return null;
			}
			com.db4o.reflect.generic.GenericClass ret = (com.db4o.reflect.generic.GenericClass
				)_classByID.Get(id);
			if (ret != null)
			{
				return ret;
			}
			com.db4o.YapReader classreader = _stream.ReadWriterByID(_trans, id);
			com.db4o.inside.marshall.ClassMarshaller marshaller = MarshallerFamily()._class;
			com.db4o.inside.marshall.RawClassSpec spec = marshaller.ReadSpec(_trans, classreader
				);
			string className = spec.Name();
			ret = (com.db4o.reflect.generic.GenericClass)_classByName.Get(className);
			if (ret != null)
			{
				_classByID.Put(id, ret);
				_pendingClasses.Add(id);
				return ret;
			}
			com.db4o.reflect.ReflectClass nativeClass = _delegate.ForName(className);
			ret = new com.db4o.reflect.generic.GenericClass(this, nativeClass, className, EnsureClassAvailability
				(spec.SuperClassID()));
			ret.SetDeclaredFieldCount(spec.NumFields());
			_classByID.Put(id, ret);
			_pendingClasses.Add(id);
			return ret;
		}

		private com.db4o.inside.marshall.MarshallerFamily MarshallerFamily()
		{
			return com.db4o.inside.marshall.MarshallerFamily.ForConverterVersion(_stream.ConverterVersion
				());
		}

		private void EnsureClassRead(int id)
		{
			com.db4o.reflect.generic.GenericClass clazz = (com.db4o.reflect.generic.GenericClass
				)_classByID.Get(id);
			com.db4o.YapReader classreader = _stream.ReadWriterByID(_trans, id);
			com.db4o.inside.marshall.ClassMarshaller classMarshaller = MarshallerFamily()._class;
			com.db4o.inside.marshall.RawClassSpec classInfo = classMarshaller.ReadSpec(_trans
				, classreader);
			string className = classInfo.Name();
			if (_classByName.Get(className) != null)
			{
				return;
			}
			_classByName.Put(className, clazz);
			_classes.Add(clazz);
			int numFields = classInfo.NumFields();
			com.db4o.reflect.generic.GenericField[] fields = new com.db4o.reflect.generic.GenericField
				[numFields];
			com.db4o.inside.marshall.FieldMarshaller fieldMarshaller = MarshallerFamily()._field;
			for (int i = 0; i < numFields; i++)
			{
				com.db4o.inside.marshall.RawFieldSpec fieldInfo = fieldMarshaller.ReadSpec(_stream
					, classreader);
				string fieldName = fieldInfo.Name();
				int handlerID = fieldInfo.HandlerID();
				if (fieldInfo.IsVirtual())
				{
					fields[i] = new com.db4o.reflect.generic.GenericVirtualField(fieldName);
					continue;
				}
				com.db4o.reflect.generic.GenericClass fieldClass = null;
				switch (handlerID)
				{
					case com.db4o.YapHandlers.ANY_ID:
					{
						fieldClass = (com.db4o.reflect.generic.GenericClass)ForClass(j4o.lang.JavaSystem.GetClassForType
							(typeof(object)));
						break;
					}

					case com.db4o.YapHandlers.ANY_ARRAY_ID:
					{
						fieldClass = ((com.db4o.reflect.generic.GenericClass)ForClass(j4o.lang.JavaSystem.GetClassForType
							(typeof(object)))).ArrayClass();
						break;
					}

					default:
					{
						EnsureClassAvailability(handlerID);
						fieldClass = (com.db4o.reflect.generic.GenericClass)_classByID.Get(handlerID);
						break;
					}
				}
				fields[i] = new com.db4o.reflect.generic.GenericField(fieldName, fieldClass, fieldInfo
					.IsPrimitive(), fieldInfo.IsArray(), fieldInfo.IsNArray());
			}
			clazz.InitFields(fields);
		}

		public virtual void RegisterPrimitiveClass(int id, string name, com.db4o.reflect.generic.GenericConverter
			 converter)
		{
			com.db4o.reflect.generic.GenericClass existing = (com.db4o.reflect.generic.GenericClass
				)_classByID.Get(id);
			if (existing != null)
			{
				if (null != converter)
				{
					existing.SetSecondClass();
				}
				else
				{
					existing.SetConverter(null);
				}
				return;
			}
			com.db4o.reflect.ReflectClass clazz = _delegate.ForName(name);
			com.db4o.reflect.generic.GenericClass claxx = null;
			if (clazz != null)
			{
				claxx = EnsureDelegate(clazz);
			}
			else
			{
				claxx = new com.db4o.reflect.generic.GenericClass(this, null, name, null);
				_classByName.Put(name, claxx);
				claxx.InitFields(new com.db4o.reflect.generic.GenericField[] { new com.db4o.reflect.generic.GenericField
					(null, null, true, false, false) });
				claxx.SetConverter(converter);
				_classes.Add(claxx);
			}
			claxx.SetSecondClass();
			claxx.SetPrimitive();
			_classByID.Put(id, claxx);
		}

		public virtual void SetParent(com.db4o.reflect.Reflector reflector)
		{
		}
	}
}
