package com.db4o.io;

import com.db4o.ext.*;
import com.db4o.internal.caching.*;

public class CachingStorage extends StorageDecorator {

	private static int DEFAULT_PAGE_SIZE = 1024;

	private static int DEFAULT_PAGE_COUNT = 64;

	public CachingStorage(Storage storage) {
	    super(storage);
    }
	
	@Override
	public Bin open(String uri, boolean lockFile, long initialLength, boolean readOnly) throws Db4oIOException {
	    final Bin storage = super.open(uri, lockFile, initialLength, readOnly);
	    if (readOnly) {
	    	return new ReadOnlyBin(new NonFlushingCachingBin(storage, newCache(), DEFAULT_PAGE_COUNT, DEFAULT_PAGE_SIZE));
	    }
	    return new CachingBin(storage, newCache(), DEFAULT_PAGE_COUNT, DEFAULT_PAGE_SIZE);
	}

	protected Cache4<Object, Object> newCache() {
	    return CacheFactory.new2QXCache(DEFAULT_PAGE_COUNT);
    }

	private static final class NonFlushingCachingBin extends CachingBin {
		
		public NonFlushingCachingBin(Bin bin, Cache4 cache, int pageCount, int pageSize) throws Db4oIOException {
			super(bin, cache, pageCount, pageSize);
		}
		
		@Override protected void flushAllPages() {
		}
	}
}