package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.dao.NhapKhoDAO;
import poly.edu.dao.SanPhamDAO;
import poly.edu.entity.NhapKho;
import poly.edu.entity.SanPham;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/employee/import")
@RequiredArgsConstructor
public class NhapKhoController {

    private final SanPhamDAO sanPhamDAO;
    private final NhapKhoDAO nhapKhoDAO;

    // GET: hiển thị cả 2 tab dùng chung URL
    @GetMapping
    public String view(Model model) {
        // Đổ toàn bộ sản phẩm cho bảng + select
        List<SanPham> sanPhams = sanPhamDAO.findAll(Sort.by(Sort.Direction.ASC, "maSP"));
        model.addAttribute("sanPhams", sanPhams);

        // Lịch sử nhập kho - mới nhất trước (giới hạn 100 cho nhẹ trang)
        List<NhapKho> listNK = nhapKhoDAO.findTop100ByOrderByNgayNKDesc();
        model.addAttribute("listNK", listNK);

        return "employee/NV_NhapKho"; // đổi nếu tên view khác
    }

    // POST: nhập nhanh (form trên đầu + form ở từng dòng)
    @PostMapping("/row")
    @Transactional
    public String nhapNhanh(@RequestParam("maSP") Integer maSP,
                            @RequestParam("soLuong") Integer soLuong,
                            RedirectAttributes ra) {

        if (soLuong == null || soLuong <= 0) {
            ra.addFlashAttribute("error", "Số lượng phải > 0.");
            return "redirect:/employee/import";
        }

        SanPham sp = sanPhamDAO.findById(maSP).orElse(null);
        if (sp == null) {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm: " + maSP);
            return "redirect:/employee/import";
        }

        // Cộng tồn kho
        int tonHienTai = sp.getSoLuong() == null ? 0 : sp.getSoLuong();
        sp.setSoLuong(tonHienTai + soLuong);
        sanPhamDAO.save(sp);

        // Ghi lịch sử
        NhapKho nk = new NhapKho();
        nk.setSanPham(sp);
        nk.setSoLuong(soLuong);
        nk.setNgayNK(LocalDate.now()); // <- dùng LocalDateTime cho khớp HTML
        nhapKhoDAO.save(nk);

        ra.addFlashAttribute("success", "Đã nhập +" + soLuong + " cho " + sp.getTenSP() + " (Mã " + sp.getMaSP() + ").");
        return "redirect:/employee/import";
    }
}
