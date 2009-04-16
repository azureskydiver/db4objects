package com.db4o.db4ounit.jre12.handlers;

import java.util.*;

import com.db4o.config.*;
import com.db4o.db4ounit.jre12.collections.*;
import com.db4o.typehandlers.*;
import com.db4o.typehandlers.internal.*;

@decaf.Remove(decaf.Platform.JDK11)
public class TreeSetTypeHandlerTestSuite extends TreeSetTestSuite {
	
	{
		testUnits(TestUnitWithTypeHandler.class);
	}
	
	public static class TestUnitWithTypeHandler extends TestUnit {
		
		@Override
		protected void configure(Configuration config) throws Exception {
		    super.configure(config);
		    
		    config.registerTypeHandler(
		    		new SingleClassTypeHandlerPredicate(TreeSet.class),
		    		new TreeSetTypeHandler());
		}
		
	}

}
