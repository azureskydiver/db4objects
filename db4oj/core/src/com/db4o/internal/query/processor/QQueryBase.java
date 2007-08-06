/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.*;
import com.db4o.internal.query.result.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.types.*;


/**
 * QQuery is the users hook on our graph.
 * 
 * A QQuery is defined by it's constraints.
 * 
 * NOTE: This is just a 'partial' base class to allow for variant implementations
 * in db4oj and db4ojdk1.2. It assumes that itself is an instance of QQuery
 * and should never be used explicitly.
 * 
 * @exclude
 */
public abstract class QQueryBase implements Unversioned {

    private static final transient IDGenerator i_orderingGenerator = new IDGenerator();

    transient Transaction _trans;
    
    public Collection4 i_constraints = new Collection4();

    public QQuery i_parent;
    
    public String i_field;
    
    private transient QueryEvaluationMode _evaluationMode;
    
    public int _evaluationModeAsInt;
    
    public QueryComparator _comparator;
    
    private transient final QQuery _this;
    
    protected QQueryBase() {
        // C/S only
    	_this = cast(this);
    }

    protected QQueryBase(Transaction a_trans, QQuery a_parent, String a_field) {
    	_this = cast(this);
        _trans = a_trans;
        i_parent = a_parent;
        i_field = a_field;
    }

    void addConstraint(QCon a_constraint) {
        i_constraints.add(a_constraint);
    }

    private void addConstraint(Collection4 col, Object obj) {
        if(attachToExistingConstraints(col, obj, true)){
            return;
        }
        if(attachToExistingConstraints(col, obj, false)){
            return;
        }
        QConObject newConstraint = new QConObject(_trans, null, null, obj);
        addConstraint(newConstraint);
        col.add(newConstraint);
    }

    private boolean attachToExistingConstraints(Collection4 col, Object obj, boolean onlyForPaths) {
        boolean found = false;
        Iterator4 j = iterateConstraints();
        while (j.moveNext()) {
            QCon existingConstraint = (QCon)j.current();
            boolean[] removeExisting = { false };
            if(! onlyForPaths || (existingConstraint instanceof QConPath) ){
                QCon newConstraint = existingConstraint.shareParent(obj, removeExisting);
                if (newConstraint != null) {
                    addConstraint(newConstraint);
                    col.add(newConstraint);
                    if (removeExisting[0]) {
                        removeConstraint(existingConstraint);
                    }
                    found = true;
                    if(! onlyForPaths){
                        return true;
                    }
                }
            }
        }
        return found;
    }

    /**
	 * Search for slot that corresponds to class. <br>If not found add it.
	 * <br>Constrain it. <br>
	 */
    public Constraint constrain(Object example) {
        synchronized (streamLock()) {
            example = Platform4.getClassForType(example);
            
            ReflectClass claxx = reflectClassForClass(example);             
            if (claxx != null) {                
                return addClassConstraint(claxx);
            }
            
            QConEvaluation eval = Platform4.evaluationCreate(_trans, example);
			if (eval != null) {
                return addEvaluationToAllConstraints(eval);
            }
			
            Collection4 constraints = new Collection4();
            addConstraint(constraints, example);
            return toConstraint(constraints);
        }
    }

	private Constraint addEvaluationToAllConstraints(QConEvaluation eval) {

	    if(i_constraints.size() == 0){
	        _trans.container().classCollection().iterateTopLevelClasses(new Visitor4() {
                public void visit(Object obj) {
                    ClassMetadata classMetadata = (ClassMetadata) obj;
                    QConClass qcc = new QConClass(_trans,classMetadata.classReflector());
                    addConstraint(qcc);
                }
            });
	    }
	    
		Iterator4 i = iterateConstraints();
		while (i.moveNext()) {
		    ((QCon)i.current()).addConstraint(eval);
		}
		
		// FIXME: should return valid Constraint object
		return null;
	}

	private Constraint addClassConstraint(ReflectClass claxx) {
		if(claxx.equals(stream()._handlers.ICLASS_OBJECT)){
			return null;
		}
		
		Collection4 col = new Collection4();
		if (claxx.isInterface()) {
		    return addInterfaceConstraint(claxx);
		}

		Iterator4 constraintsIterator = iterateConstraints();
		while (constraintsIterator.moveNext()) {
		    QCon existingConstraint = (QConObject)constraintsIterator.current();
		    boolean[] removeExisting = { false };
		    QCon newConstraint =
		        existingConstraint.shareParentForClass(claxx, removeExisting);
		    if (newConstraint != null) {
		        addConstraint(newConstraint);
		        col.add(newConstraint);
		        if (removeExisting[0]) {
		            removeConstraint(existingConstraint);
		        }
		    }
		}
		if (col.size() == 0) {
		    QConClass qcc = new QConClass(_trans,claxx);
		    addConstraint(qcc);
		    return qcc;
		}

		return toConstraint(col);
	}

	private Constraint addInterfaceConstraint(ReflectClass claxx) {
		Collection4 classes = stream().classCollection().forInterface(claxx);
		if (classes.size() == 0) {
		    QConClass qcc = new QConClass(_trans, null, null, claxx);
		    addConstraint(qcc);
		    return qcc;
		}
		Iterator4 i = classes.iterator();
		Constraint constr = null;
		while (i.moveNext()) {
		    ClassMetadata yapClass = (ClassMetadata)i.current();
		    ReflectClass yapClassClaxx = yapClass.classReflector();
		    if(yapClassClaxx != null){
		        if(! yapClassClaxx.isInterface()){
		            if(constr == null){
		                constr = constrain(yapClassClaxx);
		            }else{
		                constr = constr.or(constrain(yapClass.classReflector()));
		            }
		        }
		    }
		    
		}
		return constr;
	}

	private ReflectClass reflectClassForClass(Object example) {		
		if(example instanceof ReflectClass){
			return (ReflectClass)example;
		}
		if(example instanceof Class) {
			return _trans.reflector().forClass((Class)example);
		}
		return null;
	}

    public Constraints constraints() {
        synchronized (streamLock()) {
            Constraint[] constraints = new Constraint[i_constraints.size()];
            i_constraints.toArray(constraints);
            return new QConstraints(_trans, constraints);
        }
    }

    public Query descend(final String a_field) {
        synchronized (streamLock()) {
            final QQuery query = new QQuery(_trans, _this, a_field);
            int[] run = { 1 };
            if (!descend1(query, a_field, run)) {

                // try to add unparented nodes on the second run,
                // if not added in the first run and a descendant
                // was not found

                if (run[0] == 1) {
                    run[0] = 2;
                    if (!descend1(query, a_field, run)) {
                        new QConUnconditional(_trans, false).attach(query, a_field);
                    }
                }
            }
            return query;
        }
    }

    private boolean descend1(final QQuery query, final String a_field, int[] run) {
        final boolean[] foundClass = { false };
        if (run[0] == 2 || i_constraints.size() == 0) {

            // On the second run we are really creating a second independant
            // query network that is not joined to other higher level
			// constraints.
            // Let's see how this works out. We may need to join networks.

            run[0] = 0; // prevent a double run of this code

            final boolean[] anyClassCollected = { false };

            stream().classCollection().attachQueryNode(a_field, new Visitor4() {

                public void visit(Object obj) {

                    Object[] pair = ((Object[]) obj);
                    ClassMetadata parentYc = (ClassMetadata)pair[0];
                    FieldMetadata yf = (FieldMetadata)pair[1];
                    ClassMetadata childYc = yf.getFieldYapClass(stream());

                    boolean take = true;

                    if (childYc instanceof UntypedFieldHandler) {
                        if (anyClassCollected[0]) {
                            take = false;
                        } else {
                            anyClassCollected[0] = true;
                        }
                    }

                    if (take) {

                        QConClass qcc =
                            new QConClass(
                                _trans,
                                null,
                                yf.qField(_trans),
                                parentYc.classReflector());
                        addConstraint(qcc);
                    }

                }

            });

        }
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            if (((QCon)i.current()).attach(query, a_field)) {
                foundClass[0] = true;
            }
        }
        return foundClass[0];
    }

    public ObjectSet execute() {
		Callbacks callbacks = stream().callbacks();
		callbacks.queryOnStarted(_trans, cast(this));
	    QueryResult qresult = getQueryResult();
	    callbacks.queryOnFinished(_trans, cast(this));
		return new ObjectSetFacade(qresult);
	}

	public QueryResult getQueryResult() {
		synchronized (streamLock()) {
	        if(i_constraints.size() == 0){
	            return stream().getAll(_trans);
	        }
			QueryResult result = classOnlyQuery();
			if(result != null) {
				return result;
			}
	        return stream().executeQuery(_this);
	    }
	}

	protected ObjectContainerBase stream() {
		return _trans.container();
	}

	private QueryResult classOnlyQuery() {
        
		if(i_constraints.size()!=1||_comparator!=null) {
			return null;
		}
		Constraint constr=singleConstraint(); 
		if(constr.getClass()!=QConClass.class) {
			return null;
		}
		QConClass clazzconstr=(QConClass)constr;
		ClassMetadata clazz=clazzconstr.i_yapClass;
		if(clazz==null) {
			return null;
		}
		if(clazzconstr.hasChildren() || clazz.isArray()) {
			return null;
		}
		
		QueryResult queryResult = stream().classOnlyQuery(_trans, clazz);
		if(queryResult == null){
			return null;
		}
		sort(queryResult);
		
		return queryResult;
        
	}

	private Constraint singleConstraint() {
		return (Constraint)i_constraints.singleElement();
	}

    public static class CreateCandidateCollectionResult {
    	public final boolean checkDuplicates;
        public final boolean topLevel;
        public final List4 candidateCollection;
        
    	public CreateCandidateCollectionResult(List4 candidateCollection_, boolean checkDuplicates_, boolean topLevel_) {
    		candidateCollection = candidateCollection_;
    		topLevel = topLevel_;
    		checkDuplicates = checkDuplicates_;
		}
    }
    
    public Iterator4 executeSnapshot(){
    	
		final CreateCandidateCollectionResult r = createCandidateCollection();
		
        final Collection4 executionPath = executionPath(r);
        
        Iterator4 candidatesIterator = new Iterator4Impl(r.candidateCollection);
        
        Collection4 snapshots = new Collection4();
        while(candidatesIterator.moveNext()){
        	QCandidates candidates = (QCandidates) candidatesIterator.current(); 
        	snapshots.add( candidates.executeSnapshot(executionPath));
        }
        
        Iterator4 snapshotsIterator = snapshots.iterator();
        final CompositeIterator4 resultingIDs = new CompositeIterator4(snapshotsIterator);
        
        if(!r.checkDuplicates){
        	return resultingIDs;
        }
        
		return checkDuplicates(resultingIDs);
    }
    
    public Iterator4 executeLazy(){
    	
		final CreateCandidateCollectionResult r = createCandidateCollection();
		
        final Collection4 executionPath = executionPath(r);
        
        Iterator4 candidateCollection = new Iterator4Impl(r.candidateCollection);
        
        MappingIterator executeCandidates = new MappingIterator(candidateCollection){
			protected Object map(Object current) {
				return ((QCandidates)current).executeLazy(executionPath);
			}
        };
        
        CompositeIterator4 resultingIDs = new CompositeIterator4(executeCandidates);
        
        if(!r.checkDuplicates){
        	return resultingIDs;
        }
        
		return checkDuplicates(resultingIDs);
    }

	private MappingIterator checkDuplicates(CompositeIterator4 executeAllCandidates) {
		return new MappingIterator(executeAllCandidates) {
        	private TreeInt ids = new TreeInt(0);
			protected Object map(Object current) {
				int id = ((Integer)current).intValue();
				if(ids.find(id) != null){
					return MappingIterator.SKIP;
				}
				ids = (TreeInt)ids.add(new TreeInt(id));
				return current;
			}

		};
	}

	private Collection4 executionPath(final CreateCandidateCollectionResult r) {
		return r.topLevel ? null : fieldPathFromTop();
	}

    public void executeLocal(final IdListQueryResult result) {
        
		CreateCandidateCollectionResult r = createCandidateCollection();
        
        boolean checkDuplicates = r.checkDuplicates;
        boolean topLevel = r.topLevel;
        List4 candidateCollection = r.candidateCollection;
        
        if (Debug.queries) {
        	logConstraints();
        }
        
        if (candidateCollection != null) {
        	
        	final Collection4 executionPath = topLevel ? null : fieldPathFromTop();

        	Iterator4 i = new Iterator4Impl(candidateCollection);
            while (i.moveNext()) {
                ((QCandidates)i.current()).execute();
            }

            if (candidateCollection._next != null) {
                checkDuplicates = true;
            }

            if (checkDuplicates) {
                result.checkDuplicates();
            }

            final ObjectContainerBase stream = stream();
            i = new Iterator4Impl(candidateCollection);
            while (i.moveNext()) {
                QCandidates candidates = (QCandidates)i.current();
                if (topLevel) {
                    candidates.traverse(result);
                } else {
                    candidates.traverse(new Visitor4() {
                        public void visit(Object a_object) {
                            QCandidate candidate = (QCandidate)a_object;
                            if (candidate.include()) {
                                TreeInt ids = new TreeInt(candidate._key);
                                final TreeInt[] idsNew = new TreeInt[1];
                                Iterator4 itPath = executionPath.iterator();
                                while (itPath.moveNext()) {
                                    idsNew[0] = null;
                                    final String fieldName = (String) (itPath.current());
                                    if (ids != null) {
                                        ids.traverse(new Visitor4() {
                                            public void visit(Object treeInt) {
                                                int id = ((TreeInt)treeInt)._key;
                                                StatefulBuffer reader =
                                                    stream.readWriterByID(_trans, id);
                                                if (reader != null) {
                                                    ObjectHeader oh = new ObjectHeader(stream, reader);
                                                    idsNew[0] = oh.classMetadata().collectFieldIDs(
                                                            oh._marshallerFamily,
                                                            oh._headerAttributes,
                                                            idsNew[0],
                                                            reader,
                                                            fieldName);
                                                }
                                            }
                                        });
                                    }
                                    ids = idsNew[0];
                                }
                                if(ids != null){
                                    ids.traverse(new Visitor4() {
	                                    public void visit(Object treeInt) {
	                                        result.addKeyCheckDuplicates(((TreeInt)treeInt)._key);
	                                    }
	                                });
                                }
                            }
                        }
                    });
                }
            }
        }
        sort(result);
//        result.reset();
    }
    
    private Collection4 fieldPathFromTop(){
        QQueryBase q = this;
        final Collection4 fieldPath = new Collection4();
        while (q.i_parent != null) {
            fieldPath.prepend(q.i_field);
            q = q.i_parent;
        }
        return fieldPath;
    }

	private void logConstraints() {
		if (Debug.queries) {
			Iterator4 i = iterateConstraints();
			while (i.moveNext()) {
			    ((QCon)i.current()).log("");
			}
		}
	}

	public CreateCandidateCollectionResult createCandidateCollection() {
		boolean checkDuplicates = false;
        boolean topLevel = true;
        List4 candidateCollection = null;
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            QCon qcon = (QCon)i.current();
            QCon old = qcon;            
            qcon = qcon.getRoot();
            if (qcon != old) {
                checkDuplicates = true;
                topLevel = false;
            }
            ClassMetadata yc = qcon.getYapClass();
            if (yc == null) {
            	break;
            }
            candidateCollection = addConstraintToCandidateCollection(candidateCollection, qcon);
        }
		return new CreateCandidateCollectionResult(candidateCollection, checkDuplicates, topLevel);
	}

	private List4 addConstraintToCandidateCollection(List4 candidateCollection, QCon qcon) {
		
		if (candidateCollection != null) {
		    if (tryToAddToExistingCandidate(candidateCollection, qcon)) {
		    	return candidateCollection;
		    }
		}
		
		QCandidates candidates = new QCandidates((LocalTransaction) _trans, qcon.getYapClass(), null);
		candidates.addConstraint(qcon);
		return new List4(candidateCollection, candidates);
	}

	private boolean tryToAddToExistingCandidate(List4 candidateCollection, QCon qcon) {
		Iterator4 j = new Iterator4Impl(candidateCollection);
		while (j.moveNext()) {
		    QCandidates candidates = (QCandidates)j.current();
		    if (candidates.tryAddConstraint(qcon)) {
		        return true;
		    }
		}
		return false;
	}

    public final Transaction getTransaction() {
        return _trans;
    }
    
    Iterator4 iterateConstraints(){
    	// clone the collection first to avoid
    	// InvalidIteratorException as i_constraints might be 
    	// modified during the execution of callee
        return new Collection4(i_constraints).iterator();
    }

    public Query orderAscending() {
        synchronized (streamLock()) {
            setOrdering(i_orderingGenerator.next());
            return _this;
        }
    }

    public Query orderDescending() {
        synchronized (streamLock()) {
            setOrdering(-i_orderingGenerator.next());
            return _this;
        }
    }

    private void setOrdering(final int ordering) {
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            ((QCon)i.current()).setOrdering(ordering);
        }
    }
    
    public void marshall() {
    	_evaluationModeAsInt = _evaluationMode.asInt();
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            ((QCon)i.current()).getRoot().marshall();
        }
    }

    public void unmarshall(final Transaction a_trans) {
    	_evaluationMode = QueryEvaluationMode.fromInt(_evaluationModeAsInt);
        _trans = a_trans;
        Iterator4 i = iterateConstraints();
        while (i.moveNext()) {
            ((QCon)i.current()).unmarshall(a_trans);
        }
    }

    void removeConstraint(QCon a_constraint) {
        i_constraints.remove(a_constraint);
    }

    Constraint toConstraint(final Collection4 constraints) {       
        if (constraints.size() == 1) {
        	return (Constraint) constraints.singleElement();
        } else if (constraints.size() > 0) {
            Constraint[] constraintArray = new Constraint[constraints.size()];
            constraints.toArray(constraintArray);
            return new QConstraints(_trans, constraintArray);
        }
        return null;
    }
    
	protected Object streamLock() {
        return stream()._lock;
    }

	public Query sortBy(QueryComparator comparator) {
		_comparator=comparator;
		return _this;
	}
	
	private void sort(QueryResult result) {
        if(_comparator!=null) {
        	result.sort(_comparator);
        }
	}
	
    // cheat emulating '(QQuery)this'
	private static QQuery cast(QQueryBase obj) {
		return (QQuery)obj;
	}
	
	public boolean requiresSort() {
		if (_comparator != null){
			return true;
		}
		Iterator4 i = iterateConstraints();
		while(i.moveNext()){
			QCon qCon = (QCon) i.current();
			if(qCon.requiresSort()){
				return true;
			}
		}
		return false;
	}
	
	public QueryComparator comparator() {
		return _comparator;
	}
	
	public QueryEvaluationMode evaluationMode(){
		return _evaluationMode;
	}
	
	public void evaluationMode(QueryEvaluationMode mode){
		_evaluationMode = mode;
	}

}
