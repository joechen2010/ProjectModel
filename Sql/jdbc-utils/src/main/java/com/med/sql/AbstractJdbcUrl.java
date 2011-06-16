package com.med.sql;

/*
 * This class implements non-DBMS specific features of a JdbcUrl.
 *
 * @author Jane Eisenstein
 *
 * Copyright (c) 2008 MEDecision, Inc. All rights reserved.
 */
public abstract class AbstractJdbcUrl implements JdbcUrl {

		protected String url = null;
		protected String host = null;
		protected Long port = null;
		protected String database = null;

		protected abstract String getHostPrefix();
		protected abstract String getPortPrefix();
		protected abstract String getDatabasePrefix();

		/* (non-Javadoc)
		 * @see com.med.installer.JdbcUrl#setProperties(java.lang.String, java.lang.String, java.lang.String)
		 */
		public void setProperties(String host, Long port, String database) throws IllegalArgumentException {
			if (host != null && port != null && database != null) {
				this.host = host;
				this.port = port;
				this.database = database;
				url = getHostPrefix()+host+getPortPrefix()+port+getDatabasePrefix()+database;
			} else
				throw new IllegalArgumentException();
		}

		/* (non-Javadoc)
		 * @see com.med.installer.JdbcUrl#setUrl(java.lang.String)
		 */
		public void setUrl(String url) throws IllegalArgumentException {
			boolean valid = url != null && url.startsWith(getHostPrefix());

			if (valid) {
				int hostIdx = getHostPrefix().length();
				int portIdx = url.indexOf(getPortPrefix(), hostIdx) + 1;
				int databaseIdx = url.indexOf(getDatabasePrefix(), portIdx) + 1;

				valid = portIdx > 1 && databaseIdx > 1;

				if (valid) {
					host = url.substring(hostIdx, portIdx - 1);
					String portString = url.substring(portIdx, databaseIdx - 1);
					port = new Long(portString);
					database = url.substring(databaseIdx, url.length());
					this.url = url;
				}
			}

			if (!valid)
				throw new IllegalArgumentException();
		}

		/* (non-Javadoc)
		 * @see com.med.installer.JdbcUrl#getUrl()
		 */
		public String getUrl() {
			return url;
		}

		/* (non-Javadoc)
		 * @see com.med.installer.JdbcUrl#getHost()
		 */
		public String getHost() {
			return host;
		}

		/* (non-Javadoc)
		 * @see com.med.installer.JdbcUrl#getPort()
		 */
		public Long getPort() {
			return port;
		}

		/* (non-Javadoc)
		 * @see com.med.installer.JdbcUrl#getDatabase()
		 */
		public String getDatabase() {
			return database;
		}

		public String toString() {
			String string =
				"host="+getHost() +
				", port="+getPort() +
				", database=" + getDatabase() +
				", url="+ getUrl();
			return string;
		}
	}

