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
namespace com.db4o.test.types {

   public class MasterMonster: RTest {
      
      public MasterMonster() : base() {
      }
      public Object[] ooo;
      
      public override void set(int ver) {
         Object[] classes = allClassesButThis();
         ooo = new Object[classes.Length];
         for (int i = 0; i < classes.Length; i++) {
            try {
               {
                  RTestable test = (RTestable)classes[i];
               }
            }  catch (Exception e) {
               {
                  throw new RuntimeException("MasterMonster instantiation failed.");
               }
            }
         }
      }
      
      internal Object[] allClassesButThis() {
         Object[] all = Regression.allClasses();
         Object[] classes = new Object[all.Length - 1];
         j4o.lang.JavaSystem.arraycopy(all, 0, classes, 0, all.Length - 1);
         return classes;
      }
   }
}