/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.enums;

import java.awt.Color;
import java.io.File;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;


public class EnumExample {
	public final static String YAPFILENAME="formula1.yap";
	public static void main(String[] args) {
        setPilots();
        checkPilots();
    
        deletePilots();
        checkPilots();
        deleteQualification();
        updateQualification();
  }
	// end main
	
	
	
	public static void setPilots(){
		new File(YAPFILENAME).delete();
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			db.set(new Pilot("Michael Schumacher",Qualification.WINNER));
			db.set(new Pilot("Rubens Barrichello",Qualification.PROFESSIONAL));
		} finally {
			db.close();
		}
	}
	// end setPilots
	
	public static void checkPilots(){
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
	        ObjectSet result = db.query(Pilot.class);
	        System.out.println("Saved pilots: " + result.size());
	        for(int x = 0; x < result.size(); x++){
	        	Pilot pilot = (Pilot )result.get(x);
	        	if (pilot.getQualification() == Qualification.WINNER){
	        		System.out.println("Winner pilot: " + pilot);
	        	} else if (pilot.getQualification() == Qualification.PROFESSIONAL){
	        		System.out.println("Professional pilot: " + pilot);
	        	}  else {
	        		System.out.println("Uncategorized pilot: " + pilot);
	        	}
	        }
		} finally {
			db.close();
		}
    }
	// end checkPilots
	
	public static void updateQualification(){
		System.out.println("Updating WINNER qualification constant");
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			Query query = db.query();
			query.constrain(Qualification.class);
			query.descend("qualification").constrain("WINNER");
	        ObjectSet result = query.execute();
	        for(int x = 0; x < result.size(); x++){
	        	Qualification qualification = (Qualification)result.get(x);
	        	qualification.testChange("WINNER2006");
	        	db.set(qualification);
	        }
		} finally {
			db.close();
		}
		printQualification();
    }
	// end updateQualification
	
	public static void deletePilots(){
		System.out.println("Qualification enum before delete Pilots");
		printQualification();
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		db.ext().configure() .objectClass(Pilot.class).objectField("qualification").cascadeOnDelete(true);

		try {
	        ObjectSet result = db.query(Pilot.class);
	        for(int x = 0; x < result.size(); x++){
	        	Pilot pilot = (Pilot )result.get(x);
	        	db.delete(pilot);
	        }
		} finally {
			db.close();
		}
		System.out.println("Qualification enum after delete Pilots");
		printQualification();
    }
	// end deletePilots
	
	public static void printQualification(){
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		try {
			ObjectSet  result = db.query(Qualification.class);
			System.out.println("results: " + result.size());
	        for(int x = 0; x < result.size(); x++){
	        	Qualification pq = (Qualification)result.get(x);
	        	System.out.println("Category: "+pq);
	        }
		} finally {
			db.close();
		}
	}
	// end printQualification
	
	public static void deleteQualification(){
		System.out.println("Explicit delete of Qualification enum");
		ObjectContainer db=Db4o.openFile(YAPFILENAME);
		db.ext().configure() .objectClass(Qualification.class).cascadeOnDelete(true);
		try {
			ObjectSet  result = db.query(Qualification.class);
	        for(int x = 0; x < result.size(); x++){
	        	Qualification pq = (Qualification)result.get(x);
	        	db.delete(pq);
	        }
		} finally {
			db.close();
		}
		printQualification();
    }
	// end deleteQualification
}
