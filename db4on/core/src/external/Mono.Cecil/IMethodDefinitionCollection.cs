//
// IMethodDefinitionCollection.cs
//
// Author:
//   Jb Evain (jbevain@gmail.com)
//
// Generated by /CodeGen/cecil-gen.rb do not edit
// Tue Nov 22 20:25:25 CET 2005
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

	public class MethodDefinitionEventArgs : EventArgs {

		private MethodDefinition m_item;

		public MethodDefinition MethodDefinition {
			get { return m_item; }
		}

		public MethodDefinitionEventArgs (MethodDefinition item)
		{
			m_item = item;
		}
	}

	public delegate void MethodDefinitionEventHandler (
		object sender, MethodDefinitionEventArgs ea);

	public interface IMethodDefinitionCollection : ICollection, IReflectionVisitable {

		MethodDefinition this [int index] { get; }

		TypeDefinition Container { get; }

		event MethodDefinitionEventHandler OnMethodDefinitionAdded;
		event MethodDefinitionEventHandler OnMethodDefinitionRemoved;

		void Add (MethodDefinition value);
		void Clear ();
		bool Contains (MethodDefinition value);
		int IndexOf (MethodDefinition value);
		void Insert (int index, MethodDefinition value);
		void Remove (MethodDefinition value);
		void RemoveAt (int index);

		MethodDefinition [] GetMethod (string name);
		MethodDefinition GetMethod (string name, Type [] parameters);
		MethodDefinition GetMethod (string name, ITypeReference [] parameters);
		MethodDefinition GetMethod (string name, ParameterDefinitionCollection parameters);
	}
}
