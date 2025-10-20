package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import poly.edu.dao.DanhMucDAO;
import poly.edu.dao.HoaDonCTDAO;
import poly.edu.dao.KhachHangDAO;
import poly.edu.dao.NhanVienDAO;
import poly.edu.dao.SanPhamDAO;
import poly.edu.dao.UsersDAO;
import poly.edu.service.AuthService;
import poly.edu.service.ThongKeService;

import java.text.SimpleDateFormat;
import java.util.List;
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
	@GetMapping({"", "/index", "/thongke"})
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
    public String products(Model model) { 
        model.addAttribute("title", "Quản lý sản phẩm");
        model.addAttribute("role", "employee");
        return "employee/NV_QLsanpham";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("title", "Quản lý đơn hàng");
        model.addAttribute("role", "employee");
        return "employee/NV_QLdonhang";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("title", "Quản lý người dùng");
        model.addAttribute("role", "employee");
        return "employee/NV_QLuser";
    }

    @GetMapping("/import")
    public String importStock(Model model) {
        model.addAttribute("title", "Nhập kho");
        model.addAttribute("role", "employee");
        return "employee/NV_NhapKho";
    }
}