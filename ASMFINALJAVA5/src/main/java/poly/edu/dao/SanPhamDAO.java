package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.SanPham;
import java.util.List;

public interface SanPhamDAO extends JpaRepository<SanPham, Integer> {
    List<SanPham> findAllByOrderByMaSPAsc();
}
