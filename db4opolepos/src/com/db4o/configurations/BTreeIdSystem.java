/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.configurations;

import org.polepos.framework.*;

import com.db4o.internal.*;
import com.db4o.internal.config.*;

public class BTreeIdSystem implements ConfigurationSetting {

	public String name() {
		return "BTreeIdSystem";
	}
	
	public void apply(Object config) {
		Db4oLegacyConfigurationBridge.asIdSystemConfiguration((Config4Impl)config).useBTreeSystem();
	}

}
