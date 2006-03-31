/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;

class Config4Class extends Config4Abstract implements ObjectClass, Cloneable,
    DeepClone {

    private int			 	   i_callConstructor;
    
    private Config4Impl        i_config;

    private Hashtable4 i_exceptionalFields;

    private int                i_generateUUIDs;
    
    private int                i_generateVersionNumbers;
    
    /**
     * We are running into cyclic dependancies on reading the PBootRecord
     * object, if we maintain MetaClass information there 
     */
    private boolean            _maintainMetaClass = true;

    private int                i_maximumActivationDepth;

    private MetaClass          i_metaClass;

    private int                i_minimumActivationDepth;

    private boolean            i_persistStaticFieldValues;
    
    private ObjectAttribute    i_queryAttributeProvider;
    
    private boolean            i_storeTransientFields;
    
    private ObjectTranslator   _translator;

    private String             _translatorName;
    
    private int                i_updateDepth;
    
    private String             _writeAs;
    
    private boolean    _processing;

    Config4Class(Config4Impl a_configuration, String a_name) {
        i_config = a_configuration;
        i_name = a_name;
    }

    int adjustActivationDepth(int a_depth) {
        if ((i_cascadeOnActivate == 1)&& a_depth < 2) {
            a_depth = 2;
        }
        if((i_cascadeOnActivate == -1)  && a_depth > 1){
            a_depth = 1;
        }
        if (i_config.classActivationDepthConfigurable()) {
            if (i_minimumActivationDepth != 0) {
                if (a_depth < i_minimumActivationDepth) {
                    a_depth = i_minimumActivationDepth;
                }
            }
            if (i_maximumActivationDepth != 0) {
                if (a_depth > i_maximumActivationDepth) {
                    a_depth = i_maximumActivationDepth;
                }
            }
        }
        return a_depth;
    }
    
    public void callConstructor(boolean flag){
        i_callConstructor = flag ? YapConst.YES : YapConst.NO;
    }

    String className() {
        return getName();
    }
    
    ReflectClass classReflector() throws ClassNotFoundException {
    	return i_config.reflector().forName(i_name);
    }

    public void compare(ObjectAttribute comparator) {
        i_queryAttributeProvider = comparator;
    }

    Config4Field configField(String fieldName) {
        if (i_exceptionalFields == null) {
            return null;
        }
        return (Config4Field) i_exceptionalFields.get(fieldName);
    }

    public Object deepClone(Object param){
        Config4Class ret = null;
        try {
            ret = (Config4Class) clone();
            ret._processing = false;
        } catch (CloneNotSupportedException e) {
            // won't happen
        }
        ret.i_config = (Config4Impl) param;
        if (i_exceptionalFields != null) {
            ret.i_exceptionalFields = (Hashtable4) i_exceptionalFields
                .deepClone(ret);
        }
        return ret;
    }

	public void enableReplication(boolean setting) {
		generateUUIDs(setting);
		generateVersionNumbers(setting);
	}
    
    public void generateUUIDs(boolean setting) {
        i_generateUUIDs = setting ? YapConst.YES : YapConst.NO;
    }

    public void generateVersionNumbers(boolean setting) {
        i_generateVersionNumbers = setting ? YapConst.YES : YapConst.NO;
    }

    public ObjectTranslator getTranslator() {
        if (_translator != null) {
        	return _translator;
        }
        
        if (_translatorName == null) {
        	return null;
        }
        
        try {
            _translator = (ObjectTranslator) i_config.reflector().forName(
                _translatorName).newInstance();
        } catch (Throwable t) {
            if (! Deploy.csharp){
            	// TODO: why?
                try{
                    _translator = (ObjectTranslator) Class.forName(_translatorName).newInstance();
                    if(_translator != null){
                        return _translator;
                    }
                }catch(Throwable th){
                }
            }
            Messages.logErr(i_config, 48, _translatorName, null);
            _translatorName = null;
        }
        return _translator;
    }

    public boolean initOnUp(Transaction systemTrans, final int[] metaClassID) {
        if(_processing){
            return false;
        }
        _processing = true;
        if (Tuning.fieldIndices) {
            YapStream stream = systemTrans.i_stream;
            if (stream.maintainsIndices()) {
                if(_maintainMetaClass){
                    
                    if(metaClassID[0] > 0){
                        i_metaClass = (MetaClass)stream.getByID1(systemTrans, metaClassID[0]);
                    }
                    
                    if(i_metaClass == null){
                        i_metaClass = (MetaClass) stream.get1(systemTrans,new MetaClass(i_name)).next();
                        metaClassID[0] = stream.getID1(systemTrans, i_metaClass);
                    }
                            
                    if (i_metaClass == null) {
                        i_metaClass = new MetaClass(i_name);
                        stream.setInternal(systemTrans, i_metaClass, Integer.MAX_VALUE, false);
                        metaClassID[0] = stream.getID1(systemTrans, i_metaClass);
                    } else {
                        stream.activate1(systemTrans, i_metaClass,
                            Integer.MAX_VALUE);
                    }
                }
            }
        }
        _processing = false;
        return true;
    }

    Object instantiate(YapStream a_stream, Object a_toTranslate) {
        return ((ObjectConstructor) _translator).onInstantiate(a_stream,
            a_toTranslate);
    }

    boolean instantiates() {
        return getTranslator() instanceof ObjectConstructor;
    }

    public void maximumActivationDepth(int depth) {
        i_maximumActivationDepth = depth;
    }

    public void minimumActivationDepth(int depth) {
        i_minimumActivationDepth = depth;
    }
    
    public int callConstructor() {
        if(_translator != null){
            return YapConst.YES;
        }
        return i_callConstructor;
    }
    
    public ObjectField objectField(String fieldName) {
        if (i_exceptionalFields == null) {
            i_exceptionalFields = new Hashtable4(16);
        }
        Config4Field c4f = (Config4Field) i_exceptionalFields.get(fieldName);
        if (c4f == null) {
            c4f = new Config4Field(this, fieldName);
            i_exceptionalFields.put(fieldName, c4f);
        }
        return c4f;
    }

    public void persistStaticFieldValues() {
        i_persistStaticFieldValues = true;
    }

    boolean queryEvaluation(String fieldName) {
        if (i_exceptionalFields != null) {
            Config4Field field = (Config4Field) i_exceptionalFields
                .get(fieldName);
            if (field != null) {
                return field.queryEvaluation();
            }
        }
        return true;
    }
    
   public void readAs(Object clazz) {
       ReflectClass claxx = i_config.reflectorFor(clazz);
       if (claxx == null) {
           return;
       }
       _writeAs = i_name;
       i_config.readAs().put(_writeAs, claxx.getName());
   }

    public void rename(String newName) {
        i_config.rename(new Rename("", i_name, newName));
        i_name = newName;
    }

    public void storeTransientFields(boolean flag) {
        i_storeTransientFields = flag;
    }

    public void translate(ObjectTranslator translator) {
        if (translator == null) {
            _translatorName = null;
        }
        _translator = translator;
    }

    void translateOnDemand(String a_translatorName) {
        _translatorName = a_translatorName;
    }

    public void updateDepth(int depth) {
        i_updateDepth = depth;
    }

	Config4Impl config() {
		return i_config;
	}

	int generateUUIDs() {
		return i_generateUUIDs;
	}

	int generateVersionNumbers() {
		return i_generateVersionNumbers;
	}

	void maintainMetaClass(boolean flag){
		_maintainMetaClass = flag;
	}

	MetaClass metaClass() {
		return i_metaClass;
	}

	boolean staticFieldValuesArePersisted() {
		return i_persistStaticFieldValues;
	}

	ObjectAttribute queryAttributeProvider() {
		return i_queryAttributeProvider;
	}

	boolean storeTransientFields() {
		return i_storeTransientFields;
	}

	int updateDepth() {
		return i_updateDepth;
	}

	String writeAs() {
		return _writeAs;
	}

}