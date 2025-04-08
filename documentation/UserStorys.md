**1. Registrierung & Login**

**User Story 1.1: Registrierung**

*Als Student möchte ich mich registrieren, damit ich personalisierte Funktionen nutzen kann.*

• **Akzeptanzkriterien:**

• Es gibt ein Registrierungsformular, in dem ich mindestens meine E-Mail-Adresse, ein sicheres Passwort und ggf. weitere relevante Daten (z. B. Studiengang, Fakultät) angeben kann.

• Die Registrierung bestätigt per E-Mail oder durch eine Captcha-Lösung, dass es sich nicht um einen Spam-Anmeldeprozess handelt.

• Es wird geprüft, ob die E-Mail bereits existiert, und es erfolgt eine aussagekräftige Fehlermeldung, wenn dem so ist.

**User Story 1.2: Login**

*Als registrierter Student möchte ich mich einloggen, damit ich Zugriff auf alle Funktionen der Plattform habe.*

• **Akzeptanzkriterien:**

• Ein Login-Formular mit Eingabefeldern für E-Mail und Passwort ist vorhanden.

• Erfolgreiches Login leitet mich in meinen persönlichen Bereich weiter.

• Bei fehlerhaften Daten werden hilfreiche Fehlermeldungen angezeigt.

• Es besteht eine Option zur Passwort-Wiederherstellung.

---

**2. Inhalte hochladen**

**User Story 2.1: Upload von Studienmaterialien**

*Als Student möchte ich meine Zusammenfassungen, Kursmaterialien, Klausurlösungen und Klausuren hochladen, um sie mit anderen zu teilen.*

• **Akzeptanzkriterien:**

• Ein Upload-Formular ermöglicht das Auswählen und Hochladen von Dateien.

• Es gibt Pflichtfelder zur Eingabe von Metadaten wie Titel, Beschreibung und Dateityp.

• Beim Upload muss ich den passenden Kurs aus einem vordefinierten Katalog auswählen.

**User Story 2.2: Auswahl der Kursstruktur**

*Als Student möchte ich beim Hochladen die Kursstruktur (Fakultät → Kurs → Dozent) auswählen, damit die Datei richtig eingeordnet wird.*

• **Akzeptanzkriterien:**

• Es existiert ein dreistufiges Auswahlmenü, in dem ich zuerst die Fakultät, dann den Kurs und schließlich den Dozenten auswähle.

• Die Auswahlfelder sind dynamisch und abhängig voneinander (z. B. werden nach Auswahl der Fakultät nur die zugehörigen Kurse angezeigt).

**User Story 2.3: Kategorisierung der Inhalte**

*Als Student möchte ich meine hochgeladenen Dateien in Kategorien wie „Altklausur“, „Altklausur Lösung“ etc. einordnen, damit andere Nutzer die Materialien besser finden können.*

• **Akzeptanzkriterien:**

• Es wird eine Dropdown-Liste oder Auswahlbuttons angeboten, in denen ich die Kategorie auswählen kann.

• Die gewählte Kategorie wird zusammen mit dem Upload gespeichert und später bei der Suche und Filterung genutzt.

**User Story 2.4: Optionale Beschreibung für Inhalte**

*Als Student möchte ich meinen Hochgeladenen Inhalten eine Beschreibung geben können, um anderen Studenten falls der Inhalt nicht in eine der Kategorien passt wissen um was es sich handelt.*

• **Akzeptanzkriterien:**

• Es wird ein Textfeld geben, in dem ich eine Beschreibung eingeben kann kann.

• Die Beschreibung wird zusammen mit dem Upload gespeichert und später bei der Suche und angezeigt.

---

**3. Interaktion mit Inhalten**

**User Story 3.1: Bewertung von Inhalten**

*Als Student möchte ich hochgeladene Inhalte mit 1-5 Sternen bewerten und kommentieren, um anderen bei der Auswahl zu helfen.*

• **Akzeptanzkriterien:**

• Jede Datei verfügt über eine Bewertungsfunktion, die eine Sternebewertung (1 bis 5 Sterne) und ein optionales Kommentarfeld bietet.

• Die durchschnittliche Bewertung wird zusammen mit den Kommentaren angezeigt.

• Es wird sichergestellt, dass ich als Nutzer pro Inhalt nur einmal bewerten kann, aber meine Bewertung ggf. ändern darf.

**User Story 3.2: Veraltet-Markierung**

*Als Student möchte ich über einen „veraltet“-Button anzeigen können, dass ein Inhalt möglicherweise nicht mehr aktuell ist.*

• **Akzeptanzkriterien:**

• Jeder Inhalt zeigt einen Button, über den ich angeben kann, dass der Inhalt veraltet ist.

• Es wird eine Zählung der Klicks angezeigt, sodass ersichtlich ist, wie viele Nutzer diesen Inhalt als veraltet markiert haben.

• Ein Nutzer darf pro Inhalt nur einmal den „veraltet“-Button klicken.

**User Story 3.3: Meldefunktion**

*Als Student möchte ich Inhalte melden, falls diese falsch klassifiziert sind oder nicht zur Plattform gehören, um die Qualität der Plattform zu sichern.*

• **Akzeptanzkriterien:**

• Es gibt einen gut sichtbaren Melde-Button bei jedem Inhalt.

• Beim Klick erscheint ein kurzes Formular oder Dialog, in dem ich den Grund der Meldung angeben kann.

• Die Meldung wird an die Administratoren weitergeleitet und im Admin-Bereich gelistet.

**User Story 3.4: Durchsuchen & Filtern von Inhalten**

*Als Student möchte ich Inhalte nach Fakultät, Kurs, Dozent und Kategorie durchsuchen und filtern können, um schnell die relevanten Materialien zu finden.*

• **Akzeptanzkriterien:**

• Es existiert eine Such- und Filterfunktion, die eine Mehrfachauswahl erlaubt.

• Ergebnisse werden übersichtlich dargestellt und können nach Relevanz, Upload-Datum oder Bewertung sortiert werden.

• Eine intuitive Navigation hilft, die Inhalte der vorgegebenen Kursstruktur zu folgen.

---

**4. Administratorfunktionen**

**User Story 4.1: Admin-Login & Dashboard**

*Als Administrator möchte ich mich einloggen und über ein Dashboard Zugang zu allen Admin-Funktionen haben.*

• **Akzeptanzkriterien:**

• Ein separater Login-Bereich für Administratoren ist vorhanden.

• Nach erfolgreichem Login wird ein übersichtliches Dashboard angezeigt, das den Zugang zu allen Verwaltungsfunktionen bietet.

**User Story 4.2: Verwaltung von Fakultäten, Kursen und Dozenten**

*Als Administrator möchte ich neue Fakultäten, Kurse und Dozenten erstellen und bestehende Einträge bearbeiten oder löschen, damit die Plattform stets aktuell ist.*

• **Akzeptanzkriterien:**

• Es gibt Formulare zur Eingabe neuer Einträge sowie zur Bearbeitung und Löschung bestehender Daten.

• Die Eingaben werden validiert, und es gibt Bestätigungsdialoge, bevor Daten endgültig geändert werden.

• Änderungen werden sofort in der vordefinierten Kursstruktur übernommen.

**User Story 4.3: Content Moderation**

*Als Administrator möchte ich alle hochgeladenen Inhalte moderieren, um sicherzustellen, dass nur relevante und korrekte Materialien auf der Plattform bleiben.*

• **Akzeptanzkriterien:**

• Eine Seite zeigt alle gemeldeten Inhalte an, idealerweise mit Filter- und Sortieroptionen (z. B. nach Anzahl der Meldungen).

• Es besteht die Möglichkeit, Inhalte direkt zu löschen oder den Status der Meldung zu bearbeiten.

• Moderationsaktionen werden protokolliert, sodass nachvollzogen werden kann, welche Änderungen wann vorgenommen wurden.

---

**5. Allgemeine Funktionen & Qualitätsmerkmale**

**User Story 5.1: Sicherheit & Datenschutz**

*Als Student und Administrator möchte ich sicher sein, dass meine persönlichen Daten und hochgeladenen Inhalte geschützt sind, damit ich der Plattform vertrauen kann.*

• **Akzeptanzkriterien:**

• Alle Datenübertragungen erfolgen verschlüsselt (z. B. über SSL/TLS).

• Passwörter werden sicher (gehasht und gesalzen) gespeichert.

• Die Plattform hält sich an geltende Datenschutzbestimmungen, und entsprechende Hinweise sind gut sichtbar.

**User Story 5.2: Responsives Design**

*Als Student möchte ich die Plattform auch auf mobilen Geräten und Tablets nutzen können, um jederzeit auf die Inhalte zugreifen zu können.*

• **Akzeptanzkriterien:**

• Das Design passt sich an unterschiedliche Bildschirmgrößen an (responsive Layout).

• Navigation und Bedienung sind auch auf Touch-Geräten intuitiv und fehlerfrei.