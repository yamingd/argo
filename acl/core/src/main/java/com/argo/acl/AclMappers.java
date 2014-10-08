package com.argo.acl;


import com.argo.acl.SysResource;

import com.argo.acl.SysRole;

import com.argo.acl.SysRoleResource;

import com.argo.acl.SysRoleUser;


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

    
}