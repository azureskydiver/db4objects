/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com */

using Db4objects.Db4o.Query;

namespace Db4objects.Db4o.Query
{
	/// <summary>
	/// constraint to limit the objects returned upon
	/// <see cref="Db4objects.Db4o.Query.IQuery.Execute">query execution</see>
	/// .
	/// <br/><br/>
	/// Constraints are constructed by calling
	/// <see cref="Db4objects.Db4o.Query.IQuery.Constrain">Db4objects.Db4o.Query.IQuery.Constrain
	/// </see>
	/// .
	/// <br/><br/>
	/// Constraints can be joined with the methods
	/// <see cref="Db4objects.Db4o.Query.IConstraint.And">Db4objects.Db4o.Query.IConstraint.And
	/// </see>
	/// and
	/// <see cref="Db4objects.Db4o.Query.IConstraint.Or">Db4objects.Db4o.Query.IConstraint.Or
	/// </see>
	/// .
	/// <br/><br/>
	/// The methods to modify the constraint evaluation algorithm may
	/// be merged, to construct combined evaluation rules.
	/// Examples:
	/// <ul>
	/// <li> <code>Constraint.Smaller().Equal()</code> for "smaller or equal" </li>
	/// <li> <code>Constraint.Not().Like()</code> for "not like" </li>
	/// <li> <code>Constraint.Not().Greater().Equal()</code> for "not greater or equal" </li>
	/// </ul>
	/// </summary>
	public interface IConstraint
	{
		/// <summary>links two Constraints for AND evaluation.</summary>
		/// <remarks>
		/// links two Constraints for AND evaluation.
		/// For example:<br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>query.Descend("points").Constrain(101).Smaller().And(query.Descend("name").Constrain("Test Pilot0"));	</code><br/>
		/// will retrieve all pilots with points less than 101 and name as "Test Pilot0"<br/>
		/// </remarks>
		/// <param name="with">
		/// the other
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// </param>
		/// <returns>
		/// a new
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// , that can be used for further calls
		/// to
		/// <see cref="Db4objects.Db4o.Query.IConstraint.And">And</see>
		/// and
		/// <see cref="Db4objects.Db4o.Query.IConstraint.Or">Or</see>
		/// </returns>
		IConstraint And(IConstraint with);

		/// <summary>links two Constraints for OR evaluation.</summary>
		/// <remarks>
		/// links two Constraints for OR evaluation.
		/// For example:<br/><br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>query.Descend("points").Constrain(101).Greater().Or(query.Descend("name").Constrain("Test Pilot0"));</code><br/>
		/// will retrieve all pilots with points more than 101 or pilots with the name "Test Pilot0"<br/>
		/// </remarks>
		/// <param name="with">
		/// the other
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// </param>
		/// <returns>
		/// a new
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// , that can be used for further calls
		/// to
		/// <see cref="Db4objects.Db4o.Query.IConstraint.And">And</see>
		/// and
		/// <see cref="Db4objects.Db4o.Query.IConstraint.Or">Or</see>
		/// </returns>
		IConstraint Or(IConstraint with);

		/// <summary>
		/// Used in conjunction with
		/// <see cref="Db4objects.Db4o.Query.IConstraint.Smaller">Db4objects.Db4o.Query.IConstraint.Smaller
		/// </see>
		/// or
		/// <see cref="Db4objects.Db4o.Query.IConstraint.Greater">Db4objects.Db4o.Query.IConstraint.Greater
		/// </see>
		/// to create constraints
		/// like "smaller or equal", "greater or equal".
		/// For example:<br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>query.Descend("points").Constrain(101).Smaller().Equal();</code><br/>
		/// will return all pilots with points &lt;= 101.<br/>
		/// </summary>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint Equal();

		/// <summary>sets the evaluation mode to <code>&gt;</code>.</summary>
		/// <remarks>
		/// sets the evaluation mode to <code>&gt;</code>.
		/// For example:<br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>query.Descend("points").Constrain(101).Greater()</code><br/>
		/// will return all pilots with points &gt; 101.<br/>
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint Greater();

		/// <summary>sets the evaluation mode to <code>&lt;</code>.</summary>
		/// <remarks>
		/// sets the evaluation mode to <code>&lt;</code>.
		/// For example:<br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>query.Descend("points").Constrain(101).Smaller()</code><br/>
		/// will return all pilots with points &lt; 101.<br/>
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint Smaller();

		/// <summary>sets the evaluation mode to identity comparison.</summary>
		/// <remarks>
		/// sets the evaluation mode to identity comparison. In this case only
		/// objects having the same database identity will be included in the result set.
		/// For example:<br/>
		/// <code>Pilot pilot = new Pilot("Test Pilot1", 100);</code><br/>
		/// <code>Car car = new Car("BMW", pilot);</code><br/>
		/// <code>container.Store(car);</code><br/>
		/// <code>// Change the name, the pilot instance stays the same</code><br/>
		/// <code>pilot.SetName("Test Pilot2");</code><br/>
		/// <code>// create a new car</code><br/>
		/// <code>car = new Car("Ferrari", pilot);</code><br/>
		/// <code>container.Store(car);</code><br/>
		/// <code>IQuery query = container.Query();</code><br/>
		/// <code>query.Constrain(typeof(Car));</code><br/>
		/// <code>// All cars having pilot with the same database identity</code><br/>
		/// <code>// will be retrieved. As we only created Pilot object once</code><br/>
		/// <code>// it should mean all car objects</code><br/>
		/// <code>query.Descend("_pilot").Constrain(pilot).Identity();</code><br/><br/>
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint Identity();

		/// <summary>set the evaluation mode to object comparison (query by example).</summary>
		/// <remarks>set the evaluation mode to object comparison (query by example).</remarks>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint ByExample();

		/// <summary>sets the evaluation mode to "like" comparison.</summary>
		/// <remarks>
		/// sets the evaluation mode to "like" comparison. This mode will include
		/// all objects having the constrain expression somewhere inside the string field.
		/// For example:<br/>
		/// <code>Pilot pilot = new Pilot("Test Pilot1", 100);</code><br/>
		/// <code>container.Store(pilot);</code><br/>
		/// <code> ...</code><br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>// All pilots with the name containing "est" will be retrieved</code><br/>
		/// <code>query.Descend("name").Constrain("est").Like();</code><br/>
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint Like();

		/// <summary>sets the evaluation mode to containment comparison.</summary>
		/// <remarks>
		/// sets the evaluation mode to containment comparison.
		/// For example:<br/>
		/// <code>Pilot pilot1 = new Pilot("Test 1", 1);</code><br/>
		/// <code>list.Add(pilot1);</code><br/>
		/// <code>Pilot pilot2 = new Pilot("Test 2", 2);</code><br/>
		/// <code>list.Add(pilot2);</code><br/>
		/// <code>Team team = new Team("Ferrari", list);</code><br/>
		/// <code>container.Store(team);</code><br/>
		/// <code>IQuery query = container.Query();</code><br/>
		/// <code>query.Constrain(typeof(Team));</code><br/>
		/// <code>query.Descend("pilots").Constrain(pilot2).Contains();</code><br/>
		/// will return the Team object as it contains pilot2.<br/>
		/// If applied to a String object, this constrain will behave as
		/// <see cref="Db4objects.Db4o.Query.IConstraint.Like">Db4objects.Db4o.Query.IConstraint.Like
		/// </see>
		/// .
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint Contains();

		/// <summary>sets the evaluation mode to string StartsWith comparison.</summary>
		/// <remarks>
		/// sets the evaluation mode to string StartsWith comparison.
		/// For example:<br/>
		/// <code>Pilot pilot = new Pilot("Test Pilot0", 100);</code><br/>
		/// <code>container.Store(pilot);</code><br/>
		/// <code> ...</code><br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>query.Descend("name").Constrain("Test").StartsWith(true);</code><br/>
		/// </remarks>
		/// <param name="caseSensitive">comparison will be case sensitive if true, case insensitive otherwise
		/// </param>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint StartsWith(bool caseSensitive);

		/// <summary>sets the evaluation mode to string EndsWith comparison.</summary>
		/// <remarks>
		/// sets the evaluation mode to string EndsWith comparison.
		/// For example:<br/>
		/// <code>Pilot pilot = new Pilot("Test Pilot0", 100);</code><br/>
		/// <code>container.Store(pilot);</code><br/>
		/// <code> ...</code><br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>query.Descend("name").Constrain("T0").EndsWith(false);</code><br/>
		/// </remarks>
		/// <param name="caseSensitive">comparison will be case sensitive if true, case insensitive otherwise
		/// </param>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint EndsWith(bool caseSensitive);

		/// <summary>turns on Not() comparison.</summary>
		/// <remarks>
		/// turns on Not() comparison. All objects not fullfilling the constrain condition will be returned.
		/// For example:<br/>
		/// <code>Pilot pilot = new Pilot("Test Pilot1", 100);</code><br/>
		/// <code>container.Store(pilot);</code><br/>
		/// <code> ...</code><br/>
		/// <code>query.Constrain(typeof(Pilot));</code><br/>
		/// <code>query.Descend("name").Constrain("t0").EndsWith(true).Not();</code><br/>
		/// </remarks>
		/// <returns>
		/// this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// to allow the chaining of method calls.
		/// </returns>
		IConstraint Not();

		/// <summary>
		/// returns the Object the query graph was constrained with to
		/// create this
		/// <see cref="Db4objects.Db4o.Query.IConstraint">Db4objects.Db4o.Query.IConstraint</see>
		/// .
		/// </summary>
		/// <returns>Object the constraining object.</returns>
		object GetObject();
	}
}
