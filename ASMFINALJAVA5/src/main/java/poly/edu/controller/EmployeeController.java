package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import jakarta.transaction.Transactional;
import poly.edu.dao.DanhMucDAO;
import poly.edu.dao.HoaDonCTDAO;
import poly.edu.dao.KhachHangDAO;
import poly.edu.dao.NhanVienDAO;
import poly.edu.dao.SanPhamDAO;
import poly.edu.dao.UsersDAO;
import poly.edu.service.AuthService;
import poly.edu.service.ThongKeService;


import poly.edu.entity.KhachHang;
import poly.edu.entity.NhanVien;
import poly.edu.entity.Users;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private ThongKeService thongKeService;
	@Autowired
    private HoaDonCTDAO hoaDonCTDAO;

    @Autowired
    private SanPhamDAO sanPhamDAO;

    @Autowired
    private KhachHangDAO khachHangDAO; // Thẻ "Tổng người dùng" cũ có thể là khách hàng

    @Autowired
    private UsersDAO usersDAO; // Thống kê mới

    @Autowired
    private NhanVienDAO nhanVienDAO; // Thống kê mới

    @Autowired
    private DanhMucDAO danhMucDAO; // Thống kê mới

	// Đổi tên mapping để phù hợp với các file HTML đã tạo
	@GetMapping({"/dashboard", "/index", "/thongke"})
    public String dashboard(Model model) {
        // Kiểm tra quyền admin
        if (!authService.isAdmin()) {
            return "redirect:/employee/products"; 
        }
        
        // --- Dữ liệu cho các thẻ thống kê ---
        // Dữ liệu cũ
        model.addAttribute("totalRevenue", thongKeService.getTotalRevenue());
        model.addAttribute("totalProducts", thongKeService.getTotalProducts());
        model.addAttribute("totalUsers", thongKeService.getTotalUsers()); // Giả sử đây là tổng khách hàng

        // Dữ liệu mới (bạn cần thêm các phương thức này vào ThongKeService)
        model.addAttribute("totalEmployees", thongKeService.getTotalEmployees());
        model.addAttribute("totalAccounts", thongKeService.getTotalAccounts());
        model.addAttribute("totalCategories", thongKeService.getTotalCategories());
        
        // --- Dữ liệu cho biểu đồ ---
        List<Object[]> revenueData = thongKeService.getRevenueByDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");

        List<String> labels = revenueData.stream()
            .map(row -> dateFormat.format((java.util.Date) row[0]))
            .collect(Collectors.toList());
        
        List<Double> data = revenueData.stream()
            .map(row -> (Double) row[1])
            .collect(Collectors.toList());

        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartData", data);
        
        // --- Các thuộc tính cho layout ---
        model.addAttribute("title", "Dashboard - Thống kê");
        model.addAttribute("role", "employee");
        return "employee/NV_index"; // Trả về view đã cập nhật
    }

    // --- Các phương thức khác giữ nguyên ---
	@GetMapping("/products")
	public String redirectToProductManagement() {
	    return "redirect:/employee/product-management";
	}

//    @GetMapping("/orders")
//    public String orders(Model model) {
//        model.addAttribute("title", "Quản lý đơn hàng");
//        model.addAttribute("role", "employee");
//        return "employee/NV_QLdonhang";
//    }

    
	@GetMapping("/users")
    public String users(Model model, 
                        @RequestParam(value = "roleFilter", required = false, defaultValue = "") String roleFilter,
                        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                        @RequestParam(value = "editId", required = false) Integer editId,
                        @RequestParam(value = "p", defaultValue = "0") int p) { // <-- THÊM THAM SỐ TRANG 'p'
        
        // 1. TẠO PAGEABLE (trang 'p', 5 user mỗi trang)
        Pageable pageable = PageRequest.of(p, 8);

        // 2. GỌI DAO ĐỂ LỌC VÀ PHÂN TRANG
        Page<Users> page = usersDAO.findByFilter(keyword, roleFilter, pageable);
        model.addAttribute("page", page); // Gửi đối tượng 'page' ra view

        // --- XÓA TẤT CẢ LOGIC LỌC BẰNG JAVA CŨ (userList, finalUserList...) ---
        
        // 3. GỬI CÁC THÔNG TIN CŨ RA VIEW
        model.addAttribute("nhanVienDAO", nhanVienDAO);
        model.addAttribute("khachHangDAO", khachHangDAO);
        
        model.addAttribute("roleFilter", roleFilter); // Giữ lại giá trị filter
        model.addAttribute("keyword", keyword); // Giữ lại giá trị keyword

        // 4. LOGIC CHUẨN BỊ EDIT (giữ nguyên)
        model.addAttribute("userEdit", new Users());
        model.addAttribute("nhanVienEdit", new NhanVien());
        model.addAttribute("khachHangEdit", new KhachHang());
        
        if (editId != null) {
            usersDAO.findById(editId).ifPresent(user -> {
                model.addAttribute("userEdit", user);
                nhanVienDAO.findByUser(user).ifPresent(nv -> model.addAttribute("nhanVienEdit", nv));
                khachHangDAO.findByUser(user).ifPresent(kh -> model.addAttribute("khachHangEdit", kh));
                model.addAttribute("activeTab", "edit");
            });
        }

        model.addAttribute("title", "Quản lý người dùng");
        model.addAttribute("role", "employee");
        return "employee/NV_QLuser";
    }

    /**
     * XỬ LÝ TẠO MỚI (CREATE)
     */
    @PostMapping("/users/create")
    @Transactional
    public String createUser(@RequestParam("mail") String mail,
                             @RequestParam("pass") String pass,
                             @RequestParam("role") String role,
                             @RequestParam(value = "fullname", required = false) String fullname,
                             @RequestParam(value = "sdt", required = false) String sdt,
                             RedirectAttributes redirectAttributes) {
        // 1. Tạo và lưu Users
        Users user = new Users();
        user.setMail(mail);
        user.setPass(pass); // Cần mã hóa mật khẩu trong thực tế
        Users savedUser = usersDAO.save(user);

        // 2. Dựa vào vai trò để tạo NhanVien hoặc KhachHang
        if ("NV".equals(role)) {
            NhanVien nv = new NhanVien();
            nv.setTenNV(fullname);
            nv.setVaiTro("Nhân viên");
            nv.setUser(savedUser);
            nhanVienDAO.save(nv);
        } else if ("KH".equals(role)) {
            KhachHang kh = new KhachHang();
            kh.setTenKH(fullname);
            kh.setSdt(sdt);
            kh.setUser(savedUser);
            khachHangDAO.save(kh);
        }
        
        redirectAttributes.addFlashAttribute("message", "Tạo người dùng thành công!");
        return "redirect:/employee/users";
    }

    /**
     * XỬ LÝ CẬP NHẬT (UPDATE)
     */
    @PostMapping("/users/update")
    @Transactional
    public String updateUser(@RequestParam("userID") Integer userID,
                             @RequestParam("mail") String mail,
                             @RequestParam(value="pass", required=false) String pass,
                             @RequestParam(value = "fullname", required = false) String fullname,
                             @RequestParam(value = "sdt", required = false) String sdt,
                             RedirectAttributes redirectAttributes) {
        
        usersDAO.findById(userID).ifPresent(user -> {
            user.setMail(mail);
            if (pass != null && !pass.isEmpty()) {
                user.setPass(pass); // Chỉ cập nhật pass nếu người dùng nhập pass mới
            }
            usersDAO.save(user);

            // Cập nhật thông tin ở bảng NhanVien hoặc KhachHang
            nhanVienDAO.findByUser(user).ifPresent(nv -> {
                nv.setTenNV(fullname);
                nhanVienDAO.save(nv);
            });
            khachHangDAO.findByUser(user).ifPresent(kh -> {
                kh.setTenKH(fullname);
                kh.setSdt(sdt);
                khachHangDAO.save(kh);
            });
        });

        redirectAttributes.addFlashAttribute("message", "Cập nhật thành công!");
        return "redirect:/employee/users?editId=" + userID; // Quay lại tab edit sau khi update
    }

    /**
     * XỬ LÝ XÓA (DELETE)
     */
    @PostMapping("/users/delete")
    @Transactional
    public String deleteUser(@RequestParam("userID") Integer userID, RedirectAttributes redirectAttributes) {
        usersDAO.findById(userID).ifPresent(user -> {
            // Phải xóa ở bảng phụ (NhanVien/KhachHang) trước
            nhanVienDAO.findByUser(user).ifPresent(nv -> nhanVienDAO.delete(nv));
            khachHangDAO.findByUser(user).ifPresent(kh -> khachHangDAO.delete(kh));
            // Sau đó mới xóa ở bảng chính (Users)
            usersDAO.delete(user);
        });
        redirectAttributes.addFlashAttribute("message", "Xóa người dùng thành công!");
        return "redirect:/employee/users";
    }
    //KETTHUC USER
//    @GetMapping("/users")
//    public String users(Model model) {
//        model.addAttribute("title", "Quản lý người dùng");
//        model.addAttribute("role", "employee");
//        return "employee/NV_QLuser";
//    }

//    @GetMapping("/import")
//    public String importStock(Model model) {
//        model.addAttribute("title", "Nhập kho");
//        model.addAttribute("role", "employee");
//        return "employee/NV_NhapKho";
//    }
}