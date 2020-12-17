package com.contacts.java_personal_contacts.contacts.repository;

import com.contacts.java_personal_contacts.contacts.models.Contact;
import com.contacts.java_personal_contacts.contacts.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContactRepository extends CrudRepository<Contact, Long> {
    Page<Contact> findByTagAndAuthor(String tag, User author, Pageable pageable);
    Page<Contact> findByNameAndAuthor(String name, User author, Pageable pageable);
    Page<Contact> findByAuthor(User author, Pageable pageable);
    Contact findById(Integer id);
}
