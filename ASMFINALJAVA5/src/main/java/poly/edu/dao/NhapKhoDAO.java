package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.NhapKho;
import java.util.List;

public interface NhapKhoDAO extends JpaRepository<NhapKho, Integer> {
	List<NhapKho> findTop100ByOrderByNgayNKDesc();
}
