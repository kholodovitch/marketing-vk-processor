package com.nonamedevelopment.marketing.vk.processor.datalayer;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nonamedevelopment.marketing.vk.processor.App;

public class BaseConnector {
	private static final Logger logger = LogManager.getLogger(BaseConnector.class);

	public static abstract class SqlUpdateCallback {
		public void addParams(PreparedStatement statement) throws SQLException {
		}

		public void finished() throws SQLException {
		}
	}

	public static abstract class SqlRequestCallback<T> {
		public void addParams(PreparedStatement statement) throws SQLException {
		}

		public abstract T process(ResultSet resultSet) throws SQLException;
	}

	private static BaseConnector instance;

	public static final BaseConnector getInstance() throws PropertyVetoException {
		if (instance == null) {
			instance = new BaseConnector();
		}
		return instance;
	}

	private ComboPooledDataSource dataSource;

	private BaseConnector() throws PropertyVetoException {
		dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass("com.mysql.jdbc.Driver");
		dataSource.setJdbcUrl(App.Settings.getDbJdbcConnectionString());
		dataSource.setIdleConnectionTestPeriod(60);

		dataSource.setInitialPoolSize(10);
		dataSource.setMinPoolSize(5);
		dataSource.setAcquireIncrement(5);
		dataSource.setMaxPoolSize(20);
		dataSource.setCheckoutTimeout(30000);

		dataSource.setUnreturnedConnectionTimeout(25);
		dataSource.setDebugUnreturnedConnectionStackTraces(true);
	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	private void close(AutoCloseable... closableArray) {
		for (AutoCloseable autoCloseable : closableArray) {
			close(autoCloseable);
		}
	}

	private void close(AutoCloseable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (Throwable e) {
			// don't throw now as it might leave following closables in
			// undefined state
			logger.warn("Error on closing element", e);
		}
	}

	public <T> T processRequestAlone(String sql, SqlRequestCallback<T> callback) throws SQLException {
		Connection connect = null;

		try {
			connect = getConnection();
			return processRequestAlone(connect, sql, callback);
		} finally {
			close(connect);
		}
	}

	public <T> T processRequestAlone(Connection connect, String sql, SqlRequestCallback<T> callback) throws SQLException {
		List<T> list = processRequest(connect, sql, callback);
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	public <T> List<T> processRequest(String sql, SqlRequestCallback<T> callback) throws SQLException {
		Connection connect = null;

		try {
			connect = getConnection();
			return processRequest(connect, sql, callback);
		} finally {
			close(connect);
		}
	}

	public <T> List<T> processRequest(Connection connect, String sql, SqlRequestCallback<T> callback) throws SQLException {
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		List<T> result = new ArrayList<T>();

		try {
			statement = connect.prepareStatement(sql);
			callback.addParams(statement);

			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result.add(callback.process(resultSet));
			}
		} finally {
			close(resultSet, statement);
		}
		return result;
	}

	public int processUpdate(String sql, SqlUpdateCallback callback) throws SQLException {
		Connection connect = null;

		try {
			connect = getConnection();
			return processUpdate(connect, sql, callback);
		} finally {
			close(connect);
		}
	}

	public int processUpdate(Connection connect, String sql, SqlUpdateCallback callback) throws SQLException {
		PreparedStatement statement = null;

		try {
			statement = connect.prepareStatement(sql);
			callback.addParams(statement);
			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close(statement);
			callback.finished();
		}
	}

}
