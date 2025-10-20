package poly.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

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
        KhachHang khachHang = khachHangDAO.findById(3).orElse(null);
        if (khachHang == null) return "redirect:/auth/login";
        Optional<SanPham> sanPhamOpt = sanPhamDAO.findById(maSP);
        if (sanPhamOpt.isPresent()) {
            SanPham sanPham = sanPhamOpt.get();
            GioHang existingItem = gioHangDAO.findByKhachHangAndSanPham(khachHang, sanPham);
            if (existingItem != null) {
                existingItem.setSoLuong(existingItem.getSoLuong() + soLuong);
                gioHangDAO.save(existingItem);
            } else {
                GioHang newItem = new GioHang();
                newItem.setKhachHang(khachHang);
                newItem.setSanPham(sanPham);
                newItem.setSoLuong(soLuong);
                gioHangDAO.save(newItem);
            }
        }
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
        List<Integer> selectedItemsIds = (List<Integer>) session.getAttribute("selectedCartItemIds");
        if (selectedItemsIds == null || selectedItemsIds.isEmpty()) {
            return "redirect:/customer/cart";
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

        List<GioHang> cartItems = gioHangDAO.findAllById(selectedItemsIds);
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

    @PostMapping("/place-order")
    @Transactional
    public String placeOrder(@RequestParam("maDC") Integer maDC, RedirectAttributes redirectAttributes) {
        List<Integer> selectedItemsIds = (List<Integer>) session.getAttribute("selectedCartItemIds");
        if (selectedItemsIds == null || selectedItemsIds.isEmpty()) {
            return "redirect:/customer/cart";
        }
        KhachHang khachHang = khachHangDAO.findById(3).orElse(null);
        if (khachHang == null) return "redirect:/auth/login";
        DiaChi diaChi = diaChiDAO.findById(maDC).orElse(null);
        if (diaChi == null) return "redirect:/customer/checkout?error=address_not_found";
        
        String diaChiJsonString;
        try {
            // 2. Chuyển đối tượng DiaChi thành chuỗi JSON
            diaChiJsonString = objectMapper.writeValueAsString(diaChi);
        } catch (Exception e) {
            // Xử lý nếu có lỗi xảy ra khi chuyển đổi
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý địa chỉ, vui lòng thử lại!");
            return "redirect:/customer/checkout";
        }
        
        List<GioHang> cartItemsToOrder = gioHangDAO.findAllById(selectedItemsIds);
        HoaDon newOrder = new HoaDon();
        newOrder.setKhachHang(khachHang);
        
        newOrder.setDiaChiJson(diaChiJsonString);
        
        newOrder.setNgayMua(new Date());
        newOrder.setTrangThai("Chờ Duyệt");
        HoaDon savedOrder = hoaDonDAO.save(newOrder);
        
        for (GioHang item : cartItemsToOrder) {
            HoaDonCT orderDetail = new HoaDonCT();
            orderDetail.setHoaDon(savedOrder);
            orderDetail.setSanPham(item.getSanPham());
            orderDetail.setSoLuong(item.getSoLuong());
            orderDetail.setDonGia(item.getSanPham().getDonGia());
            hoaDonCTDAO.save(orderDetail);
            SanPham product = item.getSanPham();
            product.setSoLuong(product.getSoLuong() - item.getSoLuong());
            sanPhamDAO.save(product);
        }
        gioHangDAO.deleteAllById(selectedItemsIds);
        session.removeAttribute("selectedCartItemIds");
        redirectAttributes.addFlashAttribute("orderSuccess", "Đặt hàng thành công!");
        return "redirect:/customer/orders";
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
}
