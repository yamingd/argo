package com.argo.acl.service.impl;

import com.argo.acl.SysRoleUser;
import com.argo.acl.service.SysRoleUserService;
import com.argo.acl.service.SysRoleUserTx;
import com.argo.core.annotation.Model;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.service.annotation.RmiService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@Model(SysRoleUser.class)
@RmiService(serviceInterface=SysRoleUserService.class)
public class SysRoleUserServiceImpl extends AclBaseServiceImpl implements SysRoleUserService{

    @Override
    public SysRoleUser findById(Long oid) throws EntityNotFoundException {
        return super.findById(oid);
    }

    public Long add(SysRoleUser entity) throws ServiceException {
        return super.add(entity);
    }

    public boolean update(SysRoleUser entity) throws ServiceException {
        return false;
    }

    @Override
    public boolean remove(Long oid) throws ServiceException {
        return false;
    }

    @Override
    @SysRoleUserTx
    public boolean remove(SysRoleUser item) {
        String sql = "delete from sys_role_user where roleId=? and userId=?";
        return this.jdbcTemplateM.update(sql, item.getRoleId(), item.getUserId()) > 0;
    }

    @Override
    @SysRoleUserTx
    public boolean removeByUser(Integer userId) {
        String sql = "delete from sys_role_user where userId=?";
        return this.jdbcTemplateM.update(sql, userId) > 0;
    }

    @Override
    public List<Integer> findByRole(Integer roleId) {
        String sql = "select userId from sys_role_user where roleId=? order by createAt desc";
        return this.jdbcTemplateS.queryForList(sql, Integer.class, roleId);
    }

    @Override
    @SysRoleUserTx
    public void addBatch(final Integer userId, final List<Integer> items) {
        String sql = "delete from sys_role_user where userId=?";
        this.jdbcTemplateM.update(sql, userId);

        sql = "insert into sys_role_user(userId, roleId, createAt)values(?,?,?)";

        int[] ret = this.jdbcTemplateM.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {

                ps.setObject(1, userId);
                ps.setObject(2, items.get(i));
                ps.setObject(3, new Date());

            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }

    @Override
    public List<Integer> findByUser(Integer id) {
        String sql = "select roleId from sys_role_user where userId=? order by createAt desc";
        return this.jdbcTemplateS.queryForList(sql, Integer.class, id);
    }
}