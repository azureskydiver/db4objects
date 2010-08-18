/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import com.db4o.drs.foundation.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.inband.*;
import com.versant.event.*;

public class EventProcessorFactory {
	
	public static EventProcessor newInstance (EventConfiguration eventConfiguration,
			LinePrinter linePrinter) {
		VodCobra cobra = new VodCobra(new VodDatabase(eventConfiguration.databaseName));
		VodEventClient client = new VodEventClient(eventConfiguration, new ExceptionListener (){
	        public void exceptionOccurred (Throwable exception){
	        	EventProcessor.unrecoverableExceptionOccurred(exception);
	        }
	    });
		Object lock = new Object();
		EventProcessorSideCommunication comm = new InBandEventProcessorSideCommunication(cobra, client, lock);
		EventProcessor eventProcessor = new EventProcessor(client, linePrinter, cobra, comm, lock);
		return eventProcessor;
	}

}