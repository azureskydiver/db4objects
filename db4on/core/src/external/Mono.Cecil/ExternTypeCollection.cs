//
// ExternTypeCollection.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen.rb do not edit
// Wed Oct 05 20:38:26 CEST 2005
//
// (C) 2005 Jb Evain
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//

namespace Mono.Cecil {

	using System;
	using System.Collections;
	using System.Collections.Specialized;

	using Mono.Cecil.Cil;

	using Hcp = Mono.Cecil.HashCodeProvider;
	using Cmp = System.Collections.Comparer;

	public sealed class ExternTypeCollection : NameObjectCollectionBase, IExternTypeCollection {

		ModuleDefinition m_container;

		public event ExternTypeEventHandler OnExternTypeAdded;
		public event ExternTypeEventHandler OnExternTypeRemoved;
	
		public TypeReference this [int index] {
			get { return this.BaseGet (index) as TypeReference; }
			set { this.BaseSet (index, value); }
		}

		public TypeReference this [string fullName] {
			get { return this.BaseGet (fullName) as TypeReference; }
			set { this.BaseSet (fullName, value); }
		}

		public ModuleDefinition Container {
			get { return m_container; }
		}

		public bool IsSynchronized {
			get { return (this as ICollection).IsSynchronized; }
		}

		public object SyncRoot {
			get { return (this as ICollection).SyncRoot; }
		}

		public ExternTypeCollection (ModuleDefinition container) :
			base (Hcp.Instance, Cmp.Default)
		{
			m_container = container;
		}

		public void Add (TypeReference value)
		{
			if (value == null)
				throw new ArgumentNullException ("value");

			if (OnExternTypeAdded != null && !this.Contains (value))
				OnExternTypeAdded (this, new ExternTypeEventArgs (value));

			this.BaseSet (value.FullName, value);
		}

		public void Clear ()
		{
			if (OnExternTypeRemoved != null)
				foreach (TypeReference item in this)
					OnExternTypeRemoved (this, new ExternTypeEventArgs (item));
			this.BaseClear ();
		}

		public bool Contains (TypeReference value)
		{
			return Contains (value.FullName);
		}

		public bool Contains (string fullName)
		{
			return this.BaseGet (fullName) != null;
		}

		public int IndexOf (TypeReference value)
		{
			return Array.IndexOf (this.BaseGetAllKeys (), value.FullName);
		}

		public void Remove (TypeReference value)
		{
			if (OnExternTypeRemoved != null && this.Contains (value))
				OnExternTypeRemoved (this, new ExternTypeEventArgs (value));
			this.BaseRemove (value.FullName);
		}

		public void RemoveAt (int index)
		{
			if (OnExternTypeRemoved != null)
				OnExternTypeRemoved (this, new ExternTypeEventArgs (this [index]));
			this.BaseRemoveAt (index);
		}

		public void CopyTo (Array ary, int index)
		{
			(this as ICollection).CopyTo (ary, index);
		}

		public new IEnumerator GetEnumerator ()
		{
			return this.BaseGetAllValues ().GetEnumerator ();
		}

		public void Accept (IReflectionVisitor visitor)
		{
			visitor.VisitExternTypeCollection (this);
		}
	}
}
