/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Activation;
using Db4objects.Db4o.Internal.Marshall;

namespace Db4objects.Db4o.Internal.Marshall
{
	/// <exclude></exclude>
	public abstract class AbstractReadContext : BufferContext, IInternalReadContext
	{
		protected IActivationDepth _activationDepth = UnknownActivationDepth.Instance;

		protected AbstractReadContext(Transaction transaction, ByteArrayBuffer buffer) : 
			base(transaction, buffer)
		{
		}

		protected AbstractReadContext(Transaction transaction) : this(transaction, null)
		{
		}

		public virtual object Read(ITypeHandler4 handlerType)
		{
			ITypeHandler4 handler = CorrectHandlerVersion(handlerType);
			if (!IsIndirected(handler))
			{
				return handler.Read(this);
			}
			int indirectedOffSet = ReadInt();
			ReadInt();
			// length, not needed
			int offset = Offset();
			Seek(indirectedOffSet);
			object obj = handler.Read(this);
			Seek(offset);
			return obj;
		}

		public virtual object ReadObject()
		{
			int id = ReadInt();
			if (id == 0)
			{
				return null;
			}
			ClassMetadata classMetadata = ClassMetadataForId(id);
			if (null == classMetadata)
			{
				return null;
			}
			IActivationDepth depth = ActivationDepth().Descend(classMetadata);
			if (PeekPersisted())
			{
				return Container().PeekPersisted(Transaction(), id, depth, false);
			}
			object obj = Container().GetByID2(Transaction(), id);
			if (null == obj)
			{
				return null;
			}
			// this is OK for primitive YapAnys. They will not be added
			// to the list, since they will not be found in the ID tree.
			Container().StillToActivate(Transaction(), obj, depth);
			return obj;
		}

		private ClassMetadata ClassMetadataForId(int id)
		{
			HardObjectReference hardRef = Container().GetHardObjectReferenceById(Transaction(
				), id);
			if (null == hardRef || hardRef._reference == null)
			{
				// com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
				return null;
			}
			return hardRef._reference.ClassMetadata();
		}

		protected virtual bool PeekPersisted()
		{
			return false;
		}

		public virtual object ReadObject(ITypeHandler4 handlerType)
		{
			ITypeHandler4 handler = CorrectHandlerVersion(handlerType);
			if (!IsIndirected(handler))
			{
				return handler.Read(this);
			}
			int payLoadOffset = ReadInt();
			ReadInt();
			// length - never used
			if (payLoadOffset == 0)
			{
				return null;
			}
			int savedOffset = Offset();
			Seek(payLoadOffset);
			object obj = handler.Read(this);
			Seek(savedOffset);
			return obj;
		}

		public virtual IActivationDepth ActivationDepth()
		{
			return _activationDepth;
		}

		public virtual void ActivationDepth(IActivationDepth depth)
		{
			_activationDepth = depth;
		}

		public virtual bool IsIndirected(ITypeHandler4 handler)
		{
			if (HandlerVersion() == 0)
			{
				return false;
			}
			return HandlerRegistry().IsVariableLength(handler);
		}

		private Db4objects.Db4o.Internal.HandlerRegistry HandlerRegistry()
		{
			return Container().Handlers();
		}

		public virtual IReadWriteBuffer ReadIndirectedBuffer()
		{
			int address = ReadInt();
			int length = ReadInt();
			if (address == 0)
			{
				return null;
			}
			return Container().BufferByAddress(address, length);
		}
	}
}
