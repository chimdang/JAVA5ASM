package poly.edu.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "NhapKho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "sanPham")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NhapKho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaNK")
    @EqualsAndHashCode.Include
    private Integer maNK;

    // MaSP cho phép NULL trong DB => optional = true
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "MaSP") // FK tới SanPham.MaSP
    private SanPham sanPham;

    // SoLuong cho phép NULL (theo DB); nếu bạn muốn bắt buộc, đổi nullable=false và ràng buộc ở Controller/Validation
    @Column(name = "SoLuong", nullable = true)
    private Integer soLuong;

    // DB là DATE => dùng LocalDate (không cần @Temporal)
    @Column(name = "NgayNK", nullable = true)
    private LocalDate ngayNK;
}
