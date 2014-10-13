package com.argo.equation.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Yaming on 2014/10/13.
 */
@Controller
@RequestMapping("/sample")
public class SampleController extends BaseController {

    @RequestMapping(value = "/index", method= RequestMethod.GET)
    public String index(){
        return "sample/index";
    }

    @RequestMapping(value = "/buttons", method= RequestMethod.GET)
    public String buttons(){
        return "sample/buttons";
    }

    @RequestMapping(value = "/calendar", method= RequestMethod.GET)
    public String calendar(){
        return "sample/calendar";
    }

    @RequestMapping(value = "/editors", method= RequestMethod.GET)
    public String editors(){
        return "sample/editors";
    }

    @RequestMapping(value = "/forms", method= RequestMethod.GET)
    public String forms(){
        return "sample/forms";
    }

    @RequestMapping(value = "/login", method= RequestMethod.GET)
    public String login(){
        return "sample/login";
    }

    @RequestMapping(value = "/signup", method= RequestMethod.GET)
    public String signup(){
        return "sample/signup";
    }

    @RequestMapping(value = "/stats", method= RequestMethod.GET)
    public String stats(){
        return "sample/stats";
    }

    @RequestMapping(value = "/tables", method= RequestMethod.GET)
    public String tables(){
        return "sample/tables";
    }
}
