/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.inside.query.*;
import com.db4o.inside.query.result.*;


public class IdListQueryResultTestCase extends QueryResultTestCase {

	public static void main(String[] args) {
		new IdListQueryResultTestCase().runSolo();
	}

	protected AbstractQueryResult newQueryResult() {
		return new IdListQueryResult(trans());
	}

}
