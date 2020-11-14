package com.contacts.java_personal_contacts.contacts.repository;

import com.contacts.java_personal_contacts.contacts.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}
