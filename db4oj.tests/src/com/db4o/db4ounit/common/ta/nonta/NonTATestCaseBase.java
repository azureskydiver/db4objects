/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.nonta;

import com.db4o.db4ounit.common.ta.*;

public abstract class NonTATestCaseBase extends TransparentActivationTestCaseBase {
    
    protected void store() throws Exception {
        Object value = createValue();
        store(value);
    }
    
    public void test() throws Exception {
        Object item = retrieveOnlyInstance(itemClass());
        assertNullItem(item);
        assertValue(item);
    }
    
    protected abstract void assertValue(Object obj);
    
    protected abstract Class itemClass();
    
    protected abstract Object createValue();
    
    void assertNullItem(Object obj) {
        //do nothing for non-TA tests
        return;
    }
}
