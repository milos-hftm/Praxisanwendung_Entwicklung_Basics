package ch.hftm.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.hftm.model.Teilnahme;
import ch.hftm.model.TeilnahmeStatus;

/**
 * Repository für Teilnahme-Datenbankoperationen
 */
public class TeilnahmeRepository {

    private static final Logger LOGGER = Logger.getLogger(TeilnahmeRepository.class.getName());

    public List<Teilnahme> findAll() {
        List<Teilnahme> list = new ArrayList<>();
        String sql = "SELECT teilnahme_id, mitglied_id, termin_id, formular_id, status FROM teilnahme ORDER BY teilnahme_id DESC";
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Teilnahmen", e);
        }
        return list;
    }

    public List<Teilnahme> findByMitglied(int mitgliedId) {
        List<Teilnahme> list = new ArrayList<>();
        String sql = "SELECT teilnahme_id, mitglied_id, termin_id, formular_id, status FROM teilnahme WHERE mitglied_id=? ORDER BY teilnahme_id DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mitgliedId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Teilnahmen für Mitglied " + mitgliedId, e);
        }
        return list;
    }

    public List<Teilnahme> findByTermin(int terminId) {
        List<Teilnahme> list = new ArrayList<>();
        String sql = "SELECT teilnahme_id, mitglied_id, termin_id, formular_id, status FROM teilnahme WHERE termin_id=? ORDER BY teilnahme_id DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, terminId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Teilnahmen für Termin " + terminId, e);
        }
        return list;
    }

    public boolean save(Teilnahme t) {
        String sql = "INSERT INTO teilnahme(mitglied_id, termin_id, formular_id, status) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getMitgliedId());
            ps.setInt(2, t.getTerminId());
            if (t.getFormularId() > 0) {
                ps.setInt(3, t.getFormularId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, t.getStatus() != null ? t.getStatus().name() : TeilnahmeStatus.ZUGESAGT.name());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern der Teilnahme", e);
            return false;
        }
    }

    public boolean update(Teilnahme t) {
        String sql = "UPDATE teilnahme SET mitglied_id=?, termin_id=?, formular_id=?, status=? WHERE teilnahme_id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getMitgliedId());
            ps.setInt(2, t.getTerminId());
            if (t.getFormularId() > 0) {
                ps.setInt(3, t.getFormularId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, t.getStatus() != null ? t.getStatus().name() : TeilnahmeStatus.ZUGESAGT.name());
            ps.setInt(5, t.getTeilnahmeId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren der Teilnahme", e);
            return false;
        }
    }

    public boolean delete(int teilnahmeId) {
        String sql = "DELETE FROM teilnahme WHERE teilnahme_id=?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teilnahmeId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Löschen der Teilnahme", e);
            return false;
        }
    }

    private static Teilnahme map(ResultSet rs) throws SQLException {
        int id = rs.getInt("teilnahme_id");
        int mitglied = rs.getInt("mitglied_id");
        int termin = rs.getInt("termin_id");
        int formular = rs.getInt("formular_id");
        String statusStr = rs.getString("status");
        TeilnahmeStatus status = TeilnahmeStatus.ZUGESAGT;
        try {
            status = statusStr != null ? TeilnahmeStatus.valueOf(statusStr) : TeilnahmeStatus.ZUGESAGT;
        } catch (IllegalArgumentException ex) {
            // Falls DB andere Schreibweise hat, fallback
        }
        return new Teilnahme(id, mitglied, termin, formular > 0 ? formular : null, status);
    }
}
