package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.edu.entity.HoaDon;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface HoaDonDAO extends JpaRepository<HoaDon, Integer> {
    List<HoaDon> findByTrangThaiOrderByNgayMuaDesc(String trangThai);
    @Modifying
    @Transactional
    @Query("UPDATE HoaDon h SET h.trangThai = :st WHERE h.maHD = :id")
    int updateStatus(@Param("id") Integer maHD, @Param("st") String trangThai);
}
