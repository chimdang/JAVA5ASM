package poly.edu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data               // Tự động tạo getter, setter, toString(),...
@NoArgsConstructor  // Tự động tạo constructor không tham số
@AllArgsConstructor // Tự động tạo constructor có đủ tham số
public class DiaChiJsonDTO {

    /**
     * Tên người nhận. Phải khớp với key "TenNN" trong JSON.
     */
    private String TenNN;

    /**
     * Số điện thoại. Phải khớp với key "SDT" trong JSON.
     */
    private String SDT;

    /**
     * Địa chỉ giao hàng. Phải khớp với key "DiemGiao" trong JSON.
     */
    private String DiemGiao;
}