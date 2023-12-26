// Pengecualian khusus untuk menangani kesalahan jumlah pembelian yang tidak valid.
public class JumlahBeliException extends Exception {
    // Konstruktor untuk membuat objek JumlahBeliException dengan pesan kesalahan.
    public JumlahBeliException(String message) {
        super(message);
    }
}
