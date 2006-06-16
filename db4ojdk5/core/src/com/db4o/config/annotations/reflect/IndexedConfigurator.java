package com.db4o.config.annotations.reflect;

import com.db4o.config.ObjectField;

public class IndexedConfigurator extends FieldConfigurator {

	public IndexedConfigurator(String className, String fieldName) {
		super(className, fieldName);
	}

	@Override
	protected void configure(ObjectField objectField) {
		objectField.indexed(true);

	}

}
