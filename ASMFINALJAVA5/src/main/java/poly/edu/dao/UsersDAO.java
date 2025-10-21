package poly.edu.dao;


//Thêm các import này
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//---

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.*;
import java.util.*;


public interface UsersDAO extends JpaRepository<Users, Integer> {
	Users findByMail(String mail);
	List<Users> findByMailContaining(String keyword);
	@Query("SELECT u FROM Users u " +
		       "WHERE u.mail LIKE %:keyword% " +
		       "AND ( :roleFilter = '' OR " + // Nếu roleFilter rỗng thì lấy tất cả
		       "      (:roleFilter = 'NV' AND EXISTS (SELECT nv FROM NhanVien nv WHERE nv.user = u)) OR " + // Nếu 'NV', check Bảng NhanVien
		       "      (:roleFilter = 'KH' AND EXISTS (SELECT kh FROM KhachHang kh WHERE kh.user = u)) )") // Nếu 'KH', check Bảng KhachHang
	Page<Users> findByFilter(@Param("keyword") String keyword, 
		                         @Param("roleFilter") String roleFilter, 
		                         Pageable pageable);

}

