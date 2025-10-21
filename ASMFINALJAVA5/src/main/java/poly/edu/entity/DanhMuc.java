package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
@Table(name = "DanhMuc")
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDM;
    
    private String tenDM;
    
    // Relationship với SanPham (optional)
    @OneToMany(mappedBy = "danhMuc")
    private List<SanPham> sanPhams;
}