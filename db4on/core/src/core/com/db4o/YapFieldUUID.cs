namespace com.db4o
{
	internal class YapFieldUUID : com.db4o.YapFieldVirtual
	{
		private const int LINK_LENGTH = com.db4o.YapConst.YAPLONG_LENGTH + com.db4o.YapConst
			.YAPID_LENGTH;

		internal YapFieldUUID(com.db4o.YapStream stream) : base()
		{
			i_name = com.db4o.YapConst.VIRTUAL_FIELD_PREFIX + "uuid";
			i_handler = new com.db4o.YLong(stream);
		}

		internal override void addFieldIndex(com.db4o.YapWriter a_writer, bool a_new)
		{
			int offset = a_writer._offset;
			int id = a_writer.readInt();
			long uuid = com.db4o.YLong.readLong(a_writer);
			a_writer._offset = offset;
			com.db4o.YapFile yf = (com.db4o.YapFile)a_writer.getStream();
			if (id == 0)
			{
				a_writer.writeInt(yf.identity().getID(a_writer.getTransaction()));
			}
			else
			{
				a_writer.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
			}
			if (uuid == 0)
			{
				uuid = yf.bootRecord().newUUID();
			}
			com.db4o.YLong.writeLong(uuid, a_writer);
			if (a_new)
			{
				addIndexEntry(a_writer, uuid);
			}
		}

		internal override void delete(com.db4o.YapWriter a_bytes, bool isUpdate)
		{
			if (isUpdate)
			{
				a_bytes.incrementOffset(linkLength());
				return;
			}
			a_bytes.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH);
			long longPart = com.db4o.YLong.readLong(a_bytes);
			if (longPart > 0)
			{
				com.db4o.YapStream stream = a_bytes.getStream();
				if (stream.maintainsIndices())
				{
					removeIndexEntry(a_bytes.getTransaction(), a_bytes.getID(), longPart);
				}
			}
		}

		internal override com.db4o.inside.ix.Index4 getIndex(com.db4o.Transaction a_trans
			)
		{
			com.db4o.YapFile stream = (com.db4o.YapFile)a_trans.i_stream;
			if (i_index == null)
			{
				i_index = new com.db4o.inside.ix.Index4(stream.getSystemTransaction(), getHandler
					(), stream.bootRecord().getUUIDMetaIndex(), false);
			}
			return i_index;
		}

		internal override void instantiate1(com.db4o.Transaction a_trans, com.db4o.YapObject
			 a_yapObject, com.db4o.YapReader a_bytes)
		{
			int dbID = a_bytes.readInt();
			com.db4o.YapStream stream = a_trans.i_stream;
			stream.showInternalClasses(true);
			com.db4o.ext.Db4oDatabase db = (com.db4o.ext.Db4oDatabase)stream.getByID2(a_trans
				, dbID);
			if (db != null && db.i_signature == null)
			{
				stream.activate2(a_trans, db, 2);
			}
			a_yapObject.i_virtualAttributes.i_database = db;
			a_yapObject.i_virtualAttributes.i_uuid = com.db4o.YLong.readLong(a_bytes);
			stream.showInternalClasses(false);
		}

		public override int linkLength()
		{
			return LINK_LENGTH;
		}

		internal override void marshall1(com.db4o.YapObject a_yapObject, com.db4o.YapWriter
			 a_bytes, bool a_migrating, bool a_new)
		{
			com.db4o.YapStream stream = a_bytes.getStream();
			com.db4o.Transaction trans = a_bytes.getTransaction();
			bool indexEntry = a_new && stream.maintainsIndices();
			int dbID = 0;
			com.db4o.VirtualAttributes attr = a_yapObject.i_virtualAttributes;
			bool linkToDatabase = !a_migrating;
			if (attr != null && attr.i_database == null)
			{
				linkToDatabase = true;
			}
			if (linkToDatabase)
			{
				com.db4o.ext.Db4oDatabase db = stream.identity();
				if (db == null)
				{
					attr = null;
				}
				else
				{
					if (attr.i_database == null)
					{
						attr.i_database = db;
						if (stream is com.db4o.YapFile)
						{
							com.db4o.PBootRecord br = stream.bootRecord();
							if (br != null)
							{
								attr.i_uuid = br.newUUID();
								indexEntry = true;
							}
						}
					}
					db = attr.i_database;
					if (db != null)
					{
						dbID = db.getID(trans);
					}
				}
			}
			else
			{
				if (attr != null)
				{
					dbID = attr.i_database.getID(trans);
				}
			}
			a_bytes.writeInt(dbID);
			if (attr != null)
			{
				com.db4o.YLong.writeLong(attr.i_uuid, a_bytes);
				if (indexEntry)
				{
					addIndexEntry(a_bytes, attr.i_uuid);
				}
			}
			else
			{
				com.db4o.YLong.writeLong(0, a_bytes);
			}
		}

		internal override void marshallIgnore(com.db4o.YapWriter writer)
		{
			writer.writeInt(0);
			com.db4o.YLong.writeLong(0, writer);
		}
	}
}
