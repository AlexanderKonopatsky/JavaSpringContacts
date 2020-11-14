package com.contacts.java_personal_contacts.contacts.repository;

import com.contacts.java_personal_contacts.contacts.models.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {

    List<Contact> findByTag(String tag);

}
