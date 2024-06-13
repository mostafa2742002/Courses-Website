package com.web.ecommerce.user;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository  extends MongoRepository <User, String>{
    
    User findByEmail(String email);
    User findById(int id);
    Optional<User> findByName(String name);
}
