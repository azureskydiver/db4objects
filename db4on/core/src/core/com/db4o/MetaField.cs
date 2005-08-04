namespace com.db4o
{
	/// <summary>Field MetaData to be stored to the database file.</summary>
	/// <remarks>
	/// Field MetaData to be stored to the database file.
	/// Don't obfuscate.
	/// </remarks>
	/// <exclude></exclude>
	/// <persistent></persistent>
	public class MetaField : com.db4o.Internal
	{
		public string name;

		public com.db4o.MetaIndex index;

		public MetaField()
		{
		}

		public MetaField(string name)
		{
			this.name = name;
		}
	}
}
