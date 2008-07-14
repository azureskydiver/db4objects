/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Activation;
using Db4objects.Db4o.Internal.Delete;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Internal.Marshall;
using Db4objects.Db4o.Marshall;
using Db4objects.Db4o.Reflect;

namespace Db4objects.Db4o.Internal.Handlers
{
	/// <exclude></exclude>
	public class FirstClassObjectHandler : ITypeHandler4, IVersionedTypeHandler, IFirstClassHandler
		, IVirtualAttributeHandler
	{
		private const int HashcodeForNull = 72483944;

		private Db4objects.Db4o.Internal.ClassMetadata _classMetadata;

		public FirstClassObjectHandler(Db4objects.Db4o.Internal.ClassMetadata classMetadata
			)
		{
			_classMetadata = classMetadata;
		}

		public FirstClassObjectHandler()
		{
		}

		public virtual void Defragment(IDefragmentContext context)
		{
			FirstClassObjectHandler.TraverseFieldCommand command = new _TraverseFieldCommand_33
				(context);
			TraverseDeclaredFields(context, command);
			if (ClassMetadata().i_ancestor != null)
			{
				ClassMetadata().i_ancestor.Defragment(context);
			}
		}

		private sealed class _TraverseFieldCommand_33 : FirstClassObjectHandler.TraverseFieldCommand
		{
			public _TraverseFieldCommand_33(IDefragmentContext context)
			{
				this.context = context;
			}

			public override int FieldCount(Db4objects.Db4o.Internal.ClassMetadata classMetadata
				, ByteArrayBuffer reader)
			{
				return context.ReadInt();
			}

			public override void ProcessField(FieldMetadata field, bool isNull, Db4objects.Db4o.Internal.ClassMetadata
				 containingClass)
			{
				if (!isNull)
				{
					field.DefragField(context);
				}
			}

			private readonly IDefragmentContext context;
		}

		/// <exception cref="Db4oIOException"></exception>
		public virtual void Delete(IDeleteContext context)
		{
			context.DeleteObject();
		}

		public void InstantiateFields(UnmarshallingContext context)
		{
			BooleanByRef updateFieldFound = new BooleanByRef();
			ContextState savedState = context.SaveState();
			FirstClassObjectHandler.TraverseFieldCommand command = new _TraverseFieldCommand_61
				(updateFieldFound, context);
			TraverseDeclaredFields(context, command);
			if (updateFieldFound.value)
			{
				context.RestoreState(savedState);
				command = new _TraverseFieldCommand_77(context);
				TraverseDeclaredFields(context, command);
			}
		}

		private sealed class _TraverseFieldCommand_61 : FirstClassObjectHandler.TraverseFieldCommand
		{
			public _TraverseFieldCommand_61(BooleanByRef updateFieldFound, UnmarshallingContext
				 context)
			{
				this.updateFieldFound = updateFieldFound;
				this.context = context;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, Db4objects.Db4o.Internal.ClassMetadata
				 containingClass)
			{
				if (field.Updating())
				{
					updateFieldFound.value = true;
				}
				if (isNull)
				{
					field.Set(context.PersistentObject(), null);
					return;
				}
				field.Instantiate(context);
			}

			private readonly BooleanByRef updateFieldFound;

			private readonly UnmarshallingContext context;
		}

		private sealed class _TraverseFieldCommand_77 : FirstClassObjectHandler.TraverseFieldCommand
		{
			public _TraverseFieldCommand_77(UnmarshallingContext context)
			{
				this.context = context;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, Db4objects.Db4o.Internal.ClassMetadata
				 containingClass)
			{
				if (!isNull)
				{
					field.AttemptUpdate(context);
				}
			}

			private readonly UnmarshallingContext context;
		}

		public virtual object Read(IReadContext context)
		{
			UnmarshallingContext unmarshallingContext = (UnmarshallingContext)context;
			// FIXME: Commented out code below is the implementation plan to let
			//        FirstClassObjectHandler take responsibility of fieldcount
			//        and null Bitmap.        
			//        BitMap4 nullBitMap = unmarshallingContext.readBitMap(fieldCount);
			//        int fieldCount = context.readInt();
			InstantiateFields(unmarshallingContext);
			if (ClassMetadata().i_ancestor != null)
			{
				ClassMetadata().i_ancestor.Read(context);
			}
			return unmarshallingContext.PersistentObject();
		}

		public virtual void Write(IWriteContext context, object obj)
		{
			//        int fieldCount = _classMetadata.fieldCount();
			//        context.writeInt(fieldCount);
			//        final BitMap4 nullBitMap = new BitMap4(fieldCount);
			//        ReservedBuffer bitMapBuffer = context.reserve(nullBitMap.marshalledLength());
			Marshall(obj, (MarshallingContext)context);
			//        bitMapBuffer.writeBytes(nullBitMap.bytes());
			if (ClassMetadata().i_ancestor != null)
			{
				ClassMetadata().i_ancestor.Write(context, obj);
			}
		}

		public virtual void Marshall(object obj, MarshallingContext context)
		{
			Transaction trans = context.Transaction();
			FirstClassObjectHandler.TraverseFieldCommand command = new _TraverseFieldCommand_128
				(context, trans, obj);
			TraverseDeclaredFields(context, command);
		}

		private sealed class _TraverseFieldCommand_128 : FirstClassObjectHandler.TraverseFieldCommand
		{
			public _TraverseFieldCommand_128(MarshallingContext context, Transaction trans, object
				 obj)
			{
				this.context = context;
				this.trans = trans;
				this.obj = obj;
			}

			public override int FieldCount(Db4objects.Db4o.Internal.ClassMetadata classMetadata
				, ByteArrayBuffer buffer)
			{
				int fieldCount = classMetadata.i_fields.Length;
				context.FieldCount(fieldCount);
				return fieldCount;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, Db4objects.Db4o.Internal.ClassMetadata
				 containingClass)
			{
				object child = field.GetOrCreate(trans, obj);
				if (child == null)
				{
					context.IsNull(context.CurrentSlot(), true);
					field.AddIndexEntry(trans, context.ObjectID(), null);
					return;
				}
				if (child is IDb4oTypeImpl)
				{
					child = ((IDb4oTypeImpl)child).StoredTo(trans);
				}
				field.Marshall(context, child);
			}

			private readonly MarshallingContext context;

			private readonly Transaction trans;

			private readonly object obj;
		}

		public virtual IPreparedComparison PrepareComparison(IContext context, object source
			)
		{
			if (source == null)
			{
				return new _IPreparedComparison_155();
			}
			int id = 0;
			IReflectClass claxx = null;
			if (source is int)
			{
				id = ((int)source);
			}
			else
			{
				if (source is TransactionContext)
				{
					TransactionContext tc = (TransactionContext)source;
					object obj = tc._object;
					id = _classMetadata.Stream().GetID(tc._transaction, obj);
					claxx = _classMetadata.Reflector().ForObject(obj);
				}
				else
				{
					throw new IllegalComparisonException();
				}
			}
			return new ClassMetadata.PreparedComparisonImpl(id, claxx);
		}

		private sealed class _IPreparedComparison_155 : IPreparedComparison
		{
			public _IPreparedComparison_155()
			{
			}

			public int CompareTo(object obj)
			{
				if (obj == null)
				{
					return 0;
				}
				return -1;
			}
		}

		protected abstract class TraverseFieldCommand
		{
			private bool _cancelled = false;

			public virtual int FieldCount(ClassMetadata classMetadata, ByteArrayBuffer reader
				)
			{
				return classMetadata.ReadFieldCount(reader);
			}

			public virtual bool Cancelled()
			{
				return _cancelled;
			}

			protected virtual void Cancel()
			{
				_cancelled = true;
			}

			public abstract void ProcessField(FieldMetadata field, bool isNull, ClassMetadata
				 containingClass);
		}

		protected void TraverseAllFields(IMarshallingInfo context, FirstClassObjectHandler.TraverseFieldCommand
			 command)
		{
			ClassMetadata classMetadata = ClassMetadata();
			while (classMetadata != null)
			{
				TraverseDeclaredFields(context, classMetadata, command);
				classMetadata = classMetadata.i_ancestor;
			}
		}

		protected void TraverseDeclaredFields(IMarshallingInfo context, FirstClassObjectHandler.TraverseFieldCommand
			 command)
		{
			TraverseDeclaredFields(context, ClassMetadata(), command);
		}

		private void TraverseDeclaredFields(IMarshallingInfo context, ClassMetadata classMetadata
			, FirstClassObjectHandler.TraverseFieldCommand command)
		{
			int fieldCount = command.FieldCount(classMetadata, ((ByteArrayBuffer)context.Buffer
				()));
			for (int i = 0; i < fieldCount && !command.Cancelled(); i++)
			{
				command.ProcessField(classMetadata.i_fields[i], IsNull(context, context.CurrentSlot
					()), classMetadata);
				context.BeginSlot();
			}
		}

		protected virtual bool IsNull(IFieldListInfo fieldList, int fieldIndex)
		{
			return fieldList.IsNull(fieldIndex);
		}

		public virtual ClassMetadata ClassMetadata()
		{
			return _classMetadata;
		}

		public virtual void ClassMetadata(ClassMetadata classMetadata)
		{
			_classMetadata = classMetadata;
		}

		public override bool Equals(object obj)
		{
			if (!(obj is FirstClassObjectHandler))
			{
				return false;
			}
			FirstClassObjectHandler other = (FirstClassObjectHandler)obj;
			if (_classMetadata == null)
			{
				return other._classMetadata == null;
			}
			return _classMetadata.Equals(other._classMetadata);
		}

		public override int GetHashCode()
		{
			if (_classMetadata != null)
			{
				return _classMetadata.GetHashCode();
			}
			return HashcodeForNull;
		}

		public virtual ITypeHandler4 UnversionedTemplate()
		{
			return new FirstClassObjectHandler(null);
		}

		public virtual object DeepClone(object context)
		{
			TypeHandlerCloneContext typeHandlerCloneContext = (TypeHandlerCloneContext)context;
			FirstClassObjectHandler cloned = (FirstClassObjectHandler)Reflection4.NewInstance
				(this);
			if (typeHandlerCloneContext.original is FirstClassObjectHandler)
			{
				FirstClassObjectHandler original = (FirstClassObjectHandler)typeHandlerCloneContext
					.original;
				cloned._classMetadata = original._classMetadata;
			}
			else
			{
				if (_classMetadata == null)
				{
					throw new InvalidOperationException();
				}
				cloned._classMetadata = _classMetadata;
			}
			return cloned;
		}

		public virtual void CollectIDs(CollectIdContext context, string fieldName)
		{
			FirstClassObjectHandler.TraverseFieldCommand command = new _TraverseFieldCommand_270
				(fieldName, context);
			TraverseDeclaredFields(context, command);
			if (ClassMetadata().i_ancestor != null)
			{
				ClassMetadata().i_ancestor.CollectIDs(context, fieldName);
			}
		}

		private sealed class _TraverseFieldCommand_270 : FirstClassObjectHandler.TraverseFieldCommand
		{
			public _TraverseFieldCommand_270(string fieldName, CollectIdContext context)
			{
				this.fieldName = fieldName;
				this.context = context;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, ClassMetadata
				 containingClass)
			{
				if (isNull)
				{
					return;
				}
				if (fieldName.Equals(field.GetName()))
				{
					field.CollectIDs(context);
				}
				else
				{
					field.IncrementOffset(context);
				}
			}

			private readonly string fieldName;

			private readonly CollectIdContext context;
		}

		public virtual void CascadeActivation(ActivationContext4 context)
		{
			context.CascadeActivationToTarget(ClassMetadata(), ClassMetadata().DescendOnCascadingActivation
				());
		}

		public virtual ITypeHandler4 ReadCandidateHandler(QueryingReadContext context)
		{
			if (ClassMetadata().IsArray())
			{
				return ClassMetadata();
			}
			return null;
		}

		/// <exception cref="Db4oIOException"></exception>
		public virtual void CollectIDs(QueryingReadContext context)
		{
			int id = context.CollectionID();
			if (id == 0)
			{
				return;
			}
			Transaction transaction = context.Transaction();
			ObjectContainerBase container = context.Container();
			object obj = container.GetByID(transaction, id);
			if (obj == null)
			{
				return;
			}
			// FIXME: [TA] review activation depth
			int depth = ClassMetadata().AdjustDepthToBorders(2);
			container.Activate(transaction, obj, container.ActivationDepthProvider().ActivationDepth
				(depth, ActivationMode.Activate));
			Platform4.ForEachCollectionElement(obj, new _IVisitor4_316(context));
		}

		private sealed class _IVisitor4_316 : IVisitor4
		{
			public _IVisitor4_316(QueryingReadContext context)
			{
				this.context = context;
			}

			public void Visit(object elem)
			{
				context.Add(elem);
			}

			private readonly QueryingReadContext context;
		}

		public virtual void ReadVirtualAttributes(ObjectReferenceContext context)
		{
			FirstClassObjectHandler.TraverseFieldCommand command = new _TraverseFieldCommand_325
				(context);
			TraverseAllFields(context, command);
		}

		private sealed class _TraverseFieldCommand_325 : FirstClassObjectHandler.TraverseFieldCommand
		{
			public _TraverseFieldCommand_325(ObjectReferenceContext context)
			{
				this.context = context;
			}

			public override void ProcessField(FieldMetadata field, bool isNull, ClassMetadata
				 containingClass)
			{
				if (!isNull)
				{
					field.ReadVirtualAttribute(context);
				}
			}

			private readonly ObjectReferenceContext context;
		}
	}
}
