package poly.edu.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@Table(name = "DanhMuc")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDM")
    private Integer maDM;

    @Column(name = "TenDM")
    private String tenDM;

    @OneToMany(mappedBy = "danhMuc")
    @JsonIgnore
    private List<SanPham> sanPhams;
}