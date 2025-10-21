package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.DanhMuc;

public interface DanhMucDAO extends JpaRepository<DanhMuc, Integer> {
	
	
}