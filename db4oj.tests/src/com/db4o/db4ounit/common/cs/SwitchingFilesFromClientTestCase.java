/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;


public class SwitchingFilesFromClientTestCase extends ClientServerTestCaseBase {

	public void testSwitch() {
		client().switchToFile(SwitchingFilesFromClientUtil.FILENAME_A);
		client().switchToFile(SwitchingFilesFromClientUtil.FILENAME_B);
		client().switchToMainFile();
		client().switchToFile(SwitchingFilesFromClientUtil.FILENAME_A);
		client().switchToFile(SwitchingFilesFromClientUtil.FILENAME_A);
	}
	
	protected void db4oSetupBeforeStore() throws Exception {
		SwitchingFilesFromClientUtil.deleteFiles();
	}

	protected void db4oCustomTearDown() throws Exception {
		SwitchingFilesFromClientUtil.deleteFiles();
	}
}
