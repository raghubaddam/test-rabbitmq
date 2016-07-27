package com.rabbitmq.listener;

import com.rabbitmq.domain.Person;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Person.class)
class PersonControllerEventHandlerAnnotation {

    @HandleBeforeCreate
    protected void onAfterCreate(Person person) {
      System.out.print(person);
    }

    @HandleBeforeSave
    protected void onAfterSave(Person person) {

        System.out.print(person);

    }

}

