/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.inside.query.*;

public final class MQueryExecute extends MsgQuery {
	
	private QueryEvaluationMode _evaluationMode;
	
	public boolean processAtServer(YapServerThread serverThread) {
		unmarshall();
        writeQueryResult(execute(), serverThread, _evaluationMode);
		return true;
	}

	private AbstractQueryResult execute() {
		
		synchronized (streamLock()) {
            
            // TODO: The following used to run outside of the
            // synchronisation block for better performance but
            // produced inconsistent results, cause unknown.

            QQuery query = (QQuery) stream().unmarshall(_payLoad);
            query.unmarshall(transaction());
            
            _evaluationMode = query.evaluationMode();
            
			return executeFully(query);
			
		}
	}

	private AbstractQueryResult executeFully(QQuery query) {
		try {
			AbstractQueryResult qr = newQueryResult(query.evaluationMode());
			qr.loadFromQuery(query);
			return qr;
		} catch (Exception e) {
			return newQueryResult(query.evaluationMode()); 
		}
	}
	
}