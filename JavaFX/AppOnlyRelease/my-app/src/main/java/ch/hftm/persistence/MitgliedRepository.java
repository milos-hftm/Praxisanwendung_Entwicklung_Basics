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

import ch.hftm.model.Mitglied;
import ch.hftm.model.Rolle;

/**
 * Repository für Mitglieder-Datenbankopertionen
 */
public class MitgliedRepository {

    private static final Logger LOGGER = Logger.getLogger(MitgliedRepository.class.getName());

    /**
     * Gibt alle Mitglieder zurück
     */
    public List<Mitglied> findAll() {
        List<Mitglied> mitglieder = new ArrayList<>();
        String sql = "SELECT mitglied_id, vorname, nachname, email, rolle FROM mitglied ORDER BY nachname, vorname";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                mitglieder.add(mapResultSetToMitglied(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden der Mitglieder", e);
        }

        return mitglieder;
    }

    /**
     * Sucht ein Mitglied nach ID
     */
    public Mitglied findById(int id) {
        String sql = "SELECT mitglied_id, vorname, nachname, email, rolle FROM mitglied WHERE mitglied_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMitglied(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Laden des Mitglieds mit ID " + id, e);
        }

        return null;
    }

    /**
     * Sucht Mitglieder nach Suchbegriff (Name oder Email) Nutzt ILIKE für
     * case-insensitive Suche (PostgreSQL-spezifisch)
     */
    public List<Mitglied> search(String searchTerm) {
        List<Mitglied> mitglieder = new ArrayList<>();
        String sql = "SELECT mitglied_id, vorname, nachname, email, rolle FROM Mitglied "
                + "WHERE vorname ILIKE ? OR nachname ILIKE ? OR email ILIKE ? "
                + "ORDER BY nachname, vorname";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + searchTerm + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                mitglieder.add(mapResultSetToMitglied(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler bei der Mitgliedersuche", e);
        }

        return mitglieder;
    }

    /**
     * Speichert ein neues Mitglied
     */
    public boolean save(Mitglied mitglied) {
        String sql = "INSERT INTO mitglied (vorname, nachname, email, rolle) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, mitglied.getVorname());
            stmt.setString(2, mitglied.getNachname());
            stmt.setString(3, mitglied.getEmail());
            // Speichere als ENUM-Name (UPPERCASE) passend zum DB CHECK Constraint
            stmt.setString(4, mitglied.getRolle().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    mitglied.setMitgliedId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Speichern des Mitglieds", e);
        }

        return false;
    }

    /**
     * Aktualisiert ein bestehendes Mitglied
     */
    public boolean update(Mitglied mitglied) {
        String sql = "UPDATE mitglied SET vorname = ?, nachname = ?, email = ?, rolle = ? WHERE mitglied_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mitglied.getVorname());
            stmt.setString(2, mitglied.getNachname());
            stmt.setString(3, mitglied.getEmail());
            // Speichere als ENUM-Name (UPPERCASE)
            stmt.setString(4, mitglied.getRolle().name());
            stmt.setInt(5, mitglied.getMitgliedId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Aktualisieren des Mitglieds", e);
        }

        return false;
    }

    /**
     * Löscht ein Mitglied
     */
    public boolean delete(int mitgliedId) {
        String sql = "DELETE FROM mitglied WHERE mitglied_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, mitgliedId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Fehler beim Löschen des Mitglieds", e);
        }

        return false;
    }

    /**
     * Hilfsmethode: Mapped ResultSet zu Mitglied-Objekt
     */
    private Mitglied mapResultSetToMitglied(ResultSet rs) throws SQLException {
        int id = rs.getInt("mitglied_id");
        String vorname = rs.getString("vorname");
        String nachname = rs.getString("nachname");
        String email = rs.getString("email");
        String rolleStr = rs.getString("rolle");
        Rolle rolle;
        try {
            // Erwartet UPPERCASE Enum-Namen (z. B. MITGLIED, TRAINER, ADMIN)
            rolle = Rolle.valueOf(rolleStr);
        } catch (IllegalArgumentException ex) {
            // Fallback für alte/abweichende Werte (z. B. "Mitglied")
            rolle = Rolle.MITGLIED;
            for (Rolle r : Rolle.values()) {
                if (r.getDisplayName().equalsIgnoreCase(rolleStr)) {
                    rolle = r;
                    break;
                }
            }
        }

        return new Mitglied(id, vorname, nachname, email, rolle);
    }
}
