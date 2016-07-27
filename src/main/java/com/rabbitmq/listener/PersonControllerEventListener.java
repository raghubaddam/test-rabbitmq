package com.rabbitmq.listener;

import com.rabbitmq.domain.Person;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

@Component
class PersonControllerEventListener extends AbstractRepositoryEventListener<Person> {

    protected void onAfterCreate(Person person) {
      System.out.print(person);
    }

    protected void onAfterSave(Person person) {

        System.out.print(person);

    }

}

