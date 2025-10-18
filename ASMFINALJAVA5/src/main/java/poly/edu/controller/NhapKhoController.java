package poly.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
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

    // GET: render cả Nhập kho + Lịch sử trên cùng 1 trang
    @GetMapping
    public String view(Model model) {
        // toàn bộ sản phẩm để hiển thị bảng + select
        List<SanPham> sanPhams = sanPhamDAO.findAll(Sort.by(Sort.Direction.ASC, "maSP"));
        model.addAttribute("sanPhams", sanPhams);

        // lịch sử nhập kho (mới nhất trước). Tuỳ chọn: giới hạn top 100 để nhẹ trang
        List<NhapKho> listNK = nhapKhoDAO.findTop100ByOrderByNgayNKDesc();
        model.addAttribute("listNK", listNK);

        return "employee/NV_NhapKho";
    }

    // POST: nhập nhanh từ form trên cùng hoặc từng dòng
    @PostMapping("/row")
    public String nhapNhanh(@RequestParam("maSP") Integer maSP,
                            @RequestParam("soLuong") Integer soLuong,
                            RedirectAttributes ra) {

        if (soLuong == null || soLuong <= 0) {
            ra.addFlashAttribute("error", "Số lượng phải > 0");
            return "redirect:/employee/import";
        }

        SanPham sp = sanPhamDAO.findById(maSP).orElse(null);
        if (sp == null) {
            ra.addFlashAttribute("error", "Không tìm thấy sản phẩm: " + maSP);
            return "redirect:/employee/import";
        }

        // Lưu bản ghi nhập kho
        NhapKho nk = new NhapKho();
        nk.setSanPham(sp);
        nk.setSoLuong(soLuong);
        nk.setNgayNK(LocalDate.now()); // dùng LocalDateTime như bạn yêu cầu
        nhapKhoDAO.save(nk);

        // Cộng tồn kho
        int tonHienTai = sp.getSoLuong() == null ? 0 : sp.getSoLuong();
        sp.setSoLuong(tonHienTai + soLuong);
        sanPhamDAO.save(sp);

        ra.addFlashAttribute("success", "Đã nhập +" + soLuong + " cho " + sp.getTenSP());
        return "redirect:/employee/import";
    }
}
