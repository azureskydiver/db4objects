package com.db4o.replication.hibernate.metadata;

import com.db4o.Unobfuscated;

public class PeerSignature extends ReplicationProviderSignature {
// -------------------------- STATIC METHODS --------------------------

	public static PeerSignature generateSignature() {
		return new PeerSignature(Unobfuscated.generateSignature());
	}

// --------------------------- CONSTRUCTORS ---------------------------

	public PeerSignature() {
		super();
	}

	public PeerSignature(byte[] bytes) {
		super(bytes);
	}
}
