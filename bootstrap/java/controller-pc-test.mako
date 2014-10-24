package com.{{_company_}}.{{_project_}}.testcases.controller.{{_module_}};

import com.{{_company_}}.{{_project_}}.{{_module_}}.{{_tbi_.entityName}};
import com.argo.core.web.BsonResponse;
import com.argo.core.web.JsonResponse;
import com.{{_company_}}.{{_project_}}.testcases.BaseTestCase;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 */
public class {{_tbi_.entityName}}ControllerTest extends BaseTestCase {

    @Test
    public void testAll() throws Exception {
        String url = "/{{_mvcurl_}}/list";
        String html = super.getUrlView(url, null);
        Assert.assertNotNull(html);
    }

    @Test
    public void testAdd() throws Exception {
        String url = "/{{_mvcurl_}}/add";
        String html = super.getUrlView(url, null);
        Assert.assertNotNull(html);

        url = "/{{_mvcurl_}}/create";
        Map<String, Object> map = Maps.newHashMap();
        //TODO:设置map属性
        JsonResponse jsonResponse = super.postForm(url, map);
        Assert.assertEquals(200L, jsonResponse.getCode() * 1L);
        System.out.println(jsonResponse);
    }

    @Test
    public void testAddWithError0() throws Exception {
        String url = "/{{_mvcurl_}}/add";
        String html = super.getUrlView(url, null);
        Assert.assertNotNull(html);

        url = "/{{_mvcurl_}}/create";
        Map<String, Object> map = Maps.newHashMap();
        //TODO:设置map属性
        JsonResponse jsonResponse = super.postForm(url, map);
        Assert.assertEquals(200L, jsonResponse.getCode() * 1L);
        System.out.println(jsonResponse);
    }

    @Test
    public void testAddWithError1() throws Exception {
        String url = "/{{_mvcurl_}}/add";
        String html = super.getUrlView(url, null);
        Assert.assertNotNull(html);

        url = "/{{_mvcurl_}}/create";
        Map<String, Object> map = Maps.newHashMap();
        //TODO: 设置map属性
        JsonResponse jsonResponse = super.postForm(url, map);
        Assert.assertEquals(200L, jsonResponse.getCode() * 1L);
        System.out.println(jsonResponse);
    }

    @Test
    public void testView() throws Exception {
        String url = "/{{_mvcurl_}}/view/3";
        String html = super.getUrlView(url, null);
        Assert.assertNotNull(html);

        url = "/{{_mvcurl_}}/save/3";
        Map<String, Object> map = Maps.newHashMap();
        //TODO:设置map属性
        JsonResponse jsonResponse = super.postForm(url, map);
        System.out.println(jsonResponse);

        url = "/{{_mvcurl_}}/remove/3";
        jsonResponse = super.postForm(url, map);
        System.out.println(jsonResponse);
    }
}
