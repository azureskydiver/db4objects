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
	/// <summary>database boot record.</summary>
	/// <remarks>
	/// database boot record. Responsible for ID generation, version generation and
	/// holding a reference to the Db4oDatabase object of the ObjectContainer
	/// </remarks>
	/// <exclude></exclude>
	public class PBootRecord : com.db4o.P1Object, com.db4o.Db4oTypeImpl
	{
		[com.db4o.Transient]
		internal com.db4o.YapFile i_stream;

		public com.db4o.ext.Db4oDatabase i_db;

		public long i_uuidGenerator;

		public long i_versionGenerator;

		public int i_generateVersionNumbers;

		public int i_generateUUIDs;

		[com.db4o.Transient]
		private bool i_dirty;

		public com.db4o.MetaIndex i_uuidMetaIndex;

		public PBootRecord()
		{
		}

		public override int activationDepth()
		{
			return int.MaxValue;
		}

		internal virtual void init(com.db4o.Config4Impl a_config)
		{
			i_db = com.db4o.ext.Db4oDatabase.generate();
			i_uuidGenerator = com.db4o.Unobfuscated.randomLong();
			initConfig(a_config);
			i_dirty = true;
		}

		internal virtual bool initConfig(com.db4o.Config4Impl a_config)
		{
			bool modified = false;
			if (i_generateVersionNumbers != a_config.i_generateVersionNumbers)
			{
				i_generateVersionNumbers = a_config.i_generateVersionNumbers;
				modified = true;
			}
			if (i_generateUUIDs != a_config.i_generateUUIDs)
			{
				i_generateUUIDs = a_config.i_generateUUIDs;
				modified = true;
			}
			return modified;
		}

		internal virtual com.db4o.MetaIndex getUUIDMetaIndex()
		{
			if (i_uuidMetaIndex == null)
			{
				i_uuidMetaIndex = new com.db4o.MetaIndex();
				com.db4o.Transaction systemTrans = i_stream.getSystemTransaction();
				i_stream.setInternal(systemTrans, this, false);
				systemTrans.commit();
			}
			return i_uuidMetaIndex;
		}

		internal virtual long newUUID()
		{
			i_dirty = true;
			return i_uuidGenerator++;
		}

		internal virtual void setDirty()
		{
			i_dirty = true;
		}

		internal override void store(int a_depth)
		{
			if (i_dirty)
			{
				i_versionGenerator++;
				base.store(a_depth);
			}
			i_dirty = false;
		}

		internal virtual long version()
		{
			i_dirty = true;
			return i_versionGenerator;
		}
	}
}
