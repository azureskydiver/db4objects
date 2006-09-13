/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.btree;

import com.db4o.inside.slots.*;


/**
 * @exclude
 */
public interface SlotListener {

    public void onFree(Slot slot);

}
