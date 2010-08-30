package com.db4o.rmi.test;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.rmi.*;

import db4ounit.*;

public class TheSimplest implements TestCase, TestLifeCycle {

	public static interface Facade {
		int intCall();

		void voidCall();

		void addListener(@Proxy Runnable r);
	}

	public static class FacadeImpl implements Facade {

		private List<Runnable> listeners = new ArrayList<Runnable>();

		public int intCall() {
			return 42;
		}

		public void voidCall() {
		}

		public void addListener(Runnable r) {
			synchronized (listeners) {
				listeners.add(r);
			}
		}

		public void triggerListeners() {
			List<Runnable> l;
			synchronized (listeners) {
				l = new ArrayList<Runnable>(listeners);
			}
			for (Runnable runnable : l) {
				runnable.run();
			}
		}
	}

	Distributor<Facade> client;
	Distributor<Facade> server;
	FacadeImpl concreteFacade;

	public void setUp() {

		concreteFacade = new FacadeImpl();

		server = new Distributor<Facade>(null, concreteFacade);
		client = new Distributor<Facade>(null, Facade.class);

		server.setConsumer(client);
		client.setConsumer(server);
	}

	public void testBasic() throws InterruptedException {

		Assert.areEqual(42, client.sync().intCall());

		final BlockingQueue4<Integer> q = new BlockingQueue<Integer>();

		client.async(new Callback<Integer>() {

			public void returned(Integer value) {
				q.add(value);
			}
		}).intCall();

		Assert.areEqual(42, (int) q.next());

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}

		Assert.isFalse(q.hasNext());

		client.sync().voidCall();

	}

	public void tearDown() throws Exception {
	}

	public void testProxy() {
		final BlockingQueue4<Object> q = new BlockingQueue<Object>();

		client.sync().addListener(new Runnable() {
			public void run() {
				q.add(new Object());
			}
		});

		Assert.isFalse(q.hasNext());

		concreteFacade.triggerListeners();

		Assert.isNotNull(q.next());
	}

}
