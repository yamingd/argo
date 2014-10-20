package com.{{_company_}}.{{_project_}}.web.controllers.admin.{{_module_}};

import com.{{_company_}}.{{_project_}}.web.controllers.admin.AdminBaseController;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.web.JsonResponse;
import com.{{_company_}}.{{_project_}}.{{_module_}}.{{_entity_}};
import com.{{_company_}}.{{_project_}}.{{_module_}}.service.{{_entity_}}Service;
import com.{{_company_}}.{{_project_}}.ErrorCodes;
import com.{{_company_}}.{{_project_}}.web.controllers.admin.{{_module_}}.{{_entity_}}Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by $User on {{now.strftime('%Y-%m-%d %H:%M')}}.
 */

@Controller
@RequestMapping("/admin/{{_mvcurl_}}")
public class {{_entity_}}Controller extends AdminBaseController {
	
	@Autowired
    private {{_entity_}}Service {{_entityL_}}Service;
    
    @RequestMapping(value="list", method = RequestMethod.GET)
    public ModelAndView all(ModelAndView model, HttpServletRequest request, HttpServletResponse response){

        List<{{_entity_}}> list = {{_entityL_}}Service.findAll();
        model.setViewName("/admin/{{_mvcurl_}}/list");
        model.addObject("items", list);

        return model;
    }

	@RequestMapping(value="add", method = RequestMethod.GET)
    public ModelAndView add(ModelAndView model, HttpServletRequest request, HttpServletResponse response){

        model.setViewName("/admin/{{_mvcurl_}}/add");
        model.addObject("{{_entity_}}", new {{_entity_}}());

        return model;
    }

    @RequestMapping(value="view/{id}", method = RequestMethod.GET)
    public ModelAndView view(ModelAndView model, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response){

        try {
            {{_entity_}} item = {{_entityL_}}Service.findById(id);
            model.addObject("{{_entity_}}", item);
            model.setViewName("/admin/{{_mvcurl_}}/view");
        } catch (EntityNotFoundException e) {
            RedirectView view = new RedirectView("404");
            model.setView(view);
        }

        return model;
    }

    @RequestMapping(value="create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postCreate(@Valid {{_entity_}}Form form, BindingResult result, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        {{_entity_}} item = form.to();
        Long id = {{_entityL_}}Service.add(item);

        actResponse.add(item);

        return actResponse;
    }

    @RequestMapping(value="save/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postSave(@Valid {{_entity_}}Form form, BindingResult result, @PathVariable {{_tbi_.pkType}} id, JsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        {{_entity_}} item = form.to();
        item.setId(id);

        {{_entityL_}}Service.update(item);

        return actResponse;
    }

    @RequestMapping(value="remove/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public JsonResponse postRemove(@PathVariable Long id, JsonResponse actResponse) throws Exception {

        if (id != null) {
            {{_entityL_}}Service.remove(id);
        }

        return actResponse;
    }
    
}