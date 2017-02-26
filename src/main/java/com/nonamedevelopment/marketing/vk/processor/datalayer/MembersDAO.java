package com.nonamedevelopment.marketing.vk.processor.datalayer;

import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.nonamedevelopment.marketing.vk.processor.datalayer.BaseConnector.SqlRequestCallback;
import com.nonamedevelopment.marketing.vk.processor.datalayer.BaseConnector.SqlUpdateCallback;

public class MembersDAO {
	private static MembersDAO instance;

	public static final MembersDAO getInstance() throws PropertyVetoException {
		if (instance == null) {
			instance = new MembersDAO();
		}
		return instance;
	}

	private BaseConnector connector;

	private MembersDAO() throws PropertyVetoException {
		connector = new BaseConnector();
	}

	public List<Member> getMembers(UUID groupId) throws SQLException {
		return connector.processRequest("SELECT group_id, user_id, join_time FROM sn_members WHERE group_id = ?", new SqlRequestCallback<Member>() {
			@Override
			public void addParams(PreparedStatement statement) throws SQLException {
				statement.setString(1, groupId.toString());
			}

			@Override
			public Member process(ResultSet resultSet) throws SQLException {
				Member member = new Member();
				member.setGroupId(UUID.fromString(resultSet.getString("group_id")));
				member.setUserId(UUID.fromString(resultSet.getString("user_id")));
				member.setJoinTime(resultSet.getLong("join_time"));
				return member;
			}
		});
	}

	public boolean insert(Member newMember) throws SQLException {
		return connector.processUpdate("INSERT DELAYED INTO sn_members (group_id, user_id, join_time) VALUES (?, ?, ?)", new SqlUpdateCallback() {
			@Override
			public void addParams(PreparedStatement statement) throws SQLException {
				statement.setString(1, newMember.getGroupId().toString());
				statement.setString(2, newMember.getUserId().toString());
				statement.setLong(3, newMember.getJoinTime());
			}
		}) > 0;
	}
}
