/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding.verifiers;

import org.apache.oro.text.perl.Perl5Util;

import com.db4o.binding.verifier.IVerifier;

/**
 * FloatVerifier.  Verify data input for Floats
 *
 * @author djo
 */
public class FloatVerifier implements IVerifier {
    
    private static Perl5Util matcher = new Perl5Util();

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#verifyFragment(java.lang.String)
	 */
	public boolean verifyFragment(String fragment) {
		return matcher.match("/^\\-?[0-9]*\\.?[0-9]*([0-9]+[e|E]\\-?([0-9]+\\.)?[0-9]*)?$/", fragment);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#verifyFullValue(java.lang.String)
	 */
	public boolean verifyFullValue(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (Exception e) {
        	return false;
        }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.binding.verifier.IVerifier#getHint()
	 */
	public String getHint() {
		return "Please enter a number between " + Float.MIN_VALUE + " and " + Float.MAX_VALUE + ".";
	}

}
