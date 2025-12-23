package ch.hftm.service;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * E-Mail-Service (Stub-Implementierung)
 *
 * Dieser Service simuliert den Versand von E-Mails. In einer produktiven
 * Umgebung würde hier ein echter SMTP-Client integriert werden (z.B. JavaMail
 * API oder Apache Commons Email).
 */
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    /**
     * Sendet eine E-Mail (Stub)
     *
     * @param empfaenger E-Mail-Adresse des Empfängers
     * @param betreff Betreff der E-Mail
     * @param nachricht Inhalt der E-Mail
     * @return true wenn erfolgreich (simuliert), false bei Fehler
     */
    public boolean sendeEmail(String empfaenger, String betreff, String nachricht) {
        if (empfaenger == null || empfaenger.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "E-Mail-Versand fehlgeschlagen: Keine Empfänger-Adresse angegeben");
            return false;
        }

        if (betreff == null || betreff.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "E-Mail-Versand fehlgeschlagen: Kein Betreff angegeben");
            return false;
        }

        if (nachricht == null || nachricht.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "E-Mail-Versand fehlgeschlagen: Keine Nachricht angegeben");
            return false;
        }

        // Simulierter Versand
        LOGGER.log(Level.INFO, "=== E-Mail-Versand (SIMULIERT) ===");
        LOGGER.log(Level.INFO, "Zeitstempel: " + LocalDateTime.now());
        LOGGER.log(Level.INFO, "An: " + empfaenger);
        LOGGER.log(Level.INFO, "Betreff: " + betreff);
        LOGGER.log(Level.INFO, "Nachricht:\n" + nachricht);
        LOGGER.log(Level.INFO, "==================================");

        return true;
    }

    /**
     * Sendet eine Termin-Bestätigung an ein Mitglied (Stub)
     *
     * @param empfaengerEmail E-Mail-Adresse des Mitglieds
     * @param mitgliedName Name des Mitglieds
     * @param terminDatum Datum des Termins
     * @param terminOrt Ort des Termins
     * @return true wenn erfolgreich (simuliert)
     */
    public boolean sendeTerminBestaetigung(String empfaengerEmail, String mitgliedName,
            String terminDatum, String terminOrt) {
        String betreff = "Terminbestätigung - KUD Karadjordje Bern";
        String nachricht = String.format(
                "Hallo %s,\n\n"
                + "deine Teilnahme am folgenden Termin wurde bestätigt:\n\n"
                + "Datum: %s\n"
                + "Ort: %s\n\n"
                + "Wir freuen uns auf dich!\n\n"
                + "Mit freundlichen Grüßen\n"
                + "KUD Karadjordje Bern",
                mitgliedName, terminDatum, terminOrt
        );

        return sendeEmail(empfaengerEmail, betreff, nachricht);
    }

    /**
     * Sendet eine Formular-Erinnerung an ein Mitglied (Stub)
     *
     * @param empfaengerEmail E-Mail-Adresse des Mitglieds
     * @param mitgliedName Name des Mitglieds
     * @param formularTyp Art des Formulars
     * @param rueckgabedatum Fälligkeitsdatum
     * @return true wenn erfolgreich (simuliert)
     */
    public boolean sendeFormularErinnerung(String empfaengerEmail, String mitgliedName,
            String formularTyp, String rueckgabedatum) {
        String betreff = "Erinnerung: Formular ausstehend - KUD Karadjordje Bern";
        String nachricht = String.format(
                "Hallo %s,\n\n"
                + "bitte denke daran, das folgende Formular einzureichen:\n\n"
                + "Formular-Typ: %s\n"
                + "Rückgabedatum: %s\n\n"
                + "Bei Fragen stehen wir dir gerne zur Verfügung.\n\n"
                + "Mit freundlichen Grüßen\n"
                + "KUD Karadjordje Bern",
                mitgliedName, formularTyp, rueckgabedatum
        );

        return sendeEmail(empfaengerEmail, betreff, nachricht);
    }

    /**
     * Sendet eine Willkommens-E-Mail an ein neues Mitglied (Stub)
     *
     * @param empfaengerEmail E-Mail-Adresse des Mitglieds
     * @param mitgliedName Name des Mitglieds
     * @return true wenn erfolgreich (simuliert)
     */
    public boolean sendeWillkommensEmail(String empfaengerEmail, String mitgliedName) {
        String betreff = "Willkommen bei KUD Karadjordje Bern!";
        String nachricht = String.format(
                "Hallo %s,\n\n"
                + "herzlich willkommen bei KUD Karadjordje Bern!\n\n"
                + "Wir freuen uns, dich als neues Mitglied begrüßen zu dürfen.\n"
                + "In Kürze erhältst du weitere Informationen zu unseren Trainingszeiten und Veranstaltungen.\n\n"
                + "Bei Fragen kannst du dich jederzeit an uns wenden.\n\n"
                + "Mit freundlichen Grüßen\n"
                + "KUD Karadjordje Bern",
                mitgliedName
        );

        return sendeEmail(empfaengerEmail, betreff, nachricht);
    }
}
