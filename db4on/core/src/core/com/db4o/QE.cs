namespace com.db4o
{
	/// <summary>Query Evaluator - Represents such things as &gt;, &gt;=, &lt;, &lt;=, EQUAL, LIKE, etc.
	/// 	</summary>
	/// <remarks>Query Evaluator - Represents such things as &gt;, &gt;=, &lt;, &lt;=, EQUAL, LIKE, etc.
	/// 	</remarks>
	/// <exclude></exclude>
	public class QE
	{
		internal static readonly com.db4o.QE DEFAULT = new com.db4o.QE();

		internal virtual com.db4o.QE add(com.db4o.QE evaluator)
		{
			return evaluator;
		}

		internal virtual bool identity()
		{
			return false;
		}

		internal virtual bool isDefault()
		{
			return true;
		}

		internal virtual bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (a_value == null)
			{
				return a_constraint.getComparator(a_candidate) is com.db4o.Null;
			}
			return a_constraint.getComparator(a_candidate).isEqual(a_value);
		}

		public override bool Equals(object obj)
		{
			return j4o.lang.Class.getClassForObject(obj) == j4o.lang.Class.getClassForObject(
				this);
		}

		internal virtual bool not(bool res)
		{
			return res;
		}

		/// <summary>Specifies which part of the index to take.</summary>
		/// <remarks>
		/// Specifies which part of the index to take.
		/// Array elements:
		/// [0] - smaller
		/// [1] - equal
		/// [2] - greater
		/// [3] - nulls
		/// </remarks>
		/// <param name="bits"></param>
		internal virtual void indexBitMap(bool[] bits)
		{
			bits[1] = true;
		}

		internal virtual bool supportsIndex()
		{
			return true;
		}
	}
}
