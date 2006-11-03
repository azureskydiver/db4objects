namespace com.db4o.nativequery.expr.cmp
{
	public class ConstValue : com.db4o.nativequery.expr.cmp.ComparisonOperand
	{
		private object _value;

		public ConstValue(object value)
		{
			this._value = value;
		}

		public virtual object Value()
		{
			return _value;
		}

		public virtual void Value(object value)
		{
			_value = value;
		}

		public override string ToString()
		{
			if (_value == null)
			{
				return "null";
			}
			if (_value is string)
			{
				return "\"" + _value + "\"";
			}
			return _value.ToString();
		}

		public override bool Equals(object other)
		{
			if (this == other)
			{
				return true;
			}
			if (other == null || j4o.lang.JavaSystem.GetClassForObject(this) != j4o.lang.JavaSystem.GetClassForObject
				(other))
			{
				return false;
			}
			object otherValue = ((com.db4o.nativequery.expr.cmp.ConstValue)other)._value;
			if (otherValue == _value)
			{
				return true;
			}
			if (otherValue == null || _value == null)
			{
				return false;
			}
			return _value.Equals(otherValue);
		}

		public override int GetHashCode()
		{
			return _value.GetHashCode();
		}

		public virtual void Accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.Visit(this);
		}
	}
}
