/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

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
package com.db4o.test.legacy.soda.wrapper.untyped;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STCharWU implements STClass{
	
	final static String DESCENDANT = "i_char";
	
	public static transient SodaTest st;
	
	Object i_char;
	
	public STCharWU(){
	}
	
	private STCharWU(char a_char){
		i_char = new Character(a_char);
	}
	
	public Object[] store() {
		return new Object[]{
			new STCharWU((char)0),
			new STCharWU((char)1),
			new STCharWU((char)99),
			new STCharWU((char)909)
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(new STCharWU((char)0));  
		
		// Primitive default values are ignored, so we need an 
		// additional constraint:
		q.descend(DESCENDANT).constrain(new Character((char)0));
		st.expectOne(q, store()[0]);
	}
	
	public void testNotEquals(){
		Query q = st.query();
		Object[] r = store();
		Constraint c = q.constrain(r[0]);
		q.descend(DESCENDANT).constraints().not();
		st.expect(q, new Object[] {r[1], r[2], r[3]});
	}
	
	public void testGreater(){
		Query q = st.query();
		Constraint c = q.constrain(new STCharWU((char)9));
		q.descend(DESCENDANT).constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		Constraint c = q.constrain(new STCharWU((char)1));
		q.descend(DESCENDANT).constraints().smaller();
		st.expectOne(q, store()[0]);
	}
	
	public void testIdentity(){
		Query q = st.query();
		Constraint c = q.constrain(new STCharWU((char)1));
		ObjectSet set = q.execute();
		STCharWU identityConstraint = (STCharWU)set.next();
		identityConstraint.i_char = new Character((char)9999);
		q = st.query();
		q.constrain(identityConstraint).identity();
		identityConstraint.i_char = new Character((char)1);
		st.expectOne(q,store()[1]);
	}
	
	public void testNotIdentity(){
		Query q = st.query();
		Constraint c = q.constrain(new STCharWU((char)1));
		ObjectSet set = q.execute();
		STCharWU identityConstraint = (STCharWU)set.next();
		identityConstraint.i_char = new Character((char)9080);
		q = st.query();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.i_char = new Character((char)1);
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testConstraints(){
		Query q = st.query();
		q.constrain(new STCharWU((char)1));
		q.constrain(new STCharWU((char)0));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if(csa.length != 2){
			st.error("Constraints not returned");
		}
	}
	
	public void testEvaluation(){
		Query q = st.query();
		q.constrain(new STCharWU());
		q.constrain(new Evaluation() {
			public void evaluate(Candidate candidate) {
				STCharWU sts = (STCharWU)candidate.getObject();
				candidate.include((((Character)sts.i_char).charValue() + 2) > 100);
			}
		});
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
}

