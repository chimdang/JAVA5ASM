package poly.edu.service;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import poly.edu.dao.SanPhamDAO;
import poly.edu.entity.SanPham;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamDAO sanPhamDAO;

    public List<SanPham> findAll() {
        return sanPhamDAO.findAll();
    }
}
