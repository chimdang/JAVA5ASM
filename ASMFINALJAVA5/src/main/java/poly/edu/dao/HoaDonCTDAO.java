package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.edu.entity.HoaDonCT;
import java.util.List;
// import poly.edu.dto.RevenueByDate; // Import DTO nếu bạn tạo

public interface HoaDonCTDAO extends JpaRepository<HoaDonCT, Integer> {

    @Query("SELECT SUM(ct.soLuong * ct.donGia) FROM HoaDonCT ct JOIN ct.hoaDon h WHERE h.trangThai <> 'Đã từ chối'")
    Double getTotalRevenue();

    // THÊM PHƯƠNG THỨC MỚI
    @Query("SELECT h.ngayMua as ngayMua, SUM(ct.soLuong * ct.donGia) as totalRevenue " +
           "FROM HoaDonCT ct JOIN ct.hoaDon h " +
           "WHERE h.trangThai <> 'Đã từ chối' " +
           "GROUP BY h.ngayMua " +
           "ORDER BY h.ngayMua ASC")
    List<Object[]> getRevenueByDate(); // Hoặc List<RevenueByDate> nếu dùng DTO
}