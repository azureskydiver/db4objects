package com.db4o.config.annotations.reflect;


public class UpdateDepthConfigurator extends Db4oConfigurator {
	private String _className;

	private int _updateDepth;

	public UpdateDepthConfigurator(String className, int updateDepthDefault) {
		this._className = className;
		this._updateDepth = updateDepthDefault;
	}

	@Override
	protected void configure() {
		objectClass(_className).updateDepth(_updateDepth);

	}

}
