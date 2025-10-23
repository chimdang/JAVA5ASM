package poly.edu.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "SanPham")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaSP")
    private Integer maSP;

    @ManyToOne(fetch = FetchType.EAGER)  // ← THÊM fetch = FetchType.EAGER
    @JoinColumn(name = "MaDM")
    private DanhMuc danhMuc;

    @Column(name = "Hinh")
    private String hinh;
    
    @Column(name = "TenSP")
    private String tenSP;
    
    @Column(name = "SoLuong")
    private Integer soLuong;
    
    @Column(name = "DonGia")
    private Double donGia;
    
    @Column(name = "PhanLoai")
    private String phanLoai;
    
    @Column(name = "MoTa", columnDefinition = "nvarchar(max)")
    private String moTa;
    
    @Column(name = "TrangThai")
    private String trangThai;
}