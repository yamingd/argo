package com.argo.acl.service.impl;

import com.argo.acl.SysRoleMenu;
import com.argo.acl.SysRoleUser;
import com.argo.acl.service.SysRoleMenuService;
import com.argo.acl.service.SysRoleMenuTx;
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
@Model(SysRoleMenu.class)
@RmiService(serviceInterface=SysRoleMenuService.class)
public class SysRoleMenuServiceImpl extends AclBaseServiceImpl implements SysRoleMenuService {

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
    @SysRoleMenuTx
    public boolean removeByRole(Integer roleId) {
        String sql = "delete from sys_role_menu where roleId=?";
        int ret = this.jdbcTemplateM.update(sql, roleId);
        return ret > 0;
    }

    @Override
    @SysRoleMenuTx
    public boolean remove(SysRoleMenu item) {
        String sql = "delete from sys_role_menu where roleId=? and menuId=?";
        int ret = this.jdbcTemplateM.update(sql, item.getRoleId(), item.getMenuId());
        return ret > 0;
    }

    @Override
    public List<Integer> findByRole(Long id) {
        String sql = "select menuId from sys_role_menu where roleId=?";
        List<Integer> list = this.jdbcTemplateS.queryForList(sql, Integer.class, id);
        return list;
    }

    @Override
    @SysRoleMenuTx
    public boolean addBatch(final Integer roleId, final List<Integer> menuIds) {
        String sql = "delete from sys_role_menu where roleId=?";
        this.jdbcTemplateM.update(sql, roleId);

        sql = "insert into sys_role_menu(menuId, roleId, createAt)values(?,?,?)";

        int[] ret = this.jdbcTemplateM.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, menuIds.get(i));
                ps.setObject(2, roleId);
                ps.setObject(3, new Date());

            }

            @Override
            public int getBatchSize() {
                return menuIds.size();
            }
        });

        return ret.length > 0;
    }
}