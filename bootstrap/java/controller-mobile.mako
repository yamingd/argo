package com.{{_company_}}.{{_project_}}.web.controllers.mobile.{{_module_}};

import com.{{_company_}}.{{_project_}}.web.controllers.mobile.MobileBaseController;
import com.argo.core.base.BaseException;
import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.protobuf.ProtobufMessage;
import com.argo.core.protobuf.ProtobufResponse;
import com.argo.core.web.Enums;
import com.{{_company_}}.{{_project_}}.{{_module_}}.{{_entity_}};
import com.{{_company_}}.{{_project_}}.protobuf.{{_module_}}.P{{_entity_}};
import com.{{_company_}}.{{_project_}}.protobuf.{{_module_}}.P{{_entity_}}Wrapper;
import com.{{_company_}}.{{_project_}}.{{_module_}}.service.{{_entity_}}Service;
import com.{{_company_}}.{{_project_}}.ErrorCodes;

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
@RequestMapping("/m/{{_mvcurl_}}")
public class Mobile{{_entity_}}Controller extends MobileBaseController {
    
    @Autowired
    private {{_entity_}}Service {{_entityL_}}Service;
    
    @RequestMapping(value="all/{page}", method=RequestMethod.GET, produces = Enums.PROTOBUF_VALUE)
    @ResponseBody
    public ProtobufMessage all(ProtobufResponse actResponse, @PathVariable Integer page) throws Exception {
        List<{{_entity_}}> list = {{_entityL_}}Service.findAll();
        for({{_entity_}} item : list) {
            //convert item to P{{_entity_}}
            P{{_entity_}} msg = P{{_entity_}}Wrapper.fromEntity(item);
            actResponse.getBuilder().addData(msg.toByteString());
        }
        return actResponse.build();
    }

    @RequestMapping(value="view/{id}", method=RequestMethod.GET, produces = Enums.PROTOBUF_VALUE)
    @ResponseBody
    public ProtobufMessage view(ProtobufResponse actResponse, @PathVariable Long id){

        try {
            {{_entity_}} item = {{_entityL_}}Service.findById(id);
            //TODO: convert item to P{{_entity_}}
            P{{_entity_}} msg = P{{_entity_}}Wrapper.fromEntity(item);
            actResponse.getBuilder().addData(msg.toByteString());
        } catch (BaseException e) {
            logger.error(e.getMessage(), e);
            actResponse.getBuilder().setCode(e.getErrcode()).setMsg(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            actResponse.getBuilder().setCode(500).setMsg(e.getMessage());
        }

        return actResponse.build();
    }

    @RequestMapping(value="create", method = RequestMethod.POST, produces = Enums.PROTOBUF_VALUE)
    @ResponseBody
    public ProtobufMessage postCreate(@Valid Mobile{{_entity_}}Form form, BindingResult result, ProtobufResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse.build();
        }

        {{_entity_}} item = form.to();
        Long id = {{_entityL_}}Service.add(item);

        P{{_entity_}} msg = P{{_entity_}}Wrapper.fromEntity(item);
        actResponse.getBuilder().addData(msg.toByteString());

        return actResponse.build();
    }

    @RequestMapping(value="save/{id}", method = RequestMethod.POST, produces = Enums.PROTOBUF_VALUE)
    @ResponseBody
    public ProtobufMessage postSave(@Valid Mobile{{_entity_}}Form form, BindingResult result, @PathVariable {{_tbi_.pkType}} id, ProtobufResponse actResponse) throws Exception {

        if (result.hasErrors()){
            this.wrapError(result, actResponse);
            return actResponse.build();
        }

        {{_entity_}} item = form.to();
        item.setId(id);

        {{_entityL_}}Service.update(item);

        P{{_entity_}} msg = P{{_entity_}}Wrapper.fromEntity(item);
        actResponse.getBuilder().addData(msg.toByteString());
        
        return actResponse.build();
    }

    @RequestMapping(value="remove/{id}", method = RequestMethod.POST, produces = Enums.PROTOBUF_VALUE)
    @ResponseBody
    public ProtobufMessage postRemove(@PathVariable Long id, ProtobufResponse actResponse) throws Exception {

        if (id != null) {
            {{_entityL_}}Service.remove(id);
        }

        return actResponse.build();
    }
    
}