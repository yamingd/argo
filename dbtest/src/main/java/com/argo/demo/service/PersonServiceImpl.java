package com.argo.demo.service;

import com.argo.core.exception.EntityNotFoundException;
import com.argo.core.exception.ServiceException;
import com.argo.db.template.ServiceMSTemplate;
import com.argo.demo.Person;
import com.argo.service.annotation.RmiService;
import com.google.common.collect.Maps;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yaming_deng on 2014/9/30.
 */
@RmiService(serviceInterface = PersonService.class)
public class PersonServiceImpl extends ServiceMSTemplate<Person> implements PersonService {

    protected static final RowMapper<Person> Person_ROWMAPPER = new BeanPropertyRowMapper<Person>(
            Person.class);

    @Override
    protected String getServerName() {
        return "person";
    }

    @Override
    @PersonTx
    public boolean add(final Person user) throws ServiceException {

        final String sql = "insert into person(name, createAt)values(?, ?)";

        KeyHolder holder = new GeneratedKeyHolder();
        this.jdbcTemplateM.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(
                    Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql,
                        Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, user.getName().trim().toLowerCase());
                ps.setObject(2, new Date());
                return ps;
            }
        }, holder);

        user.setId(holder.getKey().longValue());

        System.out.println("add Person done. id=" + user.getId());

        return true;
    }

    @Override
    public Person findById(Long oid) throws EntityNotFoundException {
        String sql = "select * from person where id = ?";
        List<Person> rs =  this.jdbcTemplateS.query(sql, Person_ROWMAPPER, oid);
        if(rs.size() == 0){
            throw new EntityNotFoundException("Person", "findById", "Not Found", oid);
        }
        return rs.get(0);
    }

    @Override
    public boolean update(Person person) throws ServiceException {
        Map<String, Object> args = Maps.newHashMap();
        args.put("name", person.getName());
        int ret = this.update("person", args, "id", person.getId());
        return ret > 0;
    }

    @Override
    public boolean remove(Class<Person> clazz, Long oid) throws ServiceException {
        String sql = "delete from person where id = ?";
        int ret = this.jdbcTemplateM.update(sql, oid);
        return ret > 0;
    }
}
