/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public interface SlotListener {

    public void onFree(Slot slot);

}
