package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.*;
import java.util.*;

public interface NhanVienDAO extends JpaRepository<NhanVien, Integer> {
	NhanVien findByUser_UserID(Integer userID);
}
