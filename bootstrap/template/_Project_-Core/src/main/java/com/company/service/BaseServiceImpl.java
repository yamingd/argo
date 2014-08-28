package com.company.service;

import com.argo.core.base.BaseBean;
import com.argo.db.JdbcConfig;
import com.argo.db.MasterSlaveJdbcTemplate;
import com.argo.db.template.ServiceMSTemplate;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 14-8-19.
 */
public abstract class BaseServiceImpl extends ServiceMSTemplate{

}
