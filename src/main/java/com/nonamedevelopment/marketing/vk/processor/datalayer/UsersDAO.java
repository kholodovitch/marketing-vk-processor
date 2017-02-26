package com.nonamedevelopment.marketing.vk.processor.datalayer;

import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.nonamedevelopment.marketing.vk.processor.datalayer.BaseConnector.SqlRequestCallback;
import com.nonamedevelopment.marketing.vk.processor.datalayer.BaseConnector.SqlUpdateCallback;

public class UsersDAO {
	private static UsersDAO instance;

	public static final UsersDAO getInstance() throws PropertyVetoException {
		if (instance == null) {
			instance = new UsersDAO();
		}
		return instance;
	}

	private BaseConnector connector;

	private UsersDAO() throws PropertyVetoException {
		connector = BaseConnector.getInstance();
	}

	public User get(Integer id) throws SQLException {
		return connector.processRequestAlone("SELECT id, sn_id FROM sn_users WHERE sn_id = ?", new SqlRequestCallback<User>() {
			@Override
			public void addParams(PreparedStatement statement) throws SQLException {
				statement.setInt(1, id);
			}

			@Override
			public User process(ResultSet resultSet) throws SQLException {
				User user = new User();
				user.setId(UUID.fromString(resultSet.getString("id")));
				user.setSnId(resultSet.getInt("sn_id"));
				return user;
			}
		});
	}

	public UUID insert(User user) throws SQLException {
		final UUID newId = UUID.randomUUID();
		final String sql = "INSERT INTO sn_users (id, sn_code, sn_id, first_name, last_name, bdate, sex, relation, photo_50, country_id, city_id, can_write_private_message, can_send_friend_request) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		connector.processUpdate(sql, new SqlUpdateCallback() {
			@Override
			public void addParams(PreparedStatement statement) throws SQLException {
				statement.setString(1, newId.toString());
				statement.setString(2, "vk");
				statement.setLong(3, user.getSnId());
				statement.setString(4, user.getFirstName());
				statement.setString(5, user.getLastName());
				statement.setString(6, user.getBdate());
				statement.setInt(7, user.getSex());

				if (user.getRelation() != null)
					statement.setInt(8, user.getRelation());
				else
					statement.setNull(8, java.sql.Types.INTEGER);

				statement.setString(9, user.getPhoto50());

				if (user.getCountryId() != null)
					statement.setInt(10, user.getCountryId());
				else
					statement.setNull(10, java.sql.Types.INTEGER);

				if (user.getCityId() != null)
					statement.setInt(11, user.getCityId());
				else
					statement.setNull(11, java.sql.Types.INTEGER);

				statement.setBoolean(12, user.isCanWritePrivateMessage());
				statement.setBoolean(13, user.isCanSendFriendRequest());
			}
		});
		return newId;
	}

}
