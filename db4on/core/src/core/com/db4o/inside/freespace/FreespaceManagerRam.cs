namespace com.db4o.inside.freespace
{
	public class FreespaceManagerRam : com.db4o.inside.freespace.FreespaceManager
	{
		private readonly com.db4o.TreeIntObject _finder = new com.db4o.TreeIntObject(0);

		private com.db4o.foundation.Tree _freeByAddress;

		private com.db4o.foundation.Tree _freeBySize;

		public FreespaceManagerRam(com.db4o.YapFile file) : base(file)
		{
		}

		public virtual void TraverseFreeSlots(com.db4o.foundation.Visitor4 visitor)
		{
			com.db4o.foundation.Tree.Traverse(_freeByAddress, new _AnonymousInnerClass23(this
				, visitor));
		}

		private sealed class _AnonymousInnerClass23 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass23(FreespaceManagerRam _enclosing, com.db4o.foundation.Visitor4
				 visitor)
			{
				this._enclosing = _enclosing;
				this.visitor = visitor;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.freespace.FreeSlotNode node = (com.db4o.inside.freespace.FreeSlotNode
					)obj;
				int address = node._key;
				int length = node._peer._key;
				visitor.Visit(new com.db4o.inside.slots.Slot(address, length));
			}

			private readonly FreespaceManagerRam _enclosing;

			private readonly com.db4o.foundation.Visitor4 visitor;
		}

		private void AddFreeSlotNodes(int a_address, int a_length)
		{
			com.db4o.inside.freespace.FreeSlotNode addressNode = new com.db4o.inside.freespace.FreeSlotNode
				(a_address);
			addressNode.CreatePeer(a_length);
			_freeByAddress = com.db4o.foundation.Tree.Add(_freeByAddress, addressNode);
			_freeBySize = com.db4o.foundation.Tree.Add(_freeBySize, addressNode._peer);
		}

		public override void BeginCommit()
		{
		}

		public override void Debug()
		{
		}

		public override void EndCommit()
		{
		}

		public override void Free(int a_address, int a_length)
		{
			if (a_address <= 0)
			{
				return;
			}
			if (a_length <= DiscardLimit())
			{
				return;
			}
			a_length = _file.BlocksFor(a_length);
			_finder._key = a_address;
			com.db4o.inside.freespace.FreeSlotNode sizeNode;
			com.db4o.inside.freespace.FreeSlotNode addressnode = (com.db4o.inside.freespace.FreeSlotNode
				)com.db4o.foundation.Tree.FindSmaller(_freeByAddress, _finder);
			if ((addressnode != null) && ((addressnode._key + addressnode._peer._key) == a_address
				))
			{
				sizeNode = addressnode._peer;
				_freeBySize = _freeBySize.RemoveNode(sizeNode);
				sizeNode._key += a_length;
				com.db4o.inside.freespace.FreeSlotNode secondAddressNode = (com.db4o.inside.freespace.FreeSlotNode
					)com.db4o.foundation.Tree.FindGreaterOrEqual(_freeByAddress, _finder);
				if ((secondAddressNode != null) && (a_address + a_length == secondAddressNode._key
					))
				{
					sizeNode._key += secondAddressNode._peer._key;
					_freeBySize = _freeBySize.RemoveNode(secondAddressNode._peer);
					_freeByAddress = _freeByAddress.RemoveNode(secondAddressNode);
				}
				sizeNode.RemoveChildren();
				_freeBySize = com.db4o.foundation.Tree.Add(_freeBySize, sizeNode);
			}
			else
			{
				addressnode = (com.db4o.inside.freespace.FreeSlotNode)com.db4o.foundation.Tree.FindGreaterOrEqual
					(_freeByAddress, _finder);
				if ((addressnode != null) && (a_address + a_length == addressnode._key))
				{
					sizeNode = addressnode._peer;
					_freeByAddress = _freeByAddress.RemoveNode(addressnode);
					_freeBySize = _freeBySize.RemoveNode(sizeNode);
					sizeNode._key += a_length;
					addressnode._key = a_address;
					addressnode.RemoveChildren();
					sizeNode.RemoveChildren();
					_freeByAddress = com.db4o.foundation.Tree.Add(_freeByAddress, addressnode);
					_freeBySize = com.db4o.foundation.Tree.Add(_freeBySize, sizeNode);
				}
				else
				{
					AddFreeSlotNodes(a_address, a_length);
				}
			}
		}

		public override void FreeSelf()
		{
		}

		public override int FreeSize()
		{
			com.db4o.foundation.MutableInt mint = new com.db4o.foundation.MutableInt();
			com.db4o.foundation.Tree.Traverse(_freeBySize, new _AnonymousInnerClass137(this, 
				mint));
			return mint.Value();
		}

		private sealed class _AnonymousInnerClass137 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass137(FreespaceManagerRam _enclosing, com.db4o.foundation.MutableInt
				 mint)
			{
				this._enclosing = _enclosing;
				this.mint = mint;
			}

			public void Visit(object obj)
			{
				com.db4o.inside.freespace.FreeSlotNode node = (com.db4o.inside.freespace.FreeSlotNode
					)obj;
				mint.Add(node._key);
			}

			private readonly FreespaceManagerRam _enclosing;

			private readonly com.db4o.foundation.MutableInt mint;
		}

		public override int GetSlot(int length)
		{
			int address = GetSlot1(length);
			if (address != 0)
			{
			}
			return address;
		}

		public virtual int GetSlot1(int length)
		{
			length = _file.BlocksFor(length);
			_finder._key = length;
			_finder._object = null;
			_freeBySize = com.db4o.inside.freespace.FreeSlotNode.RemoveGreaterOrEqual((com.db4o.inside.freespace.FreeSlotNode
				)_freeBySize, _finder);
			if (_finder._object == null)
			{
				return 0;
			}
			com.db4o.inside.freespace.FreeSlotNode node = (com.db4o.inside.freespace.FreeSlotNode
				)_finder._object;
			int blocksFound = node._key;
			int address = node._peer._key;
			_freeByAddress = _freeByAddress.RemoveNode(node._peer);
			if (blocksFound > length)
			{
				AddFreeSlotNodes(address + length, blocksFound - length);
			}
			return address;
		}

		public override void Migrate(com.db4o.inside.freespace.FreespaceManager newFM)
		{
			if (_freeByAddress != null)
			{
				_freeByAddress.Traverse(new _AnonymousInnerClass181(this, newFM));
			}
		}

		private sealed class _AnonymousInnerClass181 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass181(FreespaceManagerRam _enclosing, com.db4o.inside.freespace.FreespaceManager
				 newFM)
			{
				this._enclosing = _enclosing;
				this.newFM = newFM;
			}

			public void Visit(object a_object)
			{
				com.db4o.inside.freespace.FreeSlotNode fsn = (com.db4o.inside.freespace.FreeSlotNode
					)a_object;
				int address = fsn._key;
				int length = fsn._peer._key;
				newFM.Free(address, length);
			}

			private readonly FreespaceManagerRam _enclosing;

			private readonly com.db4o.inside.freespace.FreespaceManager newFM;
		}

		public override void Read(int freeSlotsID)
		{
			if (freeSlotsID <= 0)
			{
				return;
			}
			if (DiscardLimit() == int.MaxValue)
			{
				return;
			}
			com.db4o.YapWriter reader = _file.ReadWriterByID(Trans(), freeSlotsID);
			if (reader == null)
			{
				return;
			}
			com.db4o.inside.freespace.FreeSlotNode.sizeLimit = DiscardLimit();
			_freeBySize = new com.db4o.TreeReader(reader, new com.db4o.inside.freespace.FreeSlotNode
				(0), true).Read();
			com.db4o.foundation.Tree[] addressTree = new com.db4o.foundation.Tree[1];
			if (_freeBySize != null)
			{
				_freeBySize.Traverse(new _AnonymousInnerClass210(this, addressTree));
			}
			_freeByAddress = addressTree[0];
			_file.Free(freeSlotsID, com.db4o.YapConst.POINTER_LENGTH);
			_file.Free(reader.GetAddress(), reader.GetLength());
		}

		private sealed class _AnonymousInnerClass210 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass210(FreespaceManagerRam _enclosing, com.db4o.foundation.Tree[]
				 addressTree)
			{
				this._enclosing = _enclosing;
				this.addressTree = addressTree;
			}

			public void Visit(object a_object)
			{
				com.db4o.inside.freespace.FreeSlotNode node = ((com.db4o.inside.freespace.FreeSlotNode
					)a_object)._peer;
				addressTree[0] = com.db4o.foundation.Tree.Add(addressTree[0], node);
			}

			private readonly FreespaceManagerRam _enclosing;

			private readonly com.db4o.foundation.Tree[] addressTree;
		}

		public override void Start(int slotAddress)
		{
		}

		public override byte SystemType()
		{
			return FM_RAM;
		}

		private com.db4o.Transaction Trans()
		{
			return _file.GetSystemTransaction();
		}

		public override int Write(bool shuttingDown)
		{
			if (!shuttingDown)
			{
				return 0;
			}
			int freeBySizeID = 0;
			int length = com.db4o.TreeInt.ByteCount((com.db4o.TreeInt)_freeBySize);
			com.db4o.inside.slots.Pointer4 ptr = _file.NewSlot(Trans(), length);
			freeBySizeID = ptr._id;
			com.db4o.YapWriter sdwriter = new com.db4o.YapWriter(Trans(), length);
			sdwriter.UseSlot(freeBySizeID, ptr._address, length);
			com.db4o.TreeInt.Write(sdwriter, (com.db4o.TreeInt)_freeBySize);
			sdwriter.WriteEncrypt();
			Trans().WritePointer(ptr._id, ptr._address, length);
			return freeBySizeID;
		}

		public override int EntryCount()
		{
			return com.db4o.foundation.Tree.Size(_freeByAddress);
		}
	}
}
