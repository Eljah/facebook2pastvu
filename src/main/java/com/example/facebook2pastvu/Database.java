package com.example.facebook2pastvu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Database implements AutoCloseable {
    private final Connection conn;

    public Database() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
        conn.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS matches (" +
                "post_id TEXT, fb_url TEXT, pv_url TEXT, method TEXT)");
    }

    public void saveResult(String postId, String fbUrl, String pvUrl, String method) {
        try {
            var countRs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM matches");
            if (countRs.next() && countRs.getInt(1) >= 10000) {
                return;
            }
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO matches VALUES (?,?,?,?)")) {
                ps.setString(1, postId);
                ps.setString(2, fbUrl);
                ps.setString(3, pvUrl);
                ps.setString(4, method);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Match> getMatches() {
        List<Match> result = new ArrayList<>();
        try (ResultSet rs = conn.createStatement().executeQuery(
                "SELECT post_id, fb_url, pv_url, method FROM matches ORDER BY rowid DESC")) {
            while (rs.next()) {
                result.add(new Match(
                        rs.getString("post_id"),
                        rs.getString("fb_url"),
                        rs.getString("pv_url"),
                        rs.getString("method")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }
}
