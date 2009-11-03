/* Copyright (C) 2004 - 2009  Versant Inc.  http://www.db4o.com */

using Db4objects.Db4o.CS.Config;
using Db4objects.Db4o.CS.Internal.Config;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Config;

namespace Db4objects.Db4o.CS.Internal.Config
{
	public class ServerConfigurationImpl : NetworkingConfigurationProviderImpl, IServerConfiguration
	{
		public ServerConfigurationImpl(Config4Impl config) : base(config)
		{
		}

		public virtual ICacheConfiguration Cache
		{
			get
			{
				return new CacheConfigurationImpl(Legacy());
			}
		}

		public virtual IFileConfiguration File
		{
			get
			{
				return Db4oLegacyConfigurationBridge.AsFileConfiguration(Legacy());
			}
		}

		public virtual ICommonConfiguration Common
		{
			get
			{
				return Db4oLegacyConfigurationBridge.AsCommonConfiguration(Legacy());
			}
		}
	}
}
