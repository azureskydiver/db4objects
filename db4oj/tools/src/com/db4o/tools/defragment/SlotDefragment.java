/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.inside.btree.*;
import com.db4o.inside.classindex.*;

/**
 * Slotwise defragment for yap files.
 * 
 * Db4o structures storage inside the yap file as free and occupied slots,
 * very much like a file system - and just like a file system it can be
 * fragmented.
 * 
 * The simplest way to defragment a yap file:
 * 
 * SlotDefragment.defrag("sample.yap");
 * 
 * This will move the file to "sample.yap.backup", then create a defragmented
 * version of this file in the original position, using a temporary file
 * "sample.yap.mapping". If the backup file already exists, this will throw
 * an exception and no action will be taken.
 * 
 * For more detailed configuration of the defragmentation process, provide
 * a DefragmentConfig instance:
 * 
 * DefragmentConfig config=new DefragmentConfig("sample.yap","sample.bap","sample.map");
 * config.forceBackupDelete(true);
 * config.yapClassFilter(new AvailableClassFilter());
 * SlotDefragment.defrag(config);
 * 
 * This will move the file to "sample.bap", then create a defragmented
 * version of this file in the original position, using a temporary file
 * "sample.map". If the backup file already exists, it will be deleted.
 * The defragmentation process will skip all classes that have instances
 * stored within the yap file, but that are not available on the class path
 * (through the current classloader).
 */
public class SlotDefragment {

	/**
	 * Renames the file at the given original path to a backup
	 * file and then builds a defragmented version of the file in the original place.
	 * 
	 * @param origPath The path to the file to be defragmented.
	 * @throws IOException if the original file cannot be moved to the backup location
	 */
	public static void defrag(String origPath) throws IOException {
		defrag(new DefragmentConfig(origPath),new NullListener());
	}

	/**
	 * Renames the file at the given original path to the given backup
	 * file and then builds a defragmented version of the file in the original place.
	 * 
	 * @param origPath The path to the file to be defragmented.
	 * @param backupPath The path to the backup file to be created.
	 * @throws IOException if the original file cannot be moved to the backup location
	 */
	public static void defrag(String origPath,String backupPath) throws IOException {
		defrag(new DefragmentConfig(origPath,backupPath),new NullListener());
	}

	/**
	 * Renames the file at the configured original path to the configured backup
	 * path and then builds a defragmented version of the file in the original place.
	 * 
	 * @param config The configuration for this defragmentation run.
	 * @throws IOException if the original file cannot be moved to the backup location
	 */
	public static void defrag(DefragmentConfig config) throws IOException {
		defrag(config,new NullListener());
	}

	/**
	 * Renames the file at the configured original path to the configured backup
	 * path and then builds a defragmented version of the file in the original place.
	 * 
	 * @param config The configuration for this defragmentation run.
	 * @param listener A listener for status notifications during the defragmentation
	 *         process.
	 * @throws IOException if the original file cannot be moved to the backup location
	 */
	public static void defrag(DefragmentConfig config, DefragmentListener listener) throws IOException {
		File backupFile=new File(config.backupPath());
		if(backupFile.exists()) {
			if(!config.forceBackupDelete()) {
				throw new IOException("Could not use '"+config.backupPath()+"' as backup path - file exists.");
			}
			backupFile.delete();
		}
		File4.rename(config.origPath(), config.backupPath());
		DefragContextImpl context=new DefragContextImpl(config,listener);
		int newClassCollectionID=0;
		int targetIdentityID=0;
		int targetUuidIndexID=0;
		try {
			firstPass(context,config);
			secondPass(context,config);
			defragUnindexed(context);
			newClassCollectionID=context.mappedID(context.sourceClassCollectionID());
			int sourceIdentityID=context.databaseIdentityID(DefragContextImpl.SOURCEDB);
			targetIdentityID=context.mappedID(sourceIdentityID);
			context.targetClassCollectionID(newClassCollectionID);
			targetUuidIndexID = context.mappedID(context.sourceUuidIndexID(),0);
		} 
		catch (CorruptionException exc) {
			exc.printStackTrace();
		} 
		finally {
			context.close();
		}
		setIdentity(config.origPath(), targetIdentityID,targetUuidIndexID);
	}

	private static void defragUnindexed(DefragContextImpl context) throws CorruptionException {
		Iterator4 unindexedIDs=context.unindexedIDs();
		while(unindexedIDs.moveNext()) {
			final int origID=((Integer)unindexedIDs.current()).intValue();
			if(context.hasSeen(origID)) {
				continue;
			}
			ReaderPair.processCopy(context, origID, new SlotCopyHandler() {
				public void processCopy(ReaderPair readers) throws CorruptionException {
					YapClass.defragObject(readers);
				}
				
			}, true, true);
		}
	}

	private static void setIdentity(String targetFile, int targetIdentityID, int targetUuidIndexID) {
		YapFile targetDB=(YapFile)Db4o.openFile(DefragmentConfig.db4oConfig(),targetFile);
		try {
			Db4oDatabase identity=(Db4oDatabase)targetDB.getByID(targetIdentityID);
			targetDB.setIdentity(identity);
			targetDB.systemData().uuidIndexId(targetUuidIndexID);
		}
		finally {
			targetDB.close();
		}
	}

	private static void firstPass(DefragContextImpl context,DefragmentConfig config) throws CorruptionException {
		//System.out.println("FIRST");
		pass(context,config,new FirstPassCommand());
	}

	private static void secondPass(final DefragContextImpl context,DefragmentConfig config) throws CorruptionException {
		//System.out.println("SECOND");
		pass(context,config,new SecondPassCommand());
	}		

	private static void pass(DefragContextImpl context,DefragmentConfig config,PassCommand command) throws CorruptionException {
		context.clearSeen();
		command.processClassCollection(context);
		StoredClass[] classes=context.storedClasses(DefragContextImpl.SOURCEDB);
		for (int classIdx = 0; classIdx < classes.length; classIdx++) {
			YapClass yapClass = (YapClass)classes[classIdx];
			if(!config.storedClassFilter().accept(yapClass)) {
				continue;
			}
			processYapClass(context, yapClass,command);
			command.flush(context);
		}
		BTree uuidIndex=context.sourceUuidIndex();
		if(uuidIndex!=null) {
			command.processBTree(context, uuidIndex);
		}
		command.flush(context);
		context.targetCommit();
	}

	// TODO order of class index/object slot processing is crucial:
	// - object slots before field indices (object slots register addresses for use by string indices)
	// - class index before object slots, otherwise phantom btree entries from deletions appear in the source class index?!?
	//   reproducable with SelectiveCascadingDeleteTestCase and ObjectSetTestCase - investigate.
	private static void processYapClass(final DefragContextImpl context, final YapClass curClass, final PassCommand command) throws CorruptionException {
		processClassIndex(context, curClass, command);
		processObjectsForYapClass(context, curClass, command);
		processYapClassAndFieldIndices(context, curClass, command);
	}

	private static void processObjectsForYapClass(
			final DefragContextImpl context, final YapClass curClass,
			final PassCommand command) {
		// TODO: check for string indices specifically, not field indices in general
		final boolean withStringIndex=withFieldIndex(curClass);
		context.traverseAll(curClass, new Visitor4() {
			public void visit(Object obj) {
				int id = ((Integer)obj).intValue();
				// TODO cache mapped pair and pass target id into processObjectSlot()
				if(command.hasSeen(context,id)) {
					return;
				}
				try {
					command.processObjectSlot(context,curClass,id, withStringIndex);
				} catch (CorruptionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static boolean withFieldIndex(YapClass clazz) {
		Iterator4 fieldIter=clazz.fields();
		while(fieldIter.moveNext()) {
			YapField curField=(YapField)fieldIter.current();
			if(curField.hasIndex()) {
				return true;
			}
		}
		return false;
	}

	private static void processYapClassAndFieldIndices(final DefragContextImpl context,
			final YapClass curClass, final PassCommand command)
			throws CorruptionException {
		int sourceClassIndexID=0;
		int targetClassIndexID=0;
		if(curClass.hasIndex()) {
			sourceClassIndexID = curClass.index().id();
			targetClassIndexID=context.mappedID(sourceClassIndexID,-1);
		}
		command.processClass(context,curClass,curClass.getID(),targetClassIndexID);
	}

	private static void processClassIndex(final DefragContextImpl context,
			final YapClass curClass, final PassCommand command)
			throws CorruptionException {
		if(curClass.hasIndex()) {
			BTreeClassIndexStrategy indexStrategy=(BTreeClassIndexStrategy) curClass.index();
			final BTree btree=indexStrategy.btree();
			command.processBTree(context,btree);
		}
	}
	
	private static class NullListener implements DefragmentListener {
		public void notifyDefragmentInfo(DefragmentInfo info) {
		}
	}
}
