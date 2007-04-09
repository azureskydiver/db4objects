/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.messages.*;

public class ObjectServerImpl implements ObjectServer, ExtObjectServer, Runnable,
		LoopbackSocketServer {
	
	private static final int START_THREAD_WAIT_TIMEOUT = 5000;

	private final String _name;

	private ServerSocket4 _serverSocket;
	
	private final int _port;

	private int i_threadIDGen = 1;

	private final Collection4 _dispatchers = new Collection4();

	LocalObjectContainer _container;

	private final Object _startupLock=new Object();
	
	private Config4Impl _config;
	
	private BlockingQueue _committedInfosQueue = new BlockingQueue();
	
	private CommittedCallbacksDispatcher _committedCallbacksDispatcher;
	
	public ObjectServerImpl(final LocalObjectContainer container, int port) {
		_container = container;
		_port = port;
		_config = _container.configImpl();
		_name = "db4o ServerSocket FILE: " + container.toString() + "  PORT:"+ _port;
		
		_container.setServer(true);	
		configureObjectServer();
		
		boolean ok = false;
		try {
			ensureLoadStaticClass();
			ensureLoadConfiguredClasses();
			startCommittedCallbackThread(_committedInfosQueue);
			startServer();
			ok = true;
		} finally {
			if(!ok) {
				close();
			}
		}
	}

	private void startServer() {		
		if (isEmbeddedServer()) {
			return;
		}
		
		synchronized(_startupLock) {
			startServerSocket();
			startServerThread();
			boolean started=false;
			while(!started) {
				try {
					_startupLock.wait(START_THREAD_WAIT_TIMEOUT);
					started=true;
				}
				// not specialized to InterruptException for .NET conversion
				catch (Exception exc) {
				}
			}
		}
	}

	private void startServerThread() {
		synchronized(_startupLock) {
			final Thread thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		}
	}

	private void startServerSocket() {
		try {
			_serverSocket = new ServerSocket4(_port);
			_serverSocket.setSoTimeout(_config.timeoutServerSocket());
		} catch (IOException e) {
			Exceptions4.throwRuntimeException(Messages.COULD_NOT_OPEN_PORT, "" + _port);
		}
	}

	private boolean isEmbeddedServer() {
		return _port <= 0;
	}

	private void ensureLoadStaticClass() {
		_container.produceClassMetadata(_container.i_handlers.ICLASS_STATICCLASS);
	}
	
	private void ensureLoadConfiguredClasses() {
		// make sure all configured YapClasses are up in the repository
		_config.exceptionalClasses().forEachValue(new Visitor4() {
			public void visit(Object a_object) {
				_container.produceClassMetadata(_container.reflector().forName(
						((Config4Class) a_object).getName()));
			}
		});
	}

	private void configureObjectServer() {
		_config.callbacks(false);
		_config.isServer(true);
		// the minium activation depth of com.db4o.User.class should be 1.
		// Otherwise, we may get null password.
		_config.objectClass(User.class).minimumActivationDepth(1);
	}

	public void backup(String path) throws IOException {
		_container.backup(path);
	}

	final void checkClosed() {
		if (_container == null) {
			Exceptions4.throwRuntimeException(Messages.CLOSED_OR_OPEN_FAILED, _name);
		}
		_container.checkClosed();
	}

	public synchronized boolean close() {
		closeServerSocket();
		stopCommittedCallbacksDispatcher();
		closeMessageDispatchers();
		return closeFile();
	}

	private void stopCommittedCallbacksDispatcher() {
		if(_committedCallbacksDispatcher != null){
			_committedCallbacksDispatcher.stop();
		}
	}

	private boolean closeFile() {
		if (_container != null) {
			_container.close();
			_container = null;
		}
		return true;
	}

	private void closeMessageDispatchers() {
		Iterator4 i = iterateDispatchers();
		while (i.moveNext()) {
			try {
				((ServerMessageDispatcher) i.current()).close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Iterator4 iterateDispatchers() {
		synchronized (_dispatchers) {
			return new Collection4(_dispatchers).iterator();
		}
	}

	private void closeServerSocket() {
		try {
			if (_serverSocket != null) {
				_serverSocket.close();
			}
		} catch (Exception e) {
			if (Deploy.debug) {
				System.out
						.println("YapServer.close() ServerSocket failed to close.");
			}
		}
		_serverSocket = null;
	}

	public Configuration configure() {
		return _config;
	}

	public ExtObjectServer ext() {
		return this;
	}

	ServerMessageDispatcherImpl findThread(int a_threadID) {
		synchronized (_dispatchers) {
			Iterator4 i = _dispatchers.iterator();
			while (i.moveNext()) {
				ServerMessageDispatcherImpl serverThread = (ServerMessageDispatcherImpl) i.current();
				if (serverThread.i_threadID == a_threadID) {
					return serverThread;
				}
			}
		}
		return null;
	}

	public synchronized void grantAccess(String userName, String password) {
		checkClosed();
		synchronized (_container.i_lock) {
			User existing = getUser(userName);
			if (existing != null) {
				setPassword(existing, password);
			} else {
				addUser(userName, password);
			}
			_container.commit();
		}
	}

	private void addUser(String userName, String password) {
		_container.set(new User(userName, password));
	}

	private void setPassword(User existing, String password) {
		existing.password = password;
		_container.set(existing);
	}

	public User getUser(String userName) {
		final ObjectSet result = queryUsers(userName);
		if (!result.hasNext()) {
			return null;
		}
		return (User) result.next();
	}

	private ObjectSet queryUsers(String userName) {
		_container.showInternalClasses(true);
		try {
			return _container.get(new User(userName, null));
		} finally {
			_container.showInternalClasses(false);
		}
	}

	public ObjectContainer objectContainer() {
		return _container;
	}

	public ObjectContainer openClient() {
		return openClient(Db4o.cloneConfiguration());
	}

	public synchronized ObjectContainer openClient(Configuration config) {
		checkClosed();
		ClientObjectContainer client = new ClientObjectContainer(config,
				openClientSocket(), Const4.EMBEDDED_CLIENT_USER
						+ (i_threadIDGen - 1), "", false);
		client.blockSize(_container.blockSize());
		return client;
	}

	public LoopbackSocket openClientSocket() {
		int timeout = _config.timeoutClientSocket();
		LoopbackSocket clientFake = new LoopbackSocket(this, timeout);
		LoopbackSocket serverFake = new LoopbackSocket(this, timeout, clientFake);
		try {
			ServerMessageDispatcher messageDispatcher = new ServerMessageDispatcherImpl(this, _container,
					serverFake, newThreadId(), true);
			addServerMessageDispatcher(messageDispatcher);
			messageDispatcher.startDispatcher();
			return clientFake;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	void removeThread(ServerMessageDispatcherImpl aThread) {
		synchronized (_dispatchers) {
			_dispatchers.remove(aThread);
		}
	}

	public synchronized void revokeAccess(String userName) {
		checkClosed();
		synchronized (_container.i_lock) {
			deleteUsers(userName);
			_container.commit();
		}
	}

	private void deleteUsers(String userName) {
		ObjectSet set = queryUsers(userName);
		while (set.hasNext()) {
			_container.delete(set.next());
		}
	}

	public void run() {
		setThreadName();
		logListeningOnPort();
		notifyThreadStarted();
		listen();
	}

	

	private void startCommittedCallbackThread(BlockingQueue committedInfosQueue) {
		_committedCallbacksDispatcher = new CommittedCallbacksDispatcher(this, committedInfosQueue);
		Thread thread = new Thread(_committedCallbacksDispatcher);
		thread.setDaemon(true);
		thread.start();
	}

	private void setThreadName() {
		Thread.currentThread().setName(_name);
	}

	private void listen() {
		while (_serverSocket != null) {
			try {
				ServerMessageDispatcher messageDispatcher = new ServerMessageDispatcherImpl(this, _container,
						_serverSocket.accept(), newThreadId(), false);
				addServerMessageDispatcher(messageDispatcher);
				messageDispatcher.startDispatcher();
			} catch (Exception e) {
			}
		}
	}

	private void notifyThreadStarted() {
		synchronized (_startupLock) {
			_startupLock.notifyAll();
		}
	}

	private void logListeningOnPort() {
		_container.logMsg(Messages.SERVER_LISTENING_ON_PORT, "" + _serverSocket.getLocalPort());
	}

	private int newThreadId() {
		return i_threadIDGen++;
	}

	private void addServerMessageDispatcher(ServerMessageDispatcher thread) {
		synchronized (_dispatchers) {
			_dispatchers.add(thread);
		}
	}

	public void addCommittedInfoMsg(MCommittedInfo message) {
		_committedInfosQueue.add(message);			
	}
	
	public void sendCommittedInfoMsg(MCommittedInfo message) {		
		Iterator4 i = iterateDispatchers();
		while(i.moveNext()){
			ServerMessageDispatcher dispatcher = (ServerMessageDispatcher) i.current();
			if(dispatcher.caresAboutCommitted()){
				dispatcher.writeIfAlive(message);
			}
		}
	}

}
