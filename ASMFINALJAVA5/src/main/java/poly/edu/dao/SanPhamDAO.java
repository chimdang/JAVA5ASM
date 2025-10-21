package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.edu.entity.SanPham;

import java.util.List;

public interface SanPhamDAO extends JpaRepository<SanPham, Integer> {
    
    // Tìm kiếm theo danh mục (sử dụng tên thuộc tính chính xác)
    @Query("SELECT sp FROM SanPham sp WHERE sp.danhMuc.maDM = :maDM")
    List<SanPham> findByDanhMucMaDM(@Param("maDM") Integer maDM);
    
    // Tìm kiếm theo tên hoặc mô tả (sử dụng JPQL)
    @Query("SELECT sp FROM SanPham sp WHERE " +
           "LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sp.moTa) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SanPham> findByTenSPContainingIgnoreCaseOrMoTaContainingIgnoreCase(
        @Param("keyword") String tenSP, 
        @Param("keyword") String moTa
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
    
    // Tìm sản phẩm còn hàng
    @Query("SELECT sp FROM SanPham sp WHERE sp.soLuong > :soLuong")
    List<SanPham> findBySoLuongGreaterThan(@Param("soLuong") Integer soLuong);
    
    // Custom query: Tìm kiếm nâng cao
    @Query("SELECT sp FROM SanPham sp WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sp.moTa) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(sp.phanLoai) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:maDM IS NULL OR sp.danhMuc.maDM = :maDM) AND " +
           "(:minPrice IS NULL OR sp.donGia >= :minPrice) AND " +
           "(:maxPrice IS NULL OR sp.donGia <= :maxPrice)")
    List<SanPham> searchAdvanced(
        @Param("keyword") String keyword,
        @Param("maDM") Integer maDM,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice
    );
}