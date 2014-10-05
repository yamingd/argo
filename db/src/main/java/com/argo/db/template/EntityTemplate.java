package com.argo.db.template;

import com.argo.core.annotation.EntityDef;
import com.argo.core.annotation.PK;
import com.argo.core.base.BaseEntity;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Yaming
 * Date: 2014/10/5
 * Time: 18:58
 */
public class EntityTemplate<T extends BaseEntity> {

    private Class<T> clazz;
    private EntityDef def;
    private Map<String, Field> mapping;
    private List<PK> pks;
    private Collection<Field> fields;
    private boolean hasIfDeleted = false;

    public EntityTemplate(Class<T> clazz) {
        this.clazz = clazz;
        this.def = clazz.getAnnotation(EntityDef.class);
        this.mapping = new HashMap<String, Field>();
        this.pks = new ArrayList<PK>();

        this.popupFields(this.clazz);
        this.fields = this.mapping.values();
        this.findPKs();
    }

    private void popupFields(Class clazz){
        Field[] fs = clazz.getDeclaredFields();
        for (Field f : fs){
            if (mapping.containsKey(f.getName())){
                continue;
            }
            this.mapping.put(f.getName(), f);
        }
        Class c0 = clazz.getSuperclass();
        if (c0 != null){
            popupFields(c0);
        }
    }

    private void findPKs(){
        for (Field field : this.fields){
            field.setAccessible(true);
            PK pk = field.getAnnotation(PK.class);
            if (pk != null){
                this.pks.add(pk);
            }
            if (field.getName().equalsIgnoreCase("ifDeleted")){
                hasIfDeleted = true;
            }
        }
    }

    public EntityDef getDef() {
        return def;
    }

    public String getTable(){
        return def.table();
    }

    public Collection<Field> getFields() {
        return fields;
    }

    public String getPk(){
        if (pks.size() == 0){
            return "";
        }
        return pks.get(0).value();
    }

    public boolean isHasIfDeleted() {
        return hasIfDeleted;
    }

    public boolean isMultiPK(){
        return this.pks.size() > 1;
    }
}
