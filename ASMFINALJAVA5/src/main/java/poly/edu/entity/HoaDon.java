package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

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

    @Column(name = "DiaChiJson", columnDefinition = "nvarchar(max)")
    private String diaChiJson;

    private String trangThai;

    @Temporal(TemporalType.DATE)
    private Date ngayMua;
    
    @OneToMany(mappedBy = "hoaDon")
    private List<HoaDonCT> hoaDonCTs;
}