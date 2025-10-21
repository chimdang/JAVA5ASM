package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import poly.edu.entity.*;

public interface SanPhamDAO extends JpaRepository<SanPham, Integer> {
	  // Tìm kiếm theo danh mục
    @Query("SELECT sp FROM SanPham sp WHERE sp.danhMuc.maDM = :maDM")
    List<SanPham> findByDanhMucMaDM(@Param("maDM") Integer maDM);
    
    // Tìm kiếm theo tên hoặc mô tả (GIỮ NGUYÊN 2 PARAMETERS ĐỂ TƯƠNG THÍCH VỚI CONTROLLER CŨ)
    @Query("SELECT sp FROM SanPham sp WHERE " +
           "LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :tenSP, '%')) OR " +
           "LOWER(sp.moTa) LIKE LOWER(CONCAT('%', :moTa, '%'))")
    List<SanPham> findByTenSPContainingIgnoreCaseOrMoTaContainingIgnoreCase(
        @Param("tenSP") String tenSP, 
        @Param("moTa") String moTa
    );
    
    // Tìm kiếm theo tên sản phẩm
    @Query("SELECT sp FROM SanPham sp WHERE LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :tenSP, '%'))")
    List<SanPham> findByTenSPContainingIgnoreCase(@Param("tenSP") String tenSP);
    
    // Tìm kiếm theo tên và danh mục
    @Query("SELECT sp FROM SanPham sp WHERE LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :tenSP, '%')) " +
           "AND sp.danhMuc.maDM = :maDM")
    List<SanPham> findByTenSPContainingIgnoreCaseAndDanhMucMaDM(
        @Param("tenSP") String tenSP, 
        @Param("maDM") Integer maDM
    );
    
    // Tìm kiếm theo phân loại
    @Query("SELECT sp FROM SanPham sp WHERE LOWER(sp.phanLoai) LIKE LOWER(CONCAT('%', :phanLoai, '%'))")
    List<SanPham> findByPhanLoaiContainingIgnoreCase(@Param("phanLoai") String phanLoai);
    
    // Tìm sản phẩm có giá trong khoảng
    @Query("SELECT sp FROM SanPham sp WHERE sp.donGia BETWEEN :minPrice AND :maxPrice")
    List<SanPham> findByDonGiaBetween(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // Tìm sản phẩm còn hàng (theo số lượng)
    @Query("SELECT sp FROM SanPham sp WHERE sp.soLuong > :soLuong")
    List<SanPham> findBySoLuongGreaterThan(@Param("soLuong") Integer soLuong);
    
    // Tìm sản phẩm theo trạng thái
    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = :trangThai")
    List<SanPham> findByTrangThai(@Param("trangThai") String trangThai);
    
    // Tìm sản phẩm còn hàng (theo trạng thái "Còn hàng")
    @Query("SELECT sp FROM SanPham sp WHERE sp.trangThai = 'Còn hàng'")
    List<SanPham> findAvailableProducts();
    
 // Kiểm tra xem sản phẩm có trong giỏ hàng không
    @Query("SELECT COUNT(gh) FROM GioHang gh WHERE gh.sanPham.maSP = :maSP")
    Long countGioHangBySanPham(@Param("maSP") Integer maSP);
    
    // Custom query: Tìm kiếm nâng cao (bao gồm cả trạng thái)
    @Query("SELECT sp FROM SanPham sp WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sp.moTa) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sp.phanLoai) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:maDM IS NULL OR sp.danhMuc.maDM = :maDM) AND " +
           "(:minPrice IS NULL OR sp.donGia >= :minPrice) AND " +
           "(:maxPrice IS NULL OR sp.donGia <= :maxPrice) AND " +
           "(:trangThai IS NULL OR :trangThai = '' OR sp.trangThai = :trangThai)")
    List<SanPham> searchAdvanced(
        @Param("keyword") String keyword,
        @Param("maDM") Integer maDM,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("trangThai") String trangThai
    );
}

