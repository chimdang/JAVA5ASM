// src/main/java/poly/edu/entity/SanPham.java
package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name="SanPham")
@Data @NoArgsConstructor @AllArgsConstructor
public class SanPham {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="MaSP")
    private Integer maSP;

    @Column(name="TenSP")
    private String tenSP;

    @Column(name="SoLuong")
    private Integer soLuong;

    @Column(name="DonGia")
    private Double donGia;

    @Column(name="Hinh")
    private String hinh;

    @Column(name="MaDM")
    private Integer maDM;

    @Column(name="PhanLoai")
    private String phanLoai;

    @Column(name="MoTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;
    

    @Column(name="TrangThai")
    private String trangThai;
}
