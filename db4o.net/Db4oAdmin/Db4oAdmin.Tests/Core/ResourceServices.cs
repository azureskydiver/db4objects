using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Reflection;

namespace Db4oTool.Tests.Core
{
    class ResourceServices
    {
        public static string GetResourceAsString(string resourceName)
        {
            using (Stream stream = Assembly.GetExecutingAssembly().GetManifestResourceStream(resourceName))
            {
                if (stream == null) throw new ArgumentException(resourceName, "resourceName");

                return new StreamReader(stream).ReadToEnd();
            }
        }

        public static string CompleteResourceName(Type type, string resource)
        {
            return type.Namespace + ".Resources." + resource + ".cs";
        }
    }
}
