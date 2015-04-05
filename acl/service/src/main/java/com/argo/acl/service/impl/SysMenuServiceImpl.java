package com.argo.acl.service.impl;

import com.argo.acl.AclMappers;
import com.argo.acl.SysMenu;
import com.argo.acl.service.SysMenuService;
import com.argo.acl.service.SysRoleTx;
import com.argo.core.annotation.Model;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.service.annotation.RmiService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by $User on 2014-10-08 09:58.
 */
@Model(SysMenu.class)
@RmiService(serviceInterface=SysMenuService.class)
public class SysMenuServiceImpl extends AclBaseServiceImpl implements SysMenuService{

    @Override
    public SysMenu findById(Long oid) throws EntityNotFoundException {
        return super.findById(oid);
    }

    @Override
    @SysRoleTx
    public <T> Long add(T entity) throws ServiceException {
        SysMenu menu = (SysMenu)entity;
        Long id = super.add(menu);
        menu.setId(id.intValue());
        return id;
    }

    @Override
    @SysRoleTx
    public <T> boolean update(T entity) throws ServiceException {
        return super.updateEntity(entity);
    }

    @Override
    @SysRoleTx
    public boolean remove(Long oid) throws ServiceException {
        return super.remove(oid);
    }

    @Override
    public List<SysMenu> findAll() {
        String sql = "select * from sys_menu where ifAdmin=1 order by parentId, orderNo";
        List<SysMenu> list =  this.jdbcTemplateS.query(sql, AclMappers.SysMenu_ROWMAPPER);
        buildMenuTree(list);
        return list;
    }

    @Override
    public List<SysMenu> findByUserId(Integer sysUserId) {
        String sql = "select distinct t.* from sys_menu t, sys_role_user t1, sys_role_menu t2 where t.id = t2.menuId and t2.roleId = t1.roleId and t1.userId = ? order by t.parentId, t.orderNo";
        List<SysMenu> list =  this.jdbcTemplateS.query(sql, AclMappers.SysMenu_ROWMAPPER, sysUserId);
        buildMenuTree(list);
        return list;
    }

    private void buildMenuTree(List<SysMenu> menus) {
        for (int i = 0; i < menus.size(); i++) {
            SysMenu menu = menus.get(i);
            if (menu.getSubMenus() == null){
                menu.setSubMenus(new ArrayList<SysMenu>());
            }
            for (int j = 1; j < menus.size(); j++) {
                SysMenu item = menus.get(j);
                if (item.getParentId().equals(menu.getId())){
                    menu.getSubMenus().add(item);
                    menus.remove(j);
                    j--;
                }
            }
        }
    }
}