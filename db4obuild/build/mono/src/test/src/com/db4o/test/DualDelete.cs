/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class DualDelete {
      
      public DualDelete() : base() {
      }
      internal Atom atom;
      
      public void configure() {
         Db4o.configure().objectClass(this).cascadeOnDelete(true);
         Db4o.configure().objectClass(this).cascadeOnUpdate(true);
      }
      
      public void store() {
         Test.deleteAllInstances(this);
         Test.deleteAllInstances(new Atom());
         Test.ensureOccurrences(new Atom(),0);
         DualDelete dd11 = new DualDelete();
         dd11.atom = new Atom("justone");
         Test.store(dd11);
         DualDelete dd21 = new DualDelete();
         dd21.atom = dd11.atom;
         Test.store(dd21);
      }
      
      public void test() {
         Test.ensureOccurrences(new Atom(), 1);
         Test.deleteAllInstances(this);
         Test.ensureOccurrences(new Atom(), 0);
         Test.rollBack();
         Test.ensureOccurrences(new Atom(), 1);
         Test.deleteAllInstances(this);
         Test.ensureOccurrences(new Atom(), 0);
         Test.commit();
         Test.ensureOccurrences(new Atom(), 0);
         Test.rollBack();
         Test.ensureOccurrences(new Atom(), 0);
      }
   }
}