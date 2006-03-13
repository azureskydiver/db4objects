package com.db4o.replication.hibernate.impl.ref_as_table;

import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import org.hibernate.Session;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Shared {
	public static void ensureLong(Serializable id) {
		if (!(id instanceof Long))
			throw new IllegalStateException("You must use 'long' as the type of the hibernate id");
	}

	public static long castAsLong(Serializable id) {
		ensureLong(id);
		return ((Long) id).longValue();
	}

	static void incrementObjectVersion(Session sess, Object entity, long id) {
		incrementObjectVersion(sess.connection(), entity.getClass().getName(), castAsLong(sess.getIdentifier(entity)));
	}

	static void incrementObjectVersion(Connection con, String className, long id) {
		long newVer = Util.getMaxVersion(con) + 1;
		String sql = "UPDATE " + ObjectReference.TABLE_NAME
				+ " SET " + ObjectReference.VERSION + " = " + newVer
				+ " WHERE " + ObjectReference.CLASS_NAME + " = '" + className + "'"
				+ " AND " + ObjectReference.OBJECT_ID + " = " + id;

		final Statement st = Util.getStatement(con);

		try {
			final int affected = st.executeUpdate(sql);

			if (affected != 1)
				throw new RuntimeException("unable to update the version of an object");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeStatement(st);
		}
	}

	public static long getVersion(Connection con, String className, long id) {
		final Statement st = Util.getStatement(con);

		String sql = "SELECT " + ObjectReference.VERSION + " FROM " + ObjectReference.TABLE_NAME
				+ " WHERE " + ObjectReference.CLASS_NAME + " = ?"
				+ " AND " + ObjectReference.OBJECT_ID + " = ?";
		final PreparedStatement ps = Util.prepareStatement(con, sql);


		ResultSet rs = null;
		try {
			ps.setString(1, className);
			ps.setLong(2, id);

			rs = ps.executeQuery();

			if (!rs.next())
				throw new RuntimeException("failed to get the version, the sql was = " + sql);

			return rs.getLong(1);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeStatement(st);
			Util.closeResultSet(rs);
		}
	}
}
