/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using Db4objects.Db4o;
using Db4objects.Db4o.Query;
using Db4objects.Db4o.Diagnostic;
using System;
using System.IO;

namespace Db4objects.Db4odoc.Diagnostics
{
	public class DiagnosticExample 
	{
		public readonly static string YapFileName = "formula1.yap";

        public static void Main(string[] args)
        {
            TestEmpty();
            TestArbitrary();
            TestIndexDiagnostics();
            TestTranslatorDiagnostics();
        }
        // end Main

		 public static void TestEmpty() {
    		Db4oFactory.Configure().Diagnostic().AddListener(new DiagnosticToConsole());
			File.Delete(YapFileName);    
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try {
        		SetEmptyObject(db);
			}
			finally {
				db.Close();
			}
		}
		// end TestEmpty
	    
		private static void SetEmptyObject(IObjectContainer db){
    		Empty empty = new Empty();
			db.Set(empty);
		}
		// end SetEmptyObject
	    	
		public static void TestArbitrary() {
    		Db4oFactory.Configure().Diagnostic().AddListener(new DiagnosticToConsole());
    		File.Delete(YapFileName);    
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try {
        		Pilot pilot = new Pilot("Rubens Barrichello",99);
        		db.Set(pilot);
        		QueryPilot(db);
			}
			finally {
				db.Close();
			}
		}
		// end TestArbitrary
		
		private static void QueryPilot(IObjectContainer db){
    		int[]  i = new int[]{19,100};
    		IObjectSet result = db.Query(new ArbitraryQuery(i));
    		ListResult(result);
		}
		// end QueryPilot

		public static void TestIndexDiagnostics() {
    		Db4oFactory.Configure().Diagnostic().RemoveAllListeners();
    		Db4oFactory.Configure().Diagnostic().AddListener(new IndexDiagListener());
    		Db4oFactory.Configure().UpdateDepth(3);
			File.Delete(YapFileName);    
			IObjectContainer db=Db4oFactory.OpenFile(YapFileName);
			try {
        		Pilot pilot1 = new Pilot("Rubens Barrichello",99);
        		db.Set(pilot1);
        		Pilot pilot2 = new Pilot("Michael Schumacher",100);
        		db.Set(pilot2);
        		QueryPilot(db);
        		SetEmptyObject(db);
        		IQuery query = db.Query();
        		query.Constrain(typeof(Pilot));
				query.Descend("_points").Constrain("99");
				IObjectSet  result = query.Execute();
				ListResult(result);
			}
			finally {
				db.Close();
			}
		}
		// end TestIndexDiagnostics
	     
		public static void TestTranslatorDiagnostics() {
    		StoreTranslatedCars();
    		RetrieveTranslatedCars();
    		RetrieveTranslatedCarsNQ();
    		RetrieveTranslatedCarsNQUnopt();
    		RetrieveTranslatedCarsSODAEv();
		}
		// end TestTranslatorDiagnostics
	    
		public static void StoreTranslatedCars() {
    		Db4oFactory.Configure().ExceptionsOnNotStorable(true);
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		File.Delete(YapFileName);    
			IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try {
				Car car1 = new Car("BMW");
				System.Diagnostics.Trace.WriteLine("ORIGINAL: " + car1);
				db.Set(car1);
				Car car2 = new Car("Ferrari");
				System.Diagnostics.Trace.WriteLine("ORIGINAL: " + car2);
				db.Set(car2);
			} catch (Exception exc) {
				System.Diagnostics.Trace.WriteLine(exc.Message);
				return;
			} finally {
				db.Close();
			}
		}
		// end StoreTranslatedCars

		public static void RetrieveTranslatedCars() {
    		Db4oFactory.Configure().Diagnostic().RemoveAllListeners();
    		Db4oFactory.Configure().Diagnostic().AddListener(new TranslatorDiagListener());
    		Db4oFactory.Configure().ExceptionsOnNotStorable(true);
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try {
				IQuery query = db.Query();
				query.Constrain(typeof(Car));
				IObjectSet  result = query.Execute();
				ListResult(result);
			} finally {
				db.Close();
			}
		}
		// end RetrieveTranslatedCars

		public static void RetrieveTranslatedCarsNQ() {
    		Db4oFactory.Configure().Diagnostic().RemoveAllListeners();
    		Db4oFactory.Configure().Diagnostic().AddListener(new TranslatorDiagListener());
    		Db4oFactory.Configure().ExceptionsOnNotStorable(true);
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try {
				IObjectSet  result = db.Query(new NewCarModel());
				ListResult(result);
			} finally {
				db.Close();
			}
		}
		// end RetrieveTranslatedCarsNQ
	    
		public static void RetrieveTranslatedCarsNQUnopt() {
    		Db4oFactory.Configure().OptimizeNativeQueries(false);
    		Db4oFactory.Configure().Diagnostic().RemoveAllListeners();
    		Db4oFactory.Configure().Diagnostic().AddListener(new TranslatorDiagListener());
    		Db4oFactory.Configure().ExceptionsOnNotStorable(true);
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try {
				IObjectSet  result = db.Query(new NewCarModel());
				ListResult(result);
			} finally {
				Db4oFactory.Configure().OptimizeNativeQueries(true);
				db.Close();
			}
		}
		// end RetrieveTranslatedCarsNQUnopt

		public static void RetrieveTranslatedCarsSODAEv() {
    		Db4oFactory.Configure().Diagnostic().RemoveAllListeners();
    		Db4oFactory.Configure().Diagnostic().AddListener(new TranslatorDiagListener());
    		Db4oFactory.Configure().ExceptionsOnNotStorable(true);
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).Translate(new CarTranslator());
    		Db4oFactory.Configure().ObjectClass(typeof(Car)).CallConstructor(true);
    		IObjectContainer db = Db4oFactory.OpenFile(YapFileName);
			try {
				IQuery query = db.Query();
				query.Constrain(typeof(Car));
				query.Constrain(new CarEvaluation());
				IObjectSet  result = query.Execute();
				ListResult(result);
			} finally {
				db.Close();
			}
		}
		// end RetrieveTranslatedCarsSODAEv

		public static void ListResult(IObjectSet result)
		{
			Console.WriteLine(result.Count);
			foreach (object item in result)
			{
				Console.WriteLine(item);
			}
		}
		// end ListResult
	}
}
