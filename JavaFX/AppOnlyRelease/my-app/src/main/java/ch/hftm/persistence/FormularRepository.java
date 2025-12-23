package ch.hftm.persistence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.hftm.model.Formular;
import ch.hftm.model.FormularStatus;

/**
 * Repository für Formular-Datenbankoperationen
 */
public class FormularRepository {

    private static final Logger LOGGER = Logger.getLogger(FormularRepository.class.getName());

    public List<Formular> findAll() {
        List<Formular> list = new ArrayList<>();
        String sql = "SELECT formular_id, typ, ausgabedatum, rueckgabedatum, status, mitglied_id FROM formular ORDER BY ausgabedatum DESC, formular_id DESC";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Formulare", e);
        }
        return list;
    }

    public List<Formular> searchByTyp(String keyword) {
        List<Formular> list = new ArrayList<>();
        String sql = "SELECT formular_id, typ, ausgabedatum, rueckgabedatum, status, mitglied_id FROM formular WHERE typ ILIKE ? ORDER BY ausgabedatum DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler bei Formular-Suche", e);
        }
        return list;
    }

    public boolean save(Formular f) {
        String sql = "INSERT INTO formular(typ, ausgabedatum, rueckgabedatum, status, mitglied_id) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getTyp());
            setDate(ps, 2, f.getAusgabedatum());
            setDate(ps, 3, f.getRueckgabedatum());
            ps.setString(4, f.getStatus() != null ? f.getStatus().name() : FormularStatus.AUSSTEHEND.name());
            ps.setInt(5, f.getMitgliedId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Formulars", e);
            return false;
        }
    }

    public boolean update(Formular f) {
        String sql = "UPDATE Formular SET typ=?, ausgabedatum=?, rueckgabedatum=?, status=?, mitglied_id=? WHERE formular_id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getTyp());
            setDate(ps, 2, f.getAusgabedatum());
            setDate(ps, 3, f.getRueckgabedatum());
            ps.setString(4, f.getStatus() != null ? f.getStatus().name() : FormularStatus.AUSSTEHEND.name());
            ps.setInt(5, f.getMitgliedId());
            ps.setInt(6, f.getFormularId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren des Formulars", e);
            return false;
        }
    }

    public boolean delete(int formularId) {
        String sql = "DELETE FROM Formular WHERE formular_id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, formularId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Löschen des Formulars", e);
            return false;
        }
    }

    private static void setDate(PreparedStatement ps, int idx, LocalDate date) throws SQLException {
        if (date == null) {
            ps.setDate(idx, null);
        } else {
            ps.setDate(idx, Date.valueOf(date));
        }
    }

    private static Formular map(ResultSet rs) throws SQLException {
        int id = rs.getInt("formular_id");
        String typ = rs.getString("typ");
        LocalDate ausgabe = rs.getDate("ausgabedatum") != null ? rs.getDate("ausgabedatum").toLocalDate() : null;
        LocalDate rueckgabe = rs.getDate("rueckgabedatum") != null ? rs.getDate("rueckgabedatum").toLocalDate() : null;
        String statusStr = rs.getString("status");
        FormularStatus status = null;
        try {
            status = statusStr != null ? FormularStatus.valueOf(statusStr) : FormularStatus.AUSSTEHEND;
        } catch (IllegalArgumentException ex) {
            // Falls DB andere Schreibweise speichert, fallback auf AUSSTEHEND
            status = FormularStatus.AUSSTEHEND;
        }
        int mitgliedId = rs.getInt("mitglied_id");
        return new Formular(id, typ, ausgabe, rueckgabe, status, mitgliedId);
    }
}
