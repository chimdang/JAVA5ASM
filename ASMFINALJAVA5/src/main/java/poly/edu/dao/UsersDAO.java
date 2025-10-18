package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.*;
import java.util.Optional;
public interface UsersDAO extends JpaRepository<Users, Integer> {
	Users findByMail(String mail);
	Optional<Users> findByMailAndPass(String mail, String pass);
}

