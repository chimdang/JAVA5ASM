package poly.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import poly.edu.dao.*;
import poly.edu.entity.*;
import poly.edu.service.AuthService;
import poly.edu.service.ParamService;


import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
public class GioHangController {
	@Autowired
    private GioHangDAO gioHangDAO;
    @Autowired
    private SanPhamDAO sanPhamDAO;
    @Autowired
    private KhachHangDAO khachHangDAO;
    @Autowired
    private DiaChiDAO diaChiDAO;
    @Autowired
    private HoaDonDAO hoaDonDAO;
    @Autowired
    private HoaDonCTDAO hoaDonCTDAO;
    @Autowired
    private HttpSession session;
    @Autowired
    private ObjectMapper objectMapper;
	 @Autowired AuthService authService;
	 @Autowired ParamService paramService;
    
    
	 @PostMapping("/cart/add")
	    public String addToCart(@RequestParam("maSP") Integer maSP, @RequestParam("soLuong") Integer soLuong) {
	        
	        // --- BẮT ĐẦU SỬA ---
	        
	        // 1. Lấy thông tin Users (tài khoản) đang đăng nhập
	        Users currentUser = authService.getCurrentUser();
	        
	        // 2. Kiểm tra xem đã đăng nhập chưa
	        if (currentUser == null) {
	            return "redirect:/auth/login"; // Chuyển về trang đăng nhập
	        }

	        // 3. Từ Users, tìm thông tin KhachHang (khách hàng) tương ứng
	        KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());
	        
	        // 4. Kiểm tra xem có thông tin khách hàng không
	        if (khachHang == null) {
	            // Trường hợp hiếm: có tài khoản đăng nhập nhưng không có hồ sơ khách hàng
	             return "redirect:/auth/login?error=no_customer_profile";
	        }
	        
	        // --- KẾT THÚC SỬA ---
	        
	        // Phần logic còn lại của bạn đã đúng, giữ nguyên:
	        Optional<SanPham> sanPhamOpt = sanPhamDAO.findById(maSP);
	        if (sanPhamOpt.isPresent()) {
	            SanPham sanPham = sanPhamOpt.get();
	            GioHang existingItem = gioHangDAO.findByKhachHangAndSanPham(khachHang, sanPham);
	            if (existingItem != null) {
	                existingItem.setSoLuong(existingItem.getSoLuong() + soLuong);
	                gioHangDAO.save(existingItem);
	            } else {
	                GioHang newItem = new GioHang();
	                newItem.setKhachHang(khachHang); // <-- Giờ sẽ là khách hàng đang đăng nhập
	                newItem.setSanPham(sanPham);
	                newItem.setSoLuong(soLuong);
	                gioHangDAO.save(newItem);
	            }
	        }
	        
	        // Bạn có thể redirect về giỏ hàng, hoặc redirect ngược lại trang chi tiết
	        // return "redirect:/customer/detailProduct?maSP=" + maSP;
	        return "redirect:/customer/cart";
	    }


    
    @PostMapping("/cart/update")
    @Transactional
    public String updateCart(@RequestParam("maGH") Integer maGH, @RequestParam("soLuong") Integer soLuong) {
        Optional<GioHang> cartItemOpt = gioHangDAO.findById(maGH);
        if (cartItemOpt.isPresent()) {
            GioHang cartItem = cartItemOpt.get();
            if (soLuong > 0) {
                cartItem.setSoLuong(soLuong);
                gioHangDAO.save(cartItem);
            } else {
                gioHangDAO.deleteById(maGH);
            }
        }
        return "redirect:/customer/cart";
    }

    @GetMapping("/cart/remove/{maGH}")
    public String removeFromCart(@PathVariable("maGH") Integer maGH) {
        gioHangDAO.deleteById(maGH);
        return "redirect:/customer/cart";
    }

    @PostMapping("/cart/delete-selected")
    public String deleteSelectedItems(@RequestParam("selectedIds") List<Integer> selectedIds) {
        if (selectedIds != null && !selectedIds.isEmpty()) {
            gioHangDAO.deleteAllById(selectedIds);
        }
        return "redirect:/customer/cart";
    }

    // ---- PHẦN SỬA LỖI BẮT ĐẦU TỪ ĐÂY ----

    @PostMapping("/checkout")
    public String checkout(@RequestParam(value = "selectedItems", required = false) List<Integer> selectedItems, Model model) {
        if (selectedItems == null || selectedItems.isEmpty()) {
            return "redirect:/customer/cart?error=notselected";
        }
        
        // --- SỬA Ở ĐÂY ---
        Users currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());
        if (khachHang == null) {
            return "redirect:/customer/cart?error=nocustomer"; // Lỗi không tìm thấy KH
        }
        // --- KẾT THÚC SỬA ---

        List<GioHang> cartItems = gioHangDAO.findAllById(selectedItems);
        session.setAttribute("selectedCartItemIds", selectedItems);
        List<DiaChi> addresses = diaChiDAO.findByKhachHang_MaKHAndTrangThaiXoaFalse(khachHang.getMaKH()); // Sẽ lấy đúng địa chỉ
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getSoLuong() * item.getSanPham().getDonGia())
                .sum();
        model.addAttribute("addresses", addresses);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("newAddress", new DiaChi());
        model.addAttribute("title", "Đặt hàng");
        model.addAttribute("role", "customer");
        return "customer/KH_DatHang";
    }
    
    /**
     * [THÊM MỚI] - Xử lý hiển thị trang checkout khi bị redirect về
     */
    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        
        // 1. Lấy thông tin khách hàng (phần này đã đúng)
        Users currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());
        if (khachHang == null) {
            return "redirect:/customer/cart?error=nocustomer"; 
        }
        
        // --- BẮT ĐẦU SỬA LOGIC LẤY SẢN PHẨM ---
        List<GioHang> cartItems;
        
        // 2. Ưu tiên kiểm tra "Mua Ngay" (từ session "checkoutItems")
        if (session.getAttribute("checkoutItems") != null) {
            // Lấy danh sách "mua ngay" từ session
            cartItems = (List<GioHang>) session.getAttribute("checkoutItems");
            
            // Đặt cờ để phương thức "placeOrder" (POST) biết đây là "Mua Ngay"
            session.setAttribute("isBuyNow", true); 
        } 
        // 3. Nếu không phải "Mua Ngay", lấy từ giỏ hàng (logic cũ của bạn)
        else {
            List<Integer> selectedItemsIds = (List<Integer>) session.getAttribute("selectedCartItemIds");
            if (selectedItemsIds == null || selectedItemsIds.isEmpty()) {
                // Quay về giỏ hàng nếu không có gì được chọn
                return "redirect:/customer/cart?error=notselected";
            }
            cartItems = gioHangDAO.findAllById(selectedItemsIds);
            
            // Đặt cờ là KHÔNG phải "Mua Ngay"
            session.setAttribute("isBuyNow", false);
        }
        // --- KẾT THÚC SỬA LOGIC ---

        // 4. Phần còn lại giữ nguyên (lấy địa chỉ, tính tổng tiền...)
        List<DiaChi> addresses = diaChiDAO.findByKhachHang_MaKHAndTrangThaiXoaFalse(khachHang.getMaKH()); 
        double totalPrice = cartItems.stream()
                .mapToDouble(item -> item.getSoLuong() * item.getSanPham().getDonGia())
                .sum();
        
        model.addAttribute("addresses", addresses);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("newAddress", new DiaChi());
        model.addAttribute("title", "Đặt hàng");
        model.addAttribute("role", "customer");
        return "customer/KH_DatHang";
    }




    @PostMapping("/place-order")
    @Transactional
    public String placeOrder(@RequestParam("maDC") Integer maDC, RedirectAttributes redirectAttributes) {
        
        // 1. Lấy thông tin khách hàng và địa chỉ (logic này của bạn đã đúng)
        Users currentUser = authService.getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";
        
        KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());
        if (khachHang == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin khách hàng.");
            return "redirect:/customer/checkout";
        }

        DiaChi diaChi = diaChiDAO.findById(maDC).orElse(null);
        if (diaChi == null || !diaChi.getKhachHang().getMaKH().equals(khachHang.getMaKH())) {
             redirectAttributes.addFlashAttribute("error", "Địa chỉ không hợp lệ.");
             return "redirect:/customer/checkout";
        }
        
        // Tạo chuỗi JSON (logic này của bạn đã đúng)
        String diaChiJsonString;
        try {
            ObjectNode diaChiJson = objectMapper.createObjectNode();
            diaChiJson.put("TenNN", diaChi.getTenNN());
            diaChiJson.put("SDT", diaChi.getSdt());
            diaChiJson.put("DiemGiao", diaChi.getDiemGiao());
            diaChiJsonString = objectMapper.writeValueAsString(diaChiJson);
        } catch (Exception e) {
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý địa chỉ!");
            return "redirect:/customer/checkout";
        }

        // 2. [SỬA] Lấy danh sách sản phẩm (từ "Mua Ngay" hoặc "Giỏ Hàng")
        List<GioHang> cartItemsToOrder;
        List<Integer> selectedItemsIds = null; 
        
        // Lấy cờ "isBuyNow" (đã được đặt ở GET /checkout)
        boolean isBuyNow = session.getAttribute("isBuyNow") != null && (Boolean) session.getAttribute("isBuyNow");

        if (isBuyNow) {
            // Luồng MUA NGAY: Lấy từ session "checkoutItems"
            cartItemsToOrder = (List<GioHang>) session.getAttribute("checkoutItems");
        } else {
            // Luồng GIỎ HÀNG: Lấy từ session "selectedCartItemIds"
            selectedItemsIds = (List<Integer>) session.getAttribute("selectedCartItemIds");
            if (selectedItemsIds == null || selectedItemsIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không có sản phẩm nào được chọn.");
                return "redirect:/customer/cart";
            }
            cartItemsToOrder = gioHangDAO.findAllById(selectedItemsIds);
        }
        
        if (cartItemsToOrder == null || cartItemsToOrder.isEmpty()) {
             redirectAttributes.addFlashAttribute("error", "Lỗi: Không tìm thấy sản phẩm để đặt hàng.");
             return "redirect:/customer/cart";
        }
        
        // 3. [SỬA] Tạo Hóa đơn (ĐÃ XÓA setTongTien)
        HoaDon newOrder = new HoaDon();
        newOrder.setKhachHang(khachHang); 
        newOrder.setDiaChiJson(diaChiJsonString); 
        newOrder.setNgayMua(new Date());
        newOrder.setTrangThai("Chờ duyệt");
        
        // Không set tổng tiền ở đây nữa
        // double totalPrice = ...
        // newOrder.setTongTien(totalPrice); // <--- ĐÃ XÓA DÒNG NÀY
        
        HoaDon savedOrder = hoaDonDAO.save(newOrder);
        
        // 4. Tạo Chi tiết HĐ và Cập nhật kho (thêm kiểm tra kho)
        for (GioHang item : cartItemsToOrder) {
            SanPham product = sanPhamDAO.findById(item.getSanPham().getMaSP()).orElse(null);
            
            if (product == null || product.getSoLuong() < item.getSoLuong()) {
                throw new RuntimeException("Sản phẩm " + item.getSanPham().getTenSP() + " không đủ hàng.");
            }

            HoaDonCT orderDetail = new HoaDonCT();
            orderDetail.setHoaDon(savedOrder);
            orderDetail.setSanPham(product);
            orderDetail.setSoLuong(item.getSoLuong());
            orderDetail.setDonGia(product.getDonGia()); // Lấy giá lúc đặt
            hoaDonCTDAO.save(orderDetail);
            
            product.setSoLuong(product.getSoLuong() - item.getSoLuong());
            sanPhamDAO.save(product);
        }
        
        // 5. [SỬA] Dọn dẹp session và giỏ hàng tùy theo luồng
        if (isBuyNow) {
            session.removeAttribute("checkoutItems");
        } else {
            gioHangDAO.deleteAllById(selectedItemsIds);
            session.removeAttribute("selectedCartItemIds");
        }
        session.removeAttribute("isBuyNow"); // Luôn xóa cờ này
        
        redirectAttributes.addFlashAttribute("orderSuccess", "Đặt hàng thành công!");
        return "redirect:/customer/orders"; 
    }

 // Trong GioHangController.java
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
            return 0;
        }
    }

    @GetMapping("/address/details/{maDC}")
    @ResponseBody
    public DiaChi getAddressDetails(@PathVariable("maDC") Integer maDC) {
        return diaChiDAO.findById(maDC).orElse(null);
    }
    @PostMapping("/add-addressmn")
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
                return "redirect:/customer/checkout";
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
        return "redirect:/customer/checkout";
    }

    @PostMapping("/update-addressmn")
    public String updateAddress(RedirectAttributes redirectAttributes) {
        try {
            Integer maDC = paramService.getInt("maDC", -1);
            String tenNN = paramService.getString("tenNN", "");
            String sdt = paramService.getString("sdt", "");
            String diemGiao = paramService.getString("diemGiao", "");
            boolean macDinh = paramService.getBoolean("macDinh", false);

            if (maDC == -1) {
                redirectAttributes.addFlashAttribute("error", "Mã địa chỉ không hợp lệ");
                return "redirect:/customer/checkout";
            }

            Optional<DiaChi> optionalAddress = diaChiDAO.findById(maDC);
            if (!optionalAddress.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy địa chỉ");
                return "redirect:/customer/checkout";
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
        return "redirect:/customer/checkout";
    }

    @PostMapping("/set-default-addressmn")
    public String setDefaultAddress(RedirectAttributes redirectAttributes) {
        try {
            // SỬ DỤNG PARAMSERVICE THAY CHO @RequestParam
            Integer addressId = paramService.getInt("addressId", -1);

            if (addressId == -1) {
                redirectAttributes.addFlashAttribute("error", "Mã địa chỉ không hợp lệ");
                return "redirect:/customer/checkout";
            }

            Optional<DiaChi> optionalAddress = diaChiDAO.findById(addressId);
            if (!optionalAddress.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy địa chỉ");
                return "redirect:/customer/checkout";
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
        return "redirect:/customer/checkout";
    }

    @PostMapping("/delete-addressmn")
    public String deleteAddress(RedirectAttributes redirectAttributes) {
        try {
            Integer addressId = paramService.getInt("addressId", -1);

            if (addressId == -1) {
                redirectAttributes.addFlashAttribute("error", "Mã địa chỉ không hợp lệ");
                return "redirect:/customer/checkout";
            }

            diaChiDAO.softDelete(addressId);
            redirectAttributes.addFlashAttribute("message", "Xóa địa chỉ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Xóa địa chỉ thất bại: " + e.getMessage());
        }
        return "redirect:/customer/checkout";
    }
    
    @GetMapping("/profile/edit-addressmn/{maDC}")
    public String editAddress(@PathVariable("maDC") Integer maDC, Model model) {
        try {
            Users currentUser = authService.getCurrentUser();
            if (currentUser == null) {
                return "redirect:/auth/login";
            }

            KhachHang customer = khachHangDAO.findByUser_UserID(currentUser.getUserID());
            if (customer == null) {
                model.addAttribute("error", "Không tìm thấy thông tin khách hàng");
                return "customer/checkout";
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
        
        return "customer/checkout";
    }
    @PostMapping("/buy-now")
    public String buyNow(@RequestParam("maSP") Integer maSP, 
                         @RequestParam("soLuong") Integer soLuong,
                         RedirectAttributes redirectAttributes) {

        // 1. Lấy thông tin người dùng
        Users currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());
        if (khachHang == null) {
            return "redirect:/auth/login?error=no_customer_profile";
        }

        // 2. Tìm sản phẩm
        Optional<SanPham> sanPhamOpt = sanPhamDAO.findById(maSP);
        if (!sanPhamOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không tồn tại!");
            return "redirect:/customer/index";
        }
        
        SanPham sanPham = sanPhamOpt.get();
        
        // 3. Kiểm tra số lượng tồn kho
        if (sanPham.getSoLuong() < soLuong) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm không đủ số lượng!");
            return "redirect:/customer/detailProduct?maSP=" + maSP;
        }

        // 4. Tạo một đối tượng GioHang "ảo" (không lưu vào DB)
        GioHang buyNowItem = new GioHang();
        buyNowItem.setKhachHang(khachHang);
        buyNowItem.setSanPham(sanPham);
        buyNowItem.setSoLuong(soLuong);
        
        // 5. Tạo một List chỉ chứa 1 item này
        List<GioHang> checkoutItems = new java.util.ArrayList<>();
        checkoutItems.add(buyNowItem);

        // 6. Lưu List này vào Session để trang checkout sử dụng
        session.setAttribute("checkoutItems", checkoutItems);

        // 7. Chuyển hướng đến trang thanh toán
        return "redirect:/customer/checkout";
    }

}
