namespace Db4objects.Drs
{
	/// <summary>Facade for persistence systems that provide replication support.</summary>
	/// <remarks>
	/// Facade for persistence systems that provide replication support.
	/// Interacts with another ReplicationProvider and a  ReplicationSession
	/// to allows replication of objects between two ReplicationProviders.
	/// <p/>
	/// <p/> To create an instance of this class, use the methods of
	/// <see cref="Db4objects.Drs.Replication">Db4objects.Drs.Replication</see>
	/// .
	/// </remarks>
	/// <author>Albert Kwan</author>
	/// <author>Klaus Wuestefeld</author>
	/// <version>1.2</version>
	/// <seealso cref="Db4objects.Drs.IReplicationSession">Db4objects.Drs.IReplicationSession
	/// 	</seealso>
	/// <seealso cref="Db4objects.Drs.Replication">Db4objects.Drs.Replication</seealso>
	/// <since>dRS 1.0</since>
	public interface IReplicationProvider
	{
		/// <summary>Returns newly created objects and changed objects since last replication with the opposite provider.
		/// 	</summary>
		/// <remarks>Returns newly created objects and changed objects since last replication with the opposite provider.
		/// 	</remarks>
		/// <returns>newly created objects and changed objects since last replication with the opposite provider.
		/// 	</returns>
		Db4objects.Db4o.IObjectSet ObjectsChangedSinceLastReplication();

		/// <summary>Returns newly created objects and changed objects since last replication with the opposite provider.
		/// 	</summary>
		/// <remarks>Returns newly created objects and changed objects since last replication with the opposite provider.
		/// 	</remarks>
		/// <param name="clazz">the type of objects interested</param>
		/// <returns>newly created objects and changed objects of the type specified in the clazz parameter since last replication
		/// 	</returns>
		Db4objects.Db4o.IObjectSet ObjectsChangedSinceLastReplication(System.Type clazz);
	}
}
