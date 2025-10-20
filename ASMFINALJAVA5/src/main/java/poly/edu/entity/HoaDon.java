package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Table(name = "HoaDon")
@Getter @Setter
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHD")
    private Integer maHD;

    // FK -> KhachHang(MaKH)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKH")
    private KhachHang khachHang;

    // FK -> NhanVien(MaNV)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNV")
    private NhanVien nhanVien;

    // Lưu JSON địa chỉ giao hàng (đúng với DB)
    @Lob
    @Column(name = "DiaChiJson")
    private String diaChiJson;

    @Column(name = "TrangThai")
    private String trangThai;

    @Temporal(TemporalType.DATE)
    @Column(name = "NgayMua")
    private Date ngayMua;
}
