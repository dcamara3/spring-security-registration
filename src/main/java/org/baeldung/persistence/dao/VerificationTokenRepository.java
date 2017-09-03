package org.baeldung.persistence.dao;

import com.ustn.userprofile.manager.UserManager;
import org.baeldung.persistence.model.VerificationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Component
public class VerificationTokenRepository {

    @Autowired
    UserManager userManager;

    @Autowired
    private DataSource dataSource;

    public VerificationToken findByToken(String token) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        List<VerificationToken> passwordResetTokenList =
                select.query("SELECT * FROM VerificationToken WHERE TOKEN = ?",
                        new Object[]{token}, new RowMapper<VerificationToken>() {
                            @Override
                            public VerificationToken mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                                VerificationToken verificationToken = new VerificationToken();
                                verificationToken.setId(resultSet.getLong("id"));
                                verificationToken.setToken(resultSet.getString("token"));
                                verificationToken.setExpiryDate(resultSet.getDate("expiryDate"));
                                verificationToken.setUser(userManager.getUserAccount(resultSet.getInt("user_id")));
                                return verificationToken;
                            }
                        });

        if (passwordResetTokenList != null && !passwordResetTokenList.isEmpty()) {
            return passwordResetTokenList.get(0);
        } else {
            return null;
        }
    }

    public VerificationToken save(VerificationToken token) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String insertSql = "INSERT INTO VerificationToken (id, expiryDate, token, user_id) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(insertSql, com.mysql.jdbc.Statement.RETURN_GENERATED_KEYS);

                ps.setLong(1,token.getId());
                ps.setDate(2, new Date(token.getExpiryDate().getTime()));
                ps.setString(3,token.getToken());
                ps.setLong(4,token.getUser().getId());

                return ps;
            }
        },keyHolder);
        token.setId(keyHolder.getKey().longValue());
        return token;
    }

    public void delete(VerificationToken verificationToken) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("DELETE FROM VerificationToken WHERE ID = ?", verificationToken.getId());
    }

    public void delete(long tokenId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("DELETE FROM VerificationToken WHERE ID = ?", tokenId);
    }

/*    VerificationToken findByUser(UserAccount user);
    Stream<VerificationToken> findAllByExpiryDateLessThan(Date now);
    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from VerificationToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);*/
}
