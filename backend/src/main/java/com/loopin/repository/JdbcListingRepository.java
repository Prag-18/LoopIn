package com.loopin.repository;

import com.loopin.model.ItemListing;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer implementation of ListingRepository backed by MySQL via Spring JDBC (JdbcTemplate).
 * Annotated with @Primary for automatic injection into Services and Controllers.
 */
@Repository
@Primary
public class JdbcListingRepository implements ListingRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcListingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ItemListing> rowMapper = (rs, rowNum) -> new ItemListing(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("category"),
            rs.getString("owner_name"),
            rs.getString("owner_email"),
            rs.getInt("min_trust_degree"),
            rs.getBoolean("available"),
            rs.getString("condition_state")
    );

    @Override
    public List<ItemListing> findAll() {
        String sql = "SELECT id, title, category, owner_name, owner_email, min_trust_degree, available, condition_state " +
                "FROM item_listings ORDER BY id ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<ItemListing> findById(Long id) {
        String sql = "SELECT id, title, category, owner_name, owner_email, min_trust_degree, available, condition_state " +
                "FROM item_listings WHERE id = ?";
        try {
            ItemListing listing = jdbcTemplate.queryForObject(sql, rowMapper, id);
            return Optional.ofNullable(listing);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ItemListing> findAvailableByCategory(String category) {
        if (category == null || category.isBlank()) {
            String sql = "SELECT id, title, category, owner_name, owner_email, min_trust_degree, available, condition_state " +
                    "FROM item_listings WHERE available = TRUE ORDER BY id ASC";
            return jdbcTemplate.query(sql, rowMapper);
        } else {
            String sql = "SELECT id, title, category, owner_name, owner_email, min_trust_degree, available, condition_state " +
                    "FROM item_listings WHERE available = TRUE AND LOWER(category) = LOWER(?) ORDER BY id ASC";
            return jdbcTemplate.query(sql, rowMapper, category);
        }
    }

    @Override
    public ItemListing save(ItemListing listing) {
        if (listing.getId() == null) {
            String sql = "INSERT INTO item_listings (title, category, owner_name, owner_email, min_trust_degree, " +
                    "available, condition_state) VALUES (?, ?, ?, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, listing.getTitle());
                ps.setString(2, listing.getCategory());
                ps.setString(3, listing.getOwnerName());
                ps.setString(4, listing.getOwnerEmail());
                ps.setInt(5, listing.getMinTrustDegree());
                ps.setBoolean(6, listing.isAvailable());
                ps.setString(7, listing.getCondition());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                listing.setId(keyHolder.getKey().longValue());
            }
            return listing;
        } else {
            String sql = "UPDATE item_listings SET title = ?, category = ?, owner_name = ?, owner_email = ?, " +
                    "min_trust_degree = ?, available = ?, condition_state = ? WHERE id = ?";

            jdbcTemplate.update(sql,
                    listing.getTitle(),
                    listing.getCategory(),
                    listing.getOwnerName(),
                    listing.getOwnerEmail(),
                    listing.getMinTrustDegree(),
                    listing.isAvailable(),
                    listing.getCondition(),
                    listing.getId());

            return listing;
        }
    }
}
