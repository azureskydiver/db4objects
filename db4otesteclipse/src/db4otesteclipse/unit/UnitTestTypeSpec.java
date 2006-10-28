package db4otesteclipse.unit;

import org.eclipse.debug.core.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.launching.*;

import db4otesteclipse.*;

public class UnitTestTypeSpec implements TestTypeSpec {
	private static final String PLAIN_TESTLAUNCHER_NAME = "db4ounit.UnitTestMain";
	private static final String DB4O_TESTLAUNCHER_NAME = "db4ounit.db4o.Db4oUnitTestMain";
	private static final String TESTINTERFACE_NAME = "db4ounit.TestCase";
	
	public boolean acceptTestType(IType type) throws JavaModelException {
		if (!type.exists() || Flags.isAbstract(type.getFlags())) {
			Activator.log("Not existent or abstract: "
					+ type.getFullyQualifiedName());
			return false;
		}
		return implementsTest(type);
	}

	private boolean implementsTest(IType type) throws JavaModelException {
		ITypeHierarchy hierarchy=type.newSupertypeHierarchy(null);
		IType[] interfaces=hierarchy.getAllInterfaces();
		for (int idx = 0; idx < interfaces.length; idx++) {
			if(interfaces[idx].getFullyQualifiedName().equals(TESTINTERFACE_NAME)) {
				return true;
			}
		}
		return false;
	}

	public void configureSpecific(String typeList,
			ILaunchConfigurationWorkingCopy workingCopy) {
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				PLAIN_TESTLAUNCHER_NAME);
		workingCopy.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, typeList);
	}
}
