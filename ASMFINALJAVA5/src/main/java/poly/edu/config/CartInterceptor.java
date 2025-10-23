package poly.edu.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import poly.edu.service.AuthService;
import poly.edu.dao.KhachHangDAO;
import poly.edu.dao.GioHangDAO;
import poly.edu.entity.Users;
import poly.edu.entity.KhachHang;
import poly.edu.entity.GioHang;

import java.util.List;
@Component
public class CartInterceptor implements HandlerInterceptor {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private KhachHangDAO khachHangDAO;
    
    @Autowired
    private GioHangDAO gioHangDAO;
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) throws Exception {
        
        // Chỉ xử lý khi có ModelAndView (tránh lỗi với API, static resources)
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
            try {
                Users currentUser = authService.getCurrentUser();
                int cartCount = 0;
                
                if (currentUser != null) {
                    KhachHang khachHang = khachHangDAO.findByUser_UserID(currentUser.getUserID());
                    if (khachHang != null) {
                        List<GioHang> cartItems = gioHangDAO.findByKhachHang(khachHang);
                        cartCount = cartItems.stream().mapToInt(GioHang::getSoLuong).sum();
                    }
                }
                
                // Thêm cartItemCount vào model
                modelAndView.addObject("cartItemCount", cartCount);
                
            } catch (Exception e) {
                // In lỗi để debug (có thể remove sau khi ổn định)
                System.err.println("Error in CartInterceptor: " + e.getMessage());
                modelAndView.addObject("cartItemCount", 0);
            }
        }
    }
}