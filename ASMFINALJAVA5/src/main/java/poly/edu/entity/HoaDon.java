package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHD;

    @ManyToOne
    @JoinColumn(name = "MaKH")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "MaNV")
    private NhanVien nhanVien;

    private String diaChiJson;

    private String trangThai;

    @Temporal(TemporalType.DATE)
    private Date ngayMua;
}
