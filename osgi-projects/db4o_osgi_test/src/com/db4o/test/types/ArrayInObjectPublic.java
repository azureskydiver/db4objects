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
package com.db4o.test.types;

import java.util.*;

public class ArrayInObjectPublic extends RTest
{
	public Object oBoolean;
	public Object nBoolean;
	
	public Object oByte;
	public Object nByte;
	public Object sByte;
	
	public Object oCharacter;
	public Object nCharacter;

	public Object oDouble;
	public Object nDouble;
	
	public Object oFloat;
	public Object nFloat;
	
	public Object oInteger;
	public Object nInteger;
	
	public Object oLong;
	public Object nLong;

	public Object oShort;
	public Object nShort;
	
	public Object oString;
	public Object nString;
	
	public Object oDate;
	public Object nDate;
	

	public void set(int ver){
		if(ver == 1){
			oBoolean = new Boolean[]{new Boolean(true), new Boolean(false), null };
			nBoolean = null;
			sByte = new byte[] {(byte)3};
	
			oByte = new Byte[]{ new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE), new Byte((byte)0), null};
			nByte = null;
		
			oCharacter = new Character[]{ new Character((char)(Character.MAX_VALUE - 1)), new Character(Character.MIN_VALUE), new Character((char)(0)),null};
			nCharacter = null;

			oDouble = new Double[]{new Double(Double.MAX_VALUE - 1), new Double(Double.MIN_VALUE), new Double(0), null };
			nDouble = null;
	
			oFloat = new Float[] {new Float(Float.MAX_VALUE - 1), new Float(Float.MIN_VALUE), new Float(0), null};
			nFloat = null;
	
			oInteger = new Integer[] {new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(0), null};
			nInteger = null;
	
			oLong = new Long[] { new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(0), null};
			nLong = null;

			oShort = new Short[] { new Short((short)(Short.MAX_VALUE - 1)), new Short(Short.MIN_VALUE), new Short((short)(0)), null };
			nShort = null;
	
			oString = new String[] {"db4o rules", "cool", "supergreat"};
			nString = null;
		
			oDate = new Date[] {new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime(), null};
			nDate = null;
		}else{
			oBoolean = new Boolean[]{new Boolean(false), new Boolean(true), new Boolean(true)};
			nBoolean = new Boolean[]{null, new Boolean(true), new Boolean(false)};
	
			oByte = new Byte[]{ new Byte(Byte.MIN_VALUE), new Byte(Byte.MAX_VALUE), new Byte((byte)1), new Byte((byte)-1)};
			nByte = new Byte[]{ null, new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE), new Byte((byte)0)};
			sByte = new byte[] {(byte)5};
		
			oCharacter = new Character[]{ new Character(Character.MIN_VALUE), new Character((char)(Character.MAX_VALUE - 1)), new Character((char)(0)),new Character((char)(Character.MAX_VALUE - 1)),new Character((char)1)};
			nCharacter = new Character[]{ null, new Character((char)(Character.MAX_VALUE - 1)), new Character(Character.MIN_VALUE), new Character((char)(0))};

			oDouble = new Double[]{new Double(Double.MIN_VALUE), new Double(0)};
			nDouble = new Double[]{null, new Double(Double.MAX_VALUE - 1), new Double(Double.MIN_VALUE), new Double( - 123.12344), new Double( - 12345.123445566)};
	
			oFloat = new Float[] {new Float((float)- 98.765)};
			nFloat = null;
	
			oInteger = new Integer[] {new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(111), new Integer(-333)};
			nInteger = new Integer[] {null, new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(0)};
	
			oLong = new Long[] { new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(1)};
			nLong = new Long[] { null, new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(0)};

			oShort = new Short[] { new Short(Short.MIN_VALUE), new Short((short)(Short.MAX_VALUE - 1)), new Short((short)(0))};
			nShort = new Short[] { new Short((short)(Short.MAX_VALUE - 1)), null, new Short(Short.MIN_VALUE), new Short((short)(0))};
	
			oString = new String[] {"db4o rulez", "cool", "supergreat"};
			nString = new String[] {null, "db4o rules", "cool", "supergreat", null};
		
			oDate = new Date[] {new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(1999,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime()};
			nDate = new Date[] {null, new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime(), null};
		}
	}
}
