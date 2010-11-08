/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package f1;

import f1.collection.array.*;
import f1.collection.list.*;
import f1.collection.map.*;
import f1.collection.set.*;
import f1.one_to_one.*;
import f1.stepbystep.*;
import f1.updateevent.*;

public class RunAllExamples {
	public static void main(String[] args) {
		System.out.println("Running all Examples");
		new ArrayExample().run();
		new ListExample().run();
		new MapExample().run();
		new SetExample().run();
		new OneToOneExample().run();
		new StepByStepExample().run();
		UpdateEventExample.main(null);
		System.out.println("All Examples Done!");
	}
}
