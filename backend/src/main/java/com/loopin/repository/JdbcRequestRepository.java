package com.loopin.repository;

import com.loopin.model.ItemRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer implementation of RequestRepository backed by MySQL via Spring JDBC (JdbcTemplate).
 * Annotated with @Primary so Spring injects this implementation into Service components.
 */
@Repository
@Primary
public class JdbcRequestRepository implements RequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ItemRequest> rowMapper = (rs, rowNum) -> {
        Date startDateSql = rs.getDate("start_date");
        Date endDateSql = rs.getDate("end_date");

        return new ItemRequest(
                rs.getLong("id"),
                rs.getString("item_name"),
                rs.getString("category"),
                rs.getString("requester_name"),
                rs.getString("requester_email"),
                startDateSql != null ? startDateSql.toLocalDate() : null,
                endDateSql != null ? endDateSql.toLocalDate() : null,
                rs.getInt("trust_radius"),
                rs.getString("status"),
                rs.getString("note")
        );
    };

    @Override
    public ItemRequest save(ItemRequest request) {
        if (request.getId() == null) {
            String sql = "INSERT INTO item_requests (item_name, category, requester_name, requester_email, " +
                    "start_date, end_date, trust_radius, status, note) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, request.getItemName());
                ps.setString(2, request.getCategory());
                ps.setString(3, request.getRequesterName());
                ps.setString(4, request.getRequesterEmail());
                ps.setDate(5, request.getStartDate() != null ? Date.valueOf(request.getStartDate()) : null);
                ps.setDate(6, request.getEndDate() != null ? Date.valueOf(request.getEndDate()) : null);
                ps.setInt(7, request.getTrustRadius());
                ps.setString(8, request.getStatus());
                ps.setString(9, request.getNote());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                request.setId(keyHolder.getKey().longValue());
            }
            return request;
        } else {
            String sql = "UPDATE item_requests SET item_name = ?, category = ?, requester_name = ?, " +
                    "requester_email = ?, start_date = ?, end_date = ?, trust_radius = ?, status = ?, note = ? " +
                    "WHERE id = ?";

            jdbcTemplate.update(sql,
                    request.getItemName(),
                    request.getCategory(),
                    request.getRequesterName(),
                    request.getRequesterEmail(),
                    request.getStartDate() != null ? Date.valueOf(request.getStartDate()) : null,
                    request.getEndDate() != null ? Date.valueOf(request.getEndDate()) : null,
                    request.getTrustRadius(),
                    request.getStatus(),
                    request.getNote(),
                    request.getId());

            return request;
        }
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        String sql = "SELECT id, item_name, category, requester_name, requester_email, start_date, end_date, " +
                "trust_radius, status, note FROM item_requests WHERE id = ?";
        try {
            ItemRequest request = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(request);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ItemRequest> findAll() {
        String sql = "SELECT id, item_name, category, requester_name, requester_email, start_date, end_date, " +
                "trust_radius, status, note FROM item_requests ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public List<ItemRequest> findByStatus(String status) {
        String sql = "SELECT id, item_name, category, requester_name, requester_email, start_date, end_date, " +
                "trust_radius, status, note FROM item_requests WHERE LOWER(status) = LOWER(?) ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM item_requests WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, id);
        return rowsAffected > 0;
    }
}
