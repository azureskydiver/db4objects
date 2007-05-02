/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.enums;

public enum Qualification {
	
	    WINNER("WINNER"),
	    PROFESSIONAL("PROFESSIONAL"),
	    TRAINEE("TRAINEE");
	    
	    private String qualification;
	    
	    private Qualification(String qualification) {
	       this.qualification = qualification;
	    }

	    public void testChange(String qualification){
	    	this.qualification = qualification; 
	    }
	    
	    public String toString() {
	        return qualification;
	    }
}
