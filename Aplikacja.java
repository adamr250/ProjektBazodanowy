import java.sql.*;
import java.util.Scanner;


public class Aplikacja {

    public Connection polacz() {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        Connection pol = null;
        try {
            pol = DriverManager.getConnection(url,"login","haslo");
            System.out.println("Poloczono");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if(pol == null) {
            System.out.println("Brak polaczenia");
        }
        return pol;
    }


    public static void main(String[] args){
        Aplikacja app = new Aplikacja();
        Connection pol = app.polacz();
        Scanner scan = new Scanner(System.in);
        String command;
        String help =   "Lista dostepnych komend: \n" +
                        "1. gildia\n" +
                        "2. gracze\n" +
                        "3. postacie\n" +
                        "4. role\n" +
                        "5. aktywni_weterani\n" +
                        "6. gracze_na_rok\n" +
                        "7. gotowi_rekruci\n" +
                        "8. aktualizuj_role_rekrutom\n" +
                        "9. edytuj_role\n" +
                        "10. dodaj_gracza\n" +
                        "11. usun_gracza\n\n" +
                        "Zamiast komendy można wpisac jej numer.\n" +
                        "Aby zamknac program wpisz \"exit\".\n";

        System.out.println("Wpiszy \"help\", aby uzyskać liste komend.");
        while (true) {
            command = scan.nextLine();
            if(command.equals("exit")) {
                break;

            } else if (command.equals("help")) {
                System.out.println(help);

            }  else if (command.equals("gildia") || command.equals("1")) {
                System.out.println("GRACZ - DATA DOLACZENIA - OSTATNIO AKTYWNY(dni)\n---------------------------");
                ResultSet rs = app.gildia(pol);
                try {
                    while (rs.next()) {
                        System.out.println(rs.getString(1) + "  -  " + rs.getString(2) + " - " + rs.getInt(3));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println();

            }  else if (command.equals("gracze") || command.equals("2")) {
                System.out.println("ID - NICK - CZAS W GRZE(godz) - NAZWA_POSTACI\n---------------------------");
                ResultSet rs = app.gracze(pol);
                try {
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + "  -  " + rs.getString(2) + " - " + rs.getInt(3) + " - " + rs.getString(4));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println();

            }  else if (command.equals("postacie" ) || command.equals("3")) {
                System.out.println("GRACZ(NICK) - POSTAC(NICK) - RASA\n---------------------------");
                ResultSet rs = app.postacie(pol);
                try {
                    while (rs.next()) {
                        System.out.println(rs.getString(1) + " - " + rs.getString(2) + " - " + rs.getString(3));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println();

            }  else if (command.equals("role") || command.equals("4")) {
                System.out.println("Lista graczy i ich role:");
                System.out.println("---------------------------");
                ResultSet rs = app.rolaGraczZestawienie(pol);
                try {
                    while (rs.next()) {
                        System.out.println(rs.getString(1) + "  -  " + rs.getString(2));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println();

            } else if (command.equals("aktywni_weterani") || command.equals("5")) {
                System.out.println("Lista starych członkow, ktorzy byli niedawno aktywni:");
                System.out.println("ID - NICK\n---------------------------");
                ResultSet rs = app.aktywniWeterani(pol);
                try {
                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + " - " + rs.getString(2));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println();

            } else if(command.equals("gracze_na_rok") || command.equals("6")) {
                System.out.println("Ile graczy dolaczylo w danym roku:");
                System.out.println("---------------------------");
                ResultSet rs = app.graczeNaRok(pol);
                try {
                    while (rs.next()) {
                        System.out.println(rs.getString(1) + " - " + rs.getInt(2));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println();

            } else if (command.equals("gotowi_rekruci") || command.equals("7")) {
                System.out.println("Lista rekrutow, ktorzy odbyli 90-dniowy okres próbny:");
                System.out.println("---------------------------");
                ResultSet rs = app.rekruciDoPrzyjecia(pol);
                try {
                    while(rs.next()) {
                        System.out.println(rs.getString(1));
                    }
                } catch (SQLException e ) {
                    e.printStackTrace();
                }
                System.out.println();

            } else if (command.equals("aktualizuj_role_rekrutom") || command.equals("8")) {
                app.rekrutNaGracz(pol);
                System.out.println();
            } else if (command.equals("edytuj_role") || command.equals("9")) {
                app.zmienRole(pol);
                System.out.println();
            } else if (command.equals("dodaj_gracza") || command.equals("10")) {
                app.dodajGracza(pol);
                System.out.println();
            } else if (command.equals("usun_gracza") || command.equals("11")) {
                app.usunGracza(pol);
                System.out.println();
            } else {
                command = command.replaceAll("\\s+","");
                if (!command.equals("")) {
                    System.out.println("Nie znana komenda.");
                }
            }
        }
    }

    public ResultSet rolaGraczZestawienie(Connection pol) {
        String query = "select g.gracz, r.ROLA FROM ROLE r inner join rola_gracz rg on rg.rola_id = r.rola_id inner join gracz g on rg.gracz_id = g.gracz_id ORDER BY rg.gracz_id";
        Statement stmt;
        ResultSet rs = null;
        try {
            stmt = pol.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public ResultSet gildia(Connection pol) {
        String query = "select g1.gracz, to_char(g2.data_dolaczenia) as data, g2.ostatnio_aktywny_dni_temu from gracz g1 inner join gildia g2 on g1.gracz_id = g2.gracz_id";
        Statement stmt;
        ResultSet rs = null;
        try {
            stmt = pol.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public ResultSet gracze(Connection pol) {
        String query = "select * from gracz";
        Statement stmt;
        ResultSet rs = null;
        try {
            stmt = pol.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public ResultSet postacie(Connection pol) {
        String query = "select p.nazwa_postaci, g.gracz, r.rasa from postac p inner join gracz g on p.gracz_id = g.gracz_id inner join rasa r on p.rasa_id = r.rasa_id";
        Statement stmt;
        ResultSet rs = null;
        try {
            stmt = pol.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public ResultSet aktywniWeterani(Connection pol) {
        String query = "select gracz_id, gracz from gracz where gracz_id = (select gracz_id from gildia where data_dolaczenia < to_date('01/01/20', 'DD/MM/YY') and gildia.ostatnio_aktywny_dni_temu < 7 and gildia.gracz_id = gracz.gracz_id) order by gracz_id";
        Statement stmt;
        ResultSet rs = null;
        try {
            stmt = pol.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public ResultSet graczeNaRok(Connection pol) {
        String query = "select to_char(data_dolaczenia, 'YYYY'), count(to_char(data_dolaczenia, 'YYYY')) as ilosc from gildia group by to_char(data_dolaczenia, 'YYYY') order by 1";
        Statement stmt;
        ResultSet rs = null;
        try {
            stmt = pol.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public ResultSet rekruciDoPrzyjecia(Connection pol) {
        String query = "select g.gracz from gracz g where g.gracz_id = (select r1.gracz_id from rola_gracz r1 where g.gracz_id = r1.gracz_id and r1.rola_id = (select r2.rola_id from role r2 where r2.rola = 'Rekrut')) and g.gracz_id = (select gracz_id from gildia where gildia.gracz_id = g.gracz_id and data_dolaczenia + 90 < (SELECT SYSDATE FROM dual))";
        Statement stmt;
        ResultSet rs = null;
        try {
            stmt = pol.createStatement();
            rs = stmt.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public void rekrutNaGracz(Connection pol) {
        String query = "update rola_gracz u set u.rola_id = 3 where u.gracz_id = (select gracz_id from ready_recruit where gracz_id = u.gracz_id)";
        Statement stmt;
        try {
            stmt = pol.createStatement();
            stmt.executeQuery(query);
            System.out.println("zaktualizowano");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void zmienRole(Connection pol) {
        ResultSet rs;
        PreparedStatement pstmt;
        Scanner scan = new Scanner(System.in);
        String rola1, rola2, gracz;
        int rola1_id, rola2_id;
        String sql = "update rola_gracz u set u.rola_id = ? where u.gracz_id = (select gracz_id from gracz where gracz = ?) and u.rola_id = ?";
        System.out.print("gracz: ");
        gracz = scan.nextLine();
        System.out.print("z: ");
        rola1 = scan.nextLine();
        System.out.print("na: ");
        rola2 = scan.nextLine();

        String getRola1ID = "select rola_id from role where rola = ?";
        String getRola2ID = "select rola_id from role where rola = ?";
        try  {
            pstmt = pol.prepareStatement(getRola1ID);
            pstmt.setString(1, rola1);
            rs = pstmt.executeQuery();
            rs.next();
            rola1_id = rs.getInt(1);

            pstmt = pol.prepareStatement(getRola2ID);
            pstmt.setString(1, rola2);
            rs = pstmt.executeQuery();
            rs.next();
            rola2_id = rs.getInt(1);

            pstmt = pol.prepareStatement(sql);
            pstmt.setInt(1, rola2_id);
            pstmt.setString(2, gracz);
            pstmt.setInt(3, rola1_id);
            pstmt.executeUpdate();

            System.out.println("zaktualizowano");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void dodajGracza(Connection pol) {
        ResultSet rs;
        PreparedStatement pstmt;
        Scanner scan = new Scanner(System.in);

        String gracz, postac, rasa;
        int czasGry;

        String check = "select gracz_id from gracz where gracz = ?";

        String insert1 = "INSERT INTO GRACZ  VALUES ((select max(gracz_id)+1 from gracz), ?, ?, ?)";
        String insert2 = "INSERT INTO GILDIA VALUES ((select max(gracz_id) from gracz), (SELECT SYSDATE FROM dual), 0)";
        String insert3 = "INSERT INTO POSTAC VALUES ((select nazwa_postaci from gracz where gracz_id = (select max(gracz_id) from gracz)), (select max(gracz_id) from gracz), (select rasa_id from rasa where rasa = ?))";
        String insert4 = "INSERT INTO ROLA_GRACZ VALUES ((select max(gracz_id) from gracz),4)";

        System.out.print("gracz: ");
        gracz = scan.nextLine();
        System.out.print("postac: ");
        postac = scan.nextLine();
        System.out.print("rasa: ");
        rasa = scan.nextLine();
        System.out.print("czas w grze: ");
        czasGry = scan.nextInt();

        try {
            pstmt = pol.prepareStatement(check);
            pstmt.setString(1, gracz);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                System.out.println("Gracz o podanym nicku juz jest w gildii.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            pstmt = pol.prepareStatement(insert1);
            pstmt.setString(1, gracz);
            pstmt.setInt(2, czasGry);
            pstmt.setString(3, postac);
            pstmt.executeUpdate();

            pstmt = pol.prepareStatement(insert2);
            pstmt.executeUpdate();

            pstmt = pol.prepareStatement(insert3);
            pstmt.setString(1, rasa);
            pstmt.executeUpdate();

            pstmt = pol.prepareStatement(insert4);
            pstmt.executeUpdate();

            System.out.println("Dodano gracza");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public void usunGracza(Connection pol) {
        ResultSet rs;
        PreparedStatement pstmt;
        Scanner scan = new Scanner(System.in);

        String check = "select gracz_id from gracz where gracz = ?";

        String delete1 = "DELETE FROM GILDIA WHERE gracz_id = (select gracz_id from gracz where gracz = ?)";
        String delete2 = "DELETE FROM POSTAC WHERE gracz_id = (select gracz_id from gracz where gracz = ?)";
        String delete3 = "DELETE FROM ROLA_GRACZ WHERE gracz_id = (select gracz_id from gracz where gracz = ?)";
        String delete4 = "DELETE FROM GRACZ WHERE gracz = ?";

        System.out.print("gracz: ");
        String gracz = scan.nextLine();

        try {
            pstmt = pol.prepareStatement(check);
            pstmt.setString(1, gracz);
            rs = pstmt.executeQuery();
            if(!rs.next()) {
                System.out.println("Gracz o podanym nicku nie istnieje.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            pstmt = pol.prepareStatement(delete1);
            pstmt.setString(1, gracz);
            pstmt.executeUpdate();

            pstmt = pol.prepareStatement(delete2);
            pstmt.setString(1, gracz);
            pstmt.executeUpdate();

            pstmt = pol.prepareStatement(delete3);
            pstmt.setString(1, gracz);
            pstmt.executeUpdate();

            pstmt = pol.prepareStatement(delete4);
            pstmt.setString(1, gracz);
            pstmt.executeUpdate();

            System.out.println("Usunieto gracza");
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
