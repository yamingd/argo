package com.{{_company_}}.{{_project_}}.testcases.service.{{_module_}};

import com.{{_company_}}.{{_project_}}.{{_module_}}.{{_tbi_.entityName}};
import com.{{_company_}}.{{_project_}}.{{_module_}}.service.{{_tbi_.entityName}}Service;
import com.{{_company_}}.{{_project_}}.testcases.BaseTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 */
public class {{_tbi_.entityName}}ServiceTest extends BaseTestCase {

    @Test
    public void testAdd() throws Exception {
        {{_tbi_.entityName}}Service service = context.getBean({{_tbi_.entityName}}Service.class);
        {{_tbi_.entityName}} item = new {{_tbi_.entityName}}();
        //TODO: 设置属性值
        Long id = service.add(item);
        Assert.assertNotNull(id);
        Assert.assertTrue( id > 0);
    }

    @Test
    public void testUpdate() throws Exception {
        {{_tbi_.entityName}}Service service = context.getBean({{_tbi_.entityName}}Service.class);
        {{_tbi_.entityName}} item = new {{_tbi_.entityName}}();
        //TODO: 设置属性值
        boolean flag = service.update(item);
        Assert.assertTrue(flag);
    }

    @Test
    public void testRemove() throws Exception {
        {{_tbi_.entityName}}Service service = context.getBean({{_tbi_.entityName}}Service.class);
        boolean flag = service.remove(1L);
        Assert.assertTrue(flag);

        flag = service.remove(1L);
        Assert.assertFalse(flag);
    }

    @Test
    public void testFindAll() throws Exception {
        {{_tbi_.entityName}}Service service = context.getBean({{_tbi_.entityName}}Service.class);
        List<{{_tbi_.entityName}}> list = service.findAll();
        Assert.assertEquals(1L, list.size());
    }
}
