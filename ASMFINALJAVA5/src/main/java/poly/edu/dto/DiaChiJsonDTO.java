package poly.edu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DiaChiJsonDTO {

    @JsonProperty("TenNN") // Khớp chính xác với key "TenNN" trong JSON
    private String tenNN;

    @JsonProperty("SDT") // Khớp chính xác với key "SDT" trong JSON
    private String sdt;

    @JsonProperty("DiemGiao") // Khớp chính xác với key "DiemGiao" trong JSON
    private String diemGiao;
}