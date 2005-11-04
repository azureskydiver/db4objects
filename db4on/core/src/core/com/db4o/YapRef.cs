/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;

namespace com.db4o 
{
	internal class YapRef : WeakReference
	{
		public object yapObject;

		internal YapRef(Object queue, Object yapObject, Object obj) : base(obj, false){
			this.yapObject = yapObject;
			((YapReferenceQueue) queue).add(this);
		}

		public object get(){
			return this.Target;
		}
	}
}