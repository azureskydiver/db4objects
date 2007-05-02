/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.enums;

public class Pilot {
	private String name;
    private Qualification qualification;

    public Pilot(String name,Qualification qualification) {
        this.name=name;
        this.qualification=qualification;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name+"/"+qualification;
    }
}
