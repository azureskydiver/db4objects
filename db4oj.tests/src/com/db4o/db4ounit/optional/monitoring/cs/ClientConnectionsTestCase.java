/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;

import javax.management.*;

import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.db4ounit.optional.monitoring.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.monitoring.*;
import com.db4o.monitoring.cs.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

@decaf.Ignore
public class ClientConnectionsTestCase extends TestWithTempFile implements OptOutAllButNetworkingCS {

	private static final String USER = "db4o";
	private static final String PASSWORD = "db4o";

	public void testConnectedClients() {
		for(int i=0; i < 5; i++) {
			Assert.areEqual(0, connectedClientCount(), "No client yet.");
			ExtObjectContainer client1 = openNewSession();
			Assert.areEqual(1, connectedClientCount(), "client1:" + i);
			ExtObjectContainer client2 = openNewSession();
			Assert.areEqual(2, connectedClientCount(), "client1 and client2: " + i);
			ensureClose(client1);
			Assert.areEqual(1, connectedClientCount(), "client2: " + i);
			ensureClose(client2);
			Assert.areEqual(0, connectedClientCount(), "" + i);
		}		
	}

	private void ensureClose(ExtObjectContainer client) {
		synchronized (_closeEventRaised) {
			_closeEventRaised.value = false;
			client.close();
			while (!_closeEventRaised.value) {
				try {
					_closeEventRaised.wait();
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	private ExtObjectContainer openNewSession() {
		return (ExtObjectContainer) Db4oClientServer.openClient("localhost", _server.ext().port(), USER, PASSWORD);
	}

	private long connectedClientCount() {
		MBeanProxy bean = new MBeanProxy(Db4oMBeans.mBeanNameFor(ClientConnectionsMBean.class, Db4oMBeans.mBeanIDForContainer(_server.objectContainer())));
		return bean.<Integer>getAttribute("ConnectedClientCount").longValue();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		ServerConfiguration serverConfiguration = Db4oClientServer.newServerConfiguration();
		
		_server = (ObjectServerImpl) Db4oClientServer.openServer(serverConfiguration, tempFile(), Db4oClientServer.ARBITRARY_PORT);
		_server.grantAccess(USER, PASSWORD);

		
		// We depend on the order of client connection/disconnection event firing.
		// We want the bean to be notified before the _listener in the test.
		_listener = registerCloseEventNotification();
		
		registerBean();
	}

	// TODO: Where does this setup code go? COR-1779
	private void registerBean() throws JMException {
		final ClientConnections bean = Db4oMBeans.newClientConnectionsMBean(_server);
		bean.register();
		((ObjectServerEvents)_server).closed().addListener(new EventListener4<ServerClosedEventArgs>() {
			public void onEvent(Event4<ServerClosedEventArgs> e, ServerClosedEventArgs args) {
				bean.unregister();
			}
		});
		
		((ObjectServerEvents)_server).clientConnected().addListener(new EventListener4<ClientConnectionEventArgs>() { public void onEvent(Event4<ClientConnectionEventArgs> e, ClientConnectionEventArgs args) {
			bean.notifyClientConnected();
		}});
		
		((ObjectServerEvents)_server).clientDisconnected().addListener(new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
			bean.notifyClientDisconnected();
		}});
	}

	private EventListener4<StringEventArgs> registerCloseEventNotification() {
		EventListener4<StringEventArgs> listener = new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
			synchronized (_closeEventRaised) {
				_closeEventRaised.value = true;
				_closeEventRaised.notifyAll();
			}
		}};		
		_server.clientDisconnected().addListener(listener);
		return listener;
	}
	
	public void tearDown() throws Exception {
		_server.clientDisconnected().removeListener(_listener);
		_server.close();
		
		super.tearDown();
	}
	
	final BooleanByRef _closeEventRaised = new BooleanByRef();	
	private EventListener4<StringEventArgs> _listener;
	private ObjectServerImpl _server;
	
}
