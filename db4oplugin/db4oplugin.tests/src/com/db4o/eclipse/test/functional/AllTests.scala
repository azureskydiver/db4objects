package com.db4o.eclipse.test.functional

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(
  classOf[InstrumentSingleClassTestCase]
))
class AllTests {
}