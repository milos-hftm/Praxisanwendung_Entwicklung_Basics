package ch.hftm.persistence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.hftm.model.Termin;

/**
 * Repository für Termin-Datenbankoperationen
 */
public class TerminRepository {

    private static final Logger LOGGER = Logger.getLogger(TerminRepository.class.getName());

    /**
     * Gibt alle Termine zurück
     */
    public List<Termin> findAll() {
        List<Termin> termine = new ArrayList<>();
        String sql = "SELECT termin_id, datum, uhrzeit, ort, ferien_flag FROM termin ORDER BY datum DESC, uhrzeit";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                termine.add(mapResultSetToTermin(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Termine", e);
        }

        return termine;
    }

    /**
     * Sucht einen Termin nach ID
     */
    public Termin findById(int id) {
        String sql = "SELECT termin_id, datum, uhrzeit, ort, ferien_flag FROM termin WHERE termin_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTermin(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden des Termins mit ID " + id, e);
        }

        return null;
    }

    /**
     * Gibt kommende Termine (ab heute) zurück
     */
    public List<Termin> findUpcoming() {
        List<Termin> termine = new ArrayList<>();
        String sql = "SELECT termin_id, datum, uhrzeit, ort, ferien_flag FROM termin "
                + "WHERE datum >= CURRENT_DATE ORDER BY datum ASC, uhrzeit";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                termine.add(mapResultSetToTermin(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden kommender Termine", e);
        }

        return termine;
    }

    /**
     * Speichert einen neuen Termin
     */
    public boolean save(Termin termin) {
        String sql = "INSERT INTO termin (datum, uhrzeit, ort, ferien_flag) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(termin.getDatum()));
            stmt.setTime(2, Time.valueOf(termin.getUhrzeit()));
            stmt.setString(3, termin.getOrt());
            stmt.setBoolean(4, termin.isFerienFlag());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    termin.setTerminId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Termins", e);
        }

        return false;
    }

    /**
     * Aktualisiert einen bestehenden Termin
     */
    public boolean update(Termin termin) {
        String sql = "UPDATE termin SET datum = ?, uhrzeit = ?, ort = ?, ferien_flag = ? WHERE termin_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(termin.getDatum()));
            stmt.setTime(2, Time.valueOf(termin.getUhrzeit()));
            stmt.setString(3, termin.getOrt());
            stmt.setBoolean(4, termin.isFerienFlag());
            stmt.setInt(5, termin.getTerminId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren des Termins", e);
        }

        return false;
    }

    /**
     * Löscht einen Termin
     */
    public boolean delete(int terminId) {
        String sql = "DELETE FROM termin WHERE termin_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, terminId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Löschen des Termins", e);
        }

        return false;
    }

    /**
     * Hilfsmethode: Mapped ResultSet zu Termin-Objekt
     */
    private Termin mapResultSetToTermin(ResultSet rs) throws SQLException {
        int id = rs.getInt("termin_id");
        LocalDate datum = rs.getDate("datum").toLocalDate();
        LocalTime uhrzeit = rs.getTime("uhrzeit").toLocalTime();
        String ort = rs.getString("ort");
        boolean ferienFlag = rs.getBoolean("ferien_flag");

        return new Termin(id, datum, uhrzeit, ort, ferienFlag);
    }
}
