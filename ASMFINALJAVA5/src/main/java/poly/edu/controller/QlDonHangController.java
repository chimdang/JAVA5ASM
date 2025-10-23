package poly.edu.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import poly.edu.dao.HoaDonDAO;
import poly.edu.dao.HoaDonCTDAO;
import poly.edu.dao.SanPhamDAO;
import poly.edu.entity.HoaDon;
import poly.edu.entity.HoaDonCT;
import poly.edu.entity.SanPham;

import java.sql.Date;
import java.util.*;

/**
 * Quản lý đơn hàng (hiển thị & thao tác)
 * Route: /employee/orders
 * 
 * LOGIC SỐ LƯỢNG:
 * - Khi khách đặt hàng: Số lượng ĐÃ BỊ TRỪ → "Chờ duyệt"
 * - Khi admin DUYỆT: Không đụng số lượng → "Đang giao"
 * - Khi admin TỪ CHỐI: HOÀN LẠI số lượng → "Đã từ chối"
 */
@Controller
@RequestMapping("/employee/orders")
public class QlDonHangController {

    @Autowired
    private HoaDonDAO hoaDonDAO;

    @Autowired
    private HoaDonCTDAO hoaDonCTDAO;

    @Autowired
    private SanPhamDAO sanPhamDAO;

    @PersistenceContext
    private EntityManager em;

    /** Lấy danh sách đơn theo trạng thái + tổng tiền (HoaDon, HoaDonCT, KhachHang) */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchOrdersByStatus(String status) {
        String sql = """
            /* list orders by status + total */
            SELECT 
                hd.MaHD,
                hd.NgayMua,
                hd.MaNV,
                hd.TrangThai,
                kh.SDT AS SDTKhachHang,
                ISNULL(SUM(ct.SoLuong * ct.DonGia), 0) AS TongTien
            FROM HoaDon hd
            LEFT JOIN HoaDonCT ct ON ct.MaHD = hd.MaHD
            LEFT JOIN KhachHang kh ON kh.MaKH = hd.MaKH
            WHERE hd.TrangThai = :st
            GROUP BY hd.MaHD, hd.NgayMua, hd.MaNV, hd.TrangThai, kh.SDT
            ORDER BY hd.NgayMua DESC, hd.MaHD DESC
        """;

        var rows = em.createNativeQuery(sql)
                .setParameter("st", status)
                .getResultList();

        List<Map<String,Object>> out = new ArrayList<>();
        for (Object rObj : rows) {
            Object[] r = (Object[]) rObj;
            Integer maHD     = (Integer) r[0];
            Date ngayMua     = (Date)   r[1];
            Integer maNV     = (Integer) r[2];
            String trangThai = (String) r[3];
            String sdt       = (String) r[4];
            Number tong      = (Number) r[5];

            Map<String,Object> m = new HashMap<>();
            m.put("maHD", maHD);
            m.put("maHDStr", String.format("HD%04d", maHD));
            m.put("ngayMua", ngayMua);
            m.put("maNV", (maNV == null) ? "" : String.format("NV%04d", maNV));
            m.put("trangThai", trangThai);
            m.put("sdt", sdt);
            m.put("tongTien", (tong == null) ? 0D : tong.doubleValue());
            out.add(m);
        }
        return out;
    }

    /* ===================== VIEW ===================== */

    @GetMapping
    public String view(@RequestParam(value = "tab", defaultValue = "cho") String tab, Model model) {
        model.addAttribute("choDuyet", fetchOrdersByStatus("Chờ duyệt"));
        model.addAttribute("daDuyet",  fetchOrdersByStatus("Đang giao"));
        model.addAttribute("tuChoi",   fetchOrdersByStatus("Đã từ chối"));
        model.addAttribute("activeTab", tab);
        return "employee/NV_QLdonhang";
    }

    /* ===================== ACTIONS ===================== */

    /**
     * DUYỆT ĐƠN: Chuyển từ "Chờ duyệt" → "Đang giao"
     * KHÔNG TRỪ SỐ LƯỢNG vì đã trừ lúc khách đặt hàng rồi
     */
    @PostMapping("/approve/{id}")
    @Transactional
    public String approve(@PathVariable("id") Integer id, HttpSession session) {
        Integer maNV = getMaNVFromSession(session);
        var hd = hoaDonDAO.findById(id).orElse(null);
        
        if (hd != null && "Chờ duyệt".equals(hd.getTrangThai())) {
            System.out.println("=== DUYỆT ĐƠN HD " + id + " ===");
            System.out.println(">>> Chỉ cập nhật trạng thái, KHÔNG trừ số lượng (đã trừ lúc đặt hàng)");
            
            // CHỈ cập nhật trạng thái, KHÔNG đụng số lượng
            hd.setTrangThai("Đang giao");
            assignNhanVien(hd, maNV);
            hoaDonDAO.save(hd);
            
            System.out.println("=== HOÀN TẤT DUYỆT ===");
        }
        
        return "redirect:/employee/orders?tab=duyet";
    }

    /**
     * TỪ CHỐI ĐƠN: Chuyển từ "Chờ duyệt" → "Đã từ chối"
     * HOÀN LẠI SỐ LƯỢNG vì đã trừ lúc đặt hàng
     */
    @PostMapping("/reject/{id}")
    @Transactional
    public String reject(@PathVariable("id") Integer id, HttpSession session) {
        Integer maNV = getMaNVFromSession(session);
        var hd = hoaDonDAO.findById(id).orElse(null);
        
        if (hd != null) {
            String trangThaiCu = hd.getTrangThai();
            System.out.println("=== TỪ CHỐI ĐƠN HD " + id + " ===");
            System.out.println("Trạng thái cũ: " + trangThaiCu);
            
            // Lấy chi tiết đơn hàng từ DB
            List<HoaDonCT> chiTietList = em.createQuery(
                "SELECT ct FROM HoaDonCT ct WHERE ct.hoaDon.maHD = :maHD", HoaDonCT.class)
                .setParameter("maHD", id)
                .getResultList();
            
            System.out.println("Số lượng chi tiết: " + chiTietList.size());
            
            // HOÀN LẠI số lượng nếu đơn ở trạng thái "Chờ duyệt" hoặc "Đang giao"
            // (Không hoàn nếu đã từ chối trước đó rồi)
            if ("Chờ duyệt".equals(trangThaiCu) || "Đang giao".equals(trangThaiCu)) {
                System.out.println(">>> HOÀN LẠI SỐ LƯỢNG SẢN PHẨM");
                
                for (HoaDonCT ct : chiTietList) {
                    Integer maSP = ct.getSanPham().getMaSP();
                    Integer soLuongMua = ct.getSoLuong();
                    
                    SanPham sp = sanPhamDAO.findById(maSP).orElse(null);
                    if (sp != null) {
                        int soLuongCu = sp.getSoLuong();
                        int soLuongMoi = soLuongCu + soLuongMua;
                        
                        System.out.println("SP " + maSP + ": " + soLuongCu + " + " + soLuongMua + " = " + soLuongMoi);
                        
                        sp.setSoLuong(soLuongMoi);
                        sanPhamDAO.save(sp);
                    }
                }
            } else {
                System.out.println(">>> Đơn đã từ chối trước đó -> KHÔNG hoàn lại");
            }
            
            // Cập nhật trạng thái
            hd.setTrangThai("Đã từ chối");
            assignNhanVien(hd, maNV);
            hoaDonDAO.save(hd);
            
            System.out.println("=== HOÀN TẤT TỪ CHỐI ===");
        }
        
        return "redirect:/employee/orders?tab=tuchoi";
    }

    /* ===================== HELPERS ===================== */

    /** Lấy maNV đang đăng nhập từ session */
    private Integer getMaNVFromSession(HttpSession session) {
        Object s = session.getAttribute("maNV");
        if (s instanceof Integer) return (Integer) s;
        if (s instanceof String) {
            String str = (String) s;
            if (str.matches("\\d+")) return Integer.valueOf(str);
        }
        return null;
    }

    /**
     * Gán nhân viên xử lý vào hóa đơn
     */
    private void assignNhanVien(HoaDon hd, Integer maNV) {
        if (hd == null || maNV == null) return;

        try {
            var m = hd.getClass().getMethod("setMaNV", Integer.class);
            m.invoke(hd, maNV);
            return;
        } catch (NoSuchMethodException ignore) {
        } catch (Exception e) {
        }

        try {
            Class<?> nvClass = Class.forName("poly.edu.entity.NhanVien");
            Object nvRef = em.getReference(nvClass, maNV);
            var m2 = hd.getClass().getMethod("setNhanVien", nvClass);
            m2.invoke(hd, nvRef);
        } catch (Exception e) {
        }
    }
}