namespace Db4objects.Db4o.CS.Messages
{
	public class MObjectByUuid : Db4objects.Db4o.CS.Messages.MsgD
	{
		public sealed override bool ProcessAtServer(Db4objects.Db4o.CS.YapServerThread serverThread
			)
		{
			long uuid = ReadLong();
			byte[] signature = ReadBytes();
			int id = 0;
			Db4objects.Db4o.Transaction trans = Transaction();
			lock (StreamLock())
			{
				try
				{
					object[] arr = trans.ObjectAndYapObjectBySignature(uuid, signature);
					if (arr[1] != null)
					{
						Db4objects.Db4o.YapObject yo = (Db4objects.Db4o.YapObject)arr[1];
						id = yo.GetID();
					}
				}
				catch (System.Exception e)
				{
				}
			}
			serverThread.Write(Db4objects.Db4o.CS.Messages.Msg.OBJECT_BY_UUID.GetWriterForInt
				(trans, id));
			return true;
		}
	}
}
