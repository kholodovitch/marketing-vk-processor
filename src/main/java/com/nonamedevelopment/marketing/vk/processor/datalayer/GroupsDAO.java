package com.nonamedevelopment.marketing.vk.processor.datalayer;

import java.beans.PropertyVetoException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.nonamedevelopment.marketing.vk.processor.datalayer.BaseConnector.SqlRequestCallback;

public class GroupsDAO {
	private static GroupsDAO instance;

	public static final GroupsDAO getInstance() throws PropertyVetoException {
		if (instance == null) {
			instance = new GroupsDAO();
		}
		return instance;
	}

	private BaseConnector connector;

	private GroupsDAO() throws PropertyVetoException {
		connector = new BaseConnector();
	}

	public List<Group> getGroups() throws SQLException {
		return connector.processRequest("SELECT id, sn_id, sn_name, caption FROM sn_groups", new SqlRequestCallback<Group>() {
			@Override
			public Group process(ResultSet resultSet) throws SQLException {
				Group member = new Group();
				member.setId(UUID.fromString(resultSet.getString("id")));
				member.setSnId(resultSet.getLong("sn_id"));
				member.setSnName(resultSet.getString("sn_name"));
				member.setCaption(resultSet.getString("caption"));
				return member;
			}
		});
	}

}