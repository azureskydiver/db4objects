﻿/* Copyright (C) 2007 - 2008  Versant Inc.  http://www.db4o.com */

using System;
using System.Reflection;
using System.Runtime.InteropServices;
using System.Security;

[assembly: AssemblyTitle("db4o - optional cs functionality")]
[assembly: AssemblyCompany("Versant Corp., Redwood City, CA, USA")]
[assembly: AssemblyProduct("db4o - database for objects")]
[assembly: AssemblyCopyright("Versant Corp. 2000 - 2009")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture ("")]

[assembly: ComVisible (false)]

[assembly: AssemblyVersion("7.12.123.14061")]

#if !CF && !SILVERLIGHT
[assembly: AllowPartiallyTrustedCallers]
#endif

[assembly: CLSCompliant(true)]