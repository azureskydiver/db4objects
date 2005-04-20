/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.VirtualField;
import com.db4o.query.Query;
import com.db4o.replication.ReplicationConflictHandler;
import com.db4o.replication.ReplicationProcess;

/**
 * 
 */
class ReplicationImpl implements ReplicationProcess {

    final YapStream _peerA;

    final Transaction _transA;

    final YapStream _peerB;

    final Transaction _transB;

    final ReplicationConflictHandler _conflictHandler;

    final ReplicationRecord _record;

    private YapObject _sourceReference;

    private int _direction;

    private static final int IGNORE = 0;

    private static final int TO_B = -1;

    private static final int TO_A = 1;

    private static final int CHECK_CONFLICT = -99;

	ReplicationImpl(YapStream peerA, ObjectContainer peerB,
			ReplicationConflictHandler conflictHandler) {
        
        if(conflictHandler == null){
            // We don't allow starting replication without a 
            // conflict handler, so we don't get late failures.
            throw new NullPointerException();
        }
        
		_peerA = peerA;
		_transA = peerA.checkTransaction(null);

		_peerB = (YapStream) peerB;
		_transB = _peerB.checkTransaction(null);

		_peerA.i_handlers.i_replication = this;
		_peerA.i_migrateFrom = _peerB;

		_peerB.i_handlers.i_replication = this;
		_peerB.i_migrateFrom = _peerA;

		_conflictHandler = conflictHandler;

		_record = ReplicationRecord.beginReplication(_transA, _transB);
	}

	public void checkConflict(Object obj) {
		int temp = _direction;
		_direction = CHECK_CONFLICT;
		replicate(obj);
		_direction = temp;
	}

	public void commit() {
        synchronized (_peerA.lock()) {
            synchronized (_peerB.lock()) {
        
        		_peerA.commit();
        		_peerB.commit();
        
                endReplication();
        
        		long versionA = _peerA.currentVersion() - 1;
        		long versionB = _peerB.currentVersion() - 1;
        
        		_record._version = versionB;
        
        		if (versionA > versionB) {
        			_record._version = versionA;
        			_peerB.raiseVersion(_record._version);
        		} else if (versionB > versionA) {
        			_peerA.raiseVersion(_record._version);
        		}
        
        		_record.store(_peerA);
        		_record.store(_peerB);
            }
        }
	}

	void destinationOnNew(YapObject destinationReference) {
		if (_sourceReference != null) {
			VirtualAttributes vas = _sourceReference.virtualAttributes(_transA);
			destinationReference.i_virtualAttributes = new VirtualAttributes();
			VirtualAttributes vad = destinationReference.i_virtualAttributes;
			vad.i_uuid = vas.i_uuid;
			vad.i_version = vas.i_version;
			vad.i_database = vas.i_database;
		}
	}

	private void endReplication() {
		_peerA.i_migrateFrom = null;
		_peerA.i_handlers.i_replication = null;
		_peerB.i_migrateFrom = null;
		_peerB.i_handlers.i_replication = null;
	}
    
    private int idInCaller(YapStream caller, YapObject referenceA, YapObject referenceB){
        return (caller == _peerA) ? referenceA.getID() : referenceB.getID();
    }

	private int ignoreOrCheckConflict() {
		if (_direction == CHECK_CONFLICT) {
			return CHECK_CONFLICT;
		}
		return IGNORE;
	}
	
	private boolean isInConflict(long versionA, long versionB) {
		if(versionA > _record._version && versionB > _record._version) {
			return true;
		}
		if(versionB > _record._version && _direction == TO_B) {
			return true;
		}
		if(versionA > _record._version && _direction == TO_A) {
			return true;
		}
		return false;
	}

	private long lastSynchronization() {
		return _record._version;
	}

	public ObjectContainer peerA() {
		return _peerA;
	}

	public ObjectContainer peerB() {
		return _peerB;
	}

	public void replicate(Object obj) {

		// When there is an active replication process, the set() method
		// will call back to the #process() method in this class.

		// This detour is necessary, since #set() has to handle all cases
		// anyway, for members of the replicated object, especially the
		// prevention of endless loops in case of circular references.

		YapStream stream = _peerB;

		if (_peerB.isStored(obj)) {
			if (!_peerA.isStored(obj)) {
				stream = _peerA;
			}
		}

		stream.set(obj);
	}

	public void rollback() {
		_peerA.rollback();
		_peerB.rollback();
		endReplication();
	}

	public void setDirection(ObjectContainer replicateFrom,
			ObjectContainer replicateTo) {
		if (replicateFrom == _peerA && replicateTo == _peerB) {
			_direction = TO_B;
		}
		if (replicateFrom == _peerB && replicateTo == _peerA) {
			_direction = TO_A;
		}
	}

	private void shareBinding(YapObject referenceA, Object objectA, YapObject referenceB, Object objectB) {
		if(_sourceReference == null) {
			return;
		}
		if(_sourceReference == referenceA) {
			_peerB.bind2(referenceB, objectA);
		}else {
			_peerA.bind2(referenceA, objectB);
		}
	}

	private int toA() {
		if (_direction == CHECK_CONFLICT) {
			return CHECK_CONFLICT;
		}
		if (_direction != TO_B) {
			return TO_A;
		}
		return IGNORE;
	}

	private int toB() {
		if (_direction == CHECK_CONFLICT) {
			return CHECK_CONFLICT;
		}
		if (_direction != TO_A) {
			return TO_B;
		}
		return IGNORE;
	}
    
	
	/**
	 * called by YapStream.set()
	 * @return id of reference in caller or 0 if not handled or -1
     * if #set() should stop processing because of a direction 
     * setting.
	 */
	int tryToHandle(YapStream caller, Object obj) {
        
        int notProcessed = 0;
        YapStream other = null;
        
        if(caller == _peerA){
            other = _peerB;
            if(_direction == TO_B){
                notProcessed = -1;
            }
        }else{
            other = _peerA;
            if(_direction == TO_A){
                notProcessed = -1;
            }
        }
        
		synchronized (other.i_lock) {
            
			Object objectA = obj;
			Object objectB = obj;
			
			YapObject referenceA = _peerA.getYapObject(obj);
			YapObject referenceB = _peerB.getYapObject(obj);
			
			VirtualAttributes attA = null;
			VirtualAttributes attB = null;
			
			if (referenceA == null) {
				if(referenceB == null) {
					return notProcessed;
				}
				
				_sourceReference = referenceB;
				
				attB = referenceB.virtualAttributes(_transB);
                if(attB == null){
                    return notProcessed;
                }
				
				Object[] arr = _transA.objectAndYapObjectBySignature(attB.i_uuid,
						attB.i_database.i_signature);
				if (arr[0] == null) {
					return notProcessed;
				}
				
				referenceA = (YapObject) arr[1];
				objectA = arr[0];
				
				attA = referenceA.virtualAttributes(_transA);
			}else {
				
				attA = referenceA.virtualAttributes(_transA);
                if(attA == null){
                    return notProcessed;
                }
				
				if(referenceB == null) {
					_sourceReference = referenceA;
					Object[] arr = _transB.objectAndYapObjectBySignature(attA.i_uuid,
							attA.i_database.i_signature);
					if (arr[0] == null) {
						return notProcessed;
					}
					referenceB = (YapObject) arr[1];
					objectB = arr[0];
				}else {
					_sourceReference = null;
				}
				
				attB = referenceB.virtualAttributes(_transB);
			}
            
            if(attA == null || attB == null){
                return notProcessed;
            }
			
			_peerA.refresh(objectA, 1);
			_peerB.refresh(objectB, 1);
			
			
			if (attA.i_version <= _record._version
					&& attB.i_version <= _record._version) {

				if (_direction != CHECK_CONFLICT) {
					shareBinding(referenceA, objectA, referenceB, objectB);
				}
                return idInCaller(caller, referenceA, referenceB);
			}

			int direction = ignoreOrCheckConflict();

			if (isInConflict(attA.i_version, attB.i_version)) {
				Object prevailing = _conflictHandler.resolveConflict(this,
						objectA, objectB);

				if (prevailing == objectA) {
					direction = (_direction == TO_A) ? IGNORE : toB(); 
				}

				if (prevailing == objectB) {
					direction = (_direction == TO_B) ? IGNORE : toA();
				}

				if (direction == IGNORE) {
					shareBinding(referenceA, objectA, referenceB, objectB);
                    return idInCaller(caller, referenceA, referenceB);
				}

			} else {
				direction = attB.i_version > _record._version ? toA(): toB();
			}

			if (direction == TO_A) {
				if (!referenceB.isActive()) {
					referenceB.activate(_transB, objectB, 1, false);
				}
				_peerA.bind2(referenceA, objectB);
                int idA = _peerA.setAfterReplication(_transA, objectB, 1, true);
                if(caller == _peerA){
                    return idA;
                }
			}

			if (direction == TO_B) {
				if (!referenceA.isActive()) {
					referenceA.activate(_transA, objectA, 1, false);
				}
				_peerB.bind2(referenceB, objectA);
				int idB = _peerB.setAfterReplication(_transB, objectA, 1, true);
                if(caller == _peerB){
                    return idB;
                }
			}

            return idInCaller(caller, referenceA, referenceB);
		}

	}

	public void whereModified(Query query) {
		query.descend(VirtualField.VERSION).constrain(
				new Long(lastSynchronization())).greater();
	}

}