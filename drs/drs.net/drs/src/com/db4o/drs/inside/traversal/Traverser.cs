namespace com.db4o.drs.inside.traversal
{
	public interface Traverser
	{
		/// <summary>
		/// Traversal will only stop when visitor.visit(...) returns false, EVEN IN
		/// THE PRESENCE OF CIRCULAR REFERENCES.
		/// </summary>
		/// <remarks>
		/// Traversal will only stop when visitor.visit(...) returns false, EVEN IN
		/// THE PRESENCE OF CIRCULAR REFERENCES. So it is up to the visitor to detect
		/// circular references if necessary. Transient fields are not visited. The
		/// fields of second class objects such as Strings and Dates are also not visited.
		/// </remarks>
		void TraverseGraph(object _object, com.db4o.drs.inside.traversal.Visitor visitor);

		/// <summary>Should only be called during a traversal.</summary>
		/// <remarks>
		/// Should only be called during a traversal. Will traverse the graph
		/// for the received object too, using the current visitor. Used to
		/// extend the traversal to a possibly disconnected object graph.
		/// </remarks>
		void ExtendTraversalTo(object disconnected);
	}
}
