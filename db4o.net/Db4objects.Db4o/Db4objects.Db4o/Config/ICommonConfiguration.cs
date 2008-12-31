/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com */

using System.IO;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Config.Encoding;
using Db4objects.Db4o.Diagnostic;
using Db4objects.Db4o.Reflect;
using Db4objects.Db4o.Typehandlers;

namespace Db4objects.Db4o.Config
{
	/// <summary>
	/// Common configuration methods, applicable for
	/// embedded, client and server use of db4o.<br /><br />
	/// In Client/Server use it is good practice to configure the
	/// client and the server in exactly the same way.
	/// </summary>
	/// <remarks>
	/// Common configuration methods, applicable for
	/// embedded, client and server use of db4o.<br /><br />
	/// In Client/Server use it is good practice to configure the
	/// client and the server in exactly the same way.
	/// </remarks>
	/// <since>7.5</since>
	public interface ICommonConfiguration
	{
		/// <summary>adds a new Alias for a class, namespace or package.</summary>
		/// <remarks>
		/// adds a new Alias for a class, namespace or package.
		/// <br /><br />Aliases can be used to persist classes in the running
		/// application to different persistent classes in a database file
		/// or on a db4o server.
		/// <br /><br />Two simple Alias implementations are supplied along with
		/// db4o:<br />
		/// -
		/// <see cref="Db4objects.Db4o.Config.TypeAlias">Db4objects.Db4o.Config.TypeAlias</see>
		/// provides an #equals() resolver to match
		/// names directly.<br />
		/// -
		/// <see cref="Db4objects.Db4o.Config.WildcardAlias">Db4objects.Db4o.Config.WildcardAlias
		/// 	</see>
		/// allows simple pattern matching
		/// with one single '*' wildcard character.<br />
		/// <br />
		/// It is possible to create
		/// own complex
		/// <see cref="Db4objects.Db4o.Config.IAlias">Db4objects.Db4o.Config.IAlias</see>
		/// constructs by creating own resolvers
		/// that implement the
		/// <see cref="Db4objects.Db4o.Config.IAlias">Db4objects.Db4o.Config.IAlias</see>
		/// interface.
		/// <br /><br />
		/// Examples of concrete usecases:
		/// <br /><br />
		/// <code>
		/// <b>// Creating an Alias for a single class</b><br />
		/// Db4o.configure().addAlias(<br />
		/// &#160;&#160;new TypeAlias("com.f1.Pilot", "com.f1.Driver"));<br />
		/// <br /><br />
		/// <b>// Accessing a .NET assembly from a Java package</b><br />
		/// Db4o.configure().addAlias(<br />
		/// &#160;&#160;new WildcardAlias(<br />
		/// &#160;&#160;&#160;&#160;"Tutorial.F1.*, Tutorial",<br />
		/// &#160;&#160;&#160;&#160;"com.f1.*"));<br />
		/// <br /><br />
		/// <b>// Mapping a Java package onto another</b><br />
		/// Db4o.configure().addAlias(<br />
		/// &#160;&#160;new WildcardAlias(<br />
		/// &#160;&#160;&#160;&#160;"com.f1.*",<br />
		/// &#160;&#160;&#160;&#160;"com.f1.client*"));<br /></code>
		/// <br /><br />Aliases that translate the persistent name of a class to
		/// a name that already exists as a persistent name in the database
		/// (or on the server) are not permitted and will throw an exception
		/// when the database file is opened.
		/// <br /><br />Aliases should be configured before opening a database file
		/// or connecting to a server.<br /><br />
		/// In client/server environment this setting should be used on the client
		/// and on the server side.
		/// </remarks>
		void AddAlias(IAlias alias);

		/// <summary>
		/// Removes an alias previously added with
		/// <see cref="Db4objects.Db4o.Config.IConfiguration.AddAlias">Db4objects.Db4o.Config.IConfiguration.AddAlias
		/// 	</see>
		/// .
		/// </summary>
		/// <param name="alias">the alias to remove</param>
		void RemoveAlias(IAlias alias);

		/// <summary>sets the activation depth to the specified value.</summary>
		/// <remarks>
		/// sets the activation depth to the specified value.
		/// <br /><br /><b>Why activation?</b><br />
		/// When objects are instantiated from the database, the instantiation of member
		/// objects needs to be limited to a certain depth. Otherwise a single object
		/// could lead to loading the complete database into memory, if all objects where
		/// reachable from a single root object.<br /><br />
		/// db4o uses the concept "depth", the number of field-to-field hops an object
		/// is away from another object. <b>The preconfigured "activation depth" db4o uses
		/// in the default setting is 5.</b>
		/// <br /><br />Whenever an application iterates through the
		/// <see cref="Db4objects.Db4o.IObjectSet">IObjectSet</see>
		/// of a query result, the result objects
		/// will be activated to the configured activation depth.<br /><br />
		/// A concrete example with the preconfigured activation depth of 5:<br />
		/// <pre>
		/// // Object foo is the result of a query, it is delivered by the ObjectSet
		/// Object foo = objectSet.next();</pre>
		/// foo.member1.member2.member3.member4.member5 will be a valid object<br />
		/// foo, member1, member2, member3 and member4 will be activated<br />
		/// member5 will be deactivated, all of it's members will be null<br />
		/// member5 can be activated at any time by calling
		/// <see cref="Db4objects.Db4o.IObjectContainer.Activate">ObjectContainer#activate(member5, depth)
		/// 	</see>
		/// .
		/// <br /><br />
		/// Note that raising the global activation depth will consume more memory and
		/// have negative effects on the performance of first-time retrievals. Lowering
		/// the global activation depth needs more individual activation work but can
		/// increase performance of queries.<br /><br />
		/// <see cref="Db4objects.Db4o.IObjectContainer.Deactivate">ObjectContainer#deactivate(Object, depth)
		/// 	</see>
		/// can be used to manually free memory by deactivating objects.<br /><br />
		/// In client/server environment the same setting should be used on both
		/// client and server<br /><br />.
		/// </remarks>
		/// <param name="depth">the desired global activation depth.</param>
		/// <seealso cref="Db4objects.Db4o.Config.IObjectClass.MaximumActivationDepth">configuring classes individually
		/// 	</seealso>
		/// <summary>gets the configured activation depth.</summary>
		/// <remarks>gets the configured activation depth.</remarks>
		/// <returns>the configured activation depth.</returns>
		int ActivationDepth
		{
			get;
			set;
		}

		/// <summary>
		/// adds ConfigurationItems to be applied when
		/// an ObjectContainer or ObjectServer is opened.
		/// </summary>
		/// <remarks>
		/// adds ConfigurationItems to be applied when
		/// an ObjectContainer or ObjectServer is opened.
		/// </remarks>
		/// <param name="configurationItem">the ConfigurationItem</param>
		void Add(IConfigurationItem configurationItem);

		/// <summary>turns automatic database file format version updates on.</summary>
		/// <remarks>
		/// turns automatic database file format version updates on.
		/// <br /><br />Upon db4o database file format version changes,
		/// db4o can automatically update database files to the
		/// current version. db4objects does not provide functionality
		/// to reverse this process. It is not ensured that updated
		/// database files can be read with older db4o versions.
		/// In some cases (Example: using ObjectManager) it may not be
		/// desirable to update database files automatically therefore
		/// automatic updating is turned off by default for
		/// security reasons.
		/// <br /><br />Call this method to turn automatic database file
		/// version updating on.
		/// <br /><br />If automatic updating is turned off, db4o will refuse
		/// to open database files that use an older database file format.<br /><br />
		/// In client-server environment this setting should be used on both client
		/// and server.
		/// </remarks>
		bool AllowVersionUpdates
		{
			set;
		}

		/// <summary>turns automatic shutdown of the engine on and off.</summary>
		/// <remarks>
		/// turns automatic shutdown of the engine on and off.
		/// <br /><br />Depending on the JDK, db4o uses one of the following
		/// two methods to shut down, if no more references to the ObjectContainer
		/// are being held or the JVM terminates:<br />
		/// - JDK 1.3 and above: <code>Runtime.addShutdownHook()</code><br />
		/// - JDK 1.2 and below: <code>System.runFinalizersOnExit(true)</code> and code
		/// in the finalizer.<br /><br />
		/// Some JVMs have severe problems with both methods. For these rare cases the
		/// autoShutDown feature may be turned off.<br /><br />
		/// The default and recommended setting is <code>true</code>.<br /><br />
		/// In client-server environment this setting should be used on both client
		/// and server.
		/// </remarks>
		/// <param name="flag">whether db4o should shut down automatically.</param>
		bool AutomaticShutDown
		{
			set;
		}

		/// <summary>configures the size of BTree nodes in indexes.</summary>
		/// <remarks>
		/// configures the size of BTree nodes in indexes.
		/// <br /><br />Default setting: 100
		/// <br />Lower values will allow a lower memory footprint
		/// and more efficient reading and writing of small slots.
		/// <br />Higher values will reduce the overall number of
		/// read and write operations and allow better performance
		/// at the cost of more RAM use.<br /><br />
		/// This setting should be used on both client and server in
		/// client-server environment.
		/// </remarks>
		/// <param name="size">the number of elements held in one BTree node.</param>
		int BTreeNodeSize
		{
			set;
		}

		/// <summary>turns callback methods on and off.</summary>
		/// <remarks>
		/// turns callback methods on and off.
		/// <br /><br />Callbacks are turned on by default.<br /><br />
		/// A tuning hint: If callbacks are not used, you can turn this feature off, to
		/// prevent db4o from looking for callback methods in persistent classes. This will
		/// increase the performance on system startup.<br /><br />
		/// In client/server environment this setting should be used on both
		/// client and server.
		/// </remarks>
		/// <param name="flag">false to turn callback methods off</param>
		/// <seealso cref="Db4objects.Db4o.Ext.IObjectCallbacks">Using callbacks</seealso>
		bool Callbacks
		{
			set;
		}

		/// <summary>
		/// advises db4o to try instantiating objects with/without calling
		/// constructors.
		/// </summary>
		/// <remarks>
		/// advises db4o to try instantiating objects with/without calling
		/// constructors.
		/// <br /><br />
		/// Not all JDKs / .NET-environments support this feature. db4o will
		/// attempt, to follow the setting as good as the enviroment supports.
		/// In doing so, it may call implementation-specific features like
		/// sun.reflect.ReflectionFactory#newConstructorForSerialization on the
		/// Sun Java 1.4.x/5 VM (not available on other VMs) and
		/// FormatterServices.GetUninitializedObject() on
		/// the .NET framework (not available on CompactFramework).
		/// This setting may also be overridden for individual classes in
		/// <see cref="Db4objects.Db4o.Config.IObjectClass.CallConstructor">Db4objects.Db4o.Config.IObjectClass.CallConstructor
		/// 	</see>
		/// .
		/// <br /><br />The default setting depends on the features supported by your current environment.<br /><br />
		/// In client/server environment this setting should be used on both
		/// client and server.
		/// <br /><br />
		/// </remarks>
		/// <param name="flag">
		/// - specify true, to request calling constructors, specify
		/// false to request <b>not</b> calling constructors.
		/// </param>
		/// <seealso cref="Db4objects.Db4o.Config.IObjectClass.CallConstructor">Db4objects.Db4o.Config.IObjectClass.CallConstructor
		/// 	</seealso>
		bool CallConstructors
		{
			set;
		}

		/// <summary>
		/// tuning feature: configures whether db4o checks all persistent classes upon system
		/// startup, for added or removed fields.
		/// </summary>
		/// <remarks>
		/// tuning feature: configures whether db4o checks all persistent classes upon system
		/// startup, for added or removed fields.
		/// <br /><br />If this configuration setting is set to false while a database is
		/// being created, members of classes will not be detected and stored.
		/// <br /><br />This setting can be set to false in a production environment after
		/// all persistent classes have been stored at least once and classes will not
		/// be modified any further in the future.<br /><br />
		/// In a client/server environment this setting should be configured both on the
		/// client and and on the server.
		/// <br /><br />Default value:<br />
		/// <code>true</code>
		/// </remarks>
		/// <param name="flag">the desired setting</param>
		bool DetectSchemaChanges
		{
			set;
		}

		/// <summary>returns the configuration interface for diagnostics.</summary>
		/// <remarks>returns the configuration interface for diagnostics.</remarks>
		/// <returns>
		/// the configuration interface for diagnostics.
		/// TODO: refactor to use provider?
		/// </returns>
		IDiagnosticConfiguration Diagnostic
		{
			get;
		}

		/// <summary>configures whether Exceptions are to be thrown, if objects can not be stored.
		/// 	</summary>
		/// <remarks>
		/// configures whether Exceptions are to be thrown, if objects can not be stored.
		/// <br /><br />db4o requires the presence of a constructor that can be used to
		/// instantiate objects. If no default public constructor is present, all
		/// available constructors are tested, whether an instance of the class can
		/// be instantiated. Null is passed to all constructor parameters.
		/// The first constructor that is successfully tested will
		/// be used throughout the running db4o session. If an instance of the class
		/// can not be instantiated, the object will not be stored. By default,
		/// execution will continue without any message or error. This method can
		/// be used to configure db4o to throw an
		/// <see cref="Db4objects.Db4o.Ext.ObjectNotStorableException">ObjectNotStorableException
		/// 	</see>
		/// if an object can not be stored.
		/// <br /><br />
		/// The default for this setting is <b>false</b>.<br /><br />
		/// In client/server environment this setting should be used on both
		/// client and server.<br /><br />
		/// </remarks>
		/// <param name="flag">true to throw Exceptions if objects can not be stored.</param>
		bool ExceptionsOnNotStorable
		{
			set;
		}

		/// <summary>configures db4o to call #intern() on strings upon retrieval.</summary>
		/// <remarks>
		/// configures db4o to call #intern() on strings upon retrieval.
		/// In client/server environment the setting should be used on both
		/// client and server.
		/// </remarks>
		/// <param name="flag">true to intern strings</param>
		bool InternStrings
		{
			set;
		}

		/// <summary>allows to mark fields as transient with custom attributes.</summary>
		/// <remarks>
		/// allows to mark fields as transient with custom attributes.
		/// <br /><br />.NET only: Call this method with the attribute name that you
		/// wish to use to mark fields as transient. Multiple transient attributes
		/// are possible by calling this method multiple times with different
		/// attribute names.<br /><br />
		/// In client/server environment the setting should be used on both
		/// client and server.<br /><br />
		/// </remarks>
		/// <param name="attributeName">
		/// - the fully qualified name of the attribute, including
		/// it's namespace
		/// TODO: can we provide meaningful java side semantics for this one?
		/// TODO: USE A CLASS!!!!!!
		/// </param>
		void MarkTransient(string attributeName);

		/// <summary>sets the detail level of db4o messages.</summary>
		/// <remarks>
		/// sets the detail level of db4o messages. Messages will be output to the
		/// configured output
		/// <see cref="System.IO.TextWriter">TextWriter</see>
		/// .
		/// <br /><br />
		/// Level 0 - no messages<br />
		/// Level 1 - open and close messages<br />
		/// Level 2 - messages for new, update and delete<br />
		/// Level 3 - messages for activate and deactivate<br /><br />
		/// When using client-server and the level is set to 0, the server will override this and set it to 1.  To get around this you can set the level to -1.  This has the effect of not returning any messages.<br /><br />
		/// In client-server environment this setting can be used on client or on server
		/// depending on which information do you want to track (server side provides more
		/// detailed information).<br /><br />
		/// </remarks>
		/// <param name="level">integer from 0 to 3</param>
		/// <seealso cref="Db4objects.Db4o.Config.ICommonConfiguration.OutStream">TODO: replace int with enumeration
		/// 	</seealso>
		int MessageLevel
		{
			set;
		}

		/// <summary>
		/// returns an
		/// <see cref="Db4objects.Db4o.Config.IObjectClass">IObjectClass</see>
		/// object
		/// to configure the specified class.
		/// <br /><br />
		/// The clazz parameter can be any of the following:<br />
		/// - a fully qualified classname as a String.<br />
		/// - a Class object.<br />
		/// - any other object to be used as a template.<br /><br />
		/// </summary>
		/// <param name="clazz">class name, Class object, or example object.<br /><br /></param>
		/// <returns>
		/// an instance of an
		/// <see cref="Db4objects.Db4o.Config.IObjectClass">IObjectClass</see>
		/// object for configuration.
		/// </returns>
		IObjectClass ObjectClass(object clazz);

		/// <summary>
		/// If set to true, db4o will try to optimize native queries
		/// dynamically at query execution time, otherwise it will
		/// run native queries in unoptimized mode as SODA evaluations.
		/// </summary>
		/// <remarks>
		/// If set to true, db4o will try to optimize native queries
		/// dynamically at query execution time, otherwise it will
		/// run native queries in unoptimized mode as SODA evaluations.
		/// On the Java platform the jars needed for native query
		/// optimization (db4o-X.x-nqopt.jar, bloat-X.x.jar) have to be
		/// on the classpath at runtime for this
		/// switch to have effect.
		/// <br /><br />The default setting is <code>true</code>.<br /><br />
		/// In client-server environment this setting should be used on both client and server.<br /><br />
		/// </remarks>
		/// <param name="optimizeNQ">
		/// true, if db4o should try to optimize
		/// native queries at query execution time, false otherwise
		/// </param>
		/// <summary>indicates whether Native Queries will be optimized dynamically.</summary>
		/// <remarks>indicates whether Native Queries will be optimized dynamically.</remarks>
		/// <returns>
		/// boolean true if Native Queries will be optimized
		/// dynamically.
		/// </returns>
		/// <seealso cref="Db4objects.Db4o.Config.ICommonConfiguration.OptimizeNativeQueries"
		/// 	>Db4objects.Db4o.Config.ICommonConfiguration.OptimizeNativeQueries</seealso>
		bool OptimizeNativeQueries
		{
			get;
			set;
		}

		/// <summary>returns the Query configuration interface.</summary>
		/// <remarks>returns the Query configuration interface.</remarks>
		IQueryConfiguration Queries
		{
			get;
		}

		/// <summary>configures the use of a specially designed reflection implementation.</summary>
		/// <remarks>
		/// configures the use of a specially designed reflection implementation.
		/// <br /><br />
		/// db4o internally uses java.lang.reflect.* by default. On platforms that
		/// do not support this package, customized implementations may be written
		/// to supply all the functionality of the interfaces in the com.db4o.reflect
		/// package. This method can be used to install a custom reflection
		/// implementation.<br /><br />
		/// In client-server environment this setting should be used on both the client and
		/// the server side (reflector class must be available)<br /><br />
		/// </remarks>
		void ReflectWith(IReflector reflector);

		/// <summary>
		/// Assigns a
		/// <see cref="System.IO.TextWriter">TextWriter</see>
		/// where db4o is to print its event messages.
		/// <br /><br />Messages are useful for debugging purposes and for learning
		/// to understand, how db4o works. The message level can be raised with
		/// <see cref="Db4objects.Db4o.Config.IConfiguration.MessageLevel">Db4objects.Db4o.Config.IConfiguration.MessageLevel
		/// 	</see>
		/// to produce more detailed messages.
		/// <br /><br />Use <code>outStream(System.out)</code> to print messages to the
		/// console.<br /><br />
		/// In client-server environment this setting should be used on the same side
		/// where
		/// <see cref="Db4objects.Db4o.Config.IConfiguration.MessageLevel">Db4objects.Db4o.Config.IConfiguration.MessageLevel
		/// 	</see>
		/// is used.<br /><br />
		/// </summary>
		/// <param name="outStream">the new <code>PrintStream</code> for messages.</param>
		/// <seealso cref="Db4objects.Db4o.Config.ICommonConfiguration.MessageLevel">Db4objects.Db4o.Config.ICommonConfiguration.MessageLevel
		/// 	</seealso>
		TextWriter OutStream
		{
			set;
		}

		/// <summary>configures the string encoding to be used.</summary>
		/// <remarks>
		/// configures the string encoding to be used.
		/// <br /><br />The string encoding can not be changed in the lifetime of a
		/// database file. To set up the database with the correct string encoding,
		/// this configuration needs to be set correctly <b>before</b> a database
		/// file is created with the first call to
		/// <see cref="Db4objects.Db4o.Db4oFactory.OpenFile">Db4objects.Db4o.Db4oFactory.OpenFile
		/// 	</see>
		/// or
		/// <see cref="Db4objects.Db4o.Db4oFactory.OpenServer">Db4objects.Db4o.Db4oFactory.OpenServer
		/// 	</see>
		/// .
		/// <br /><br />For subsequent open calls, db4o remembers built-in
		/// string encodings. If a custom encoding is used (an encoding that is
		/// not supplied from within the db4o library), the correct encoding
		/// needs to be configured correctly again for all subsequent calls
		/// that open database files.
		/// <br /><br />Example:<br />
		/// <code>config.stringEncoding(StringEncodings.utf8()));</code>
		/// </remarks>
		/// <seealso cref="Db4objects.Db4o.Config.Encoding.StringEncodings">Db4objects.Db4o.Config.Encoding.StringEncodings
		/// 	</seealso>
		IStringEncoding StringEncoding
		{
			set;
		}

		/// <summary>
		/// tuning feature: configures whether db4o should try to instantiate one instance
		/// of each persistent class on system startup.
		/// </summary>
		/// <remarks>
		/// tuning feature: configures whether db4o should try to instantiate one instance
		/// of each persistent class on system startup.
		/// <br /><br />In a production environment this setting can be set to <code>false</code>,
		/// if all persistent classes have public default constructors.
		/// <br /><br />
		/// In client-server environment this setting should be used on both client and server
		/// side. <br /><br />
		/// Default value:<br />
		/// <code>true</code>
		/// </remarks>
		/// <param name="flag">the desired setting</param>
		bool TestConstructors
		{
			set;
		}

		/// <summary>specifies the global updateDepth.</summary>
		/// <remarks>
		/// specifies the global updateDepth.
		/// <br /><br />see the documentation of
		/// <see cref="Db4objects.Db4o.IObjectContainer.Set"></see>
		/// for further details.<br /><br />
		/// The value be may be overridden for individual classes.<br /><br />
		/// The default setting is 1: Only the object passed to
		/// <see cref="Db4objects.Db4o.IObjectContainer.Set">Db4objects.Db4o.IObjectContainer.Set
		/// 	</see>
		/// will be updated.<br /><br />
		/// In client-server environment this setting should be used on both client and
		/// server sides.<br /><br />
		/// </remarks>
		/// <param name="depth">the depth of the desired update.</param>
		/// <seealso cref="Db4objects.Db4o.Config.IObjectClass.UpdateDepth">Db4objects.Db4o.Config.IObjectClass.UpdateDepth
		/// 	</seealso>
		/// <seealso cref="Db4objects.Db4o.Config.IObjectClass.CascadeOnUpdate">Db4objects.Db4o.Config.IObjectClass.CascadeOnUpdate
		/// 	</seealso>
		/// <seealso cref="Db4objects.Db4o.Ext.IObjectCallbacks">Using callbacks</seealso>
		int UpdateDepth
		{
			set;
		}

		/// <summary>turns weak reference management on or off.</summary>
		/// <remarks>
		/// turns weak reference management on or off.
		/// <br /><br />
		/// This method must be called before opening a database.
		/// <br /><br />
		/// Performance may be improved by running db4o without using weak
		/// references durring memory management at the cost of higher
		/// memory consumption or by alternatively implementing a manual
		/// memory management scheme using
		/// <see cref="Db4objects.Db4o.Ext.IExtObjectContainer.Purge">Db4objects.Db4o.Ext.IExtObjectContainer.Purge
		/// 	</see>
		/// <br /><br />Setting the value to <code>false</code> causes db4o to use hard
		/// references to objects, preventing the garbage collection process
		/// from disposing of unused objects.
		/// <br /><br />The default setting is <code>true</code>.
		/// <br /><br />Ignored on JDKs before 1.2.
		/// </remarks>
		bool WeakReferences
		{
			set;
		}

		/// <summary>configures the timer for WeakReference collection.</summary>
		/// <remarks>
		/// configures the timer for WeakReference collection.
		/// <br /><br />The default setting is 1000 milliseconds.
		/// <br /><br />Configure this setting to zero to turn WeakReference
		/// collection off.
		/// <br /><br />Ignored on JDKs before 1.2.<br /><br />
		/// </remarks>
		/// <param name="milliseconds">the time in milliseconds</param>
		int WeakReferenceCollectionInterval
		{
			set;
		}

		/// <summary>
		/// allows registering special TypeHandlers for customized marshalling
		/// and customized comparisons.
		/// </summary>
		/// <remarks>
		/// allows registering special TypeHandlers for customized marshalling
		/// and customized comparisons.
		/// </remarks>
		/// <param name="predicate">
		/// to specify for which classes and versions the
		/// TypeHandler is to be used.
		/// </param>
		/// <param name="typeHandler">to be used for the classes that match the predicate.</param>
		void RegisterTypeHandler(ITypeHandlerPredicate predicate, ITypeHandler4 typeHandler
			);
	}
}
