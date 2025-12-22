import java.io.Serializable;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String mssv;
    private String hoTen;
    private String ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;

    public Student() {
    }

    public Student(String mssv, String hoTen, String ngaySinh, String gioiTinh, String diaChi, String soDienThoai) {
        this.mssv = mssv;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
    }

    public String getMssv() {
        return mssv;
    }

    public void setMssv(String mssv) {
        this.mssv = mssv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    @Override
    public String toString() {
        return mssv + ";" + hoTen + ";" + ngaySinh + ";" + gioiTinh + ";" + diaChi + ";" + soDienThoai;
    }
}
