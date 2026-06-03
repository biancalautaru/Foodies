package repository;

import config.DatabaseConfiguration;
import exceptions.RepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRepository<T, ID> implements GenericRepository<T, ID> {
    protected final Connection connection;

    @FunctionalInterface
    protected interface RowMapper<R> {
        R map(ResultSet rs) throws SQLException;
    }

    protected AbstractRepository() {
        this.connection = DatabaseConfiguration.getInstance().getConnection();
    }

    protected T queryOne(String sql, RowMapper<T> mapper, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParams(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(repositoryName() + ": query failed", e);
        }
        return null;
    }

    protected List<T> queryList(String sql, RowMapper<T> mapper, Object... params) {
        List<T> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParams(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException(repositoryName() + ": query failed", e);
        }
        return result;
    }

    protected void executeWrite(String sql, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParams(stmt, params);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(repositoryName() + ": write failed", e);
        }
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof String s) {
                stmt.setString(i + 1, s);
            } else if (param instanceof Integer n) {
                stmt.setInt(i + 1, n);
            } else if (param instanceof Double d) {
                stmt.setDouble(i + 1, d);
            } else if (param instanceof Boolean b) {
                stmt.setBoolean(i + 1, b);
            } else if (param instanceof java.sql.Timestamp ts) {
                stmt.setTimestamp(i + 1, ts);
            } else if (param == null) {
                stmt.setObject(i + 1, null);
            } else {
                stmt.setObject(i + 1, param);
            }
        }
    }

    protected abstract String repositoryName();
}