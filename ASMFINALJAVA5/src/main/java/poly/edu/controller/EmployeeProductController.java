package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.edu.dao.DanhMucDAO;
import poly.edu.dao.SanPhamDAO;
import poly.edu.entity.DanhMuc;
import poly.edu.entity.SanPham;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee/product-management") // ĐỔI URL ĐỂ TRÁNH CONFLICT
public class EmployeeProductController {

    @Autowired
    private SanPhamDAO sanPhamDAO;

    @Autowired
    private DanhMucDAO danhMucDAO;

    /**
     * Hiển thị trang quản lý sản phẩm
     */
    @GetMapping("")
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        List<SanPham> sanPhams;
        List<DanhMuc> danhMucs = danhMucDAO.findAll();

        // Lọc sản phẩm theo điều kiện
        if (keyword != null && !keyword.trim().isEmpty() && categoryId != null && status != null) {
            // Tìm kiếm nâng cao
            sanPhams = sanPhamDAO.searchAdvanced(keyword.trim(), categoryId, null, null, status);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm theo từ khóa
            sanPhams = sanPhamDAO.findByTenSPContainingIgnoreCaseOrMoTaContainingIgnoreCase(
                keyword.trim(), keyword.trim()
            );
        } else if (categoryId != null && categoryId > 0) {
            // Lọc theo danh mục
            sanPhams = sanPhamDAO.findByDanhMucMaDM(categoryId);
        } else if (status != null && !status.isEmpty()) {
            // Lọc theo trạng thái
            sanPhams = sanPhamDAO.findByTrangThai(status);
        } else {
            // Hiển thị tất cả
            sanPhams = sanPhamDAO.findAll();
        }

        // Tính toán thống kê
        long totalProducts = sanPhamDAO.count();
        long activeProducts = sanPhamDAO.findByTrangThai("Còn hàng").size();
        long outOfStock = sanPhamDAO.findAll().stream()
            .filter(sp -> sp.getSoLuong() == 0).count();
        long lowStock = sanPhamDAO.findAll().stream()
            .filter(sp -> sp.getSoLuong() > 0 && sp.getSoLuong() <= 10).count();

        model.addAttribute("sanPhams", sanPhams);
        model.addAttribute("danhMucs", danhMucs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        
        // Thống kê
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("outOfStock", outOfStock);
        model.addAttribute("lowStock", lowStock);
        
        model.addAttribute("title", "Quản lý sản phẩm");
        model.addAttribute("role", "employee");

        return "employee/NV_QLsanpham";
    }

    /**
     * Thêm sản phẩm mới
     */
    @PostMapping("/add")
    public String addProduct(
            @RequestParam("tenSP") String tenSP,
            @RequestParam("phanLoai") String phanLoai,
            @RequestParam("maDM") Integer maDM,
            @RequestParam("moTa") String moTa,
            @RequestParam("donGia") Double donGia,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("trangThai") String trangThai,
            @RequestParam(value = "hinh", required = false) String hinh,
            @RequestParam(value = "productImages", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            // Validate dữ liệu đầu vào
            if (tenSP == null || tenSP.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Tên sản phẩm không được để trống!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/employee/product-management";
            }

            // Kiểm tra danh mục có tồn tại không
            Optional<DanhMuc> danhMucOpt = danhMucDAO.findById(maDM);
            if (!danhMucOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("message", "Danh mục không tồn tại!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/employee/product-management";
            }

            // Tạo sản phẩm mới
            SanPham sanPham = new SanPham();
            sanPham.setTenSP(tenSP.trim());
            sanPham.setPhanLoai(phanLoai.trim());
            sanPham.setMoTa(moTa != null ? moTa.trim() : "");
            sanPham.setDonGia(donGia);
            sanPham.setSoLuong(soLuong);
            sanPham.setTrangThai(trangThai);
            sanPham.setDanhMuc(danhMucOpt.get());
            
            // Xử lý hình ảnh
            String imageName = "default.jpg";
            if (imageFile != null && !imageFile.isEmpty()) {
                // Lưu file ảnh
                imageName = saveImageFile(imageFile);
            } else if (hinh != null && !hinh.trim().isEmpty()) {
                imageName = hinh.trim();
            }
            sanPham.setHinh(imageName);

            // Lưu vào database
            sanPhamDAO.save(sanPham);
            
            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            e.printStackTrace();
        }

        return "redirect:/employee/product-management";
    }
    
    private String saveImageFile(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null; // Trả về null nếu không có file
        }
        
        // Sử dụng đường dẫn tuyệt đối để tránh lỗi
        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";
        
        // Tạo tên file an toàn
        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = "sp_" + System.currentTimeMillis() + fileExtension;
        
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
            System.out.println("Created directory: " + uploadDir);
        }
        
        File dest = new File(uploadDir + fileName);
        imageFile.transferTo(dest);
        System.out.println("Image saved to: " + dest.getAbsolutePath());
        
        return fileName;
    }

    /**
     * Cập nhật sản phẩm
     */
    @PostMapping("/update")
    public String updateProduct(
            @RequestParam(value = "maSP", required = false) Integer maSP,  // ← THÊM required = false
            @RequestParam("tenSP") String tenSP,
            @RequestParam("phanLoai") String phanLoai,
            @RequestParam("maDM") Integer maDM,
            @RequestParam("moTa") String moTa,
            @RequestParam("donGia") Double donGia,
            @RequestParam("soLuong") Integer soLuong,
            @RequestParam("trangThai") String trangThai,
            @RequestParam(value = "hinh", required = false) String hinh,
            RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra maSP có null không
            if (maSP == null) {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy mã sản phẩm!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/employee/product-management";
            }

            // Validate dữ liệu đầu vào
            if (tenSP == null || tenSP.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("message", "Tên sản phẩm không được để trống!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/employee/product-management";
            }

            if (donGia == null || donGia < 0) {
                redirectAttributes.addFlashAttribute("message", "Giá sản phẩm không hợp lệ!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/employee/product-management";
            }

            if (soLuong == null || soLuong < 0) {
                redirectAttributes.addFlashAttribute("message", "Số lượng không hợp lệ!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/employee/product-management";
            }

            // Tìm sản phẩm cần cập nhật
            Optional<SanPham> sanPhamOpt = sanPhamDAO.findById(maSP);
            if (!sanPhamOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy sản phẩm!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/employee/product-management";
            }

            // Kiểm tra danh mục có tồn tại không
            Optional<DanhMuc> danhMucOpt = danhMucDAO.findById(maDM);
            if (!danhMucOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("message", "Danh mục không tồn tại!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
                return "redirect:/employee/product-management";
            }

            // Cập nhật thông tin sản phẩm
            SanPham sanPham = sanPhamOpt.get();
            sanPham.setTenSP(tenSP.trim());
            sanPham.setPhanLoai(phanLoai.trim());
            sanPham.setMoTa(moTa != null ? moTa.trim() : "");
            sanPham.setDonGia(donGia);
            sanPham.setSoLuong(soLuong);
            sanPham.setTrangThai(trangThai);
            sanPham.setDanhMuc(danhMucOpt.get());

            // Cập nhật hình nếu có
            if (hinh != null && !hinh.trim().isEmpty()) {
                sanPham.setHinh(hinh.trim());
            }

            // Lưu vào database
            sanPhamDAO.save(sanPham);
            
            redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            e.printStackTrace();
        }

        return "redirect:/employee/product-management";
    }

    /**
     * Xóa sản phẩm
     */
    @GetMapping("/delete/{maSP}")
    public String deleteProduct(@PathVariable Integer maSP, RedirectAttributes redirectAttributes) {
        try {
            Optional<SanPham> sanPhamOpt = sanPhamDAO.findById(maSP);
            if (sanPhamOpt.isPresent()) {
                // Kiểm tra xem sản phẩm có trong giỏ hàng không
                Long count = sanPhamDAO.countGioHangBySanPham(maSP);
                if (count > 0) {
                    redirectAttributes.addFlashAttribute("message", 
                        "Không thể xóa sản phẩm vì đang có " + count + " người dùng đã thêm vào giỏ hàng!");
                    redirectAttributes.addFlashAttribute("messageType", "danger");
                    return "redirect:/employee/product-management";
                }
                
                sanPhamDAO.deleteById(maSP);
                redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công!");
                redirectAttributes.addFlashAttribute("messageType", "success");
            } else {
                redirectAttributes.addFlashAttribute("message", "Không tìm thấy sản phẩm!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            e.printStackTrace();
        }

        return "redirect:/employee/product-management";
    }

    /**
     * API: Lấy chi tiết sản phẩm (cho modal chỉnh sửa)
     */
    @GetMapping("/detail/{maSP}")
    @ResponseBody
    public SanPham getProductDetail(@PathVariable Integer maSP) {
        return sanPhamDAO.findById(maSP).orElse(null);
    }

    /**
     * API: Lấy danh sách sản phẩm (JSON)
     */
    @GetMapping("/api/list")
    @ResponseBody
    public List<SanPham> getProductList() {
        return sanPhamDAO.findAll();
    }
    /**
     * API: Thêm danh mục mới
     */
    @PostMapping("/add-category")
    @ResponseBody
    public Map<String, Object> addCategory(@RequestParam("tenDM") String tenDM) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (tenDM == null || tenDM.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Tên danh mục không được để trống!");
                return response;
            }
            
            // Kiểm tra danh mục đã tồn tại chưa
            List<DanhMuc> existingCategories = danhMucDAO.findAll().stream()
                .filter(dm -> dm.getTenDM().equalsIgnoreCase(tenDM.trim()))
                .collect(Collectors.toList());
                
            if (!existingCategories.isEmpty()) {
                response.put("success", false);
                response.put("message", "Danh mục đã tồn tại!");
                return response;
            }
            
            // Tạo danh mục mới
            DanhMuc newCategory = new DanhMuc();
            newCategory.setTenDM(tenDM.trim());
            DanhMuc savedCategory = danhMucDAO.save(newCategory);
            
            response.put("success", true);
            response.put("maDM", savedCategory.getMaDM());
            response.put("message", "Thêm danh mục thành công!");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
    
}