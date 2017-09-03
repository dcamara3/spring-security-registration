package org.baeldung.persistence.dao;

import com.ustn.userprofile.manager.UserManager;
import org.baeldung.persistence.model.PasswordResetToken;
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
public class PasswordResetTokenRepository {

    @Autowired
    UserManager userManager;

    @Autowired
    private DataSource dataSource;

    public PasswordResetToken findByToken(String token) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        List<PasswordResetToken> passwordResetTokenList=
                select.query("SELECT * FROM PasswordResetToken WHERE TOKEN = ?",
                new Object[]{token}, new RowMapper<PasswordResetToken>() {
            @Override
            public PasswordResetToken mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                PasswordResetToken passwordResetToken = new PasswordResetToken();
                passwordResetToken.setId(resultSet.getLong("id"));
                passwordResetToken.setToken(resultSet.getString("token"));
                passwordResetToken.setExpiryDate(resultSet.getDate("expiryDate"));
                passwordResetToken.setUser(userManager.getUserAccount(resultSet.getInt("user_id")));
                return  passwordResetToken;
            }
        });

        if (passwordResetTokenList != null && !passwordResetTokenList.isEmpty()) {
            return passwordResetTokenList.get(0);
        } else {
            return null;
        }
    }

    /*PasswordResetToken findByUser(UserAccount user);
    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);
    void deleteByExpiryDateLessThan(Date now);*/

    /*@Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);*/

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PasswordResetToken save(PasswordResetToken token) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String insertSql = "INSERT INTO PasswordResetToken (id, expiryDate, token, user_id) VALUES (?, ?, ?, ?)";
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
}
