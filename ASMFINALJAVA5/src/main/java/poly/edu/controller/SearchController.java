package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.edu.dao.DanhMucDAO;
import poly.edu.dao.SanPhamDAO;
import poly.edu.entity.DanhMuc;
import poly.edu.entity.SanPham;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SanPhamDAO sanPhamDAO;

    @Autowired
    private DanhMucDAO danhMucDAO;

    // Tìm kiếm sản phẩm
    @GetMapping("")
    public String search(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {
        
        List<SanPham> sanPhams;
        List<DanhMuc> danhMucs = danhMucDAO.findAll();
        
        // Tìm kiếm theo danh mục
        if (categoryId != null && categoryId > 0) {
            sanPhams = sanPhamDAO.findByDanhMucMaDM(categoryId);
        }
        // Tìm kiếm theo từ khóa
        else if (keyword != null && !keyword.trim().isEmpty()) {
            sanPhams = sanPhamDAO.findByTenSPContainingIgnoreCaseOrMoTaContainingIgnoreCase(
                keyword.trim(), keyword.trim()
            );
        }
        // Hiển thị tất cả
        else {
            sanPhams = sanPhamDAO.findAll();
        }
        
        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("danhMucs", danhMucs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("resultCount", sanPhams.size());
        
        return "customer/KH_index";
    }

    // API tìm kiếm AJAX (tùy chọn)
    @GetMapping("/api")
    @ResponseBody
    public List<SanPham> searchApi(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        
        if (categoryId != null && categoryId > 0) {
            return sanPhamDAO.findByDanhMucMaDM(categoryId);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            return sanPhamDAO.findByTenSPContainingIgnoreCaseOrMoTaContainingIgnoreCase(
                keyword.trim(), keyword.trim()
            );
        }
        
        return sanPhamDAO.findAll();
    }
}