package main;

import java.util.ArrayList;

import data.*;
import user.*;
import controller.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
public class App extends menu {
    public static ArrayList<pemain> player = new ArrayList<>();
    public static ArrayList<akun> listAkun = new ArrayList<>();
    public static ArrayList<statistik> statistikPlayer = new ArrayList<>();
    public static ArrayList<kontrakPemain> contract = new ArrayList<>();
    public static ArrayList<interfacePemain> pemainInterface = new ArrayList<>();
    
    public static void main(String[] args) throws IOException, ParseException, SQLException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        while (true) {
            clearScreen();
            menu obj = new menu();
            obj.index();
            int pilihan = Integer.parseInt(br.readLine());
            switch (pilihan) {
                case 1:
                    login();
                    break;
                case 2:
                    regis();
                    break;
                case 3:
                    exit str = new exit();
                    str.exitProgram();
                    System.exit(0);
                default:
                    break;
            }
        }
    }

    public static void login() throws IOException, SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String username, password;
        clearScreen();
        System.out.print("Masukkan Username : ");
        username = br.readLine();
        MaskingThread masking = new MaskingThread("Masukkan Password : ");
        Thread thread = new Thread(masking);
        thread.start();
        password = br.readLine();
        masking.stopMasking();
        String passwordHash = akunController.hashPass(password);
    
        akunController dataAkunAdmin = new akunController(0, null, null, null);
        dataAkunAdmin.readAkun(username, passwordHash);
        
        try {
            akun login = null;
            for (akun akn : App.listAkun) {
                // pengecekan username dan password yang di input dengan yang ada di database
                if (akn.getUsername().equals(username) && akn.getPassword().equals(passwordHash)) {
                    login = akn;
                    break;
                }
            }
            if (login != null) {
                System.out.println("Login Berhasil");
                pause();
                clearScreen();
                String role = login.getRole();
                switch (role) {
                    case "ADMIN":
                        menuTambahDetail(pemainInterface, player, statistikPlayer, contract);
                        break;
                    case "USER":
                        // menuUser();
                    default:
                    break;
                }
            } else {
                System.out.println("Username atau Password Salah");
                pause();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Terjadi kesalahan saat mencoba login: " + e.getMessage());
            pause();
        }
    }    

    public static void regis() throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(input);
        String username, password, role;
        clearScreen();
        System.out.print("Masukkan username : ");
        username = br.readLine();
        System.out.print("Masukkan password : ");
        password = br.readLine();

        while (true) {
            System.out.print("Admin/User      : ");
            role = br.readLine().trim();
            role = role.toUpperCase();
    
            if ("ADMIN".equals(role) || "USER".equals(role)) {
                break;
            } else {
                System.out.println("Role harus 'Admin' atau 'User'. Silakan coba lagi.");
            }
        }

        String hash = akunController.hashPass(password);
        
        akunController.regis(username, hash, role);
        pause();
    }

    private static void menuTambahDetail(ArrayList<interfacePemain> pemainInterface, ArrayList<pemain> player, ArrayList<statistik> stat, ArrayList<kontrakPemain> contract) throws IOException, ParseException, SQLException {
        playerController.readPlayer();
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        menu obj = new menu();
        int pilihan;
        
        do {
            obj.tambahDetail();
            pilihan = Integer.parseInt(br.readLine());
            switch (pilihan) {
                case 1:
                    tambahPemain();
                    break;
                case 2:
                    tampilkanPemain(player);
                    break;
                case 3:
                    updatePemain(player);
                    break;
                case 4:
                    hapusPemain(player);
                    break;
                case 5:
                    lihatStatistik(player);
                    break;
                case 6:
                    tambahStatistik(player);
                    break;
                case 7:
                    pause();
                    break;
                default:
                    break;
            }
        } while (pilihan != 7);
    }

    @SuppressWarnings("unused")
    private static void menuLihatDetail(ArrayList<interfacePemain> pemainInterface, ArrayList<pemain> player, ArrayList<statistik> stat, ArrayList<kontrakPemain> contract) throws IOException, ParseException, SQLException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        menu obj = new menu();
        int pilihan;
        
        do {
            clearScreen();
            obj.lihatDetail();
            pilihan = Integer.parseInt(br.readLine());
            switch (pilihan) {
                case 1:
                    tampilkanPemain(player);
                    pause();
                    break;
                    case 2:
                    // lihatStatistik(pemainInterface, player, stat);               
                    pause();
                    break;
                    case 3:
                    // lihatKontrakPemain(pemainInterface, player, contract);
                    pause();
                    break;
                case 4:
                    pause();
                    break;
                default:
                    break;
            }
        } while (pilihan != 4);
    }
    
    private static void tampilkanPemain(ArrayList<pemain> player) throws IOException, SQLException {
        if (player.isEmpty()) {
            System.out.println("Belum Ada Data Pemain");
            pause();
        } else {
            clearScreen();
            System.out.println("===========================================================================");
            System.out.printf("|%-4s| %-25s| %-15s| %-15s| %-6s| %n", "No", "Nama Pemain", "Asal Klub", "Tanggal Lahir", "Umur" );
            for (int i = 0; i < player.size(); i++) {
                pemain plyr = player.get(i);
                String namaPemain = plyr.getNamaPemain();
                int umur = plyr.getUmur();
                String asalKlub = plyr.getAsalKlub();
                LocalDate tanggalLahir = plyr.getTanggalLahir();
                System.out.println("===========================================================================");
                System.out.printf("|%-4d| %-25s| %-15s| %-15s| %-6d| %n", i + 1, namaPemain, asalKlub, tanggalLahir, umur);
            }
            System.out.println("===========================================================================");
        }
    }
    
    private static void lihatStatistik(ArrayList<pemain> player) throws IOException, SQLException {
        statistikController.readStatistics();
        System.out.println("Daftar Pemain saat ini:");
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        tampilkanPemain(player);
        System.out.print("Masukkan ID Pemain Yang Ingin Dilihat Statistik nya : ");
        int idPemain = Integer.parseInt(br.readLine());
        pemain cekPemain = null;

        for (pemain cek : player) {
            if (cek.getIdPemain() == idPemain) {
                cekPemain = cek;
                break;
            }
        }

        if (cekPemain == null) {
            System.out.println("Pemain dengan ID " + idPemain + " tidak ditemukan!!!");
            return;
        }

        statistik state = null;
        for (statistik st : statistikPlayer) {
            if (st.getIdPemain() == idPemain) {
                state = st;
                break;
            }
        }

        if (state == null) {
            System.out.println("Statistik Tidak Ditemukan");
            return;
        }

        String namaPemain = state.getNamaPemain(); 
        String posisi = state.getPosisi();
        int gol = state.getGol();
        int assist = state.getAssist();
        int match = state.getMatch();

        System.out.println("==========================================================================");
        System.out.printf("|%-25s| %-15s| %-8s| %-8s| %-8s| %n", "Nama Pemain", "Posisi", "Match", "Gol", "Assist" );
        System.out.println("==========================================================================");
        System.out.printf("|%-25s| %-15s| %-8d| %-8d| %-8d| %n", namaPemain, posisi, match, gol, assist );
        System.out.println("==========================================================================");
        
    }
    

    // private static void lihatKontrakPemain(ArrayList<interfacePemain> pemainInterface, ArrayList<pemain> player, ArrayList<kontrakPemain> contractList) throws IOException{
    //     InputStreamReader isr = new InputStreamReader(System.in);
    //     BufferedReader br = new BufferedReader(isr);
    //     tampilkanPemain(player, pemainInterface);
    //     System.out.print("Masukkan ID Pemain Yang Ingin Dilihat Kontraknya nya : ");
    //     int idPemain = Integer.parseInt(br.readLine());
    //     pemain cekPemain = null;

    //     for (pemain cek : player) {
    //         if (cek.getIdPemain() == idPemain) {
    //             cekPemain = cek;
    //             break;
    //         }
    //     }
    
    //     if (cekPemain == null) {
    //         System.out.println("Pemain dengan ID " + idPemain + " tidak ditemukan!!!");
    //         return;
    //     }
    
    //     kontrakPemain contract = null;
    //     for (kontrakPemain ct : contractList) {
    //         if (ct.getIdPemain() == idPemain) {
    //             contract = ct;
    //             break;
    //         }
    //     }

    //     if (contract == null) {
    //         System.out.println("Statistik tidak ditemukan.");
    //         return;
    //     }

    //     String namaPemain = contract.getNamaPemain(); 
    //     Date tanggalMulaiKontrak = contract.getTanggalMulaiKontrak();
    //     Date tanggalAkhirKontrak = contract.getTanggalAkhirKontrak();
    //     double nilaiKontrak = contract.getNilaiKontrak();
    //     double klausulPelepasan = contract.getKlausulPelepasan();
    //     SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    //     String formattedTanggalMulai = formatter.format(tanggalMulaiKontrak);
    //     String formattedTanggalAkhir = formatter.format(tanggalAkhirKontrak);
    //     System.out.println("=============================================================================================================");
    //     System.out.printf("|%-23s| %-21s| %-22s| %-15s| %-18s| %n", "Nama Pemain", "Tanggal Awal Kontrak", "Tanggal Akhir Kontrak", "Nilai Kontrak", "Klausul Pelepasan" );
    //     System.out.println("=============================================================================================================");
    //     System.out.printf("|%-23s| %-21s| %-22s| %-15.2f| %-18.2f| %n", namaPemain, formattedTanggalMulai, formattedTanggalAkhir, nilaiKontrak, klausulPelepasan);
    //     System.out.println("=============================================================================================================");
        
    // }

    private static void tambahPemain() throws IOException, SQLException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String namaPemain, asalKlub;
        LocalDate tanggalLahir;
        int umur;
        System.out.print("Masukkan Nama Pemain  : ");
        namaPemain = br.readLine();
        System.out.print("Masukkan Asal Klub    : ");
        asalKlub = br.readLine();
        while (true) {
            try {
                System.out.print("Masukkan Tanggal Lahir Pemain (YYYY-MM-DD) : ");
                String tanggalInput = br.readLine();
                tanggalLahir = LocalDate.parse(tanggalInput, DateTimeFormatter.ISO_LOCAL_DATE);
                umur = Period.between(tanggalLahir, LocalDate.now()).getYears();

                if (umur < 15) {
                    System.out.println("Umur pemain harus minimal 15 tahun.");
                        continue;
                }

                break;
            } catch (DateTimeParseException e) {
                System.out.println("Format tanggal yang dimasukkan salah. Silakan coba lagi.");
            }
        }
        System.out.println("Pemain Berhasil Ditambahkan");
        playerController.addPlayer(namaPemain, asalKlub, tanggalLahir, umur);
        pause();
    }

    private static void tambahStatistik(ArrayList<pemain> player) throws IOException, SQLException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        tampilkanPemain(player);
        System.out.print("Masukkan ID Pemain     : ");
        int idPemain = Integer.parseInt(br.readLine());
        pemain cekPemain = null;
        for (pemain cek : player) {
            if (cek.getIdPemain() == idPemain) {
                cekPemain = cek;
                break;
            }
        }
        if (cekPemain == null) {
            System.out.println("Pemain dengan ID tersebut tidak ditemukan!!!");
            return;
        }
        String namaPemain = cekPemain.getNamaPemain();
        System.out.print("Masukkan Posisi Pemain : ");
        String posisi = br.readLine();
        System.out.print("Masukkan Jumlah Gol    : ");
        int gol = Integer.parseInt(br.readLine());
        System.out.print("Masukkan Jumlah Assist : ");
        int assist = Integer.parseInt(br.readLine());
        System.out.print("Masukkan Jumlah Match  : ");
        int match = Integer.parseInt(br.readLine());
    
        statistikController.addStatistik(posisi, gol, assist, match, idPemain, namaPemain);
    
        System.out.println("Statistik Pemain Berhasil Ditambahkan");
        pause();
    }
    

    // private static void tambahKontrakPemain(ArrayList<kontrakPemain> contract, ArrayList<pemain> player) throws IOException, ParseException {
    //     InputStreamReader isr = new InputStreamReader(System.in);
    //     BufferedReader br = new BufferedReader(isr);
    //     System.out.print("Masukkan ID Pemain     : ");
    //     int idPemain = Integer.parseInt(br.readLine());
    //     pemain cekPemain = null;
    //     for (pemain cek : player) {
    //         if (cek.getIdPemain() == idPemain) {
    //             cekPemain = cek;
    //             break;
    //         }
    //     }
    //     if (cekPemain == null) {
    //         System.out.println("Pemain dengan ID tersebut tidak ditemukan!!!");
    //         return;
    //     }
    //     SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    //     String namaPemain = cekPemain.getNamaPemain();
    //     // String asalKlub = cekPemain.getAsalKlub();
    //     // int umur = cekPemain.getUmur();
    //     // double marketValue = cekPemain.getMarketValue();
    //     System.out.print("Masukkan Tanggal Awal      : ");
    //     Date tanggalMulaiKontrak = formatter.parse(br.readLine());
    //     System.out.print("Masukkan Tanggal Akhir     : ");
    //     Date tanggalAkhirKontrak = formatter.parse(br.readLine());
    //     System.out.print("Masukkan Nilai Kontrak     : ");
    //     double nilaiKontrak = Double.parseDouble(br.readLine());
    //     System.out.print("Masukkan Klausul Pelepasan : ");
    //     double klausulPelepasan = Double.parseDouble(br.readLine());
    //     kontrakPemain contractPlayer = new kontrakPemain(idPemain, namaPemain, idPemain, tanggalMulaiKontrak, tanggalAkhirKontrak, nilaiKontrak, klausulPelepasan);
    //     contract.add(contractPlayer);
    //     System.out.println("Kontrak Pemain Berhasil Ditambahkan");
    //     pause();
    // }

    private static void updatePemain(ArrayList<pemain> player) throws IOException, SQLException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        String newNamePlayer, newClub;
        LocalDate newBirthDate;
        int newAge;
        System.out.println("Update Data Pemain");
        tampilkanPemain(player);
        System.out.print("Data Nomor Ke Berapa Yang Ingin Di Update? : ");
        int cari = Integer.parseInt(br.readLine()) - 1;
        for (int i = 0; i < player.size(); i++ ) {
            if (cari == i) {
                pemain newPlayer = player.get(i);
                pemain selectedPlayer = player.get(cari);
                System.out.print("Masukkan Nama Pemain Baru  : ");
                newNamePlayer = br.readLine();
                System.out.print("Masukkan Asal Klub Baru    : ");
                newClub = br.readLine();
                while (true) {
                    try {
                        System.out.print("Masukkan Tanggal Lahir Pemain Baru (YYYY-MM-DD) : ");
                        String tanggalInput = br.readLine();
                        newBirthDate = LocalDate.parse(tanggalInput, DateTimeFormatter.ISO_LOCAL_DATE);
                        newAge = Period.between(newBirthDate, LocalDate.now()).getYears();
                        
                        if (newAge < 15) {
                            System.out.println("Umur pemain harus minimal 15 tahun.");
                            continue;
                        }
        
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Format tanggal yang dimasukkan salah. Silakan coba lagi.");
                    }
                }
                newPlayer.setNamaPemain(newNamePlayer);
                newPlayer.setAsalKlub(newClub);
                newPlayer.setTanggalLahir(newBirthDate);
                newPlayer.setUmur(newAge);
                int playerId = selectedPlayer.getIdPemain() + 1;
                System.out.println(playerId);
                playerController.updatePlayer(playerId, newNamePlayer, newClub, newBirthDate, newAge);
                System.out.println("Data Pemain Berhasil Diupdate");
                pause();
            }
        }
    }

    private static void hapusPemain(ArrayList<pemain> player) throws IOException, SQLException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("Hapus Data Pemain");
        tampilkanPemain(player);
        System.out.print("Data Nomor Ke Berapa Yang Ingin Di Hapus? : ");
        int hapus = Integer.parseInt(br.readLine()) - 1;
        player.remove(hapus);
        playerController.deletePlayer(hapus + 1);
        System.out.println("Data Pemain Berhasil Dihapus");
        pause();
    }

    // public static void perpanjanganKontrak(ArrayList<interfacePemain> pemainInterface, ArrayList<pemain> player, ArrayList<kontrakPemain> contractList) throws IOException, ParseException {
    //     SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    //     InputStreamReader isr = new InputStreamReader(System.in);
    //     BufferedReader br = new BufferedReader(isr);
    //     System.out.println("Perpanjangan Kontrak Pemain");
    //     tampilkanPemain(player, pemainInterface);
    //     System.out.print("Data Nomor Ke Berapa Yang Ingin Di Update? : ");
    //     int idPemain = Integer.parseInt(br.readLine());
    //     kontrakPemain contractToUpdate = null;
    //     for (kontrakPemain ct : contractList) {
    //         if (ct.getIdPemain() == idPemain && ct instanceof kontrakPemain) {
    //             contractToUpdate = ct;
    //             break;
    //         }
    //     }

    //     if (contractToUpdate == null) {
    //         System.out.println("Statistik tidak ditemukan.");
    //         return;
    //     }

    //     try {
    //         contractToUpdate.updatePemain(br, formatter);
    //     } catch (Exception e) {
    //         System.out.println("Terjadi kesalahan saat memperbarui data: " + e.getMessage());
    //         return;
    //     }

    //     int index = contractList.indexOf(contractToUpdate);
    //     if (index != -1) {
    //         contractList.set(index, contractToUpdate);
    //         System.out.println("Data Kontrak Pemain Berhasil Diupdate");
    //         pause();
    //     } else {
    //         System.out.println("Pemain dengan ID tersebut tidak ditemukan.");
    //     }
    // }

    // public static void updateNilaiKontrak(ArrayList<interfacePemain> pemainInterface, ArrayList<pemain> player, ArrayList<kontrakPemain> contractList) throws IOException, ParseException {
    //     InputStreamReader isr = new InputStreamReader(System.in);
    //     BufferedReader br = new BufferedReader(isr);
    //     System.out.println("Update Nilai Kontrak");
    //     tampilkanPemain(player, pemainInterface);
    //     System.out.print("Data Nomor Ke Berapa Yang Ingin Di Update? : ");
    //     int idPemain = Integer.parseInt(br.readLine());
    //     kontrakPemain contractToUpdate = null;
    //     for (kontrakPemain ct : contractList) {
    //         if (ct.getIdPemain() == idPemain && ct instanceof kontrakPemain) {
    //             contractToUpdate = ct;
    //             break;
    //         }
    //     }
        
    //     if (contractToUpdate == null) {
    //         System.out.println("Statistik tidak ditemukan.");
    //         return;
    //     }
        
    //     System.out.print("Masukkan Nilai Kontrak     : ");
    //     double nilaiKontrak = Double.parseDouble(br.readLine());
    //     System.out.print("Masukkan Klausul Pelepasan : ");
    //     double klausulPelepasan = Double.parseDouble(br.readLine());
    //     try {
    //         contractToUpdate.updatePemain(nilaiKontrak, klausulPelepasan);
    //     } catch (Exception e) {
    //         System.out.println("Terjadi kesalahan saat memperbarui data: " + e.getMessage());
    //         return;
    //     }

    //     int index = contractList.indexOf(contractToUpdate);
    //     if (index != -1) {
    //         contractList.set(index, contractToUpdate);
    //         System.out.println("Data Kontrak Pemain Berhasil Diupdate");
    //         pause();
    //     } else {
    //         System.out.println("Pemain dengan ID tersebut tidak ditemukan.");
    //     }
    // }

    
    static void clearScreen() {  
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    static void pause() throws IOException {
        System.out.println("Press Any Key To Continue...");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.readLine(); 
    }
}