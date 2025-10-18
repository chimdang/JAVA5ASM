CREATE DATABASE ASMNhom2
USE [ASMNhom2]
GO
/****** Object:  Table [dbo].[DanhMuc]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DanhMuc](
	[MaDM] [int] IDENTITY(1,1) NOT NULL,
	[TenDM] [nvarchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[MaDM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[DiaChi]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DiaChi](
	[MaDC] [int] IDENTITY(1,1) NOT NULL,
	[MaKH] [int] NULL,
	[TenNN] [nvarchar](100) NULL,
	[MacDinh] [bit] NULL,
	[SDT] [nvarchar](15) NULL,
	[DiemGiao] [nvarchar](255) NULL,
	[TrangThaiXoa] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[MaDC] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[GioHang]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[GioHang](
	[MaGH] [int] IDENTITY(1,1) NOT NULL,
	[MaKH] [int] NULL,
	[MaSP] [int] NULL,
	[SoLuong] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[MaGH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HoaDon]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HoaDon](
	[MaHD] [int] IDENTITY(1,1) NOT NULL,
	[MaKH] [int] NULL,
	[MaNV] [int] NULL,
	[DiaChiJson] [nvarchar](max) NULL,
	[TrangThai] [nvarchar](50) NULL,
	[NgayMua] [date] NULL,
PRIMARY KEY CLUSTERED 
(
	[MaHD] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HoaDonCT]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HoaDonCT](
	[MaHDCT] [int] IDENTITY(1,1) NOT NULL,
	[MaHD] [int] NULL,
	[MaSP] [int] NULL,
	[SoLuong] [int] NULL,
	[DonGia] [float] NULL,
PRIMARY KEY CLUSTERED 
(
	[MaHDCT] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[KhachHang]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KhachHang](
	[MaKH] [int] IDENTITY(1,1) NOT NULL,
	[TenKH] [nvarchar](100) NULL,
	[SDT] [nvarchar](15) NULL,
	[UserID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[MaKH] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[NhanVien]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[NhanVien](
	[MaNV] [int] IDENTITY(1,1) NOT NULL,
	[TenNV] [nvarchar](100) NULL,
	[UserID] [int] NULL,
	[VaiTro] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[MaNV] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[NhapKho]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[NhapKho](
	[MaNK] [int] IDENTITY(1,1) NOT NULL,
	[MaSP] [int] NULL,
	[SoLuong] [int] NULL,
	[NgayNK] [date] NULL,
PRIMARY KEY CLUSTERED 
(
	[MaNK] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SanPham]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SanPham](
	[MaSP] [int] IDENTITY(1,1) NOT NULL,
	[MaDM] [int] NULL,
	[Hinh] [nvarchar](255) NULL,
	[TenSP] [nvarchar](100) NULL,
	[SoLuong] [int] NULL,
	[DonGia] [float] NULL,
	[PhanLoai] [nvarchar](100) NULL,
	[MoTa] [nvarchar](max) NULL,
	[TrangThai] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[MaSP] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 10/18/2025 10:38:59 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[UserID] [int] IDENTITY(1,1) NOT NULL,
	[Mail] [nvarchar](100) NULL,
	[Pass] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[DanhMuc] ON 

INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (1, N'Điện thoại')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (2, N'Máy tính bảng')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (3, N'Laptop')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (4, N'Tai nghe')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (5, N'Phụ kiện')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (6, N'Đồng hồ')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (7, N'Thiết bị nhà thông minh')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (8, N'Màn hình')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (9, N'Chuột & Bàn phím')
INSERT [dbo].[DanhMuc] ([MaDM], [TenDM]) VALUES (10, N'Thiết bị mạng')
SET IDENTITY_INSERT [dbo].[DanhMuc] OFF
GO
SET IDENTITY_INSERT [dbo].[DiaChi] ON 

INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (1, 1, N'Nguyễn Văn A', 1, N'0901111111', N'123 Lê Lợi, Q1, TP.HCM', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (2, 1, N'Nguyễn Văn A2', 0, N'0901111111', N'123 Lê Lợi, Q1, TP.HCM', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (3, 2, N'Nguyễn Văn B', 1, N'0902222222', N'45 Trần Phú, Hà Nội', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (4, 2, N'Nguyễn Văn B2', 0, N'0906666666', N'21 Bạch Đằng, Đà Nẵng', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (5, 2, N'Nguyễn Văn B3', 0, N'0903333333', N'22 Nguyễn Huệ, TP.HCM', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (6, 2, N'Nguyễn Văn B4', 0, N'0904444444', N'99 Lê Văn Sỹ, TP.HCM', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (7, 3, N'Nguyễn Văn C', 1, N'0905555555', N'12 Hai Bà Trưng, Hà Nội', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (8, 3, N'Nguyễn Văn C2', 0, N'0906666666', N'21 Bạch Đằng, Đà Nẵng', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (9, 3, N'Nguyễn Văn C3', 0, N'0907777777', N'10 Lý Thường Kiệt, Huế', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (10, 4, N'Nguyễn Văn D', 1, N'0908888888', N'55 Trần Hưng Đạo, TP.HCM', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (11, 5, N'Nguyễn Văn E', 1, N'0909999999', N'78 Cách Mạng, TP.HCM', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (12, 5, N'Nguyễn Văn E2', 0, N'0901010101', N'34 Võ Thị Sáu, Hà Nội', 0)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (13, 5, N'Nguyễn Văn E3', 0, N'0906666666', N'45 Nguyễn Văn Linh, Đà Nẵng', 1)
INSERT [dbo].[DiaChi] ([MaDC], [MaKH], [TenNN], [MacDinh], [SDT], [DiemGiao], [TrangThaiXoa]) VALUES (14, 5, N'Nguyễn Văn E4', 0, N'0906666666', N'98 Trần Hưng Đạo, Đà Nẵng', 0)
SET IDENTITY_INSERT [dbo].[DiaChi] OFF
GO
SET IDENTITY_INSERT [dbo].[GioHang] ON 

INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (1, 1, 1, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (2, 1, 2, 2)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (3, 1, 3, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (4, 2, 4, 3)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (5, 2, 5, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (6, 3, 6, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (7, 3, 7, 2)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (8, 3, 8, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (9, 3, 9, 2)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (10, 4, 10, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (11, 4, 1, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (12, 4, 2, 2)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (13, 4, 3, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (14, 4, 4, 3)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (15, 5, 5, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (16, 5, 6, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (17, 5, 7, 2)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (18, 5, 8, 1)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (19, 5, 9, 2)
INSERT [dbo].[GioHang] ([MaGH], [MaKH], [MaSP], [SoLuong]) VALUES (20, 5, 10, 1)
SET IDENTITY_INSERT [dbo].[GioHang] OFF
GO
SET IDENTITY_INSERT [dbo].[HoaDon] ON 

INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (1, 1, 2, N'{"TenNN":"Nguyễn Văn A","SDT":"0901111111","DiemGiao":"123 Lê Lợi, Q1, TP.HCM"}', N'Đang giao', CAST(N'2025-10-01' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (2, 2, 2, N'{"TenNN":"Trần Thị B","SDT":"0902222222","DiemGiao":"45 Trần Phú, Hà Nội"}', N'Đã từ chối', CAST(N'2025-10-02' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (3, 3, 2, N'{"TenNN":"Lê Văn C","SDT":"0903333333","DiemGiao":"12 Hai Bà Trưng, Hà Nội"}', N'Chờ duyệt', CAST(N'2025-10-03' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (4, 4, 3, N'{"TenNN":"Phạm Thị D","SDT":"0904444444","DiemGiao":"55 Trần Hưng Đạo, TP.HCM"}', N'Chờ duyệt', CAST(N'2025-10-04' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (5, 5, 3, N'{"TenNN":"Hoàng Văn E","SDT":"0905555555","DiemGiao":"78 Cách Mạng, TP.HCM"}', N'Chờ duyệt', CAST(N'2025-10-05' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (6, 4, 4, N'{"TenNN":"Phạm Thị D","SDT":"0908888888","DiemGiao":"55 Trần Hưng Đạo, TP.HCM"}', N'Đang giao', CAST(N'2025-10-06' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (7, 4, 4, N'{"TenNN":"Phạm Thị D","SDT":"0908888888","DiemGiao":"55 Trần Hưng Đạo, TP.HCM"}', N'Đang giao', CAST(N'2025-10-07' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (8, 2, 5, N'{"TenNN":"Trần Thị B","SDT":"0902222222","DiemGiao":"45 Trần Phú, Hà Nội"}', N'Đang giao', CAST(N'2025-10-08' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (9, 1, 5, N'{"TenNN":"Nguyễn Văn A","SDT":"0901111111","DiemGiao":"123 Lê Lợi, Q1, TP.HCM"}', N'Đang giao', CAST(N'2025-10-09' AS Date))
INSERT [dbo].[HoaDon] ([MaHD], [MaKH], [MaNV], [DiaChiJson], [TrangThai], [NgayMua]) VALUES (10, 1, 5, N'{"TenNN":"Nguyễn Văn A","SDT":"0901111111","DiemGiao":"123 Lê Lợi, Q1, TP.HCM"}', N'Chờ duyệt', CAST(N'2025-10-10' AS Date))
SET IDENTITY_INSERT [dbo].[HoaDon] OFF
GO
SET IDENTITY_INSERT [dbo].[HoaDonCT] ON 

INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (1, 1, 1, 1, 29990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (2, 1, 2, 2, 27990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (3, 1, 3, 1, 32990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (4, 1, 4, 1, 5990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (5, 2, 4, 49, 5990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (6, 2, 5, 3, 490000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (7, 2, 6, 1, 12990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (8, 2, 7, 1, 24990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (9, 3, 8, 2, 2490000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (10, 3, 9, 1, 1990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (11, 3, 10, 1, 8990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (12, 4, 1, 1, 29990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (13, 4, 3, 1, 32990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (14, 4, 4, 1, 5990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (15, 5, 5, 2, 490000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (16, 6, 6, 1, 12990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (17, 7, 7, 1, 24990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (18, 7, 8, 2, 2490000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (19, 7, 9, 1, 1990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (20, 7, 10, 1, 8990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (21, 8, 1, 1, 29990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (22, 8, 2, 2, 27990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (23, 9, 3, 1, 32990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (24, 9, 4, 1, 5990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (25, 9, 5, 4, 490000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (26, 10, 6, 3, 12990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (27, 10, 7, 1, 24990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (28, 10, 8, 2, 2490000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (29, 10, 9, 1, 1990000)
INSERT [dbo].[HoaDonCT] ([MaHDCT], [MaHD], [MaSP], [SoLuong], [DonGia]) VALUES (30, 10, 10, 1, 8990000)
SET IDENTITY_INSERT [dbo].[HoaDonCT] OFF
GO
SET IDENTITY_INSERT [dbo].[KhachHang] ON 

INSERT [dbo].[KhachHang] ([MaKH], [TenKH], [SDT], [UserID]) VALUES (1, N'Nguyễn Văn A', N'0901111111', 6)
INSERT [dbo].[KhachHang] ([MaKH], [TenKH], [SDT], [UserID]) VALUES (2, N'Trần Thị B', N'0902222222', 7)
INSERT [dbo].[KhachHang] ([MaKH], [TenKH], [SDT], [UserID]) VALUES (3, N'Lê Văn C', N'0903333333', 8)
INSERT [dbo].[KhachHang] ([MaKH], [TenKH], [SDT], [UserID]) VALUES (4, N'Phạm Thị D', N'0904444444', 9)
INSERT [dbo].[KhachHang] ([MaKH], [TenKH], [SDT], [UserID]) VALUES (5, N'Hoàng Văn E', N'0905555555', 10)
SET IDENTITY_INSERT [dbo].[KhachHang] OFF
GO
SET IDENTITY_INSERT [dbo].[NhanVien] ON 

INSERT [dbo].[NhanVien] ([MaNV], [TenNV], [UserID], [VaiTro]) VALUES (1, N'Admin', 1, N'Admin')
INSERT [dbo].[NhanVien] ([MaNV], [TenNV], [UserID], [VaiTro]) VALUES (2, N'Nhân viên 1', 2, N'Nhân viên')
INSERT [dbo].[NhanVien] ([MaNV], [TenNV], [UserID], [VaiTro]) VALUES (3, N'Nhân viên 2', 3, N'Nhân viên')
INSERT [dbo].[NhanVien] ([MaNV], [TenNV], [UserID], [VaiTro]) VALUES (4, N'Nhân viên 3', 4, N'Nhân viên')
INSERT [dbo].[NhanVien] ([MaNV], [TenNV], [UserID], [VaiTro]) VALUES (5, N'Nhân viên 4', 5, N'Nhân viên')
SET IDENTITY_INSERT [dbo].[NhanVien] OFF
GO
SET IDENTITY_INSERT [dbo].[NhapKho] ON 

INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (1, 1, 50, CAST(N'2025-09-01' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (2, 2, 40, CAST(N'2025-09-02' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (3, 3, 20, CAST(N'2025-09-03' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (4, 4, 60, CAST(N'2025-09-04' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (5, 5, 100, CAST(N'2025-09-05' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (6, 6, 30, CAST(N'2025-09-06' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (7, 7, 25, CAST(N'2025-09-07' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (8, 8, 70, CAST(N'2025-09-08' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (9, 9, 60, CAST(N'2025-09-09' AS Date))
INSERT [dbo].[NhapKho] ([MaNK], [MaSP], [SoLuong], [NgayNK]) VALUES (10, 10, 15, CAST(N'2025-09-10' AS Date))
SET IDENTITY_INSERT [dbo].[NhapKho] OFF
GO
SET IDENTITY_INSERT [dbo].[SanPham] ON 

INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (1, 1, N'sp1.jpg', N'iPhone 15 Pro', 20, 29990000, N'Điện thoại', N'Smartphone cao cấp của Apple', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (2, 1, N'sp2.jpg', N'Samsung S24 Ultra', 25, 27990000, N'Điện thoại', N'Flagship mới nhất của Samsung', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (3, 3, N'sp3.jpg', N'MacBook Air M2', 10, 32990000, N'Laptop', N'Máy mỏng nhẹ hiệu năng cao', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (4, 4, N'sp4.jpg', N'AirPods Pro 2', 50, 5990000, N'Tai nghe', N'Tai nghe không dây chống ồn', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (5, 5, N'sp5.jpg', N'Sạc nhanh 25W', 100, 490000, N'Phụ kiện', N'Sạc nhanh cho điện thoại Samsung', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (6, 6, N'sp6.jpg', N'Apple Watch 9', 15, 12990000, N'Đồng hồ', N'Đồng hồ thông minh Apple', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (7, 2, N'sp7.jpg', N'iPad Pro 13"', 12, 24990000, N'Máy tính bảng', N'Máy tính bảng mạnh mẽ', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (8, 9, N'sp8.jpg', N'Logitech MX Keys', 40, 2490000, N'Bàn phím', N'Bàn phím cơ cao cấp', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (9, 10, N'sp9.jpg', N'TP-Link Router AX3000', 30, 1990000, N'Thiết bị mạng', N'Wi-Fi 6 tốc độ cao', N'Còn hàng')
INSERT [dbo].[SanPham] ([MaSP], [MaDM], [Hinh], [TenSP], [SoLuong], [DonGia], [PhanLoai], [MoTa], [TrangThai]) VALUES (10, 8, N'sp10.jpg', N'Dell UltraSharp 27"', 8, 8990000, N'Màn hình', N'Màn hình 4K chuyên đồ họa', N'Còn hàng')
SET IDENTITY_INSERT [dbo].[SanPham] OFF
GO
SET IDENTITY_INSERT [dbo].[Users] ON 

INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (1, N'admin@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (2, N'NhanVien1@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (3, N'NhanVien2@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (4, N'NhanVien3@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (5, N'NhanVien4@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (6, N'user1@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (7, N'user2@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (8, N'user3@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (9, N'user4@gmail.com', N'123')
INSERT [dbo].[Users] ([UserID], [Mail], [Pass]) VALUES (10, N'user5@gmail.com', N'123')
SET IDENTITY_INSERT [dbo].[Users] OFF
GO
ALTER TABLE [dbo].[DiaChi]  WITH CHECK ADD FOREIGN KEY([MaKH])
REFERENCES [dbo].[KhachHang] ([MaKH])
GO
ALTER TABLE [dbo].[GioHang]  WITH CHECK ADD FOREIGN KEY([MaKH])
REFERENCES [dbo].[KhachHang] ([MaKH])
GO
ALTER TABLE [dbo].[GioHang]  WITH CHECK ADD FOREIGN KEY([MaSP])
REFERENCES [dbo].[SanPham] ([MaSP])
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD FOREIGN KEY([MaKH])
REFERENCES [dbo].[KhachHang] ([MaKH])
GO
ALTER TABLE [dbo].[HoaDon]  WITH CHECK ADD FOREIGN KEY([MaNV])
REFERENCES [dbo].[NhanVien] ([MaNV])
GO
ALTER TABLE [dbo].[HoaDonCT]  WITH CHECK ADD FOREIGN KEY([MaHD])
REFERENCES [dbo].[HoaDon] ([MaHD])
GO
ALTER TABLE [dbo].[HoaDonCT]  WITH CHECK ADD FOREIGN KEY([MaSP])
REFERENCES [dbo].[SanPham] ([MaSP])
GO
ALTER TABLE [dbo].[KhachHang]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[NhanVien]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[NhapKho]  WITH CHECK ADD FOREIGN KEY([MaSP])
REFERENCES [dbo].[SanPham] ([MaSP])
GO
ALTER TABLE [dbo].[SanPham]  WITH CHECK ADD FOREIGN KEY([MaDM])
REFERENCES [dbo].[DanhMuc] ([MaDM])
GO
