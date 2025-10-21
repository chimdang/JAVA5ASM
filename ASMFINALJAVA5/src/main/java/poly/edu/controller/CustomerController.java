package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import poly.edu.dto.DiaChiJsonDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.List;

import poly.edu.entity.*;
import poly.edu.dao.*;
import poly.edu.service.AuthService;
import poly.edu.service.ParamService;
import poly.edu.service.SessionService;


@Controller
@RequestMapping("/customer")
public class CustomerController {
	 @Autowired KhachHangDAO khachHangDAO;
	 @Autowired DiaChiDAO diaChiDAO;
	 @Autowired UsersDAO usersDAO;
	 @Autowired SessionService sessionService;
	 @Autowired ParamService paramService;
	 @Autowired AuthService authService;
	 @Autowired private GioHangDAO gioHangDAO;
	 @Autowired private HoaDonDAO hoaDonDAO;
	 @Autowired private HoaDonCTDAO hoaDonCTDAO;
	 @Autowired private SanPhamDAO sanPhamDAO;
	 @Autowired private DanhMucDAO danhMucDAO;
	
	 
	
//    @GetMapping("/index")
//    public String customerIndex(Model model) {
//        model.addAttribute("title", "Pine Shop - Trang chủ");
//        model.addAttribute("role", "customer");
//        return "customer/KH_index";
//    }
//    
//    @GetMapping("/detailProduct")
//    public String detailProduct(Model model) {
//        model.addAttribute("title", "Chi tiết đơn hàng");
//        model.addAttribute("role", "customer");
//        return "customer/KH_detail-product";
//    }
	 @GetMapping("/index")
	 public String customerIndex(Model model) {
	     // Load danh sách sản phẩm từ database
	     List<SanPham> danhSachSanPham = sanPhamDAO.findAll();
	     model.addAttribute("sanPhams", danhSachSanPham);
	     
	     // THÊM: Load danh sách danh mục
	     List<DanhMuc> danhSachDanhMuc = danhMucDAO.findAll();
	     model.addAttribute("danhMucs", danhSachDanhMuc);
	     
	     model.addAttribute("title", "Pine Shop - Trang chủ");
	     model.addAttribute("role", "customer");
	     return "customer/KH_index";
	 }
		    
		    @GetMapping("/detailProduct")
		    public String detailProduct(@RequestParam(required = false) Integer maSP, Model model) {
		        if (maSP != null) {
		            Optional<SanPham> sanPhamOpt = sanPhamDAO.findById(maSP);
		            if (sanPhamOpt.isPresent()) {
		                SanPham sanPham = sanPhamOpt.get();
		                model.addAttribute("sanPham", sanPham);
		            } else {
		                // Nếu không tìm thấy sản phẩm, redirect về trang chủ
		                return "redirect:/customer/index";
		            }
		        } else {
		            // Nếu không có maSP, redirect về trang chủ
		            return "redirect:/customer/index";
		        }
		        
		        model.addAttribute("title", "Chi tiết sản phẩm");
		        model.addAttribute("role", "customer");
		        return "customer/KH_detail-product";
		    }
	
		    @ModelAttribute("cartItemCount")
		    public int getCartItemCount() {
		        try {
		            Users currentUser = authService.getCurrentUser();
		            if (currentUser == null) {
		                return 0;
		            }
		            
		            KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());
		            if (khachHang == null) {
		                return 0;
		            }
		            
		            List<GioHang> cartItems = gioHangDAO.findByKhachHang(khachHang);
		            return cartItems.stream().mapToInt(GioHang::getSoLuong).sum();
		            
		        } catch (Exception e) {
		            // Log lỗi nếu cần
		            return 0;
		        }
		    }
    @GetMapping("/cart")
    public String cart(Model model) {
        // Khởi tạo danh sách rỗng và tổng tiền bằng 0
        List<GioHang> cartItems = List.of(); 
        double totalPrice = 0;

        try {
            // 1. Lấy thông tin Users (tài khoản) đang đăng nhập
            Users currentUser = authService.getCurrentUser();
            
            if (currentUser == null) {
                // Nếu chưa đăng nhập, chuyển hướng về trang login
                return "redirect:/auth/login"; 
            }

            // 2. Từ Users, tìm thông tin KhachHang (khách hàng) tương ứng
            KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());
            
            if (khachHang != null) {
                // 3. Nếu tìm thấy KhachHang, tải giỏ hàng của CHÍNH HỌ
                cartItems = gioHangDAO.findByKhachHang(khachHang);
                
                // 4. Tính tổng tiền cho giỏ hàng đó
                totalPrice = cartItems.stream()
                    .mapToDouble(item -> item.getSoLuong() * item.getSanPham().getDonGia())
                    .sum();
            }
            // Nếu không tìm thấy khachHang (lỗi dữ liệu), giỏ hàng sẽ là rỗng

        } catch (Exception e) {
            e.printStackTrace();
            // Báo lỗi ra view nếu có sự cố
            model.addAttribute("error", "Không thể tải giỏ hàng, vui lòng thử lại.");
        }

        // 5. Đưa dữ liệu (dù là rỗng hay có) ra view
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("title", "Giỏ hàng");
        model.addAttribute("role", "customer");
        
        return "customer/KH_GioHang";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("title", "Đơn hàng của bạn");
        model.addAttribute("role", "customer");

        try {
            // 1. Lấy thông tin Users (tài khoản) đang đăng nhập
            Users currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                // Nếu chưa đăng nhập, chuyển hướng về trang login
                return "redirect:/auth/login"; 
            }

            // 2. Từ Users, tìm thông tin KhachHang tương ứng
            KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());

            if (khachHang != null) {
                // 3. Nếu tìm thấy KhachHang, tải danh sách đơn hàng của họ
                List<HoaDon> orders = hoaDonDAO.findByKhachHangOrderByNgayMuaDesc(khachHang);
                model.addAttribute("orders", orders);
            } else {
                 // Nếu không tìm thấy thông tin khách hàng (lỗi dữ liệu), báo lỗi
                model.addAttribute("error", "Không tìm thấy thông tin khách hàng.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Báo lỗi ra view nếu có sự cố
            model.addAttribute("error", "Không thể tải danh sách đơn hàng, vui lòng thử lại.");
        }

        return "customer/KH_QLDonHang";
    }

    @GetMapping("/order/detail/{maHD}")
    public String orderDetail(@PathVariable("maHD") Integer maHD, Model model) {
        model.addAttribute("title", "Chi tiết đơn hàng");
        model.addAttribute("role", "customer");

        try {
            Optional<HoaDon> optionalOrder = hoaDonDAO.findById(maHD);

            if (optionalOrder.isPresent()) {
                HoaDon order = optionalOrder.get();

                // KIỂM TRA QUYỀN (đã có)
                Users currentUser = authService.getCurrentUser();
                if (!order.getKhachHang().getUser().getUserID().equals(currentUser.getUserID())) {
                     model.addAttribute("error", "Bạn không có quyền xem đơn hàng này.");
                     return "customer/KH_CTDonHang";
                }

                // GỬI ĐƠN HÀNG RA VIEW (đã có)
                model.addAttribute("order", order);

                // --- LOGIC MỚI: CHUYỂN ĐỔI JSON ---
                if (order.getDiaChiJson() != null && !order.getDiaChiJson().isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        // Đọc chuỗi JSON và chuyển thành đối tượng DiaChiJsonDTO
                        DiaChiJsonDTO diaChi = mapper.readValue(order.getDiaChiJson(), DiaChiJsonDTO.class);
                        // Gửi đối tượng đã chuyển đổi ra view
                        model.addAttribute("diaChi", diaChi);
                    } catch (Exception e) {
                        e.printStackTrace();
                        model.addAttribute("diaChiError", "Không thể đọc thông tin địa chỉ giao hàng.");
                    }
                }
                // --- KẾT THÚC LOGIC MỚI ---


                // TÍNH TỔNG TIỀN (đã có)
                double totalPrice = order.getHoaDonCTs().stream()
                    .mapToDouble(item -> item.getSoLuong() * item.getDonGia())
                    .sum();
                model.addAttribute("totalPrice", totalPrice);

            } else {
                model.addAttribute("error", "Không tìm thấy đơn hàng #" + maHD);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải chi tiết đơn hàng.");
            e.printStackTrace();
        }

        return "customer/KH_CTDonHang";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        try {
            Users currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            // Lấy thông tin khách hàng
            KhachHang customer = khachHangDAO.findByUser_UserID(currentUser.getUserID());
            if (customer == null) {
                model.addAttribute("error", "Không tìm thấy thông tin khách hàng");
                return "customer/KH_QLuser";
            }
            
            model.addAttribute("customer", customer);

            // Lấy danh sách địa chỉ
            List<DiaChi> addresses = diaChiDAO.findByKhachHang_MaKHAndTrangThaiXoaFalse(customer.getMaKH());
            model.addAttribute("addresses", addresses);

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thông tin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "customer/KH_QLuser";
    }

    @PostMapping("/update-profile")
    public String updateProfile(RedirectAttributes redirectAttributes) {
    	String fullname = paramService.getString("fullname", "");
    	String email = paramService.getString("email", "");
        String phone = paramService.getString("phone", "");
        try {
            Users currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/auth/login";
            }
            
            KhachHang customer = khachHangDAO.findByUser_UserID(currentUser.getUserID());
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin khách hàng");
                return "redirect:/customer/profile";
            }
            
            Users existingUser = usersDAO.findByMail(email);
            if (existingUser != null && !existingUser.getUserID().equals(currentUser.getUserID())) {
                redirectAttributes.addFlashAttribute("error", "Email đã được sử dụng bởi tài khoản khác");
                return "redirect:/customer/profile";
            }
            
            customer.setTenKH(fullname);
            customer.setSdt(phone);
            
            currentUser.setMail(email);
            usersDAO.save(currentUser);
            
            khachHangDAO.save(customer);
            
            // Cập nhật session
            sessionService.set("userName", fullname);
            sessionService.set("userMail", email);
            
            redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật thất bại: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/customer/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(RedirectAttributes redirectAttributes) {
    	String currentPassword = paramService.getString("currentPassword", "");
        String newPassword = paramService.getString("newPassword", "");
        String confirmPassword = paramService.getString("confirmPassword", "");
        try {
            String email = authService.getCurrentUserMail();
            if (email == null) {
                return "redirect:/auth/login";
            }
            
            String result = authService.changePassword(email, currentPassword, newPassword, confirmPassword);
            
            if (result.equals("OK")) {
                redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", result);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/customer/profile";
    }


    @PostMapping("/add-address")
    public String addAddress(RedirectAttributes redirectAttributes) {
        try {
            
            String tenNN = paramService.getString("tenNN", "");
            String sdt = paramService.getString("sdt", "");
            String diemGiao = paramService.getString("diemGiao", "");
            boolean macDinh = paramService.getBoolean("macDinh", false);

            Users currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/auth/login";
            }
            
            KhachHang customer = khachHangDAO.findByUser_UserID(currentUser.getUserID());
            if (customer == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin khách hàng");
                return "redirect:/customer/profile";
            }
            
            // Nếu đặt làm mặc định, hủy mặc định của các địa chỉ khác
            if (macDinh) {
                diaChiDAO.clearDefaultAddress(customer.getMaKH());
            }
            
            DiaChi newAddress = new DiaChi();
            newAddress.setKhachHang(customer);
            newAddress.setTenNN(tenNN);
            newAddress.setSdt(sdt);
            newAddress.setDiemGiao(diemGiao);
            newAddress.setMacDinh(macDinh);
            newAddress.setTrangThaiXoa(false);
            diaChiDAO.save(newAddress);
            redirectAttributes.addFlashAttribute("message", "Thêm địa chỉ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Thêm địa chỉ thất bại: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/customer/profile";
    }

    @PostMapping("/update-address")
    public String updateAddress(RedirectAttributes redirectAttributes) {
        try {
            Integer maDC = paramService.getInt("maDC", -1);
            String tenNN = paramService.getString("tenNN", "");
            String sdt = paramService.getString("sdt", "");
            String diemGiao = paramService.getString("diemGiao", "");
            boolean macDinh = paramService.getBoolean("macDinh", false);

            if (maDC == -1) {
                redirectAttributes.addFlashAttribute("error", "Mã địa chỉ không hợp lệ");
                return "redirect:/customer/profile";
            }

            Optional<DiaChi> optionalAddress = diaChiDAO.findById(maDC);
            if (!optionalAddress.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy địa chỉ");
                return "redirect:/customer/profile";
            }
            
            DiaChi address = optionalAddress.get();
            
            // Nếu đặt làm mặc định, hủy mặc định của các địa chỉ khác
            if (macDinh) {
                diaChiDAO.clearDefaultAddress(address.getKhachHang().getMaKH());
            }
            
            address.setTenNN(tenNN);
            address.setSdt(sdt);
            address.setDiemGiao(diemGiao);
            address.setMacDinh(macDinh);
            
            diaChiDAO.save(address);
            redirectAttributes.addFlashAttribute("message", "Cập nhật địa chỉ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Cập nhật địa chỉ thất bại: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/customer/profile";
    }

    @PostMapping("/set-default-address")
    public String setDefaultAddress(RedirectAttributes redirectAttributes) {
        try {
            // SỬ DỤNG PARAMSERVICE THAY CHO @RequestParam
            Integer addressId = paramService.getInt("addressId", -1);

            if (addressId == -1) {
                redirectAttributes.addFlashAttribute("error", "Mã địa chỉ không hợp lệ");
                return "redirect:/customer/profile";
            }

            Optional<DiaChi> optionalAddress = diaChiDAO.findById(addressId);
            if (!optionalAddress.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy địa chỉ");
                return "redirect:/customer/profile";
            }
            
            DiaChi address = optionalAddress.get();
            
            // Hủy mặc định của tất cả địa chỉ
            diaChiDAO.clearDefaultAddress(address.getKhachHang().getMaKH());
            
            // Đặt địa chỉ này làm mặc định
            address.setMacDinh(true);
            diaChiDAO.save(address);
            
            redirectAttributes.addFlashAttribute("message", "Đặt địa chỉ mặc định thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/customer/profile";
    }

    @PostMapping("/delete-address")
    public String deleteAddress(RedirectAttributes redirectAttributes) {
        try {
            Integer addressId = paramService.getInt("addressId", -1);

            if (addressId == -1) {
                redirectAttributes.addFlashAttribute("error", "Mã địa chỉ không hợp lệ");
                return "redirect:/customer/profile";
            }

            diaChiDAO.softDelete(addressId);
            redirectAttributes.addFlashAttribute("message", "Xóa địa chỉ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa địa chỉ thất bại: " + e.getMessage());
        }
        return "redirect:/customer/profile";
    }
    
    @GetMapping("/profile/edit-address/{maDC}")
    public String editAddress(@PathVariable("maDC") Integer maDC, Model model) {
        try {
            Users currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            KhachHang customer = khachHangDAO.findByUser_UserID(currentUser.getUserID());
            if (customer == null) {
                model.addAttribute("error", "Không tìm thấy thông tin khách hàng");
                return "customer/KH_QLuser";
            }
            
            model.addAttribute("customer", customer);

            List<DiaChi> addresses = diaChiDAO.findByKhachHang_MaKHAndTrangThaiXoaFalse(customer.getMaKH());
            model.addAttribute("addresses", addresses);

            Optional<DiaChi> addressToEdit = diaChiDAO.findById(maDC);
            if (addressToEdit.isPresent()) {
                model.addAttribute("addressToEdit", addressToEdit.get());
            }

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải thông tin: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "customer/KH_QLuser";
    }
}