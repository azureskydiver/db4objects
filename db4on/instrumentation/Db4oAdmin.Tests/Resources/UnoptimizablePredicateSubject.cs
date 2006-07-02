/* Copyright (C) 2004 - 2006  db4objects Inc.   http://www.db4o.com */

using com.db4o;
using Db4oUnit;

public class Item
{
	private string _name;

	public Item(string name)
	{
		_name = name;
	}

	public string Name
	{
		get { return _name; }
	}
}

class ByUpperNameUnoptimizable : com.db4o.query.Predicate
{
	public bool Match(Item candidate)
	{
		return candidate.Name.ToUpper() == "FOO";
	}
}

class ByName : com.db4o.query.Predicate
{
	public bool Match(Item candidate)
	{
		return candidate.Name == "bar";
	}
}

public class UnoptimizablePredicateSubject : Db4oAdmin.Tests.InstrumentedTestCase
{
	override public void SetUp()
	{
		_container.Set(new Item("foo"));
		_container.Set(new Item("bar"));
	}
	
	public void TestByUpperName()
	{
		ObjectSet result = _container.Query(new ByUpperNameUnoptimizable());
		Assert.AreEqual(1, result.Count);
		Assert.AreEqual("foo", (result[0] as Item).Name);
	}
	
	public void TestByName()
	{
		ObjectSet result = _container.Query(new ByName());
		Assert.AreEqual(1, result.Count);
		Assert.AreEqual("bar", (result[0] as Item).Name);
	}
}