/* Copyright (C) 2004 - 2009  Versant Inc.  http://www.db4o.com */

using System.Collections;
using System.Text;
using Db4objects.Db4o;
using Db4objects.Db4o.Consistency;
using Db4objects.Db4o.Ext;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Classindex;
using Db4objects.Db4o.Internal.Ids;
using Db4objects.Db4o.Internal.Slots;

namespace Db4objects.Db4o.Consistency
{
	public class ConsistencyChecker
	{
		private readonly LocalObjectContainer _db;

		private readonly IList bogusSlots = new ArrayList();

		private TreeIntObject mappings;

		public class SlotSource
		{
			public static readonly ConsistencyChecker.SlotSource IdSystem = new ConsistencyChecker.SlotSource
				("IdSystem");

			public static readonly ConsistencyChecker.SlotSource Freespace = new ConsistencyChecker.SlotSource
				("Freespace");

			private readonly string _name;

			private SlotSource(string name)
			{
				_name = name;
			}

			public override string ToString()
			{
				return _name;
			}
		}

		public class SlotWithSource
		{
			public readonly Slot slot;

			public readonly ConsistencyChecker.SlotSource source;

			public SlotWithSource(Slot slot, ConsistencyChecker.SlotSource source)
			{
				this.slot = slot;
				this.source = source;
			}

			public override string ToString()
			{
				return slot + "(" + source + ")";
			}
		}

		public class ConsistencyReport
		{
			private const int MaxReportedItems = 50;

			internal readonly IList bogusSlots;

			internal readonly IList overlaps;

			internal readonly IList invalidObjectIds;

			public ConsistencyReport(IList bogusSlots, IList overlaps, IList invalidClassIds)
			{
				this.bogusSlots = bogusSlots;
				this.overlaps = overlaps;
				this.invalidObjectIds = invalidClassIds;
			}

			public virtual bool Consistent()
			{
				return bogusSlots.Count == 0 && overlaps.Count == 0 && invalidObjectIds.Count == 
					0;
			}

			public override string ToString()
			{
				if (Consistent())
				{
					return "no inconsistencies detected";
				}
				StringBuilder message = new StringBuilder("INCONSISTENCIES DETECTED\n").Append(overlaps
					.Count + " overlaps\n").Append(bogusSlots.Count + " bogus slots\n").Append(invalidObjectIds
					.Count + " invalid class ids\n");
				message.Append("(slot lengths are non-blocked)\n");
				AppendInconsistencyReport(message, "OVERLAPS", overlaps);
				AppendInconsistencyReport(message, "BOGUS SLOTS", bogusSlots);
				AppendInconsistencyReport(message, "INVALID OBJECT IDS", invalidObjectIds);
				return message.ToString();
			}

			private void AppendInconsistencyReport(StringBuilder str, string title, ICollection
				 entries)
			{
				if (entries.Count != 0)
				{
					str.Append(title + "\n");
					int count = 0;
					for (IEnumerator entryIter = entries.GetEnumerator(); entryIter.MoveNext(); )
					{
						object entry = entryIter.Current;
						str.Append(entry).Append("\n");
						count++;
						if (count > MaxReportedItems)
						{
							str.Append("and more...\n");
							break;
						}
					}
				}
			}
		}

		public ConsistencyChecker(IObjectContainer db)
		{
			_db = (LocalObjectContainer)db;
		}

		public virtual ConsistencyChecker.ConsistencyReport CheckSlotConsistency()
		{
			MapIdSystem();
			MapFreespace();
			return new ConsistencyChecker.ConsistencyReport(bogusSlots, CollectOverlaps(), CheckClassIndices
				());
		}

		private IList CheckClassIndices()
		{
			IList invalidIds = new ArrayList();
			IIdSystem idSystem = _db.IdSystem();
			if (!(idSystem is BTreeIdSystem))
			{
				return invalidIds;
			}
			ClassMetadataIterator clazzIter = _db.ClassCollection().Iterator();
			while (clazzIter.MoveNext())
			{
				ClassMetadata clazz = clazzIter.CurrentClass();
				if (!clazz.HasClassIndex())
				{
					continue;
				}
				BTreeClassIndexStrategy index = (BTreeClassIndexStrategy)clazz.Index();
				index.TraverseAll(_db.SystemTransaction(), new _IVisitor4_123(idSystem, invalidIds
					, clazz));
			}
			return invalidIds;
		}

		private sealed class _IVisitor4_123 : IVisitor4
		{
			public _IVisitor4_123(IIdSystem idSystem, IList invalidIds, ClassMetadata clazz)
			{
				this.idSystem = idSystem;
				this.invalidIds = invalidIds;
				this.clazz = clazz;
			}

			public void Visit(object id)
			{
				try
				{
					Slot slot = idSystem.CommittedSlot((((int)id)));
					if (Slot.IsNull(slot))
					{
						invalidIds.Add(new Pair(clazz.GetName(), ((int)id)));
					}
				}
				catch (InvalidIDException)
				{
					invalidIds.Add(new Pair(clazz.GetName(), ((int)id)));
				}
			}

			private readonly IIdSystem idSystem;

			private readonly IList invalidIds;

			private readonly ClassMetadata clazz;
		}

		private IList CollectOverlaps()
		{
			IBlockConverter blockConverter = _db.BlockConverter();
			IList overlaps = new ArrayList();
			ByRef prevSlot = ByRef.NewInstance();
			mappings.Traverse(new _IVisitor4_144(prevSlot, blockConverter, overlaps));
			return overlaps;
		}

		private sealed class _IVisitor4_144 : IVisitor4
		{
			public _IVisitor4_144(ByRef prevSlot, IBlockConverter blockConverter, IList overlaps
				)
			{
				this.prevSlot = prevSlot;
				this.blockConverter = blockConverter;
				this.overlaps = overlaps;
			}

			public void Visit(object obj)
			{
				ConsistencyChecker.SlotWithSource curSlot = (ConsistencyChecker.SlotWithSource)((
					TreeIntObject)obj)._object;
				if (((ConsistencyChecker.SlotWithSource)prevSlot.value) != null)
				{
					if (((ConsistencyChecker.SlotWithSource)prevSlot.value).slot.Address() + blockConverter
						.ToBlockedLength(((ConsistencyChecker.SlotWithSource)prevSlot.value).slot).Length
						() > curSlot.slot.Address())
					{
						overlaps.Add(new Pair(((ConsistencyChecker.SlotWithSource)prevSlot.value), curSlot
							));
					}
				}
				prevSlot.value = curSlot;
			}

			private readonly ByRef prevSlot;

			private readonly IBlockConverter blockConverter;

			private readonly IList overlaps;
		}

		private void MapFreespace()
		{
			_db.FreespaceManager().Traverse(new _IVisitor4_159(this));
		}

		private sealed class _IVisitor4_159 : IVisitor4
		{
			public _IVisitor4_159(ConsistencyChecker _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object slot)
			{
				if (((Slot)slot).Address() < 0)
				{
					this._enclosing.bogusSlots.Add(new ConsistencyChecker.SlotWithSource(((Slot)slot)
						, ConsistencyChecker.SlotSource.Freespace));
				}
				this._enclosing.AddMapping(((Slot)slot), ConsistencyChecker.SlotSource.Freespace);
			}

			private readonly ConsistencyChecker _enclosing;
		}

		private void MapIdSystem()
		{
			IIdSystem idSystem = _db.IdSystem();
			if (idSystem is BTreeIdSystem)
			{
				((BTreeIdSystem)idSystem).TraverseIds(new _IVisitor4_172(this));
			}
		}

		private sealed class _IVisitor4_172 : IVisitor4
		{
			public _IVisitor4_172(ConsistencyChecker _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object mapping)
			{
				if (((IdSlotMapping)mapping)._address < 0)
				{
					this._enclosing.bogusSlots.Add(new ConsistencyChecker.SlotWithSource(((IdSlotMapping
						)mapping).Slot(), ConsistencyChecker.SlotSource.IdSystem));
				}
				if (((IdSlotMapping)mapping)._address > 0)
				{
					this._enclosing.AddMapping(((IdSlotMapping)mapping).Slot(), ConsistencyChecker.SlotSource
						.IdSystem);
				}
			}

			private readonly ConsistencyChecker _enclosing;
		}

		private void AddMapping(Slot slot, ConsistencyChecker.SlotSource source)
		{
			mappings = TreeIntObject.Add(mappings, slot.Address(), new ConsistencyChecker.SlotWithSource
				(slot, source));
		}
	}
}
