package poly.edu.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poly.edu.dao.HoaDonDAO;
import poly.edu.entity.HoaDon;

import java.sql.Date;
import java.util.*;

/**
 * Quản lý đơn hàng (hiển thị 3 tab + duyệt/từ chối)
 * Route gốc: /employee/orders
 */
@Controller
@RequestMapping("/employee/orders")
public class QlDonHangController {

    @Autowired
    private HoaDonDAO hoaDonDAO;

    @PersistenceContext
    private EntityManager em;

    /**
     * Lấy danh sách đơn theo trạng thái + tổng tiền.
     * Dựa trên bảng HoaDon, HoaDonCT, KhachHang để tính TongTien và SDT.
     */
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

        List<Map<String, Object>> out = new ArrayList<>();
        for (Object rObj : rows) {
            Object[] r = (Object[]) rObj;

            // Cột
            Integer maHD      = (Integer) r[0];
            Date ngayMua      = (Date)   r[1];
            Object maNVObj    = r[2];                 // có thể Number hoặc String
            String trangThai  = (String) r[3];
            String sdt        = (String) r[4];
            Number tong       = (Number) r[5];

            // Chuẩn hóa hiển thị
            String maNVStr;
            if (maNVObj == null) {
                maNVStr = "";
            } else if (maNVObj instanceof Number num) {
                maNVStr = String.format("NV%04d", num.intValue());
            } else {
                maNVStr = maNVObj.toString();
            }

            Map<String, Object> m = new HashMap<>();
            m.put("maHD", maHD);
            m.put("maHDStr", String.format("HD%04d", maHD));
            m.put("ngayMua", ngayMua);
            m.put("maNV", maNVStr);
            m.put("trangThai", trangThai);
            m.put("sdt", sdt);
            m.put("tongTien", (tong == null) ? 0D : tong.doubleValue());
            out.add(m);
        }
        return out;
    }

    /* ===================== VIEW ===================== */

    // Hiển thị 3 tab. Tab active lấy từ ?tab=cho|duyet|tuchoi (mặc định: cho)
    @GetMapping
    public String view(@RequestParam(name = "tab", defaultValue = "cho") String tab,
                       Model model) {
        model.addAttribute("choDuyet", fetchOrdersByStatus("Chờ duyệt"));
        model.addAttribute("daDuyet",  fetchOrdersByStatus("Đang giao"));   // map sang tab "Đã Duyệt"
        model.addAttribute("tuChoi",   fetchOrdersByStatus("Đã từ chối"));
        model.addAttribute("activeTab", tab);
        return "employee/NV_QLdonhang";  // đúng với file HTML bạn đang dùng
    }

    /* ===================== ACTIONS ===================== */

    // Duyệt: chuyển "Đang giao" rồi nhảy sang tab ĐÃ DUYỆT
    @PostMapping("/{id}/approve")
    @Transactional
    public String approve1(@PathVariable("id") Integer id, RedirectAttributes ra) {
        updateStatus(id, "Đang giao");
        ra.addAttribute("tab", "duyet");
        return "redirect:/employee/orders";
    }

    // Từ chối: chuyển "Đã từ chối" rồi nhảy sang tab ĐÃ TỪ CHỐI
    @PostMapping("/{id}/reject")
    @Transactional
    public String reject1(@PathVariable("id") Integer id, RedirectAttributes ra) {
        updateStatus(id, "Đã từ chối");
        ra.addAttribute("tab", "tuchoi");
        return "redirect:/employee/orders";
    }

    /* ====== (Tùy chọn) Giữ thêm route cũ nếu trước đó bạn đã wiring /approve/{id}, /reject/{id} ====== */

    @PostMapping("/approve/{id}")
    @Transactional
    public String approve2(@PathVariable("id") Integer id, RedirectAttributes ra) {
        updateStatus(id, "Đang giao");
        ra.addAttribute("tab", "duyet");
        return "redirect:/employee/orders";
    }

    @PostMapping("/reject/{id}")
    @Transactional
    public String reject2(@PathVariable("id") Integer id, RedirectAttributes ra) {
        updateStatus(id, "Đã từ chối");
        ra.addAttribute("tab", "tuchoi");
        return "redirect:/employee/orders";
    }

    /* ====== (Tùy chọn) Endpoint JSON để gọi AJAX nếu bạn không muốn tạo <form> trong HTML ====== */

    @PostMapping("/{id}/approve.json")
    @ResponseBody
    @Transactional
    public Map<String, Object> approveJson(@PathVariable("id") Integer id) {
        boolean ok = updateStatus(id, "Đang giao");
        return Map.of("ok", ok, "id", id, "nextTab", "duyet");
    }

    @PostMapping("/{id}/reject.json")
    @ResponseBody
    @Transactional
    public Map<String, Object> rejectJson(@PathVariable("id") Integer id) {
        boolean ok = updateStatus(id, "Đã từ chối");
        return Map.of("ok", ok, "id", id, "nextTab", "tuchoi");
    }

    /* ===================== HELPERS ===================== */

    /** Cập nhật trạng thái an toàn, không phụ thuộc DAO custom. */
    private boolean updateStatus(Integer id, String status) {
        HoaDon hd = hoaDonDAO.findById(id).orElse(null);
        if (hd == null) return false;
        hd.setTrangThai(status);
        hoaDonDAO.save(hd);
        return true;
    }
}
