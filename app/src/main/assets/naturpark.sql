PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE route (id int, region text, name text, length float, grade_avg int, grade_max int, quality int, rating int);
INSERT INTO "route" VALUES(1,'Hainich','Am Hainich entlang',12.34,10,12,2,1);
INSERT INTO "route" VALUES(2,'Hainich','Durch den Hainich',1,3,4,4,3);
INSERT INTO "route" VALUES(3,'Dresden','Elbtalhänge',23.45,8,8,1,2);
INSERT INTO "route" VALUES(4,'Dresden','Großer Garten',2.3,0,0,2,3);
INSERT INTO "route" VALUES(5,'Dresden','Altstadt',2.3,0,3,3,2);
INSERT INTO "route" VALUES(6,'Dresden','Neustadt',3.5,1,4,3,3);
INSERT INTO "route" VALUES(7, 'Hainich', '', 2.8, 1,2,3,2);
INSERT INTO "route" VALUES(8, 'Hainich', '', 7.3, 3,6,2,1);
INSERT INTO "route" VALUES(9, 'Hainich', 'Zum Baumlehrpfad', 1.5, 1,5,1,1);
INSERT INTO "route" VALUES(10, 'Hainich', 'Alte Buchen', 3.0, 3,3,2,1);
INSERT INTO "route" VALUES(11, 'Müritz', 'Entlang der Müritz', 2.0, 2,2,2,2);
INSERT INTO "route" VALUES(12, 'Müritz', 'Um die Müritz', 1.0, 1,1,1,1);
INSERT INTO "route" VALUES(13, 'Sassnitz', 'Kreidefelsen', 4.2, 2,2, 1, 1);
CREATE TABLE poi_type(id int, name text, icon_name text);
INSERT INTO "poi_type" VALUES(1,'Hotel','h');
INSERT INTO "poi_type" VALUES(2,'Aussichtspunkt','a');
INSERT INTO "poi_type" VALUES(3,'Laden','s');
INSERT INTO "poi_type" VALUES(4,'Haltestelle','oenv');
INSERT INTO "poi_type" VALUES(5,'Parkplatz','p');
INSERT INTO "poi_type" VALUES(6,'Kirche', 'k');
CREATE TABLE obstacle_type (id int, name text, icon_name text);
INSERT INTO "obstacle_type" VALUES(1,'Schranke','marker_schranke.png');
INSERT INTO "obstacle_type" VALUES(2,'Treppe','marker_treppe.png');
INSERT INTO "obstacle_type" VALUES(3,'Engstelle','marker_engstelle.png');
INSERT INTO "obstacle_type" VALUES(4,'Stufe','marker_stufe.png');
INSERT INTO "obstacle_type" VALUES(5,'Rinne','marker_rinne.png');
INSERT INTO "obstacle_type" VALUES(6,'Poller','marker_poller.png');
INSERT INTO "obstacle_type" VALUES(7,'Abhang','marker_achtung.png');
CREATE TABLE [obstacle] (
  [_id] INT, 
  [type] int, 
  [route_id] int, 
  [latitude] float, 
  [longitude] float, 
  [name] TEXT, 
  CONSTRAINT [] PRIMARY KEY ([_id] ASC));
INSERT INTO "obstacle" VALUES(1,1,1,51.05286,13.73854,'Schranke');
INSERT INTO "obstacle" VALUES(2,1,1,51.05235,13.7395,'Schranke');
INSERT INTO "obstacle" VALUES(3,2,1,51.05179,13.74054,'Treppe');
INSERT INTO "obstacle" VALUES(4,3,1,51.05125,13.74403,'Engstelle');
INSERT INTO "obstacle" VALUES(5,1,2,51.052,13.738,'Schranke');
INSERT INTO "obstacle" VALUES(6,2,2,51.053,13.738,'Treppe');
INSERT INTO "obstacle" VALUES(7,2,2,51.054,13.739,'Treppe');
INSERT INTO "obstacle" VALUES(8,3,2,51.055,13.74,'Engstelle');
CREATE TABLE [poi] (
  [_id] int, 
  [type] int, 
  [latitude] float, 
  [longitude] float, 
  [name] text, 
  [address] text, 
  [classification] TEXT, 
  [info] TEXT);
INSERT INTO "poi" VALUES(1,2,51.03785,13.76288,'Palais im Großen Garten','Addresse1','Geeignet','Das Palais im Großen Garten gilt als Inkunabel des sächsischen Barocks. In der Zeit August des Starken Schauplatz fulminanter Festivitäten von europäischer Ausstrahlung, brannte es beim Bombenangriff auf Dresden im Februar 1945 gänzlich aus. Noch heute sind die Schäden im kriegsversehrten Festsaal sichtbar.');
INSERT INTO "poi" VALUES(2,2,51.05173,13.74117,'Frauenkirche','Addresse2','Geeignet','Die Dresdner Frauenkirche wurde von 1726 bis 1743 nach einem Entwurf von George Bähr erbaut. Im Luftkrieg des Zweiten Weltkriegs wurde sie während der Luftangriffe auf Dresden in der Nacht vom 13. zum 14. Februar 1945 durch den in Dresden wütenden Feuersturm schwer beschädigt und stürzte am Morgen des 15. Februar ausgebrannt in sich zusammen. In der DDR blieb ihre Ruine erhalten und diente als Mahnmal gegen Krieg und Zerstörung. Nach der Wende begann 1994 der 2005 abgeschlossene Wiederaufbau, den Fördervereine und Spender aus aller Welt finanzieren halfen.');
INSERT INTO "poi" VALUES(3,2,51.05361,13.73786,'Katholische Hofkirche','Chiaveriegasse, Innere Altstadt, Dresden','Geeignet','Die Katholische Hofkirche in Dresden, geweiht der heiligsten Dreifaltigkeit (Sanctissimae Trinitatis), ist Kathedrale des Bistums Dresden-Meißen sowie eine Stadtpfarrkirche Dresdens. Sie wurde unter Kurfürst Friedrich August II. von Sachsen durch Gaetano Chiaveri von 1739 bis 1755 im Stil des Barocks errichtet. Im Jahr 1964 bereits zur Konkathedrale erhoben, wurde sie 1980 durch die Verlegung des Bischofssitzes von Bautzen nach Dresden zur Kathedrale des Bistums Dresden-Meißen.');
INSERT INTO "poi" VALUES(4,3,51.20996,10.46192,'Laufladen Zwei G    ',NULL,'Geeignet','Laufladen zwei G – Ihr Fachgeschäft für Sport- und Freizeitartikel');
INSERT INTO "poi" VALUES(5,2,51.210074,10.454762,'Marien Kirche',NULL,'Geeignet','Die Marienkirche ist eine gotische Kirche in der thüringischen Stadt Mühlhausen. Sie gilt als Meisterwerk der Gotik und ist, nach dem Erfurter Dom, die zweitgrößte Kirche Thüringens. Errichtet wurde sie hauptsächlich im 14. Jahrhundert. Ihr 86,7 Meter hoher Mittelturm ist der höchste des Bundeslandes und prägt maßgeblich die Stadtsilhouette. Die Marienkirche war darüber hinaus ein Ereignisort des Bauernkriegs um 1525, da der Revolutionsführer Thomas Müntzer hier als Pfarrer wirkte.');
INSERT INTO "poi" VALUES(6,4,51.21155,10.460057,'Bus Bahnhof','Bahnhofstraße 1, 99974 Mühlhausen/Thüringen ','Bedingt','Öffnungszeiten ZOB:
Montag bis Freitag
06:30 Uhr bis 09:30 Uhr
12:00 Uhr bis 17:30 Uhr ');
INSERT INTO "poi" VALUES(7,5,51.210496,10.463987,'Hanfsack Parkplatz ',NULL,'Geeignet','Anfahrt über Kreuzgraben - Hauptmannstraße - Hinter der Mauer

    Fahrzeugtypen: PKW
    Begrenzung: keine
    Gebühren: Mo - Fr von 8.00 - 17.00 Uhr und Sa von 7.00 - 9.30 Uhr

    pro Stunde 1,00 €
    Mindestgebühr 0,25 €
    Tageskarte: 5,00 €

2 Gehminuten bis zum Zentrum.');
INSERT INTO "poi" VALUES(8,1,51.209325,10.469315,'Mirage Hotel',NULL,'Geeignet','Sie erwartet ein Komfort-Hotel mit 3 Sternen. 
    -zentrale, dennoch ruhige Lage
    -Nur 3 Gehminuten zur historischen Altstadt Mühlhausen
    -Kostenfreie Parkplätze für PKW''s und Reisebusse
    -Zusätzlich kostenfrei: abgeschlossene Tiefgarage
    -2 bequeme Aufzüge im Haus
    -W-LAN HotSpot für viele Zimmer und alle Tagungsräume 
');
INSERT INTO "poi" VALUES(9,4,51.208889,10.473819,'Bahnhof',NULL,'Geeignet','Der Bahnhof Mühlhausen (Thür) ist gemessen an seiner Frequentierung der wichtigste Personenbahnhof im Unstrut-Hainich-Kreis und der einzige Bahnhof von Mühlhausen/Thüringen. Er liegt östlich des Zentrums von Mühlhausen im Tal der Unstrut.');
INSERT INTO "poi" VALUES(10,2,51.20686,10.458101,'Haus der Kirche ',NULL,'Geeignet','Die ehemalige Komturei des Deutschen Ordens am Kristanplatz wurde in 14 Monaten aufwendig saniert und modernisiert. Seit den 1960er dient das Haus als Gemeindehaus der Kirchenarbeit. ');
INSERT INTO "poi" VALUES(11,1,51.207521,10.457243,'Brauhaus zum Löwen Hotel Gaststätte ',NULL,'Geeignet','Das Brauhaus
Erleben Sie Brautradition hautnah! In gemütlicher Gasthaus-Atmosphäre, direkt bei den Sudkesseln, können Sie unserem Braumeister über die Schulter schauen. Nach untergäriger Brauweise und selbstverständlich streng nach dem Deutschen Reinheitsgebot, entstehen hier unser "Brauhaus Pilsener" und das "Apotheker Dunkel".
Unsere Brauerei kann auch besichtigt werden.');
INSERT INTO "poi" VALUES(12,2,51.205776,10.456276,'Thüringen Therme Hallenbad ',NULL,'Bedingt','Ziehen Sie Ihre Bahnen in unserem Sportbecken oder lassen Sie sich während des Wellenbetriebes durch das glasklare Wasser wirbeln. Entdecken Sie neue Energien beim Sprung vom 3-Meter-Turm. Erleben Sie großen Spaß in unserer Riesenrutsche oder den Genuss der attraktiven Wasserspaßangebote in fünf unterschiedlichen Becken. Eine fantastische Saunalandschaft mit kulinarischem Verwöhnangebot lässt Sie den Alltag vergessen. Kräfte sammeln und sich stabilisieren können Sie unter modernsten Bedingungen in unserem Aktivzentrum mit Sport, Prävention und Therapie.');
INSERT INTO "poi" VALUES(13,3,51.214954,10.483348,'Tankstelle Görmar ',NULL,'Geeignet','BFT Tankstelle');
CREATE INDEX [id] ON [poi] ([_id]);
CREATE INDEX [type] ON [poi] ([type]);
COMMIT;
