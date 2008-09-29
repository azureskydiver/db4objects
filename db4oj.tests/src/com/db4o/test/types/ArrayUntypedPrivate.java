/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.types;

import java.util.*;

@SuppressWarnings("unused")
public class ArrayUntypedPrivate extends RTest
{
	private Object[] oBoolean;
	private Object[] nBoolean;
	
	private Object[] oByte;
	private Object[] nByte;
	
	private Object[] oCharacter;
	private Object[] nCharacter;

	private Object[] oDouble;
	private Object[] nDouble;
	
	private Object[] oFloat;
	private Object[] nFloat;
	
	private Object[] oInteger;
	private Object[] nInteger;
	
	private Object[] oLong;
	private Object[] nLong;

	private Object[] oShort;
	private Object[] nShort;
	
	private Object[] oString;
	private Object[] nString;
	
	private Object[] oDate;
	private Object[] nDate;
	
	private Object[] oObject;
	private Object[] nObject;

	public void set(int ver){
		if(ver == 1){
			oBoolean = new Boolean[]{new Boolean(true), new Boolean(false), null };
			nBoolean = null;
	
			oByte = new Byte[]{ new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE), new Byte((byte)0), null};
			nByte = null;
		
			oCharacter = new Character[]{ new Character((char)(Character.MAX_VALUE - 1)), new Character((Character.MIN_VALUE)), new Character((char)(0)),null};
			nCharacter = null;

			oDouble = new Double[]{new Double(Double.MAX_VALUE - 1), new Double(Double.MIN_VALUE), new Double(0), null };
			nDouble = null;
	
			oFloat = new Float[] {new Float(Float.MAX_VALUE - 1), new Float(Float.MIN_VALUE), new Float(0), null};
			nFloat = null;
	
			oInteger = new Integer[] {new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(0), null};
			nInteger = null;
	
			oLong = new Long[] { new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(0), null};
			nLong = null;

			oShort = new Short[] { new Short((short)(Short.MAX_VALUE - 1)), new Short((Short.MIN_VALUE)), new Short((short)(0)), null };
			nShort = null;
	
			oString = new String[] {"db4o rules", "cool", "supergreat"};
			nString = null;
		
			oDate = new Date[] {new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime(), null};
			nDate = null;
		
			oObject = new ObjectSimplePrivate[]{new ObjectSimplePrivate("so"), null, new ObjectSimplePrivate("far"), new ObjectSimplePrivate("O.K.")};
			nObject = null;
		}else{
			oBoolean = new Boolean[]{new Boolean(false), new Boolean(true), new Boolean(true)};
			nBoolean = new Boolean[]{null, new Boolean(true), new Boolean(false)};
	
			oByte = new Byte[]{ new Byte(Byte.MIN_VALUE), new Byte(Byte.MAX_VALUE), new Byte((byte)1), new Byte((byte)-1)};
			nByte = new Byte[]{ null, new Byte(Byte.MAX_VALUE), new Byte(Byte.MIN_VALUE), new Byte((byte)0)};
		
			oCharacter = new Character[]{ new Character(Character.MIN_VALUE), new Character((char)(Character.MAX_VALUE - 1)), new Character((char)(0)),new Character((char)(Character.MAX_VALUE - 1)),new Character((char)1)};
			nCharacter = new Character[]{ null, new Character((char)(Character.MAX_VALUE - 1)), new Character((Character.MIN_VALUE)), new Character((char)(0))};

			oDouble = new Double[]{new Double(Double.MIN_VALUE), new Double(0)};
			nDouble = new Double[]{null, new Double(Double.MAX_VALUE - 1), new Double(Double.MIN_VALUE), new Double( - 123.12344), new Double( - 12345.123445566)};
	
			oFloat = new Float[] {new Float((float)- 98.765)};
			nFloat = null;
	
			oInteger = new Integer[] {new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(111), new Integer(-333)};
			nInteger = new Integer[] {null, new Integer(Integer.MAX_VALUE - 1), new Integer(Integer.MIN_VALUE), new Integer(0)};
	
			oLong = new Long[] { new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(1)};
			nLong = new Long[] { null, new Long(Long.MAX_VALUE - 1), new Long(Long.MIN_VALUE), new Long(0)};

			oShort = new Short[] { new Short((Short.MIN_VALUE)), new Short((short)(Short.MAX_VALUE - 1)), new Short((short)(0))};
			nShort = new Short[] { new Short((short)(Short.MAX_VALUE - 1)), null, new Short((Short.MIN_VALUE)), new Short((short)(0))};
	
			oString = new String[] {"db4o rulez", "cool", "supergreat"};
			nString = new String[] {null, "db4o rules", "cool", "supergreat", null};
		
			oDate = new Date[] {new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(1999,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime()};
			nDate = new Date[] {null, new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2000,0,1).getTime(), new GregorianCalendar(2001,11,31).getTime(), null};
		
			oObject = new ObjectSimplePrivate[]{new ObjectSimplePrivate("works"),  new ObjectSimplePrivate("far"), new ObjectSimplePrivate("excellent")};
			nObject = new ObjectSimplePrivate[]{};
		}
	}
	
	public boolean jdk2(){
		return true;
	}
}
