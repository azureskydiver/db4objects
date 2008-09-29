/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.config;

import java.io.IOException;

import com.db4o.config.Alias;
import com.db4o.config.ConfigScope;
import com.db4o.config.Configuration;
import com.db4o.config.FreespaceConfiguration;
import com.db4o.config.GlobalOnlyConfigException;
import com.db4o.config.TypeAlias;
import com.db4o.config.WildcardAlias;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.foundation.NotSupportedException;
import com.db4o.io.IoAdapter;

public interface LocalConfiguration {

	/**
     * adds a new Alias for a class, namespace or package.
     * <br><br>Aliases can be used to persist classes in the running
     * application to different persistent classes in a database file
     * or on a db4o server.
     * <br><br>Two simple Alias implementations are supplied along with 
     * db4o:<br>
     * - {@link TypeAlias} provides an #equals() resolver to match
     * names directly.<br>
     * - {@link WildcardAlias} allows simple pattern matching
     * with one single '*' wildcard character.<br>
     * <br>
     * It is possible to create
     * own complex {@link Alias} constructs by creating own resolvers
     * that implement the {@link Alias} interface.
     * <br><br>
     * Examples of concrete usecases:
     * <br><br>
     * <code>
     * <b>// Creating an Alias for a single class</b><br> 
     * Db4o.configure().addAlias(<br>
     * &#160;&#160;new TypeAlias("com.f1.Pilot", "com.f1.Driver"));<br>
     * <br><br>
     * <b>// Accessing a .NET assembly from a Java package</b><br> 
     * Db4o.configure().addAlias(<br>
     * &#160;&#160;new WildcardAlias(<br>
     * &#160;&#160;&#160;&#160;"Tutorial.F1.*, Tutorial",<br>
     * &#160;&#160;&#160;&#160;"com.f1.*"));<br>
     * <br><br>
     * <b>// Mapping a Java package onto another</b><br> 
     * Db4o.configure().addAlias(<br>
     * &#160;&#160;new WildcardAlias(<br>
     * &#160;&#160;&#160;&#160;"com.f1.*",<br>
     * &#160;&#160;&#160;&#160;"com.f1.client*"));<br></code>
     * <br><br>Aliases that translate the persistent name of a class to 
     * a name that already exists as a persistent name in the database 
     * (or on the server) are not permitted and will throw an exception
     * when the database file is opened.
     * <br><br>Aliases should be configured before opening a database file
     * or connecting to a server.<br><br>
     * In client/server environment this setting should be used on the server side.
     */
    public void addAlias(Alias alias);
    
    /**
     * Removes an alias previously added with {@link Configuration#addAlias(Alias)}.
     * 
     * @param alias the alias to remove
     */
    public void removeAlias(Alias alias);
    
    /**
     * sets the storage data blocksize for new ObjectContainers. 
     * <br><br>The standard setting is 1 allowing for a maximum
     * database file size of 2GB. This value can be increased
     * to allow larger database files, although some space will
     * be lost to padding because the size of some stored objects
     * will not be an exact multiple of the block size. A 
     * recommended setting for large database files is 8, since
     * internal pointers have this length.<br><br>
     * This setting is only effective when the database is first created, in 
     * client-server environment in most cases it means that the setting 
     * should be used on the server side.
     * @param bytes the size in bytes from 1 to 127
     * 
     * @sharpen.property
     */
    public void blockSize(int bytes);
    
	/**
	 * configures the size database files should grow in bytes, when no 
	 * free slot is found within.
	 * <br><br>Tuning setting.
	 * <br><br>Whenever no free slot of sufficient length can be found 
	 * within the current database file, the database file's length
	 * is extended. This configuration setting configures by how much
	 * it should be extended, in bytes.<br><br>
	 * This configuration setting is intended to reduce fragmentation.
	 * Higher values will produce bigger database files and less
	 * fragmentation.<br><br>
	 * To extend the database file, a single byte array is created 
	 * and written to the end of the file in one write operation. Be 
	 * aware that a high setting will require allocating memory for 
	 * this byte array.
	 *  
     * @param bytes amount of bytes
     * 
     * @sharpen.property
     */
    public void databaseGrowthSize(int bytes);

    /**
     * turns commit recovery off.
     * <br><br>db4o uses a two-phase commit algorithm. In a first step all intended
     * changes are written to a free place in the database file, the "transaction commit
     * record". In a second step the
     * actual changes are performed. If the system breaks down during commit, the
     * commit process is restarted when the database file is opened the next time.
     * On very rare occasions (possibilities: hardware failure or editing the database
     * file with an external tool) the transaction commit record may be broken. In this
     * case, this method can be used to try to open the database file without commit
     * recovery. The method should only be used in emergency situations after consulting
     * db4o support. 
     */
    public void disableCommitRecovery();
    
    /**
     * returns the freespace configuration interface.
     * 
     * @sharpen.property
     */
    public FreespaceConfiguration freespace();

    /**
     * configures db4o to generate UUIDs for stored objects.
     * 
     * This setting should be used when the database is first created.<br><br>
     * @param setting the scope for UUID generation: disabled, generate for all classes, or configure individually
     * 
     * @sharpen.property
     */
    public void generateUUIDs(ConfigScope setting);

    /**
     * configures db4o to generate version numbers for stored objects.
     * 
     * This setting should be used when the database is first created.
     * @param setting the scope for version number generation: disabled, generate for all classes, or configure individually
     * 
     * @sharpen.property
     */
    public void generateVersionNumbers(ConfigScope setting);

    /**
     * allows to configure db4o to use a customized byte IO adapter.
     * <br><br>Derive from the abstract class {@link IoAdapter} to
     * write your own. Possible usecases could be improved performance
     * with a native library, mirrored write to two files, encryption or 
     * read-on-write fail-safety control.<br><br>An example of a custom
     * io adapter can be found in xtea_db4o community project:<br>
     * http://developer.db4o.com/ProjectSpaces/view.aspx/XTEA<br><br>
     * In client-server environment this setting should be used on the server 
     * (adapter class must be available)<br><br>
     * @param adapter - the IoAdapter
     * 
     * @sharpen.property
     */
    public void io(IoAdapter adapter) throws GlobalOnlyConfigException;

    /**
     * returns the configured {@link IoAdapter}.
     * 
     * @return
     * 
     * @sharpen.property
     */
    public IoAdapter io();

    /**
     * can be used to turn the database file locking thread off. 
     * <br><br>Since Java does not support file locking up to JDK 1.4,
     * db4o uses an additional thread per open database file to prohibit
     * concurrent access to the same database file by different db4o
     * sessions in different VMs.<br><br>
     * To improve performance and to lower ressource consumption, this
     * method provides the possibility to prevent the locking thread
     * from being started.<br><br><b>Caution!</b><br>If database file
     * locking is turned off, concurrent write access to the same
     * database file from different JVM sessions will <b>corrupt</b> the
     * database file immediately.<br><br> This method
     * has no effect on open ObjectContainers. It will only affect how
     * ObjectContainers are opened.<br><br>
     * The default setting is <code>true</code>.<br><br>
     * In client-server environment this setting should be used on both client and server.<br><br>  
     * @param flag <code>false</code> to turn database file locking off.
     * 
     * @sharpen.property
     */
    public void lockDatabaseFile(boolean flag);

    /**
     * tuning feature only: reserves a number of bytes in database files.
     * <br><br>The global setting is used for the creation of new database
     * files. Continous calls on an ObjectContainer Configuration context
     * (see  {@link com.db4o.ext.ExtObjectContainer#configure()}) will
     * continually allocate space. 
     * <br><br>The allocation of a fixed number of bytes at one time
     * makes it more likely that the database will be stored in one
     * chunk on the mass storage. Less read/write head movement can result
     * in improved performance.<br><br>
     * <b>Note:</b><br> Allocated space will be lost on abnormal termination
     * of the database engine (hardware crash, VM crash). A Defragment run
     * will recover the lost space. For the best possible performance, this
     * method should be called before the Defragment run to configure the
     * allocation of storage space to be slightly greater than the anticipated
     * database file size.
     * <br><br> 
     * In client-server environment this setting should be used on the server side. <br><br>
     * Default configuration: 0<br><br> 
     * @param byteCount the number of bytes to reserve
     * 
     * @sharpen.property
     */
    public void reserveStorageSpace(long byteCount) throws DatabaseReadOnlyException, NotSupportedException;
    
    /**
     * configures the path to be used to store and read 
     * Blob data.
     * <br><br>
     * In client-server environment this setting should be used on the
     * server side. <br><br>
     * @param path the path to be used
     * 
     * @sharpen.property
     */
    public void blobPath(String path) throws IOException;

}
