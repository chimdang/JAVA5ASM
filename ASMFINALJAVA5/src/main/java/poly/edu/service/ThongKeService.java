package poly.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.edu.dao.*; // Import các DAO cần thiết

import java.util.List;

@Service
public class ThongKeService {

    // Các DAO đã có
    @Autowired
    private HoaDonCTDAO hoaDonCTDAO;

    @Autowired
    private SanPhamDAO sanPhamDAO;

    @Autowired
    private KhachHangDAO khachHangDAO;

    // Các DAO mới cần thêm
    @Autowired
    private NhanVienDAO nhanVienDAO;

    @Autowired
    private UsersDAO usersDAO;

    @Autowired
    private DanhMucDAO danhMucDAO;

    /**
     * Lấy tổng doanh thu từ các hóa đơn không bị từ chối.
     * @return Tổng doanh thu.
     */
    public Double getTotalRevenue() {
        Double revenue = hoaDonCTDAO.getTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }

    /**
     * Lấy tổng số lượng sản phẩm.
     * @return Tổng sản phẩm.
     */
    public long getTotalProducts() {
        return sanPhamDAO.count();
    }

    /**
     * Lấy tổng số lượng khách hàng.
     * (Thường được dùng cho thẻ "Tổng người dùng" cũ).
     * @return Tổng khách hàng.
     */
    public long getTotalUsers() {
        return khachHangDAO.count();
    }

    /**
     * Lấy dữ liệu doanh thu theo từng ngày để vẽ biểu đồ.
     * @return Danh sách các mảng Object, mỗi mảng chứa [Ngày, DoanhThu].
     */
    public List<Object[]> getRevenueByDate() {
        return hoaDonCTDAO.getRevenueByDate();
    }

    // ===============================================
    // CÁC PHƯƠNG THỨC MỚI ĐƯỢC BỔ SUNG
    // ===============================================

    /**
     * Lấy tổng số lượng nhân viên.
     * @return Tổng nhân viên.
     */
    public long getTotalEmployees() {
        return nhanVienDAO.count();
    }

    /**
     * Lấy tổng số lượng tài khoản trong bảng Users.
     * @return Tổng tài khoản.
     */
    public long getTotalAccounts() {
        return usersDAO.count();
    }

    /**
     * Lấy tổng số lượng danh mục sản phẩm.
     * @return Tổng danh mục.
     */
    public long getTotalCategories() {
        return danhMucDAO.count();
    }
}