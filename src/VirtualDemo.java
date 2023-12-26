import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class VirtualDemo {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean inputValid = false;

        while (!inputValid) {
            try {
                // Menampilkan informasi waktu saat ini menggunakan fungsi getCurrentDate dan getCurrentTime
                System.out.println("          Data            ");
                System.out.println("Tanggal: " + getCurrentDate());
                System.out.println("Waktu  : " + getCurrentTime());

                // Input data untuk log in
                System.out.println("-------------------------------------");
                System.out.println("   SILAHKAN LOG IN TERLEBIH DAHULU");
                System.out.println("-------------------------------------");
                System.out.print("Username: ");
                String username = scanner.nextLine();

                System.out.print("Password: ");
                String password = scanner.nextLine();

                // Input dan verifikasi captcha
                String captcha = generateCaptcha();
                System.out.println("Captcha: " + captcha);

                System.out.print("Masukkan Captcha (case insensitive): ");
                String userCaptcha = scanner.nextLine();

                // Verifikasi log in dan captcha
                if (isValidLogin(username, password) && isValidCaptcha(captcha, userCaptcha)) {
                    System.out.println("Log in berhasil!");

                    int menuChoice;
                    do {
                        System.out.println("Menu Utama:");
                        System.out.println("1. Lihat Semua Barang");
                        System.out.println("2. Tambah Data Barang");
                        System.out.println("3. Hapus Data Barang");
                        System.out.println("4. Edit Harga Barang");  
                        System.out.println("5. Input Data Pembelian");
                        System.out.println("6. Keluar");
                        System.out.print("Pilih menu (1-6): ");
                        menuChoice = scanner.nextInt();
                        scanner.nextLine(); // Consumes the newline character
                    
                        switch (menuChoice) {
                            case 1:
                                lihatSemuaBarang();
                                break;
                            case 2:
                                tambahBarang();
                                break;
                            case 3:
                                hapusBarang();
                                break;
                            case 4:
                                updateBarang();  // Added case for updating data barang
                                break;
                            case 5:
                                Pelanggan pelanggan = inputPelanggan();
                                inputPembelian(pelanggan);
                                break;
                            case 6:
                                System.out.println("Keluar dari Menu Utama.");
                                inputValid = true;
                                break;
                            default:
                                System.out.println("Pilihan tidak valid. Silakan pilih lagi.");
                        }
                    } while (!inputValid);
                    
                    
                    
                } else {
                    System.out.println("Log in gagal. Pastikan username, password, dan captcha benar.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Error: Pastikan input sesuai dengan format yang benar.");
                System.out.println("Silahkan ulangi pengisian sesuai dengan ketentuan.");
                scanner.nextLine();
            } catch (NumberFormatException e) {
                System.out.println("Error: Harga Barang dan Jumlah Beli harus berupa angka.");
                System.out.println("Silahkan ulangi pengisian sesuai dengan ketentuan.");
                scanner.nextLine();
            } catch (JumlahBeliException e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine();
            // } catch (SQLException e) {
            //     System.out.println("Error: Terjadi kesalahan SQL - " + e.getMessage());
            //     scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Terjadi kesalahan: " + e.getMessage());
                scanner.nextLine();
            }
        }

        scanner.close();
    }
    private static void hapusBarang() {
        System.out.println("HAPUS DATA BARANG");
        System.out.println("-------------------------------------");
    
        System.out.print("Input Kode Barang yang akan dihapus: ");
        String kodeBarang = scanner.nextLine();
    
        // Panggil metode untuk menghapus barang
        hapusBarangDariDatabase(kodeBarang);
    
        System.out.println("Data barang berhasil dihapus.");
        System.out.println("=====================================");
    }
    private static void updateBarang() {
        System.out.println("EDIT DATA BARANG");
        System.out.println("-------------------------------------");

        System.out.print("Input Kode Barang yang akan diupdate: ");
        String kodeBarang = scanner.nextLine();

        // Retrieve existing barang data
        Barang existingBarang = Barang.getBarangByKode(kodeBarang);

        if (existingBarang != null) {
            System.out.println("Data Barang Saat Ini:");
            System.out.println(existingBarang);

            System.out.print("Nama Barang         : ");
            String namaBarang = scanner.nextLine();

            double hargaBarang;
            try {
                System.out.print("Ubah Harga Barang   : ");
                hargaBarang = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Harga Barang harus berupa angka.");
                return;
            }

            // Create a new Barang object with updated data
            Barang updatedBarang = new Barang(kodeBarang, namaBarang, hargaBarang);

            // Update barang in the database
            Barang.updateBarang(updatedBarang);

            System.out.println("Data barang berhasil diupdate.");
        } else {
            System.out.println("Error: Barang dengan kode " + kodeBarang + " tidak ditemukan.");
        }
    }
    private static Pelanggan inputPelanggan() {
        System.out.println("INPUT DATA PELANGGAN");
        System.out.println("-------------------------------------");
        System.out.print("Input Nama Pelanggan   : ");
        String namaPelanggan = scanner.nextLine();
    
        System.out.print("Input No. HP Pelanggan : ");
        String noHpPelanggan = scanner.nextLine();
    
        System.out.print("Input Alamat Pelanggan : ");
        String alamatPelanggan = scanner.nextLine();
    
        Pelanggan pelanggan = new Pelanggan(namaPelanggan, noHpPelanggan, alamatPelanggan);
    
        try {
            pelanggan.simpanKeDatabase();
            System.out.println("Data pelanggan berhasil disimpan.");
        } catch (SQLException e) {
            System.out.println("Gagal menyimpan data pelanggan: " + e.getMessage());
        }
    
        return pelanggan;
    }
    
    private static void inputPembelian(Pelanggan pelanggan) throws JumlahBeliException {
        System.out.println("INPUT DATA PEMBELIAN BARANG");
        System.out.println("-------------------------------------");
    
        System.out.print("No. Faktur              : ");
        String noFaktur = scanner.nextLine();
    
        System.out.print("Input Kode Barang       : ");
        String kodeBarang = scanner.nextLine();
    
        System.out.print("Input Nama Barang       : ");
        String namaBarang = scanner.nextLine();
    
        double hargaBarang;
        try {
            System.out.print("Input Harga Barang      : ");
            hargaBarang = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Error: Harga Barang harus berupa angka.");
            return;
        }
    
        int jumlahBeli;
        try {
            System.out.print("Input Jumlah Beli       : ");
            jumlahBeli = Integer.parseInt(scanner.nextLine());
            if (jumlahBeli < 0) {
                throw new JumlahBeliException("Jumlah Beli harus lebih besar dari 0.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Jumlah Beli harus berupa angka.");
            return;
        } catch (JumlahBeliException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
    
        System.out.print("Input Nama Kasir        : ");
        String kasir = scanner.nextLine();
    
        // Membuat objek Pemesanan dengan data yang telah diinput
        Pemesanan pesanan = new Pemesanan(noFaktur, pelanggan, new Barang(kodeBarang, namaBarang, hargaBarang), jumlahBeli, kasir);
    
        // Menampilkan informasi pemesanan
        pesanan.tampilkanInformasi();
        
        //Save Order
        pesanan.simpanPemesanan();
    
        System.out.println("=====================================");
    }
    



    private static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private static String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a zzz");
        return timeFormat.format(Calendar.getInstance().getTime());
    }

    private static String generateCaptcha() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 6;
        StringBuilder captcha = new StringBuilder();

        for (int i = 0; i < length; i++) {
            captcha.append(characters.charAt((int) (Math.random() * characters.length())));
        }

        return captcha.toString();
    }

    private static boolean isValidLogin(String username, String password) {
        return "diva".equals(username) && "diva123".equals(password);
    }

    private static boolean isValidCaptcha(String generatedCaptcha, String userCaptcha) {
        return generatedCaptcha.equalsIgnoreCase(userCaptcha);
    }

    private static void lihatSemuaBarang() {
        List<Barang> daftarBarang = Barang.bacaSemuaBarang();

        System.out.println("DAFTAR SEMUA BARANG");
        System.out.println("-------------------------------------");
        System.out.println("| Kode   | Nama Barang           | Harga   |");
        System.out.println("-------------------------------------");

        for (Barang barang : daftarBarang) {
            System.out.printf("| %-6s | %-20s | %-7.2f |\n", barang.getKodeBarang(), barang.getNamaBarang(), barang.getHargaBarang());
        }

        System.out.println("-------------------------------------");
    }
    public static void tambahBarang() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/divamart_db", "root", "")) {
            String query = "INSERT INTO barang (kode_barang, nama_barang, harga_barang) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                System.out.print("Input Kode Barang   : ");
                String kodeBarang = scanner.nextLine();
    
                System.out.print("Input Nama Barang   : ");
                String namaBarang = scanner.nextLine();
    
                double hargaBarang;
                try {
                    System.out.print("Input Harga Barang  : ");
                    hargaBarang = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Error: Harga Barang harus berupa angka.");
                    return;
                }
    
                statement.setString(1, kodeBarang);
                statement.setString(2, namaBarang);
                statement.setDouble(3, hargaBarang);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
        private static void hapusBarangDariDatabase(String kodeBarang) {
        // Panggil metode dari kelas Barang untuk menghapus barang dari database
        Barang.hapusBarang(kodeBarang);
}  

    
// Change the hapusBarang method
public static void hapusBarang(String kodeBarang) {
    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/divamart_db", "root", "")) {
        String query = "DELETE FROM barang WHERE kode_barang = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, kodeBarang);
            statement.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
