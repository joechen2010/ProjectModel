package cn.jcenterhome.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class SessionFactory {
	private static String CONFIG_FILE_LOCATION = "/hibernate.cfg.xml";
	private static org.hibernate.SessionFactory sessionFactory;

	public static synchronized void buildSessionFactory() throws SQLException {
		if (sessionFactory == null) {
			Map<String, String> jchConfig = JavaCenterHome.jchConfig;
			String dbHost = jchConfig.get("dbHost");
			String dbPort = jchConfig.get("dbPort");
			String dbName = jchConfig.get("dbName");
			String dbUser = jchConfig.get("dbUser");
			String dbPw = jchConfig.get("dbPw");
			String dbCharset = jchConfig.get("dbCharset");
			if (connect(dbHost, dbPort, dbName, dbUser, dbPw, dbCharset)) {
				Properties extraProperties = new Properties();
				extraProperties.setProperty("hibernate.connection.url", "jdbc:mysql://" + dbHost + ":"
						+ dbPort + "/" + dbName + "?zeroDateTimeBehavior=convertToNull");
				extraProperties.setProperty("hibernate.connection.username", dbUser);
				extraProperties.setProperty("hibernate.connection.password", dbPw);
				extraProperties.setProperty("hibernate.connection.characterEncoding", dbCharset);
				extraProperties.setProperty("hibernate.connection.characterSetResults", dbCharset);
				Configuration configuration = new Configuration();
				configuration = configuration.configure(CONFIG_FILE_LOCATION);
				configuration = configuration.addProperties(extraProperties);
				sessionFactory = configuration.buildSessionFactory();
			}
		}
	}

	public static Session getSession() throws SQLException {
		if (sessionFactory == null) {
			buildSessionFactory();
		}
		return sessionFactory.getCurrentSession();
	}

	public static void rebuildSessionFactory() throws SQLException {
		sessionFactory = null;
		buildSessionFactory();
	}

	public static org.hibernate.SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	private static boolean connect(String dbHost, String dbPort, String dbName, String dbUser, String dbPw,
			String dbCharset) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
				+ "?useUnicode=true&characterEncoding=" + dbCharset, dbUser, dbPw);
		if (conn != null) {
			if (!conn.isClosed()) {
				conn.close();
				conn = null;
			}
			return true;
		}
		return false;
	}
}