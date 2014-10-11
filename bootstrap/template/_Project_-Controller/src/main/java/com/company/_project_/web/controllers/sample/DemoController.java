package com.company._project_.web.controllers.sample;

import com.argo.core.web.BsonResponse;
import com.argo.core.web.Enums;
import com.argo.core.web.JsonResponse;
import com.company._project_.web.controllers.BaseController;
import com.company._project_.web.controllers.ErrorCodes;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.Date;

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

    /**
     * 接收提交Form. 并进行数据校验.
     * 访问URL：/demo/save
     * @param form 封装提交的数据
     * @param result 数据检验结果
     * @return
     * @throws Exception
     */
    @RequestMapping(value="save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postSave(@Valid DemoForm form, BindingResult result, JsonResponse actResponse) throws Exception {
        //access url: /demo/post1
        //

        if (result.hasErrors()){
            actResponse.setCode(ErrorCodes.FORM_DATA_INVALID);
            return actResponse;
        }

        String val = this.request.getHeader("header-name");
        this.response.addHeader("header-name", "value");

        return actResponse;
    }

    /**
     * 接收提交Form. 并进行数据校验.
     * 访问URL：/demo/save2
     * @param form
     * @param result
     * @param model 存放校验结果并输出到模板
     * @return
     * @throws Exception
     */
    @RequestMapping(value="save2", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView postSave2(@Valid DemoForm form, BindingResult result, ModelAndView model) throws Exception {

        if (result.hasErrors()){
            model.addObject("error", true);
            model.setViewName("demo/view-error");
            return model;
        }

        String val = this.request.getHeader("header-name");
        this.response.addHeader("header-name", "value");

        model.addObject("uid", 100L);
        model.setViewName("demo/view-ok");

        return model;
    }

    /**
     * 接收简单的提交. 参数少于3个.
     * 访问URL: /demo/save3
     * @param uname
     * @param passwd
     * @param actResponse
     * @return
     */
    @RequestMapping(value="save3", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postSave3(
            @RequestParam(value = "uname", defaultValue = "") String uname,
            @RequestParam(value = "passwd", defaultValue = "") String passwd, JsonResponse actResponse){

        return actResponse;
    }

    @RequestMapping(value="save4", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView postSave4(
            @RequestParam(value = "uname", defaultValue = "") String uname,
            @RequestParam(value = "passwd", defaultValue = "") String passwd, ModelAndView model){

        return model;
    }

    @RequestMapping(value="get1", method = RequestMethod.GET)
    public ModelAndView getView1(ModelAndView model){
        //access url: /demo/get1
        model.setViewName("demo/view-name");
        return model;
    }

    @RequestMapping(value="get2", method = RequestMethod.GET)
    public String getView2AndRedirect(ModelAndView model){
        //access url: /demo/get2  and redirect
        RedirectView view = new RedirectView("/home");
        model.setView(view);
        return "redirect:/home";
    }

    @RequestMapping(value="get3", method = RequestMethod.GET)
    public ModelAndView getView3AndRedirect(ModelAndView model){
        //access url: /demo/get2  and redirect
        RedirectView view = new RedirectView("/home");
        model.setView(view);
        return model;
    }

    @RequestMapping(value="get/{userid}", method=RequestMethod.GET)
    public ModelAndView getView3(@PathVariable String userId, ModelAndView model) {
        //access url: /demo/get/{userid}
        // implementation omitted...
        model.setViewName("demo/view-name");
        return model;
    }

    /**
     * 读取记录信息 <br />
     * 返回文本JSON.
     * 访问URL: /demo/get/{userid}
     * @param userId
     * @return ActResponse
     */
    @RequestMapping(value="get/{userid}", method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse getJson(@PathVariable String userId, JsonResponse actResponse) {
        DemoJson item = new DemoJson();
        item.setId(1L);
        item.setName("demo");
        item.setCreateAt(new Date());

        actResponse.getData().add(item);

        return actResponse;
    }

    /**
     * 读取记录信息 <br />
     * 返回二进制JSON.
     * 访问URL: /demo/get2/{userid}
     * @param userId
     * @return DemoJson
     */
    @RequestMapping(value="get2/{userid}", method=RequestMethod.GET, produces = Enums.APPLICATION_BJSON_VALUE)
    @ResponseBody
    public BsonResponse getJson2(@PathVariable String userId, BsonResponse actResponse) {

        DemoJson item = new DemoJson();
        item.setId(1L);
        item.setName("demo");
        item.setCreateAt(new Date());

        try {
            actResponse.add(item);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return actResponse;
    }

    @RequestMapping(value="owners/{ownerId}", method=RequestMethod.GET)
    public String findOwner(@PathVariable String ownerId, Model model) {
        //access url: /demo/owners/{ownerId}
        model.addAttribute("owner", null);
        return "view-name";
    }


}
