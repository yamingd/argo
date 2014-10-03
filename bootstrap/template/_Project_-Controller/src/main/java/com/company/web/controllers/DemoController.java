package com.company.web.controllers;

import com.company.web.controllers.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Created by yaming_deng on 14-8-20.
 */
@Controller
@RequestMapping("demo")
public class DemoController extends BaseController {

    @Override
    public boolean needLogin() {
        return false;
    }

    @RequestMapping(value="post1", method = RequestMethod.POST)
    @ResponseBody
    public ActResponse post1(@Valid DemoForm form, BindingResult result) throws Exception {
        //access url: /demo/post1
        //
        return okResp;
    }

    @RequestMapping(value="post2", method = RequestMethod.POST)
    @ResponseBody
    public ActResponse post2(
            @RequestParam(value = "uname", defaultValue = "") String uname,
            @RequestParam(value = "passwd", defaultValue = "") String passwd){

        return okResp;
    }

    @RequestMapping(value="get1", method = RequestMethod.GET)
    public String get(){
        //access url: /demo/get1
        return "view-name";
    }

    @RequestMapping(value="get2", method = RequestMethod.GET)
    public String get(){
        //access url: /demo/get2
        return "redirect:/home";
    }

    @RequestMapping("get/{userid}", method=RequestMethod.GET)
    public String getUser(@PathVariable String userId) {
        //access url: /demo/get/{userid}
        // implementation omitted...
    }

    @RequestMapping("owners/{ownerId}", method=RequestMethod.GET)
    public String findOwner(@PathVariable String ownerId, Model model) {
        //access url: /demo/owners/{ownerId}
        model.addAttribute("owner", null);
        return "view-name";
    }
}
