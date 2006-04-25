namespace com.db4o
{
	/// <summary>QQuery is the users hook on our graph.</summary>
	/// <remarks>
	/// QQuery is the users hook on our graph.
	/// A QQuery is defined by it's constraints.
	/// NOTE: This is just a 'partial' base class to allow for variant implementations
	/// in db4oj and db4ojdk1.2. It assumes that itself is an instance of QQuery
	/// and should never be used explicitly.
	/// </remarks>
	/// <exclude></exclude>
	public abstract class QQueryBase : com.db4o.types.Unversioned
	{
		[com.db4o.Transient]
		private static readonly com.db4o.IDGenerator i_orderingGenerator = new com.db4o.IDGenerator
			();

		[com.db4o.Transient]
		internal com.db4o.Transaction i_trans;

		public com.db4o.foundation.Collection4 i_constraints = new com.db4o.foundation.Collection4
			();

		public com.db4o.QQuery i_parent;

		public string i_field;

		public com.db4o.query.QueryComparator _comparator;

		private readonly com.db4o.QQuery _this;

		protected QQueryBase()
		{
			_this = cast(this);
		}

		protected QQueryBase(com.db4o.Transaction a_trans, com.db4o.QQuery a_parent, string
			 a_field)
		{
			_this = cast(this);
			i_trans = a_trans;
			i_parent = a_parent;
			i_field = a_field;
		}

		internal virtual void addConstraint(com.db4o.QCon a_constraint)
		{
			i_constraints.add(a_constraint);
		}

		private void addConstraint(com.db4o.foundation.Collection4 col, object obj)
		{
			bool found = false;
			com.db4o.foundation.Iterator4 j = iterateConstraints();
			while (j.hasNext())
			{
				com.db4o.QCon existingConstraint = (com.db4o.QCon)j.next();
				bool[] removeExisting = { false };
				com.db4o.QCon newConstraint = existingConstraint.shareParent(obj, removeExisting);
				if (newConstraint != null)
				{
					addConstraint(newConstraint);
					col.add(newConstraint);
					if (removeExisting[0])
					{
						removeConstraint(existingConstraint);
					}
					found = true;
				}
			}
			if (!found)
			{
				com.db4o.QConObject newConstraint = new com.db4o.QConObject(i_trans, null, null, 
					obj);
				addConstraint(newConstraint);
				col.add(newConstraint);
			}
		}

		/// <summary>Search for slot that corresponds to class.</summary>
		/// <remarks>
		/// Search for slot that corresponds to class. <br />If not found add it.
		/// <br />Constrain it. <br />
		/// </remarks>
		public virtual com.db4o.query.Constraint constrain(object example)
		{
			lock (streamLock())
			{
				com.db4o.reflect.ReflectClass claxx = null;
				example = com.db4o.Platform4.getClassForType(example);
				com.db4o.reflect.Reflector reflector = i_trans.reflector();
				if (example is com.db4o.reflect.ReflectClass)
				{
					claxx = (com.db4o.reflect.ReflectClass)example;
				}
				else
				{
					if (example is j4o.lang.Class)
					{
						claxx = reflector.forClass((j4o.lang.Class)example);
					}
				}
				if (claxx != null)
				{
					if (claxx.Equals(i_trans.i_stream.i_handlers.ICLASS_OBJECT))
					{
						return null;
					}
					com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
					if (claxx.isInterface())
					{
						com.db4o.foundation.Collection4 classes = i_trans.i_stream.i_classCollection.forInterface
							(claxx);
						if (classes.size() == 0)
						{
							com.db4o.QConClass qcc = new com.db4o.QConClass(i_trans, null, null, claxx);
							addConstraint(qcc);
							return qcc;
						}
						com.db4o.foundation.Iterator4 i = classes.iterator();
						com.db4o.query.Constraint constr = null;
						while (i.hasNext())
						{
							com.db4o.YapClass yapClass = (com.db4o.YapClass)i.next();
							com.db4o.reflect.ReflectClass yapClassClaxx = yapClass.classReflector();
							if (yapClassClaxx != null)
							{
								if (!yapClassClaxx.isInterface())
								{
									if (constr == null)
									{
										constr = constrain(yapClassClaxx);
									}
									else
									{
										constr = constr.or(constrain(yapClass.classReflector()));
									}
								}
							}
						}
						return constr;
					}
					com.db4o.foundation.Iterator4 constraintsIterator = iterateConstraints();
					while (constraintsIterator.hasNext())
					{
						com.db4o.QCon existingConstraint = (com.db4o.QConObject)constraintsIterator.next(
							);
						bool[] removeExisting = { false };
						com.db4o.QCon newConstraint = existingConstraint.shareParentForClass(claxx, removeExisting
							);
						if (newConstraint != null)
						{
							addConstraint(newConstraint);
							col.add(newConstraint);
							if (removeExisting[0])
							{
								removeConstraint(existingConstraint);
							}
						}
					}
					if (col.size() == 0)
					{
						com.db4o.QConClass qcc = new com.db4o.QConClass(i_trans, null, null, claxx);
						addConstraint(qcc);
						return qcc;
					}
					if (col.size() == 1)
					{
						return (com.db4o.query.Constraint)col.iterator().next();
					}
					com.db4o.query.Constraint[] constraintArray = new com.db4o.query.Constraint[col.size
						()];
					col.toArray(constraintArray);
					return new com.db4o.QConstraints(i_trans, constraintArray);
				}
				com.db4o.QConEvaluation eval = com.db4o.Platform4.evaluationCreate(i_trans, example
					);
				if (eval != null)
				{
					com.db4o.foundation.Iterator4 i = iterateConstraints();
					while (i.hasNext())
					{
						((com.db4o.QCon)i.next()).addConstraint(eval);
					}
					return null;
				}
				com.db4o.foundation.Collection4 constraints = new com.db4o.foundation.Collection4
					();
				addConstraint(constraints, example);
				return toConstraint(constraints);
			}
		}

		public virtual com.db4o.query.Constraints constraints()
		{
			lock (streamLock())
			{
				com.db4o.query.Constraint[] constraints = new com.db4o.query.Constraint[i_constraints
					.size()];
				i_constraints.toArray(constraints);
				return new com.db4o.QConstraints(i_trans, constraints);
			}
		}

		public virtual com.db4o.query.Query descend(string a_field)
		{
			lock (streamLock())
			{
				com.db4o.QQuery query = new com.db4o.QQuery(i_trans, _this, a_field);
				int[] run = { 1 };
				if (!descend1(query, a_field, run))
				{
					if (run[0] == 1)
					{
						run[0] = 2;
						if (!descend1(query, a_field, run))
						{
							return null;
						}
					}
				}
				return query;
			}
		}

		private bool descend1(com.db4o.QQuery query, string a_field, int[] run)
		{
			bool[] foundClass = { false };
			if (run[0] == 2 || i_constraints.size() == 0)
			{
				run[0] = 0;
				bool[] anyClassCollected = { false };
				i_trans.i_stream.i_classCollection.attachQueryNode(a_field, new _AnonymousInnerClass212
					(this, anyClassCollected));
			}
			com.db4o.foundation.Iterator4 i = iterateConstraints();
			while (i.hasNext())
			{
				if (((com.db4o.QCon)i.next()).attach(query, a_field))
				{
					foundClass[0] = true;
				}
			}
			return foundClass[0];
		}

		private sealed class _AnonymousInnerClass212 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass212(QQueryBase _enclosing, bool[] anyClassCollected)
			{
				this._enclosing = _enclosing;
				this.anyClassCollected = anyClassCollected;
			}

			public void visit(object obj)
			{
				object[] pair = ((object[])obj);
				com.db4o.YapClass parentYc = (com.db4o.YapClass)pair[0];
				com.db4o.YapField yf = (com.db4o.YapField)pair[1];
				com.db4o.YapClass childYc = yf.getFieldYapClass(this._enclosing.i_trans.i_stream);
				bool take = true;
				if (childYc is com.db4o.YapClassAny)
				{
					if (anyClassCollected[0])
					{
						take = false;
					}
					else
					{
						anyClassCollected[0] = true;
					}
				}
				if (take)
				{
					com.db4o.QConClass qcc = new com.db4o.QConClass(this._enclosing.i_trans, null, yf
						.qField(this._enclosing.i_trans), parentYc.classReflector());
					this._enclosing.addConstraint(qcc);
				}
			}

			private readonly QQueryBase _enclosing;

			private readonly bool[] anyClassCollected;
		}

		public virtual com.db4o.ObjectSet execute()
		{
			return new com.db4o.inside.query.ObjectSetFacade(getQueryResult());
		}

		public virtual com.db4o.inside.query.QueryResult getQueryResult()
		{
			lock (streamLock())
			{
				com.db4o.YapStream stream = i_trans.i_stream;
				if (i_constraints.size() == 0)
				{
					com.db4o.QueryResultImpl res = stream.createQResult(i_trans);
					stream.getAll(i_trans, res);
					return res;
				}
				com.db4o.inside.query.QueryResult result = classOnlyQuery();
				if (result != null)
				{
					result.reset();
					return result;
				}
				com.db4o.QueryResultImpl qResult = new com.db4o.QueryResultImpl(i_trans);
				execute1(qResult);
				return qResult;
			}
		}

		private com.db4o.inside.query.QueryResult classOnlyQuery()
		{
			if (i_constraints.size() != 1 || _comparator != null)
			{
				return null;
			}
			com.db4o.query.Constraint constr = (com.db4o.query.Constraint)iterateConstraints(
				).next();
			if (j4o.lang.Class.getClassForObject(constr) != j4o.lang.Class.getClassForType(typeof(
				com.db4o.QConClass)))
			{
				return null;
			}
			com.db4o.QConClass clazzconstr = (com.db4o.QConClass)constr;
			com.db4o.YapClass clazz = clazzconstr.i_yapClass;
			if (clazz == null)
			{
				return null;
			}
			if (clazzconstr.hasChildren() || clazz.isArray())
			{
				return null;
			}
			com.db4o.ClassIndex classIndex = clazz.getIndex();
			if (classIndex == null)
			{
				return null;
			}
			if (i_trans.i_stream.isClient())
			{
				long[] ids = classIndex.getInternalIDs(i_trans, clazz.getID());
				com.db4o.QResultClient resClient = new com.db4o.QResultClient(i_trans, ids.Length
					);
				for (int i = 0; i < ids.Length; i++)
				{
					resClient.add((int)ids[i]);
				}
				sort(resClient);
				return resClient;
			}
			com.db4o.Tree tree = classIndex.cloneForYapClass(i_trans, clazz.getID());
			if (tree == null)
			{
				return new com.db4o.QueryResultImpl(i_trans);
			}
			com.db4o.QueryResultImpl resLocal = new com.db4o.QueryResultImpl(i_trans, tree.size
				());
			tree.traverse(new _AnonymousInnerClass323(this, resLocal));
			sort(resLocal);
			return resLocal;
		}

		private sealed class _AnonymousInnerClass323 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass323(QQueryBase _enclosing, com.db4o.QueryResultImpl resLocal
				)
			{
				this._enclosing = _enclosing;
				this.resLocal = resLocal;
			}

			public void visit(object a_object)
			{
				resLocal.add(((com.db4o.TreeInt)a_object)._key);
			}

			private readonly QQueryBase _enclosing;

			private readonly com.db4o.QueryResultImpl resLocal;
		}

		internal virtual void execute1(com.db4o.QueryResultImpl result)
		{
			if (i_trans.i_stream.isClient())
			{
				marshall();
				((com.db4o.YapClient)i_trans.i_stream).queryExecute(_this, result);
			}
			else
			{
				executeLocal(result);
			}
		}

		internal virtual void executeLocal(com.db4o.QueryResultImpl result)
		{
			bool checkDuplicates = false;
			bool topLevel = true;
			com.db4o.foundation.List4 candidateCollection = null;
			com.db4o.foundation.Iterator4 i = iterateConstraints();
			while (i.hasNext())
			{
				com.db4o.QCon qcon = (com.db4o.QCon)i.next();
				com.db4o.QCon old = qcon;
				bool found = false;
				qcon = qcon.getRoot();
				if (qcon != old)
				{
					checkDuplicates = true;
					topLevel = false;
				}
				com.db4o.YapClass yc = qcon.getYapClass();
				if (yc != null)
				{
					if (candidateCollection != null)
					{
						com.db4o.foundation.Iterator4 j = new com.db4o.foundation.Iterator4Impl(candidateCollection
							);
						while (j.hasNext())
						{
							com.db4o.QCandidates candidates = (com.db4o.QCandidates)j.next();
							if (candidates.tryAddConstraint(qcon))
							{
								found = true;
								break;
							}
						}
					}
					if (!found)
					{
						com.db4o.QCandidates candidates = new com.db4o.QCandidates(i_trans, qcon.getYapClass
							(), null);
						candidates.addConstraint(qcon);
						candidateCollection = new com.db4o.foundation.List4(candidateCollection, candidates
							);
					}
				}
			}
			if (candidateCollection != null)
			{
				i = new com.db4o.foundation.Iterator4Impl(candidateCollection);
				while (i.hasNext())
				{
					((com.db4o.QCandidates)i.next()).execute();
				}
				if (candidateCollection._next != null)
				{
					checkDuplicates = true;
				}
				if (checkDuplicates)
				{
					result.checkDuplicates();
				}
				i = new com.db4o.foundation.Iterator4Impl(candidateCollection);
				while (i.hasNext())
				{
					com.db4o.QCandidates candidates = (com.db4o.QCandidates)i.next();
					if (topLevel)
					{
						candidates.traverse(result);
					}
					else
					{
						com.db4o.QQueryBase q = (com.db4o.QQueryBase)this;
						com.db4o.foundation.Collection4 fieldPath = new com.db4o.foundation.Collection4();
						while (q.i_parent != null)
						{
							fieldPath.add(q.i_field);
							q = q.i_parent;
						}
						candidates.traverse(new _AnonymousInnerClass411(this, fieldPath, result));
					}
				}
			}
			sort(result);
			result.reset();
		}

		private sealed class _AnonymousInnerClass411 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass411(QQueryBase _enclosing, com.db4o.foundation.Collection4
				 fieldPath, com.db4o.QueryResultImpl result)
			{
				this._enclosing = _enclosing;
				this.fieldPath = fieldPath;
				this.result = result;
			}

			public void visit(object a_object)
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_object;
				if (candidate.include())
				{
					com.db4o.TreeInt ids = new com.db4o.TreeInt(candidate._key);
					com.db4o.TreeInt[] idsNew = new com.db4o.TreeInt[1];
					com.db4o.foundation.Iterator4 itPath = fieldPath.iterator();
					while (itPath.hasNext())
					{
						idsNew[0] = null;
						string fieldName = (string)(itPath.next());
						if (ids != null)
						{
							ids.traverse(new _AnonymousInnerClass422(this, idsNew, fieldName));
						}
						ids = idsNew[0];
					}
					if (ids != null)
					{
						ids.traverse(new _AnonymousInnerClass446(this, result));
					}
				}
			}

			private sealed class _AnonymousInnerClass422 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass422(_AnonymousInnerClass411 _enclosing, com.db4o.TreeInt[]
					 idsNew, string fieldName)
				{
					this._enclosing = _enclosing;
					this.idsNew = idsNew;
					this.fieldName = fieldName;
				}

				public void visit(object treeInt)
				{
					int id = ((com.db4o.TreeInt)treeInt)._key;
					com.db4o.YapWriter reader = this._enclosing._enclosing.i_trans.i_stream.readWriterByID
						(this._enclosing._enclosing.i_trans, id);
					if (reader != null)
					{
						com.db4o.YapClass yc = this._enclosing._enclosing.i_trans.i_stream.getYapClass(reader
							.readInt());
						idsNew[0] = yc.collectFieldIDs(idsNew[0], reader, fieldName);
					}
				}

				private readonly _AnonymousInnerClass411 _enclosing;

				private readonly com.db4o.TreeInt[] idsNew;

				private readonly string fieldName;
			}

			private sealed class _AnonymousInnerClass446 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass446(_AnonymousInnerClass411 _enclosing, com.db4o.QueryResultImpl
					 result)
				{
					this._enclosing = _enclosing;
					this.result = result;
				}

				public void visit(object treeInt)
				{
					result.addKeyCheckDuplicates(((com.db4o.TreeInt)treeInt)._key);
				}

				private readonly _AnonymousInnerClass411 _enclosing;

				private readonly com.db4o.QueryResultImpl result;
			}

			private readonly QQueryBase _enclosing;

			private readonly com.db4o.foundation.Collection4 fieldPath;

			private readonly com.db4o.QueryResultImpl result;
		}

		internal virtual com.db4o.Transaction getTransaction()
		{
			return i_trans;
		}

		internal virtual com.db4o.foundation.Iterator4 iterateConstraints()
		{
			return i_constraints.iterator();
		}

		public virtual com.db4o.query.Query orderAscending()
		{
			lock (streamLock())
			{
				setOrdering(i_orderingGenerator.next());
				return _this;
			}
		}

		public virtual com.db4o.query.Query orderDescending()
		{
			lock (streamLock())
			{
				setOrdering(-i_orderingGenerator.next());
				return _this;
			}
		}

		private void setOrdering(int ordering)
		{
			com.db4o.foundation.Iterator4 i = iterateConstraints();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).setOrdering(ordering);
			}
		}

		internal virtual void marshall()
		{
			com.db4o.foundation.Iterator4 i = iterateConstraints();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).getRoot().marshall();
			}
		}

		internal virtual void removeConstraint(com.db4o.QCon a_constraint)
		{
			i_constraints.remove(a_constraint);
		}

		internal virtual void unmarshall(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
			com.db4o.foundation.Iterator4 i = iterateConstraints();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).unmarshall(a_trans);
			}
		}

		internal virtual com.db4o.query.Constraint toConstraint(com.db4o.foundation.Collection4
			 constraints)
		{
			com.db4o.foundation.Iterator4 i = constraints.iterator();
			if (constraints.size() == 1)
			{
				return (com.db4o.query.Constraint)i.next();
			}
			else
			{
				if (constraints.size() > 0)
				{
					com.db4o.query.Constraint[] constraintArray = new com.db4o.query.Constraint[constraints
						.size()];
					constraints.toArray(constraintArray);
					return new com.db4o.QConstraints(i_trans, constraintArray);
				}
			}
			return null;
		}

		protected virtual object streamLock()
		{
			return i_trans.i_stream.i_lock;
		}

		public virtual com.db4o.query.Query sortBy(com.db4o.query.QueryComparator comparator
			)
		{
			_comparator = comparator;
			return _this;
		}

		private void sort(com.db4o.QueryResultImpl result)
		{
			if (_comparator != null)
			{
				result.sort(_comparator);
			}
		}

		private static com.db4o.QQuery cast(com.db4o.QQueryBase obj)
		{
			return (com.db4o.QQuery)obj;
		}
	}
}
