//
// ModuleDefinitionCollection.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen.rb do not edit
// Thu Sep 29 22:11:55 CEST 2005
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

	using Mono.Cecil.Cil;

	public sealed class ModuleDefinitionCollection : IModuleDefinitionCollection {

		IList m_items;
		AssemblyDefinition m_container;

		public event ModuleDefinitionEventHandler OnModuleDefinitionAdded;
		public event ModuleDefinitionEventHandler OnModuleDefinitionRemoved;

		public ModuleDefinition this [int index] {
			get { return m_items [index] as ModuleDefinition; }
			set { m_items [index] = value; }
		}

		public AssemblyDefinition Container {
			get { return m_container; }
		}

		public int Count {
			get { return m_items.Count; }
		}

		public bool IsSynchronized {
			get { return false; }
		}

		public object SyncRoot {
			get { return this; }
		}

		public ModuleDefinitionCollection (AssemblyDefinition container)
		{
			m_container = container;
			m_items = new ArrayList ();
		}

		public void Add (ModuleDefinition value)
		{
			if (OnModuleDefinitionAdded != null && !this.Contains (value))
				OnModuleDefinitionAdded (this, new ModuleDefinitionEventArgs (value));
			m_items.Add (value);
		}

		public void Clear ()
		{
			if (OnModuleDefinitionRemoved != null)
				foreach (ModuleDefinition item in this)
					OnModuleDefinitionRemoved (this, new ModuleDefinitionEventArgs (item));
			m_items.Clear ();
		}

		public bool Contains (ModuleDefinition value)
		{
			return m_items.Contains (value);
		}

		public int IndexOf (ModuleDefinition value)
		{
			return m_items.IndexOf (value);
		}

		public void Insert (int index, ModuleDefinition value)
		{
			if (OnModuleDefinitionAdded != null && !this.Contains (value))
				OnModuleDefinitionAdded (this, new ModuleDefinitionEventArgs (value));
			m_items.Insert (index, value);
		}

		public void Remove (ModuleDefinition value)
		{
			if (OnModuleDefinitionRemoved != null && this.Contains (value))
				OnModuleDefinitionRemoved (this, new ModuleDefinitionEventArgs (value));
			m_items.Remove (value);
		}

		public void RemoveAt (int index)
		{
			if (OnModuleDefinitionRemoved != null)
				OnModuleDefinitionRemoved (this, new ModuleDefinitionEventArgs (this [index]));
			m_items.Remove (index);
		}

		public void CopyTo (Array ary, int index)
		{
			m_items.CopyTo (ary, index);
		}

		public IEnumerator GetEnumerator ()
		{
			return m_items.GetEnumerator ();
		}

		public void Accept (IReflectionStructureVisitor visitor)
		{
			visitor.VisitModuleDefinitionCollection (this);
		}
	}
}
