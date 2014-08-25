package com.company.user.service;

import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.password.PasswordServiceFactory;
import com.argo.core.service.annotation.RmiService;
import com.company.service.BaseServiceImpl;
import com.company.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-19.
 */
@RmiService(serviceInterface=UserService.class)
public class UserServiceImpl extends BaseServiceImpl implements UserService{

    protected static final RowMapper<User> User_ROWMAPPER = new BeanPropertyRowMapper<User>(
            User.class);

    public static final String SERVER_USER = "user";

    @Autowired
    private PasswordServiceFactory passwordServiceFactory;

    @Override
    @UserTx
    public void addUser(final User user) throws Exception {

        String passwd = passwordServiceFactory.getDefaultService().encrypt(user.getHashPasswd(), user.getEmail());
        user.setHashPasswd(passwd);

        final String sql = "insert into user(userName, realName, email, hashPasswd, iconUrl, createAt, recommenderId, loginIpAddress)values(?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder holder = new GeneratedKeyHolder();
        this.jdbcTemplateM.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(
                    Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, user.getUserName().trim().toLowerCase());
                ps.setObject(2, user.getRealName());
                ps.setObject(3, user.getEmail());
                ps.setObject(4, user.getHashPasswd());
                ps.setObject(5, user.getIconUrl());
                ps.setObject(6, new Date());
                ps.setObject(7, user.getRecommenderId());
                ps.setObject(8, user.getLoginIpAddress());
                return ps;
            }
        }, holder);

        user.setUid(holder.getKey().longValue());

        //TODO:发确认邮件.
        throw new Exception("roll back");
    }

    @Override
    @UserTx
    public void updateUser(Long uid, Map<String, Object> args) throws Exception  {

        this.update("user", args, "uid", uid);

    }

    @Override
    @UserTx
    public void confirm(Long uid) throws Exception {
        String sql = "update user set confirmed=1, confirmAt=? where uid = ?";
        this.jdbcTemplateM.update(sql, new Date(), uid);
    }

    @Override
    public User findById(Long uid) throws EntityNotFoundException {
        String sql = "select * from user where uid = ?";
        List<User> rs =  this.jdbcTemplateS.query(sql, User_ROWMAPPER, uid);
        if(rs.size() == 0){
            throw new EntityNotFoundException("User", "findById", "Not Found", uid);
        }
        return rs.get(0);
    }

    @Override
    public User findByEmail(String email) throws EntityNotFoundException {
        String sql = "select * from user where email = ?";
        List<User> rs =  this.jdbcTemplateS.query(sql, User_ROWMAPPER, email.trim().toLowerCase());
        if(rs.size() == 0){
            return null;
        }
        return rs.get(0);
    }

    @Override
    public User findByUserName(String userName) throws EntityNotFoundException {
        String sql = "select * from user where userName = ?";
        List<User> rs =  this.jdbcTemplateS.query(sql, User_ROWMAPPER, userName.trim().toLowerCase());
        if(rs.size() == 0){
            return null;
        }
        return rs.get(0);
    }

    @Override
    protected String getServerName() {
        return SERVER_USER;
    }
}
