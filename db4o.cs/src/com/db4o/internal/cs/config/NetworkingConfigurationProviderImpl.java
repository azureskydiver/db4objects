/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.config;

import com.db4o.cs.config.*;
import com.db4o.internal.*;

public class NetworkingConfigurationProviderImpl implements
		NetworkingConfigurationProvider {

	private final NetworkingConfigurationImpl _networking;

	public NetworkingConfigurationProviderImpl(Config4Impl config) {
		_networking = new NetworkingConfigurationImpl(config);
	}

	public NetworkingConfiguration networking() {
		return _networking;
	}
	
	protected Config4Impl config() {
		return _networking.config();
	}

}
