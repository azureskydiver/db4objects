namespace com.db4o.nativequery.expr
{
	public class OrExpression : com.db4o.nativequery.expr.Expression
	{
		public interface Visitor : com.db4o.nativequery.expr.ExpressionVisitor
		{
			void visit(com.db4o.nativequery.expr.OrExpression expression);
		}

		private com.db4o.nativequery.expr.Expression _left;

		private com.db4o.nativequery.expr.Expression _right;

		public OrExpression(com.db4o.nativequery.expr.Expression left, com.db4o.nativequery.expr.Expression
			 right)
		{
			this._left = left;
			this._right = right;
		}

		public virtual com.db4o.nativequery.expr.Expression left()
		{
			return _left;
		}

		public virtual com.db4o.nativequery.expr.Expression right()
		{
			return _right;
		}

		public override string ToString()
		{
			return "(" + _left + ")||(" + _right + ")";
		}

		public override bool Equals(object other)
		{
			if (this == other)
			{
				return true;
			}
			if (other == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject
				(other))
			{
				return false;
			}
			com.db4o.nativequery.expr.OrExpression casted = (com.db4o.nativequery.expr.OrExpression
				)other;
			return _left.Equals(casted._left) && (_right.Equals(casted._right)) || _left.Equals
				(casted._right) && (_right.Equals(casted._left));
		}

		public override int GetHashCode()
		{
			return _left.GetHashCode() + _right.GetHashCode();
		}

		public virtual void accept(com.db4o.nativequery.expr.ExpressionVisitor visitor)
		{
			((com.db4o.nativequery.expr.OrExpression.Visitor)visitor).visit(this);
		}
	}
}
