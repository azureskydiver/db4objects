/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import java.net.*;
import java.util.Date;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.handlers.*;
import com.db4o.inside.handlers.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.types.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public final class Platform4 {
    

    private static final String	JDK_PACKAGE	= "com.db4o.inside.";

	static private int collectionCheck;

    static private JDK jdkWrapper;
    static private int nioCheck;

    static private int setAccessibleCheck;
    static private int shutDownHookCheck;
    static int callConstructorCheck;
    static ShutDownRunnable shutDownRunnable;

    static Thread shutDownThread;
    
    static final String ACCESSIBLEOBJECT = "java.lang.reflect.AccessibleObject";
    static final String GETCONSTRUCTOR = "newConstructorForSerialization";
    static final String REFERENCEQUEUE = "java.lang.ref.ReferenceQueue"; 
    static final String REFLECTIONFACTORY = "sun.reflect.ReflectionFactory";
    static final String RUNFINALIZERSONEXIT = "runFinalizersOnExit";
    
    static final String UTIL = "java.util.";
    static final String DB4O_PACKAGE = "com.db4o.";
    static final String DB4O_CONFIG = DB4O_PACKAGE + "config.";
    static final String DB4O_ASSEMBLY = ", db4o";
    
    
    // static private int cCreateNewFile;
    static private int weakReferenceCheck;
    
    private static final Class[] SIMPLE_CLASSES = {
		Integer.class,
		Long.class,
		Float.class,
		Boolean.class,
		Double.class,
		Byte.class,
		Character.class,
		Short.class,
		String.class,
		java.util.Date.class
	};


    static final void addShutDownHook(Object a_stream, Object a_lock) {
        synchronized (a_lock) {
            if (hasShutDownHook()) {
                if (shutDownThread == null) {
                    shutDownRunnable = new ShutDownRunnable();
                    shutDownThread = jdk().addShutdownHook(shutDownRunnable);
                }
                shutDownRunnable.ensure(a_stream);
            }
        }
    }

    public static final boolean canSetAccessible() {
        if (Deploy.csharp) {
            return true;
        }
        if (setAccessibleCheck == Const4.UNCHECKED) {
            if (Deploy.csharp) {
                setAccessibleCheck = Const4.NO;
            } else {
                if (jdk().ver() >= 2) {
                    setAccessibleCheck = Const4.YES;
                } else {
                    setAccessibleCheck = Const4.NO;
                    if (((Config4Impl)Db4o.configure()).messageLevel() >= 0) {
                        Messages.logErr(Db4o.configure(), 47, null, null);
                    }
                }
            }
        }
        return setAccessibleCheck == Const4.YES;
    }
    
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    static final boolean classIsAvailable(String className) {
        try {
            return Class.forName(className) != null;
        } catch (Throwable t) {
            return false;
        }
    }
    
    static Db4oCollections collections(Object a_object){
        return jdk().collections((ObjectContainerBase)a_object);
    }
    
    static final Reflector createReflector(Object classLoader){
        return jdk().createReflector(classLoader);
    }

    static final Object createReferenceQueue() {
        return jdk().createReferenceQueue();
    }
    
    public static Object createWeakReference(Object obj){
        return jdk().createWeakReference(obj);
    }

    static final Object createYapRef(Object a_queue, Object a_yapObject, Object a_object) {
        return jdk().createYapRef(a_queue, (ObjectReference) a_yapObject, a_object);
    }
    
    static Object deserialize(byte[] bytes) {
    	return jdk().deserialize(bytes);
    }
    
    public static final long doubleToLong(double a_double) {
        return Double.doubleToLongBits(a_double);
    }

    static final QConEvaluation evaluationCreate(Transaction a_trans, Object example){
        if(example instanceof Evaluation){
            return new QConEvaluation(a_trans, example);
        }
        return null;
    }
    
    static final void evaluationEvaluate(Object a_evaluation, Candidate a_candidate){
        ((Evaluation)a_evaluation).evaluate(a_candidate);
    }

    /** may be needed for YapConfig processID() at a later date */
    /*
    static boolean createNewFile(File file) throws IOException{
    	return file.createNewFile();
    }
    */
	
	public static Object[] collectionToArray(ObjectContainerBase stream, Object obj){
		Collection4 col = flattenCollection(stream, obj);
		Object[] ret = new Object[col.size()];
		col.toArray(ret);
		return ret;
	}

    static final Collection4 flattenCollection(ObjectContainerBase stream, Object obj) {
        Collection4 col = new Collection4();
        flattenCollection1(stream, obj, col);
        return col;
    }

    /**
     * Should create additional configuration, for example through reflection
     * on annotations.
     * 
     * - If a valid configuration is passed as classConfig, any additional
     *   configuration, if available, should be applied to this object, and
     *   this object should be returned.
     * - If classConfig is null and there is no additional configuration,
     *   null should be returned.
     * - If classConfig is null and there is additional configuration, this code
     *   should create and register a new configuration via config.objectClass(),
     *   apply additional configuration there and return this new instance.
     * 
     * The reason for this dispatch is to avoid creation of a configuration
     * for a class that doesn't need configuration at all.
     * 
     * @param clazz The class to be searched for additional configuration information
     * @param config The global database configuration
     * @param classConfig A class configuration, if one already exists
     * @return classConfig, if not null, a newly created ObjectClass otherwise.
     */
    public static Config4Class extendConfiguration(ReflectClass clazz,Configuration config,Config4Class classConfig) {
    	return jdk().extendConfiguration(clazz, config, classConfig);
    }
    
    static final void flattenCollection1(ObjectContainerBase stream, Object obj, Collection4 col) {
        if (obj == null) {
            col.add(null);
        } else {
            ReflectClass claxx = stream.reflector().forObject(obj);
            if (claxx.isArray()) {
                Object[] objects;
                if (claxx.getComponentType().isArray()) {
                    objects = new MultidimensionalArrayHandler(stream, null, false).allElements(obj);
                } else {
                    objects = new ArrayHandler(stream, null, false).allElements(obj);
                }
                for (int i = 0; i < objects.length; i++) {
                    flattenCollection1(stream, objects[i], col);
                }
            } else {
                flattenCollection2(stream, obj, col);
            }
        }
    }

    static final void flattenCollection2(final ObjectContainerBase a_stream, Object a_object, final com.db4o.foundation.Collection4 col) {
        Reflector reflector = a_stream.reflector();
        if (reflector.forObject(a_object).isCollection()) {
            forEachCollectionElement(a_object, new Visitor4() {
                public void visit(Object obj) {
                    flattenCollection1(a_stream, obj, col);
                }
            });
        } else {
            col.add(a_object);
        }
    }

    static final void forEachCollectionElement(Object a_object, Visitor4 a_visitor) {
        jdk().forEachCollectionElement(a_object, a_visitor);
    }

    public static final String format(Date date, boolean showTime) {
    	return jdk().format(date, showTime);
    }

    public static Object getClassForType(Object obj) {
        return obj;
    }

    public static final void getDefaultConfiguration(Config4Impl config) {
		
    	// Initialize all JDK stuff first, before doing ClassLoader stuff
    	jdk();
    	hasWeakReferences();
    	hasNio();
    	hasCollections();
    	hasShutDownHook();
        
    	if(config.reflector()==null) {
    		config.reflectWith(jdk().createReflector(null));
    	}
    	
        config.objectClass("java.lang.StringBuffer").compare(new ObjectAttribute() {
            public Object attribute(Object original) {
                if (original instanceof StringBuffer) {
                    return ((StringBuffer) original).toString();
                }
                return original;
            }
        });
        
        translate(config.objectClass("java.lang.Class"), "TClass");
        translateCollection(config, "Hashtable", "THashtable", true);
        if (jdk().ver() >= 2) {
			try {
				translateCollection(config, "AbstractCollection", "TCollection", false);
				translateUtilNull(config, "AbstractList");
				translateUtilNull(config, "AbstractSequentialList");
				translateUtilNull(config, "LinkedList");
				translateUtilNull(config, "ArrayList");
				translateUtilNull(config, "Vector");
				translateUtilNull(config, "Stack");
				translateUtilNull(config, "AbstractSet");
				translateUtilNull(config, "HashSet");
				translate(config, UTIL + "TreeSet", "TTreeSet");
				translateCollection(config, "AbstractMap", "TMap", true);
				translateUtilNull(config, "HashMap");
				translateUtilNull(config, "WeakHashMap");
				translate(config, UTIL + "TreeMap", "TTreeMap");
			} catch (Exception e) {
			}
        } else {
			translateCollection(config, "Vector", "TVector", false);
        }
        netReadAsJava(config, "ext.Db4oDatabase");
        netReadAsJava(config, "P1Object");
        netReadAsJava(config, "P1Collection");
        netReadAsJava(config, "P1HashElement");
        netReadAsJava(config, "P1ListElement");
        netReadAsJava(config, "P2HashMap");
        netReadAsJava(config, "P2LinkedList");
        netReadAsJava(config, "StaticClass");
        netReadAsJava(config, "StaticField");
    }
    
    public static Object getTypeForClass(Object obj){
        return obj;
    }

    static final Object getYapRefObject(Object a_object) {
        return jdk().getYapRefObject(a_object);
    }

    static final synchronized boolean hasCollections() {
        if (collectionCheck == Const4.UNCHECKED) {
            if (!Deploy.csharp) {
                if (classIsAvailable(UTIL + "Collection")) {
                    collectionCheck = Const4.YES;
                    return true;
                }
            }
            collectionCheck = Const4.NO;
        }
        return collectionCheck == Const4.YES;
    }

    static final boolean hasLockFileThread() {
        return !Deploy.csharp;
    }

    static final boolean hasNio() {
        if (!Debug.nio) {
            return false;
        }
        if (nioCheck == Const4.UNCHECKED) {
            if ((jdk().ver() >= 4)
                && (!noNIO())) {
                nioCheck = Const4.YES;
                return true;
            }
            nioCheck = Const4.NO;
        }
        return nioCheck == Const4.YES;

    }

    static final boolean hasShutDownHook() {
        if (shutDownHookCheck == Const4.UNCHECKED) {
            if (!Deploy.csharp) {
                if (jdk().ver() >= 3){
                    shutDownHookCheck = Const4.YES;
                    return true;
                } 
                JDKReflect.invoke(System.class, RUNFINALIZERSONEXIT, new Class[] {boolean.class}, new Object[]{new Boolean(true)});
            }
            shutDownHookCheck = Const4.NO;
        }
        return shutDownHookCheck == Const4.YES;
    }

    static final boolean hasWeakReferences() {
        if (!Debug.weakReferences) {
            return false;
        }
        if (weakReferenceCheck == Const4.UNCHECKED) {
            if (!Deploy.csharp) {
                if (classIsAvailable(ACCESSIBLEOBJECT)
                    && classIsAvailable(REFERENCEQUEUE)
                    && jdk().ver() >= 2) {
                    weakReferenceCheck = Const4.YES;
                    return true;
                }
            }
            weakReferenceCheck = Const4.NO;
        }
        return weakReferenceCheck == Const4.YES;
    }
    
    static final boolean ignoreAsConstraint(Object obj){
        return false;
    }

    static final boolean isCollectionTranslator(Config4Class a_config) {
        return jdk().isCollectionTranslator(a_config); 
    }
    
    public static boolean isConnected(Socket socket) {
        return jdk().isConnected(socket);
    }   
    
    public static final boolean isValueType(ReflectClass claxx){
    	return false;
    }
    
    public static JDK jdk() {
        if (jdkWrapper == null) {
            createJdk();
        }
        return jdkWrapper;
    }
    
    private static void createJdk() {
    	
        if (classIsAvailable("java.lang.reflect.Method")){
            jdkWrapper = (JDK)createInstance(JDK_PACKAGE + "JDKReflect");
        }

        if (classIsAvailable(Platform4.ACCESSIBLEOBJECT)){
        	jdkWrapper = createJDKWrapper("1_2");
        }
        
        if (jdk().methodIsAvailable("java.lang.Runtime","addShutdownHook",
                new Class[] { Thread.class })){
        	jdkWrapper = createJDKWrapper("1_3");
        }

        if(classIsAvailable("java.nio.channels.FileLock")){
        	jdkWrapper = createJDKWrapper("1_4");
        }
        
        if(classIsAvailable("java.lang.Enum")){
        	jdkWrapper = createJDKWrapper("5");
        }
        
    }
    
    private static JDK createJDKWrapper(String name){
        JDK newWrapper = (JDK)createInstance(JDK_PACKAGE + "JDK_" + name);
        if(newWrapper != null){
            return newWrapper;
        }
        return jdkWrapper;
    }
    
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    private static final Object createInstance(String name) {
        try {
            Class clazz = Class.forName(name);
            if(clazz != null){
                return clazz.newInstance();
            }
        } catch (Exception e) {
        }
        return null;
    }
    
	public static boolean isSimple(Class a_class){
		for (int i = 0; i < SIMPLE_CLASSES.length; i++) {
			if(a_class == SIMPLE_CLASSES[i]){
				return true;
			}
		}
		return false;
	}
    
    static final void killYapRef(Object a_object){
    	jdk().killYapRef(a_object);
    }
    
    public static void link(){
        if (!Deploy.csharp) {
            
            // link standard translators, so they won't get deleted
            // by deployment
            
            new TClass();
            new TVector();
            new THashtable();
            new TNull();
        }
    }

    // FIXME: functionality should really be in IoAdapter
    public static final void lockFile(Object file) {
        if (!hasNio()) {
            return;
        }

        // FIXME: libgcj 3.x isn't able to properly lock the database file
        String fullversion = System.getProperty("java.fullversion");
        if (fullversion != null && fullversion.indexOf("GNU libgcj") >= 0) {
            System.err.println("Warning: Running in libgcj 3.x--not locking database file!");
            return;
        }
        
        jdk().lockFile(file);
    }
    
    public static final void unlockFile(Object file) {
        if (hasNio()) {
            jdk().unlockFile(file);
        }
    }

    public static final double longToDouble(long a_long) {
        return Double.longBitsToDouble(a_long);
    }

    static void markTransient(String a_marker) {
        // do nothing
    }

    static boolean callConstructor() {
        if (callConstructorCheck == Const4.UNCHECKED) {
            
            if(jdk().methodIsAvailable(
                REFLECTIONFACTORY,
                GETCONSTRUCTOR,
                new Class[]{Class.class, jdk().constructorClass()}
                )){
                
                callConstructorCheck = Const4.NO;
                return false;
            }
            callConstructorCheck = Const4.YES;
        }
        return callConstructorCheck == Const4.YES;
    }
    
    private static final void netReadAsJava(Config4Impl config, String className){
        Config4Class classConfig = (Config4Class)config.objectClass(DB4O_PACKAGE + className + DB4O_ASSEMBLY);
        if(classConfig == null){
            return;
        }
        classConfig.maintainMetaClass(false);
        classConfig.readAs(DB4O_PACKAGE + className);
    }

    private static final boolean noNIO() {
        try {
            if (propertyIs("java.vendor", "Sun")
                && propertyIs("java.version", "1.4.0")
                && (propertyIs("os.name", "Linux")
                    || propertyIs("os.name", "Windows 95")
                    || propertyIs("os.name", "Windows 98"))) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    static final void pollReferenceQueue(Object a_stream, Object a_referenceQueue) {
        jdk().pollReferenceQueue((ObjectContainerBase) a_stream, a_referenceQueue);
    }

    public static void postOpen(ObjectContainer a_oc) {
        // do nothing 
     }
    
    static void preClose(ObjectContainer a_oc) {
        // do nothing 
    }

    private static final boolean propertyIs(String propertyName, String propertyValue) {
        String property = System.getProperty(propertyName);
        return (property != null) && (property.indexOf(propertyValue) == 0);
    }
    
	public static void registerCollections(GenericReflector reflector) {
		if(!Deploy.csharp){
			reflector.registerCollection(P1Collection.class);
			jdk().registerCollections(reflector);
		}
	}


    static final void removeShutDownHook(Object a_stream, Object a_lock) {
        synchronized (a_lock) {
            if (hasShutDownHook()) {
                if (shutDownRunnable != null) {
                    shutDownRunnable.remove(a_stream);
                    if (shutDownRunnable.size() == 0) {
                        if (!shutDownRunnable.dontRemove) {
                            try {
                                jdk().removeShutdownHook(shutDownThread);
                            } catch (Exception e) {
                                // this is safer than attempting perfect
                                // synchronisation
                            }
                        }
                        shutDownThread = null;
                        shutDownRunnable = null;
                    }
                }
            }
        }
    }
    
    static final byte[] serialize(Object obj) throws Exception{
    	return jdk().serialize(obj);
    }

    public static final void setAccessible(Object a_accessible) {
        if (!Deploy.csharp) {
            if (setAccessibleCheck == Const4.UNCHECKED) {
                canSetAccessible();
            }
            if (setAccessibleCheck == Const4.YES) {
                jdk().setAccessible(a_accessible);
            }
        }
    }
    
    public static boolean storeStaticFieldValues(Reflector reflector, ReflectClass claxx) {
        return jdk().isEnum(reflector, claxx);
    }

    private static final void translate(ObjectClass oc, String to) {
        ((Config4Class)oc).translateOnDemand(DB4O_CONFIG + to);
    }

    private static final void translate(Config4Impl config, String from, String to) {
        translate(config.objectClass(from), to);
    }

    private static final void translateCollection(
        Config4Impl config,
        String from,
        String to,
        boolean cascadeOnDelete) {
        ObjectClass oc = config.objectClass(UTIL + from);
        oc.updateDepth(3);
        if (cascadeOnDelete) {
            oc.cascadeOnDelete(true);
        }
        translate(oc, to);
    }

    private static final void translateUtilNull(Config4Impl config, String className) {
        translate(config, UTIL + className, "TNull");
    }

    static final NetTypeHandler[] types(ObjectContainerBase stream) {
        return new NetTypeHandler[] {
        		new NetDateTime(stream),
        		new NetDecimal(stream),
        		new NetSByte(stream),
        		new NetUInt(stream),
        		new NetULong(stream),
        		new NetUShort(stream)
        };
    }
    
    public static byte[] updateClassName(byte[] bytes) {
        // needed for .NET only: update assembly names if necessary
        return bytes;
    }
    
    public static Object weakReferenceTarget(Object weakRef){
        return jdk().weakReferenceTarget(weakRef);
    }

	public static Object wrapEvaluation(Object evaluation) {
		throw Exceptions4.virtualException();
	}

	public static boolean isTransient(ReflectClass a_class) {
		return false;
	}

	public static boolean isDb4oClass(String className) {
		if (className.indexOf("com.db4o.test") == 0) {
            return false;
        }
		return className.indexOf("com.db4o") == 0;
	}
}