namespace com.db4o.inside.query
{
	public interface ICachingStrategy
	{
		void Add(object key, object item);
		object Get(object key);
	}
}