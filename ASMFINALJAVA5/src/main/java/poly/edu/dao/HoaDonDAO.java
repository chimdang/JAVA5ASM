package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.edu.entity.HoaDon;
import poly.edu.entity.KhachHang; // Thêm import này
import java.util.List; // Thêm import này

public interface HoaDonDAO extends JpaRepository<HoaDon, Integer> {
    
    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangThai <> 'Đã từ chối'")
    long countTotalOrders();
    
    // Thêm phương thức này để tìm tất cả hóa đơn của một khách hàng, sắp xếp theo ngày mua mới nhất
    List<HoaDon> findByKhachHangOrderByNgayMuaDesc(KhachHang khachHang);
}