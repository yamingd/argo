package com.argo.acl;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

/**
 * Created by $User on 2014-10-08 09:58.
 */
public class AclMappers {
	
	

    public static final RowMapper<SysResource> SysResource_ROWMAPPER = new BeanPropertyRowMapper<SysResource>(
            SysResource.class);

    

    public static final RowMapper<SysRole> SysRole_ROWMAPPER = new BeanPropertyRowMapper<SysRole>(
            SysRole.class);

    

    public static final RowMapper<SysRoleResource> SysRoleResource_ROWMAPPER = new BeanPropertyRowMapper<SysRoleResource>(
            SysRoleResource.class);

    

    public static final RowMapper<SysRoleUser> SysRoleUser_ROWMAPPER = new BeanPropertyRowMapper<SysRoleUser>(
            SysRoleUser.class);


    public static final RowMapper<SysMenu> SysMenu_ROWMAPPER = new BeanPropertyRowMapper<SysMenu>(
            SysMenu.class);

    public static final RowMapper<SysUser> SysUser_ROWMAPPER = new BeanPropertyRowMapper<SysUser>(
            SysUser.class);
}