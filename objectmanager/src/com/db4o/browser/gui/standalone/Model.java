/*
 * This file is part of com.db4o.browser.
 *
 * com.db4o.browser is free software; you can redistribute it and/or modify
 * it under the terms of version 2 of the GNU General Public License
 * as published by the Free Software Foundation.
 *
 * com.db4o.browser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.swtworkbench.ed; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.db4o.browser.gui.standalone;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.StoredClass;
import com.db4o.query.Query;
import com.swtworkbench.community.xswt.metalogger.Logger;

/**
 * Class Model.
 * 
 * @author djo
 */
public class Model {
    /**
     * @param container
     */
    public Model(String path) {
        container = Db4o.openFile(path);
        if (container == null)
            throw new IllegalArgumentException("Could not open: " + path);
    }

	private ObjectContainer container = null;
    
    public StoredClass[] storedClasses() {
       	return container.ext().storedClasses();
    }
    
    public ObjectSet instances(String clazz) {
        Query q = container.query();
//        IClass toReturn = null;
//        try {
//            toReturn = CReflect.getDefault().forName(clazz);
//        }
        Class toReturn = null;
        try {
            toReturn = Class.forName(clazz);
        }
        catch (Exception e) {
            Logger.log().error(e, "Unable to Class.forName()");
            throw new RuntimeException();
        }
        return container.get(toReturn);
    }
    
    public void close() {
        if (container != null)
            container.close();
        container = null;
    }
    
}
