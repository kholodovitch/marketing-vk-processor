package com.nonamedev.marketing.vk.processor.datalayer;

import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.nonamedev.marketing.vk.processor.datalayer.BaseConnector.SqlRequestCallback;
import com.nonamedev.marketing.vk.processor.datalayer.BaseConnector.SqlUpdateCallback;

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
		connector = BaseConnector.getInstance();
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
				member.setMemberId(new MemberIdentity(UUID.fromString(resultSet.getString("group_id")), UUID.fromString(resultSet.getString("user_id"))));
				member.setJoinTime(resultSet.getLong("join_time"));
				return member;
			}
		});
	}

	public boolean insert(Member newMember) throws SQLException {
		return connector.processUpdate("INSERT INTO sn_members (group_id, user_id, join_time) VALUES (?, ?, ?)", new SqlUpdateCallback() {
			@Override
			public void addParams(PreparedStatement statement) throws SQLException {
				statement.setString(1, newMember.getMemberId().getGroupId().toString());
				statement.setString(2, newMember.getMemberId().getUserId().toString());
				statement.setLong(3, newMember.getJoinTime());
			}
		}) > 0;
	}
}
