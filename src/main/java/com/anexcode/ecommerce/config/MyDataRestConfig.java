package com.anexcode.ecommerce.config;

import com.anexcode.ecommerce.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    @Value("${allowed.origins}")
    private String[] allowedOrigins;
    private EntityManager nttManager;

    @Autowired
    public MyDataRestConfig(EntityManager entityManager){
        this.nttManager = entityManager;
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);

        //cors mapping
        cors.addMapping(config.getBasePath() + "/**").allowedOrigins(allowedOrigins);

        HttpMethod[] theUnsupportedActions = {HttpMethod.PUT,HttpMethod.POST,
                                              HttpMethod.DELETE,HttpMethod.PATCH};

        //Disable Http Methods
        disableHttpMethods(Product.class, config, theUnsupportedActions);
        disableHttpMethods(ProductCategory.class, config, theUnsupportedActions);
        disableHttpMethods(Country.class, config, theUnsupportedActions);
        disableHttpMethods(State.class, config, theUnsupportedActions);
        disableHttpMethods(Order.class, config, theUnsupportedActions);

        // call  internal helper method for expose ids in json response
        exposeIds(config);
        //expose_v2(config);

    }

    private void disableHttpMethods(Class theClass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {
        config.getExposureConfiguration()
                .forDomainType(theClass)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        //expose entity ids
        //

        //get a list of all entity classes from the entity manager
        Set<EntityType<?>> entities = nttManager.getMetamodel().getEntities();

        //create an array of the entity types
        List<Class> entityClasses = new ArrayList<>();

        //get the entity types for the entities
        for (EntityType nttType : entities){
            entityClasses.add(nttType.getJavaType());
        }

        //expose the entity ids for the array of entity types
        Class[] types = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(types);
    }
    private void expose_v2(RepositoryRestConfiguration config){
        Class[] classes = nttManager.getMetamodel()
                .getEntities().stream().map(jakarta.persistence.metamodel.Type::getJavaType).toArray(Class[]::new);
        config.exposeIdsFor(classes);
    }
}
