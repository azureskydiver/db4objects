namespace com.db4o.ext
{
	/// <summary>a unique universal identify for an object.</summary>
	/// <remarks>
	/// a unique universal identify for an object. <br /><br />The db4o UUID consists of
	/// two parts:<br /> - an indexed long for fast access,<br /> - the signature of the
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// the object was created with.
	/// <br /><br />Db4oUUIDs are valid representations of objects over multiple
	/// ObjectContainers
	/// </remarks>
	public class Db4oUUID
	{
		private readonly long longPart;

		private readonly byte[] signaturePart;

		public Db4oUUID(long longPart_, byte[] signaturePart_)
		{
			longPart = longPart_;
			signaturePart = signaturePart_;
		}

		/// <summary>returns the long part of this UUID.</summary>
		/// <remarks>
		/// returns the long part of this UUID. <br /><br />To uniquely identify an object
		/// universally, db4o uses an indexed long and a reference to the
		/// Db4oDatabase object it was created on.
		/// </remarks>
		/// <returns>the long part of this UUID.</returns>
		public virtual long GetLongPart()
		{
			return longPart;
		}

		/// <summary>returns the signature part of this UUID.</summary>
		/// <remarks>
		/// returns the signature part of this UUID. <br /><br /> <br /><br />To uniquely
		/// identify an object universally, db4o uses an indexed long and a reference to
		/// the Db4oDatabase singleton object of the
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// it was created on. This method
		/// returns the signature of the Db4oDatabase object of the ObjectContainer: the
		/// signature of the origin ObjectContainer.
		/// </remarks>
		/// <returns>the signature of the Db4oDatabase for this UUID.</returns>
		public virtual byte[] GetSignaturePart()
		{
			return signaturePart;
		}

		public override bool Equals(object o)
		{
			if (this == o)
			{
				return true;
			}
			if (o == null || j4o.lang.JavaSystem.GetClassForObject(this) != j4o.lang.JavaSystem.GetClassForObject
				(o))
			{
				return false;
			}
			com.db4o.ext.Db4oUUID other = (com.db4o.ext.Db4oUUID)o;
			if (longPart != other.longPart)
			{
				return false;
			}
			if (signaturePart == null)
			{
				return other.signaturePart == null;
			}
			if (signaturePart.Length != other.signaturePart.Length)
			{
				return false;
			}
			for (int i = 0; i < signaturePart.Length; i++)
			{
				if (signaturePart[i] != other.signaturePart[i])
				{
					return false;
				}
			}
			return true;
		}

		public override int GetHashCode()
		{
			return (int)(longPart ^ ((longPart) >> (32 & 0x1f)));
		}

		public override string ToString()
		{
			return base.ToString();
			string sig = string.Empty;
			for (int i = 0; i < signaturePart.Length; i++)
			{
				sig += signaturePart[i] + " ";
			}
			return "long " + longPart + " ,  signature " + sig;
		}
	}
}
