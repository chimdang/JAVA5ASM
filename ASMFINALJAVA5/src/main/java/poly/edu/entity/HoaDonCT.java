package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Setter @Getter
@Entity
@Data
@Table(name = "HoaDonCT")
public class HoaDonCT {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHDCT;

    @ManyToOne
    @JoinColumn(name = "MaHD")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    private Integer soLuong;
    private Double donGia;
}
