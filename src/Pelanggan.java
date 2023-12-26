import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// Representasi objek Pelanggan dengan atribut namaPelanggan, noHp, dan alamat.
// Menyediakan metode untuk mengakses informasi dan representasi string.
public class Pelanggan {
    private String namaPelanggan;
    private String noHp;
    private String alamat;

    // Konstruktor untuk inisialisasi objek Pelanggan.
    public Pelanggan(String namaPelanggan, String noHp, String alamat) {
        this.namaPelanggan = namaPelanggan;
        this.noHp = noHp;
        this.alamat = alamat;
    }

    // Metode getter untuk mendapatkan namaPelanggan.
    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    // Metode getter untuk mendapatkan noHp.
    public String getNoHp() {
        return noHp;
    }

    // Metode getter untuk mendapatkan alamat.
    public String getAlamat() {
        return alamat;
    }
    public static List<Pelanggan> bacaSemuaPelanggan() {
        List<Pelanggan> daftarPelanggan = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/divamart_db", "root", "")) {
            String query = "SELECT * FROM pelanggan";
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    while (resultSet.next()) {
                        Pelanggan pelanggan = new Pelanggan(
                                resultSet.getString("nama_pelanggan"),
                                resultSet.getString("no_hp_pelanggan"),
                                resultSet.getString("alamat_pelanggan")
                        );
                        daftarPelanggan.add(pelanggan);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daftarPelanggan;
    }

    public static void tambahPelanggan(Pelanggan pelanggan) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/divamart_db", "root", "")) {
            String query = "INSERT INTO pelanggan (nama_pelanggan, no_hp_pelanggan, alamat_pelanggan) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, pelanggan.getNamaPelanggan());
                statement.setString(2, pelanggan.getNoHp());
                statement.setString(3, pelanggan.getAlamat());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePelanggan(Pelanggan pelanggan) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/divamart_db", "root", "")) {
            String query = "UPDATE pelanggan SET no_hp_pelanggan = ?, alamat_pelanggan = ? WHERE nama_pelanggan = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, pelanggan.getNoHp());
                statement.setString(2, pelanggan.getAlamat());
                statement.setString(3, pelanggan.getNamaPelanggan());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void hapusPelanggan(String namaPelanggan) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/divamart_db", "root", "")) {
            String query = "DELETE FROM pelanggan WHERE nama_pelanggan = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, namaPelanggan);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // New method to save the customer data to the database
    public void simpanKeDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/divamart_db", "root", "")) {
            String query = "INSERT INTO pelanggan (nama_pelanggan, no_hp_pelanggan, alamat_pelanggan) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, getNamaPelanggan());
                statement.setString(2, getNoHp());
                statement.setString(3, getAlamat());
                statement.executeUpdate();
            }
        }
    }
}