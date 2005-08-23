/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */
using System;
using com.db4o;
using com.db4o.query;
using System.Collections;

namespace com.db4o.test.nativequeries
{
	public class Cat 
	{
		public String name;
    
		public Cat()
		{
		}
    
		public Cat(String name)
		{
			this.name = name;
		}
    
		public void store()
		{
			Tester.store(new Cat("Fritz"));
			Tester.store(new Cat("Garfield"));
			Tester.store(new Cat("Tom"));
			Tester.store(new Cat("Occam"));
			Tester.store(new Cat("Zora"));
		}
    
		public void testClassPredicate()
		{
			ObjectContainer objectContainer = Tester.objectContainer();
			ObjectSet objectSet = objectContainer.query(new CatPredicate());
            Tester.ensureEquals(2, objectSet.Count);
            ensureContains(objectSet, "Occam");
            ensureContains(objectSet, "Zora");
		}
    
		public class CatPredicate : Predicate
		{
			public bool match(Cat cat)
			{
				return cat.name == "Occam"  || cat.name == "Zora"; 
			}
		}

#if NET_2_0
        public void testGenericPredicate()
        {
            ObjectContainer objectContainer = Tester.objectContainer();
            System.Collections.Generic.IList<Cat> found = objectContainer.query<Cat>(delegate(Cat c)
            {
                return c.name == "Occam" || c.name == "Zora";
            });
            Tester.ensureEquals(2, found.Count);
            ensureContains(found, "Occam");
            ensureContains(found, "Zora");
        }
#endif

        private void ensureContains(IEnumerable objectSet, string catName)
        {
            foreach (Cat cat in objectSet)
            {
              if (cat.name == catName) return;
            }
            Tester.ensure(catName + " expected!", false);
        }
	}
}
