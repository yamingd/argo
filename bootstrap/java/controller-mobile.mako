package com.{{_company_}}.{{_project_}}.web.controllers.mobile.{{_module_}};

import com.{{_company_}}.{{_project_}}.web.controllers.mobile.MobileBaseController;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.web.BsonResponse;
import com.argo.core.web.Enums;
import com.{{_company_}}.{{_project_}}.{{_module_}}.{{_entity_}};
import com.{{_company_}}.{{_project_}}.{{_module_}}.service.{{_entity_}}Service;
import com.{{_company_}}.{{_project_}}.ErrorCodes;
import com.{{_company_}}.{{_project_}}.web.controllers.mobile.{{_module_}}.{{_entity_}}Form;
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
@RequestMapping("/mobile/{{_mvcurl_}}")
public class {{_entity_}}Controller extends MobileBaseController {
	
	@Autowired
    private {{_entity_}}Service {{_entityL_}}Service;
    
    @RequestMapping(value="all/{page}", method=RequestMethod.GET, produces = Enums.APPLICATION_BJSON_VALUE)
    @ResponseBody
    public BsonResponse all(BsonResponse actResponse, @PathVariable Integer page) throws Exception {
        List<{{_entity_}}> list = {{_entityL_}}Service.findAll();
        actResponse.addAll(list);
        return actResponse;
    }

    @RequestMapping(value="view/{id}", method=RequestMethod.GET, produces = Enums.APPLICATION_BJSON_VALUE)
    @ResponseBody
    public BsonResponse view(BsonResponse actResponse, @PathVariable Long id){

        try {
            {{_entity_}} item = {{_entityL_}}Service.findById(id);
            actResponse.add(item);
        } catch (Exception e) {
            actResponse.setCode(ErrorCodes.RECORD_NOT_FOUND);
            actResponse.setMsg("");
        }

        return actResponse;
    }

    @RequestMapping(value="create", method = RequestMethod.POST, produces = Enums.APPLICATION_BJSON_VALUE)
    @ResponseBody
    public BsonResponse postCreate(@Valid {{_entity_}}Form form, BindingResult result, BsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        {{_entity_}} item = form.to();
        Long id = {{_entityL_}}Service.add(item);

        actResponse.add(item);

        return actResponse;
    }

    @RequestMapping(value="save/{id}", method = RequestMethod.POST, produces = Enums.APPLICATION_BJSON_VALUE)
    @ResponseBody
    public BsonResponse postSave(@Valid {{_entity_}}Form form, BindingResult result, @PathVariable {{_tbi_.pkType}} id, BsonResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse;
        }

        {{_entity_}} item = form.to();
        item.setId(id);

        {{_entityL_}}Service.update(item);

        return actResponse;
    }

    @RequestMapping(value="remove/{id}", method = RequestMethod.POST, produces = Enums.APPLICATION_BJSON_VALUE)
    @ResponseBody
    public BsonResponse postRemove(@PathVariable Long id, BsonResponse actResponse) throws Exception {

        if (id != null) {
            {{_entityL_}}Service.remove(id);
        }

        return actResponse;
    }
    
}