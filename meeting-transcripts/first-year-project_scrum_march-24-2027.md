**Hannibal | ITU**: Så har vi skøm nu her, og så tænker jeg, at lad os gøre det sådan nu, at når vi taler, så skal de sige os navne.

**Hannibal | ITU**: Fordi så får vi det med på transcriptet, og så får vi det med i Weekly Log.

**Hannibal | ITU**: Noget mere specifikt, ja.

**Hannibal | ITU**: Cool.

**Hannibal | ITU**: Hvem starter?

**Hannibal | ITU**: Vil du starte?

**Hannibal | ITU**: Jeg starter.

**Hannibal | ITU**: Ja.

**Filip | ITU**: Jeg har...

**Hannibal | ITU**: Du hedder Philip.

**Filip | ITU**: Jeg hedder Philip.

**Filip | ITU**: Jeg har kigget, prøvet for det her Priority Queue og Dijkstra til at fungere sammen.

**Filip | ITU**: Og så har jeg fundet sådan en Lazy Dijkstra, som jeg prøver at implementere.

**Filip | ITU**: Se om det hjælper.

**Filip | ITU**: Så er der bare alt det her med en masse klasser med Directed Edge, Edge-Waited Diagraphs og alle mulige hjælpeklasser.

**Filip | ITU**: Jamen jeg kan ikke helt få det til at fungere.

**Filip | ITU**: Så det er super fedt.

**Filip | ITU**: Det prøver jeg at råde i.

**Filip | ITU**: Hvad var det egentlig, I fandt ud af i forhold til Edges?

**Filip | ITU**: Jamen nu har jeg fundet den her klasse, der hedder Directed Edge.

**Hannibal | ITU**: Ja.

**Filip | ITU**: Øhm.

**Filip | ITU**: Ja, som bare er en helt almindelig klasse i sig selv.

**Filip | ITU**: Okay.

**Filip | ITU**: Helt godt klasse.

**Filip | ITU**: Ja.

**Filip | ITU**: Det er det, jeg skal finde ud af.

**Filip | ITU**: Den tager bare en edge fra et punkt til et andet, og så giver den en vægt,

**Filip | ITU**: når jeg ikke helt får ud af præcis, hvad den gør.

**Sebastian | ITU**: Det er også det, jeg har faktisk videre over, hvordan det er, at du tager vægten af den ene node til den næste node.

**Sebastian | ITU**: For eksempel.

**Sebastian | ITU**: Fordi det er jo mega afgørende for testningen, hvordan man tester.

**Hannibal | ITU**: Altså den måde, jeg vil sige, at vægten skulle være, det er sådan, at I bare startede med at tage,

**Hannibal | ITU**: fordi hver node har et koordinat, ikke?

**Hannibal | ITU**: Jo.

**Hannibal | ITU**: Altså tager afstanden til to koordinat.

**Sebastian | ITU**: Det der, hvad hedder det, Manhattan distance, ikke?

**Hannibal | ITU**: Nej, det er bare, hvad hedder det, Pythagoras.

**Sebastian | ITU**: Nej, men hvad hedder det, vores z-a sagde også, at man nævnte, at vi nok burde bruge det der Manhattan distance.

**Sebastian | ITU**: Altså det er bare x-koordinat og y-koordinat, altså y1, eller y2 minus y1, x2 minus x1,

**Sebastian | ITU**: og så vores præcise distancen mellem dem, hvis det giver mening.

**Hannibal | ITU**: Ja, det er rigtigt, men så er distancen altså givet i en xy i stedet.

**Hannibal | ITU**: Så det er nemmere bare at sige, at afstanden er lige med kvadratråden af y2 minus y1.

**Hannibal | ITU**: Det var den anden, der var to forskellige.

**Filip | ITU**: Det er den der Euclidean.

**Filip | ITU**: Det er den, vi er enige om at starte med at prøve med.

**Filip | ITU**: Den er meget nemmere.

**Filip | ITU**: Det er den, vi kender for en kundasjæ.

**Filip | ITU**: Ja.

**Filip | ITU**: Så det er bare den, I startede med, så low weight.

**Filip | ITU**: Det er det.

**Filip | ITU**: Men præcis, hvordan vi implementerer det i, hvordan vi...

**Filip | ITU**: Ja.

**Filip | ITU**: Det er det, jeg prøver at finde ud af.

**Filip | ITU**: Okay.

**Hannibal | ITU**: Cool.

**Filip | ITU**: Ja.

**Filip | ITU**: Alright.

**Sebastian | ITU**: Fedt. Hvad med dig, Siffy?

**Sebastian | ITU**: Jeg hedder Sebastian.

**Sebastian | ITU**: Jeg har redigeret lidt eller lavet om på...

**Sebastian | ITU**: Altså den der test-class, den er mere læslig nu, men den er fucking god i hvordan den er skrevet op.

**Sebastian | ITU**: Det er umuligt den bedste implementation, fordi det står sikkert bare som om...

**Sebastian | ITU**: Altså nu har jeg...

**Sebastian | ITU**: Det er lige før, at hvis der var en skærm, jeg kunne sætte det op på, så kunne jeg vise det, men...

**Sebastian | ITU**: Det er virkelig en lortet test-class lige nu.

**Sebastian | ITU**: Den er mere som en "boilerplate" eller sådan.

**Sebastian | ITU**: Det her er skabelogen på, hvordan det kan være,

**Sebastian | ITU**: fordi der er ikke noget brugbar på det endnu.

**Sebastian | ITU**: Jeg vil gerne sætte den op, lave et "pull" eller "push" på Gens,

**Sebastian | ITU**: så kan I se, hvad det er.

**Sebastian | ITU**: Det står der bare, men det er blevet mere læst,

**Sebastian | ITU**: man kan se, hvad der foregår.

**Sebastian | ITU**: Det er så det meste, jeg gjorde.

**Sebastian | ITU**: har jeg også kigget på noget Dijkstra, altså læst det op på.

**Sebastian | ITU**: Det var fordi, jeg var lidt forvirret over, hvad det der Relax for eksempel var.

**Sebastian | ITU**: Jeg troede det var noget, der var inde under Dijkstra, men det er seriøst bare.

**Sebastian | ITU**: Altså Relax, det er jo bare der, hvor du udregner den shortest path distance mellem nuder for eksempel.

**Sebastian | ITU**: Det der Relax, det er der, hvor du implementerer den korteste vej i metoden.

**Sebastian | ITU**: Og der er lidt misforstået med, hvad Relax var.

**Sebastian | ITU**: Det er jo sådan set allerede implementeret inde i den der Dijkstra-klasse, vi har.

**Sebastian | ITU**: Så jeg har egentlig bare læst op på Dijkstra, så jeg ændrer det i test.

**Sebastian | ITU**: Det har jeg gjort, men ikke meget mere end det.

**Sebastian | ITU**: Okay.

**Hannibal | ITU**: Yes.

**Hannibal | ITU**: Cool.

**Sebastian | ITU**: Jeg har ikke mere.

**Sebastian | ITU**: Hvad er planen fremmejere?

**Sebastian | ITU**: Det skal jeg lige drøfte med jer.

**Sebastian | ITU**: Jeg skal høre, hvor langt I nu, og så tror jeg ellers, så er det bare samarbejde med Philip på Dijkstra.

**Sebastian | ITU**: Bare gøre den helt clean, men da jeg lige snakker, og så hører, hvor langt I er.

**Sebastian | ITU**: Jeg ved faktisk ikke hvad jeg skal gøre i gang med nu, hvis det er bare et dykstyr.

**Hannibal | ITU**: Helt sikkert. Fedt, men så snakker vi om DBH.

**Hannibal | ITU**: Ja.

**Hannibal | ITU**: Fedt mand.

**Hannibal | ITU**: Hvad vil jeg med at sige morgen?

**Hannibal | ITU**: Jeg hedder Simon.

**Simon | ITU**: Øhm, siden sidst har vi fået rendering op at køre.

**Simon | ITU**: Øhm, vi får nu tegnet Bornholm.

**Simon | ITU**: Øhm, vi har sat nogle farver på, basic farver, alt efter hvilke tags de måske lige Waze har.

**Simon | ITU**: Men har bare gjort indtil videre, så alle varier bliver samme farver, og alle landarealer bliver samme farver.

**Simon | ITU**: Så jeg tænkte, vi skal ikke godt så ophængig af om det f.eks. er en græsmark eller en skov.

**Simon | ITU**: Det bliver forskellige farver.

**Simon | ITU**: Og så er Vores Bornholm også meget gennemsigtigt lige nu, fordi det kun er omridserne af alle, der bliver tegnet.

**Simon | ITU**: Så jeg tror, vi skal have fundet en måde at "fill" alle de der "ways" på.

**Simon | ITU**: Måske ophænger af noget areal.

**Simon | ITU**: De største elemente bliver tegnet først.

**Sebastian | ITU**: Ja.

**Sebastian | ITU**: Fændte de også ud af noget med tags?

**Sebastian | ITU**: Var det ikke det vi snakkede om sidste år?

**Sebastian | ITU**: Der var sådan en sindssyg mange tags, hvor man skulle sætte nogle standard tags?

**Simon | ITU**: Jo jo. Lige nu har vi bare så de forskellige ways, der fx har 1 tag, bliver alle samme farver.

**Simon | ITU**: Så dem der har 2 andre, bliver alle andre farver.

**Simon | ITU**: Og hvis der er nogle elemente, der ikke har nogen af tags, så bliver de bare sorte.

**Filip | ITU**: - Ja, okay det er også cool. - Nice.

**Hannibal | ITU**: Cool mand. Og Danny hører med på den ord, det er.

**Hannibal | ITU**: - Ja, cool. - Hvad hedder det?

**Hannibal | ITU**: Og jeg hedder Hannibal, og jeg har arbejdet på R3'er, og ikke fået lavet så meget nyt i implementationen,

**Hannibal | ITU**: fordi jeg så er i gang med at finde ud af, hvordan de fungerer.

**Hannibal | ITU**: Så er det det, jeg er i gang med.

**Hannibal | ITU**: - Fedt mand. - Fedt mand.

**Hannibal | ITU**: - Godt scrum, drenge. - Godt scrum.

**Hannibal | ITU**: - Cool.

**Hannibal | ITU**: - Skal vi snakke om det, at dig ikke skulle noget?

**Hannibal | ITU**: - Ja, cool.

**Hannibal | ITU**: - Hvordan er det, I havde planlagt det der med at bygge tests op?

**Sebastian | ITU**: - Jamen, det har vi ikke planlagt sådan, så.

**Sebastian | ITU**: - Nej.

**Sebastian | ITU**: - Jeg kan lige smide.

**Sebastian | ITU**: - Lige skal.

**Sebastian | ITU**: - Så det, jeg har gjort her.

**Hannibal | ITU**: - Yes.

**Hannibal | ITU**: - Må jeg skruer for lyset?

**Hannibal | ITU**: - Ja, det var godt.

**Hannibal | ITU**: - Jeg skruer for lyset.

**Filip | ITU**: - Okay, ja.

**Sebastian | ITU**: - Ja.

**Sebastian | ITU**: - Så det, jeg har gjort her, det er en hashmap med noden A, og så den her Arraylist, det er så de der noder, som A kan gå hen til.

**Sebastian | ITU**: For eksempel her, der sætter vi graph.get A med noden A, og så lægger vi en ny path B, som giver vægten 1 til den.

**Sebastian | ITU**: Her har vi stadig A, så laver vi en anden path, der kan gå direkte til C, den får vi så vægt 4.

**Sebastian | ITU**: Så går vi videre til node B her, og så må vi også bruge sin egen Arraylist.

**Sebastian | ITU**: I kan godt se nu, at det er nok ikke den fedeste implementation,

**Sebastian | ITU**: fordi jeg tror godt, det kan tage meget læsetid at hver eneste node får sin egen Arraylist.

**Sebastian | ITU**: Det går jeg ud fra.

**Sebastian | ITU**: Men det er i det mindste læselid.

**Sebastian | ITU**: Så giver vi en ny part fra B til C, som giver vegt 2.

**Sebastian | ITU**: Som I nu kan se, så har vi fra A, der går til B, vegt 1,

**Sebastian | ITU**: fra B til C, det er vegt 2, det giver 3 i alt.

**Sebastian | ITU**: Og vi har en fra A til C her, som har vegt 4.

**Sebastian | ITU**: Og det er jo den længste vej, hvis vi skulle teste det.

**Sebastian | ITU**: Så hvis vi ved, at den tager fra A til C på vik 4, så ved vi at testen fejler.

**Sebastian | ITU**: Så tester vi.

**Sebastian | ITU**: Hernede, vi vil gerne have et Aspen E-post, så vi vil gerne have det rammer på vik 3.

**Sebastian | ITU**: Result.

**Sebastian | ITU**: Og ja, kan vi køre på testen.

**Sebastian | ITU**: Og så siger den, at det er rigtigt, men det er...

**Sebastian | ITU**: I kan godt se hvordan implementationen nok ikke...

**Sebastian | ITU**: Altså den holder jo ikke i længden, det er mere bare sådan en skabelon på hvordan det kan se ud.

**Sebastian | ITU**: Og jeg tror heller ikke, at det er meningen, at vi skal lave ArrayList på den måde der.

**Sebastian | ITU**: Det var bare mere læsligt forståelse af hvad det foregår.

**Filip | ITU**: Jeg tror bare, at i stedet for at lave en ArrayList, så skal man bare lave et Array,

**Filip | ITU**: Når man tæller den snapper op og laver et array på den størrelse.

**Sebastian | ITU**: Jo, det tror jeg godt man kan.

**Sebastian | ITU**: Men jeg føler bare, at det er nemmere, når vi har en idé for os,

**Sebastian | ITU**: hvordan vi laver de der weighted graphs, at teste det.

**Sebastian | ITU**: Fordi lige nu er det jo mere en test på, hvordan det kan være.

**Hannibal | ITU**: Hvordan er det i...

**Hannibal | ITU**: Altså, den der dykes forvirker jo?

**Sebastian | ITU**: Jamen, den virker jo.

**Sebastian | ITU**: Men det var fordi, jeg var meget forvirret over det der...

**Sebastian | ITU**: Inden på deres slides, fx, der har de skrevet rigtig meget med sådan "relax".

**Sebastian | ITU**: Der er også videoer, jeg har set med relax.

**Sebastian | ITU**: Jeg troede, at relax var noget andet indenover i Dijkstra.

**Sebastian | ITU**: Men relax, det er simpelthen bare der, hvor du har...

**Sebastian | ITU**: Altså, du går fra den ene part til den næste.

**Sebastian | ITU**: Og hvis den part plus den anden part, altså den er den korteste, så relaxer den.

**Sebastian | ITU**: Så implementeres den korteste vej.

**Sebastian | ITU**: Og det var sådan lidt det, jeg var forvirret om, hvad fanden er det, relax egentlig er.

**Sebastian | ITU**: Fordi jeg så, der var indenover i L, hvordan skal man sige, Dijkstra's aludritme.

**Sebastian | ITU**: Og jeg havde ikke skrevet noget, der hed relax.

**Sebastian | ITU**: Men det er bare den shortest path distance.

**Sebastian | ITU**: Det er bare det de kalder den.

**Hannibal | ITU**: Så for at forstå det her rigtigt, så går den igennem,

**Hannibal | ITU**: den kigger en masse notes igennem, og når den så er fundet ud af,

**Hannibal | ITU**: det her er den kortste vej til videre,

**Hannibal | ITU**: så relaxer den for at se,

**Sebastian | ITU**: det er en ny udgangspunkt.

**Hannibal | ITU**: Så hver gang den finder en ny, ja, præcis.

**Sebastian | ITU**: Og det var egentlig det, jeg var bare forvirret over,

**Sebastian | ITU**: hvad det der relax var, fordi jeg så det overalt,

**Sebastian | ITU**: men jeg havde ikke sådan rigtigt,

**Sebastian | ITU**: men det er bare Dijkstra.

**Sebastian | ITU**: Det er inden i Dijkstra algoritmen,

**Sebastian | ITU**: det er det der hvor, ja.

**Hannibal | ITU**: Men hvad er så meningen med det du arbejdede med?

**Filip | ITU**: Det er fordi sidst så kunne jeg ikke få det der til at fungere.

**Filip | ITU**: Nej.

**Filip | ITU**: Jeg tror det var også, at vi prøvede at koble dem på en anden.

**Sebastian | ITU**: Det gik øvel også.

**Filip | ITU**: Ja, så nu prøvede jeg bare sådan.

**Filip | ITU**: Alt det her edges og sådan noget, hvor jeg også prøvede at tage udgangspunkt i de her.

**Filip | ITU**: Herindefra.

**Filip | ITU**: Okay.

**Filip | ITU**: Men shit.

**Filip | ITU**: Det kan jeg ikke mene.

**Filip | ITU**: Det kan være, at jeg lige skal blive bønne til det der og så tage udgangspunkt i den.

**Filip | ITU**: Igen.

**Hannibal | ITU**: Det tror jeg også umiddelbart.

**Hannibal | ITU**: Nu har du i hvert fald et udgangspunkt, der virker her.

**Hannibal | ITU**: Og digstrug.

**Hannibal | ITU**: Ja, digstrug.

**Hannibal | ITU**: Så den her virker. Så det er jo perfekt.

**Hannibal | ITU**: Ja, indtil videre.

**Sebastian | ITU**: Men virker den udgør?

**Sebastian | ITU**: Ja, den virker jo.

**Sebastian | ITU**: Og du har selv skrevet den, ikke?

**Sebastian | ITU**: Jeg har skrevet...

**Sebastian | ITU**: Det skal lige sige, den her kode heroppe, hvor der kommer sådan en comparator,

**Sebastian | ITU**: comparing double, det fik jeg hjælp til.

**Sebastian | ITU**: Men ellers...

**Sebastian | ITU**: Forstår du, hvordan den fungerer?

**Sebastian | ITU**: Jeg kan godt forklare, hvordan den fungerer.

**Sebastian | ITU**: Hvis det er...

**Sebastian | ITU**: Kan jeg ikke de få fuldskærm her?

**Sebastian | ITU**: Ja, det skal lige siges, at vi har også en class her, sådan en node distance.

**Sebastian | ITU**: Det er bare distancen på, fordi det får vi brug for inden til...

**Sebastian | ITU**: Hvad hedder det? Ja.

**Sebastian | ITU**: Vi har vores shortest distance her.

**Sebastian | ITU**: Det er node start. Det er 0.

**Sebastian | ITU**: Og så har vi node target.

**Sebastian | ITU**: Og så har vi også, hvad hedder det...

**Sebastian | ITU**: Ja, vores hashmap.

**Sebastian | ITU**: Ja. Som en list. Nej, som en map, det kan jeg godt se.

**Sebastian | ITU**: Ja. Vi sætter Distance.

**Sebastian | ITU**: Put Start her.

**Sebastian | ITU**: Og så har vi vores Unsettled Nodes.

**Sebastian | ITU**: Det er en ny...

**Sebastian | ITU**: Distance, start og så distance.

**Sebastian | ITU**: Og så selvfølgelig, det her har alle videoer.

**Sebastian | ITU**: Det der med, at while Unsettled Nodes, altså så længe der ikke er nogen Unsettled Nodes,

**Sebastian | ITU**: - hvor den ikke er empty, så kører programmet.

**Sebastian | ITU**: Hvis den er empty, så stopper den.

**Sebastian | ITU**: Det her pole er faktisk super vigtigt.

**Sebastian | ITU**: Hvis du har en priority queue, så poler du det element, der er forrest køen.

**Sebastian | ITU**: Det fjernes.

**Sebastian | ITU**: Og det er så udgangspunkt i det, som kører.

**Sebastian | ITU**: Men det der poll, det skulle jeg også lige læse op på.

**Sebastian | ITU**: Det er faktisk meget smart i det der, at også se, hvis du har en linked list,

**Sebastian | ITU**: f.eks. hvis du har 5, 10, 15, 20, og du så går fra en note til den næste,

**Sebastian | ITU**: så fjerner du det automatisk. F.eks. hvis du er på note 5, går du over til note B,

**Sebastian | ITU**: du fjerner 5 automatisk. Det er alle de ekstra salueritner har det.

**Sebastian | ITU**: Smart.

**Sebastian | ITU**: If visited that contains current node, yeah, continue.

**Sebastian | ITU**: If current node that equals target, return distance.getCurrentNode

**Sebastian | ITU**: Øhh...

**Sebastian | ITU**: Nu bliver jeg lidt i tvivl.

**Sebastian | ITU**: Hvis det er current node = target...

**Sebastian | ITU**: Nå, ja okay.

**Sebastian | ITU**: Hvis current node = target,

**Sebastian | ITU**: Hvis det er endepunktet.

**Sebastian | ITU**: Så returnerer du distancen på den current node.

**Sebastian | ITU**: Øhh...

**Sebastian | ITU**: "ish" for "neighbors"

**Sebastian | ITU**: "note_neighbors = "ish.gettarget"

**Sebastian | ITU**: "dottomuist distance"

**Sebastian | ITU**: Det jeg er ret sikker på, der sker her, det er at vi for eksempel går fra den ene node til den anden

**Sebastian | ITU**: og tjekker distancen for den, altså hvor langt der er

**Sebastian | ITU**: og så hvis det er nogen her, "note_new_distance"

**Sebastian | ITU**: den er kortere end "distance_get_default"

**Sebastian | ITU**: altså der hvor du kom fra

**Sebastian | ITU**: den tester så for, hvad hedder det

**Sebastian | ITU**: om den nye distance er kortere end den anden.

**Sebastian | ITU**: Og det er også det her double dot positive infinity.

**Sebastian | ITU**: Det er alle de der noder, som er unsettlede.

**Sebastian | ITU**: De er sat til positive infinity.

**Sebastian | ITU**: Altså indtil du besøger dem, så får de distance.

**Sebastian | ITU**: Den der weighted graph.

**Sebastian | ITU**: Ja, sådan den nye sag.

**Sebastian | ITU**: Men der er ikke stress-sikler-ritme.

**Sebastian | ITU**: Min erfaring med den er, at den er rimelig simpel, implementationen.

**Sebastian | ITU**: Men det tager noget tid lige at blive klog på den.

**Sebastian | ITU**: Ja, men det der var cirka en uges tid siden.

**Sebastian | ITU**: Ideen er god nok på Dijs'algoritme, den er bare lidt...

**Sebastian | ITU**: Men I kan også godt se, at ude fra Dijs'algoritme, så er testklassen, den er altså også...

**Sebastian | ITU**: Den er ret simplificeret i forhold til.

**Sebastian | ITU**: Men ja, den er god nok. Vi skal bare finde ud af, hvordan vi skal lave sådan en weighted graph.

**Sebastian | ITU**: Altså, vigtigt noget på, så tror jeg, den virker fin nok.

**Hannibal | ITU**: Hvordan går det med at lave weighted graph?

**Sebastian | ITU**: Det har vi slet ikke gjort.

**Filip | ITU**: Det er det, jeg har prøvet med det her edge weighted digraph, men det gav bare overhovedet ikke mening.

**Filip | ITU**: Og så spurgte jeg chatten inden i det der AI-chat i NZJ, og der var for at vælte helt op.

**Filip | ITU**: Der er ikke nogen fejl nu, men nu falder der ingenting.

**Filip | ITU**: Jeg tror også, at vi bliver nødt til at lave dem på bund, eller så kan vi ikke finde ud af det.

**Filip | ITU**: Nu prøvede jeg bare lige at se om jeg kunne forstå det, men der er det.

**Hannibal | ITU**: Ja, det er shit.

**Filip | ITU**: Det er det.

**Filip | ITU**: Jeg tror, jeg skal tilbage til den der version.

**Hannibal | ITU**: Helt sikkert.

**Hannibal | ITU**: Hvad hedder det?

**Hannibal | ITU**: Alright.

**Hannibal | ITU**: Det er som jeg tænker lige nu.

**Hannibal | ITU**: Vil du ikke hoppe ind på din dike strike igen, Serby?

**Hannibal | ITU**: Yes.

**Hannibal | ITU**: Det er som jeg tænker lige nu, det vil være bedst, ikke?

**Hannibal | ITU**: Ja, fordi vi skal bare...

**Hannibal | ITU**: Den her dike strike virker jo bare lige nu.

**Hannibal | ITU**: Du er perfekt.

**Hannibal | ITU**: - og lige have den til at virke med de rigtige interfaces og sådan noget.

**Hannibal | ITU**: Så jeg tror, den første ting, jeg måske ville gøre ved jer, var sådan at...

**Hannibal | ITU**: - vi nok gør det via en Code Whitney session, så i begge to, hvis du kan sidde og opdag...

**Hannibal | ITU**: - I stedet for den der "no distance", så skulle du måske lave den om til en edge, tænker jeg?

**Hannibal | ITU**: - Ja.

**Hannibal | ITU**: - Vi kan skrive det ned, og så kan vi opse mere bagefter, hvis der er...

**Hannibal | ITU**: Det er bare så lige, vi kan se i skærmen samtidig.

**Hannibal | ITU**: Nå ja, sorry.

**Hannibal | ITU**: Hvad hedder det?

**Hannibal | ITU**: Tænker jeg, i stedet for det der note distance, så skal vi have en edge.

**Hannibal | ITU**: Og sådan, hver edge har ligesom en...

**Hannibal | ITU**: Når det er en edge?

**Hannibal | ITU**: Det er, den har ligesom en startpunkt og en sluttpunkt.

**Hannibal | ITU**: Og så vil det måske være smart, hvis I lavede en record.

**Hannibal | ITU**: Det vil sige, en record, det er faktisk bare en...

**Hannibal | ITU**: Det du nævnte sidst, det kan jeg godt huske.

**Hannibal | ITU**: Vi kan bare opsummere bagefter.

**Hannibal | ITU**: En record det er jo bare en klasse, som har en masse fede hjælpfunktioner.

**Hannibal | ITU**: Så det er egentlig bare en klasse i.

**Hannibal | ITU**: Den her edge skal jo så have en A-node, og så skal den også have en B-node.

**Hannibal | ITU**: altså det er jo et eller andet sted

**Hannibal | ITU**: så er du ligegyldig

**Hannibal | ITU**: bare for en det er, for det er jo ikke ens rettede vej

**Hannibal | ITU**: det her, så du kan jo altid gå fra A til B

**Hannibal | ITU**: og du kan også gå fra B til A

**Hannibal | ITU**: og så skal den så også have en weight, og weight det kalder vi

**Hannibal | ITU**: W, og den skal måske være en double

**Hannibal | ITU**: og så skal vi finde ud af

**Hannibal | ITU**: I behøver ikke bruge en record

**Hannibal | ITU**: I kan faktisk bare starte med en klasse

**Hannibal | ITU**: det er måske nemmere at starte med en klasse

**Hannibal | ITU**: inden i constructoren, når I så laver den

**Hannibal | ITU**: så constructoren vil så have

**Hannibal | ITU**: Edge

**Hannibal | ITU**: Node

**Hannibal | ITU**: A

**Hannibal | ITU**: Node

**Hannibal | ITU**: B

**Hannibal | ITU**: Og

**Hannibal | ITU**: Double

**Hannibal | ITU**: Weight

**Sebastian | ITU**: Altså det der laver den der kvadratrude whatever

**Hannibal | ITU**: Præcis, for så siger jeg så heran

**Hannibal | ITU**: Så siger jeg så, at this

**Hannibal | ITU**: A er lige med A

**Hannibal | ITU**: Og

**Hannibal | ITU**: This

**Hannibal | ITU**: Øh, W

**Hannibal | ITU**: Åh undskyld, så den her skal jo faktisk ikke være med i en prop, fordi den skal udregne.

**Hannibal | ITU**: Så vi kan kun give note B og note A, ikke?

**Hannibal | ITU**: Altså, this note, W, skal så være lige med.

**Hannibal | ITU**: Her kan man så kalde den, øh, I kan måske bare kalde den, øh,

**Hannibal | ITU**: Øh, calc.

**Hannibal | ITU**: Hvad er det for en navn?

**Hannibal | ITU**: Euclidean, i stedet for at calculate.

**Hannibal | ITU**: Nå, er det bare navnet på den?

**Simon | ITU**: - Så er det bare "waste" er en source. - "Euclidean dist".

**Hannibal | ITU**: Så kan I lave denne her "a" "b".

**Hannibal | ITU**: Og det er sådan en constructor, så jeg kan se det.

**Hannibal | ITU**: Og så kan I lave den her funktion hernede, der hedder "calc.euclidean dist".

**Hannibal | ITU**: Så vi igen tager "node a" og "node b".

**Hannibal | ITU**: Og så skal den, det første som skal gøre det, er så skal jeg regne det her ud.

**Hannibal | ITU**: Måske skal vi starte med at sige...

**Hannibal | ITU**: Øhm...

**Hannibal | ITU**: Hvad skulle det være?

**Simon | ITU**: Nu er det der, der er nogle varende, der bliver tegnet ud.

**Hannibal | ITU**: Jo, det er der, hvor vi så skal finde det, der hedder Delta Lat.

**Hannibal | ITU**: Så vi skal finde forskellen mellem den ene Delta og den anden Delta.

**Hannibal | ITU**: Så delta lat er lige med b lat minus a lat.

**Hannibal | ITU**: Og delta long er lige med b long minus a long.

**Hannibal | ITU**: Og så skal den bare return.

**Hannibal | ITU**: Math.squared.

**Hannibal | ITU**: Hvorfor siger delta lat i anden.

**Hannibal | ITU**: Og plus delta long i anden.

**Hannibal | ITU**: Så måden, at vi skal initialize det, det her er efter, at vi skal lave den her klasse, edgeklassen, som så ligesom kan calculate den her hovedet her.

**Filip | ITU**: Yes.

**Hannibal | ITU**: Og så er det så sådan, at inde i den, du har, der hedder note.

**Sebastian | ITU**: Det er et helt dumt spørgsmål, hvordan fandme, når man laver en anden.

**Hannibal | ITU**: Du kan enten lave deltalat gang deltalat,

**Hannibal | ITU**: eller så kan du lave

**Hannibal | ITU**: math.pow,

**Hannibal | ITU**: og så parentesk skriver deltalat,

**Hannibal | ITU**: og så kommer 2.

**Sebastian | ITU**: Jeg tror bare, jeg laver gang deltalat.

**Hannibal | ITU**: Ja, det var også helt fint.

**Hannibal | ITU**: Og så brug den i stedet for note distance.

**Hannibal | ITU**: Det er det første.

**Hannibal | ITU**: Så der igennem har vi også den her

**Hannibal | ITU**: bidirectional edge.

**Hannibal | ITU**: Og så er der den der,

**Hannibal | ITU**: hvad var det, det hedder?

**Hannibal | ITU**: Den der graf,

**Hannibal | ITU**: - Det er det, vi skal lave. Og grafen, vi skal lave, den tænker jeg måske, det vil være genial at bygge op på...

**Hannibal | ITU**: Hvem var det, fandt du en graf? Er det en map, eller er det en liste?

**Sebastian | ITU**: - Ja, en map. Du har en graf, du har nodeen, punktet A, B, C, whatever.

**Sebastian | ITU**: Og så har du en Aradis, der kommer efterfølgende.

**Sebastian | ITU**: Inden på...

**Sebastian | ITU**: ...på...

**Sebastian | ITU**: ...value-positionen.

**Hannibal | ITU**: Cool.

**Sebastian | ITU**: Så tror jeg, at det er...

**Sebastian | ITU**: Men det er måske noget, vi lige skal kigge op på.

**Sebastian | ITU**: Bliv lidt klogere på os.

**Sebastian | ITU**: Det er i hvert fald sådan, jeg har gjort til min test.

**Hannibal | ITU**: Det lyder også meget rigtigt, for så måden det er på det...

**Hannibal | ITU**: Hvis I har fire nodes.

**Hannibal | ITU**: A, B, C, D.

**Hannibal | ITU**: Så kommer det til at være et HashMap med fire keys i.

**Hannibal | ITU**: som så har en liste med edges i.

**Hannibal | ITU**: Og så betyder det så, at I skal jo så lave en standard, som vi skal benytte i vores dataindlæsning.

**Hannibal | ITU**: Så I skal lave en standard for, hvordan det er, at data skal indlæses.

**Hannibal | ITU**: Fordi vi skal nå til det sted, der hedder, at det skal være en HashMap, hvor keys and nodes...

**Hannibal | ITU**: Havde vi egentlig en anden class, der hedder Edge?

**Hannibal | ITU**: Nej, det var bare noget, vi snakkede om.

**Hannibal | ITU**: Hvad hedder det?

**Hannibal | ITU**: Det skal være en HashMap, hvor keys and nodes og values er lister af edges.

**Hannibal | ITU**: Så det er egentlig bare, at jeg skal sætte den standard.

**Hannibal | ITU**: Og så implementere det her Edge-ting ind i selve den der Dijkstra-ting, som vi har nu.

**Hannibal | ITU**: Og så bare sørge for, at det fungerer.

**Hannibal | ITU**: Og så når det så er gjort, så skal I lave i forhold til de der tests der.

**Hannibal | ITU**: Så i stedet for at lave testen på den måde, som du har gjort, det fungerer godt, men det er bare mega langsomt.

**Hannibal | ITU**: Så er det måske smartere, hvis I lavede de der tekstfiler.

**Hannibal | ITU**: Jeg ved ikke, om du bare skal teste 001 eller sådan noget.

**Hannibal | ITU**: Og så contentet i den vil så være, så første linje kunne så være 55, 71, 53.

**Hannibal | ITU**: 32, det er så først en af den note A, og det er en af den note B.

**Hannibal | ITU**: Og så er...

**Hannibal | ITU**: Der er faktisk noget galt.

**Hannibal | ITU**: Fordi I bliver nødt til også at sige, hvordan den note er forbundet.

**Hannibal | ITU**: Så I bliver faktisk nødt til I'ers test, bliver I nødt til at give en hashmap mellem bestemte værdier I, ikke?

**Hannibal | ITU**: Jo.

**Hannibal | ITU**: I er nød af mine?

**Hannibal | ITU**: Så I bliver nødt til at have en tekstfil, for eksempel, som man kan indlæse ind, og så kan I parse tekstfilens, for så at den så leder over til et hashmap.

**Simon | ITU**: Jeg var på Java 21, for jeg har udmattet, og det skal du over til Java 24.

**Filip | ITU**: Nu spørger jeg måske dumt, ikke?

**Filip | ITU**: Men hvis et punkt, ligesom...

**Filip | ITU**: Lige nu så har vi, når vi regner det derude, så er det jo kun, hvis det er sådan...

**Filip | ITU**: Hvis det endepunkt, vi skal hen i, hvis dansk koordinater er større end...

**Filip | ITU**: Hvis man skal sådan den anden vej, så bliver det jo mindre.

**Hannibal | ITU**: Ja, og det er så der, hvor I skal...

**Hannibal | ITU**: Den bedste måde at gøre det på, faktisk nok, er, at I tager...

**Simon | ITU**: - Den får jeg ikke en fejl i hvert fald.

**Hannibal | ITU**: - Ja, fordi I skal finde den.

**Hannibal | ITU**: - Okay.

**Hannibal | ITU**: - Okay, jeg ser to muligheder, men jeg ved faktisk ikke om jeg først kan fungere.

**Hannibal | ITU**: Den ene mulighed er jo, så i stedet for at I så kører med B og A,

**Hannibal | ITU**: skal I måske køre den med sådan 1st og 2nd,

**Hannibal | ITU**: og så skal I så assigne 1st og 2nd note af left-roffen for størst.

**Hannibal | ITU**: - I know what I mean?

**Hannibal | ITU**: - Okay.

**Hannibal | ITU**: - Så kan vi så se, okay, hvis B er større end A,

**Hannibal | ITU**: Men det er jo svært, fordi den både har latitude og longitude.

**Hannibal | ITU**: Præcis.

**Hannibal | ITU**: Men det er jo faktisk fint at...

**Hannibal | ITU**: Ja, det ved jeg ikke.

**Hannibal | ITU**: Kan vi vide, om det er et problem?

**Hannibal | ITU**: Okay, nu prøver vi.

**Hannibal | ITU**: Det er jo faktisk ikke et problem, tror jeg.

**Filip | ITU**: Man skal måske lave det sådan, at...

**Filip | ITU**: Ej, det ved jeg ikke.

**Filip | ITU**: Lad os lige prøve at finde ud af det.

**Hannibal | ITU**: Så nu hvis vi har et eksempel, hvor b er 10,4, og a er 7,6.

**Hannibal | ITU**: Så betyder det så, at delta lat er lige med 10 minus 7, som er 3.

**Hannibal | ITU**: Og delta long er lige med 4 minus 6 minus 2.

**Hannibal | ITU**: Ja, altså andet alternativ vil jo være, at hvis vi bytter om på dem,

**Hannibal | ITU**: At delta lat vil være 7.

**Sebastian | ITU**: Jeg har måske et andet dumt spørgsmål.

**Sebastian | ITU**: Hvis vi nu vil antage, at det der kommer ind i den der dykstra,

**Sebastian | ITU**: at det er minus 2 nu, ikke?

**Sebastian | ITU**: Det må der vel ikke være minus i dykstra.

**Sebastian | ITU**: Nej.

**Hannibal | ITU**: Hvad mener du med minus?

**Sebastian | ITU**: Der må ikke være nogen dykstra algoritme virker,

**Sebastian | ITU**: så længe der ikke er nogen grad,

**Sebastian | ITU**: altså nogen vægtet retning, som er minus.

**Hannibal | ITU**: Men man kan faktisk ikke tage kvadrat rundt af minus?

**Hannibal | ITU**: og når du sætter to ting i andet

**Hannibal | ITU**: så kan det heller ikke blive minus

**Sebastian | ITU**: det er faktisk utroligt klubt

**Hannibal | ITU**: men der er lige noget andet

**Hannibal | ITU**: det er fordi

**Hannibal | ITU**: at

**Hannibal | ITU**: okay, så hvis vi siger

**Hannibal | ITU**: at b er størst

**Hannibal | ITU**: og a er mindst

**Hannibal | ITU**: hvis vi så starter med at sige

**Hannibal | ITU**: okay, men b, y det er 10

**Hannibal | ITU**: minus a, y det er 7

**Hannibal | ITU**: det vil så give 3

**Hannibal | ITU**: og så nedover det til long være

**Hannibal | ITU**: hvor vi så siger

**Hannibal | ITU**: b, x er 4

**Hannibal | ITU**: minus a, x, det er 6

**Hannibal | ITU**: og det er så lige med minus 2

**Hannibal | ITU**: nu prøvede jeg bare lige at teste den

**Hannibal | ITU**: and I'm embarrassed at I didn't know this before

**Hannibal | ITU**: men hvis vi havde byttet om på den

**Hannibal | ITU**: der stod a, y minus b, y

**Hannibal | ITU**: så har det været 7 minus 10

**Hannibal | ITU**: i stedet for 10 minus 7

**Hannibal | ITU**: og det har så givet minus 3 stedet

**Hannibal | ITU**: og det samme hernede med

**Hannibal | ITU**: delta long, så hvis vi så sagde

**Hannibal | ITU**: a, x minus b, x

**Hannibal | ITU**: det har så givet 2

**Hannibal | ITU**: hvor hvis vi havde sagt b, x minus a, x

**Hannibal | ITU**: så havde vi givet minus 2

**Hannibal | ITU**: Men det er fint nok, fordi at -3 i anden, det er det samme som 3 i anden.

**Hannibal | ITU**: Og 2 i anden er også det samme som -2 i anden.

**Hannibal | ITU**: Så det er faktisk ikke et problem, vi behøver ikke tænke på.

**Hannibal | ITU**: Det behøver I slet ikke bekymrer over.

**Hannibal | ITU**: Men nice, fordi så har I så readen på den måde.

**Hannibal | ITU**: Jeg vil sige, den større...

**Sebastian | ITU**: Lad os prøve at implementere det, også hvis det giver noget rud.

**Hannibal | ITU**: Ja, starte med at implementere det der edge der.

**Filip | ITU**: Men jeg har prøvet at lave det der "math.square-route", så gav den mig en eller anden fejl.

**Hannibal | ITU**: Måske fordi du ikke sagde det.

**Hannibal | ITU**: Nå, du skal sige "squirt".

**Hannibal | ITU**: Nååååååååååååååååååååååååååååå

**Hannibal | ITU**: - Mhm. - Så ser du sådan der.

**Hannibal | ITU**: - For access. - Start session.

**Hannibal | ITU**: - Præcis. Det skal vi lige confirmere.

**Hannibal | ITU**: - Det er bare noget med, at de gerne må tage de nye, hvis du dør.

**Hannibal | ITU**: - Ja, ja, ja. - Start session.

**Hannibal | ITU**: - Fedt. Link copy. Og så kan du bare sende den i Discorden.

**Hannibal | ITU**: - Ah. - Inde i demiluten.

**Hannibal | ITU**: - Er det rule? - Fedt.

**Filip | ITU**: Gang gang.

**Filip | ITU**: - Joj.

**Filip | ITU**: - Så burde du få sådan en lige om lidt.

**Filip | ITU**: - Så kan vi kun det er fair.

**Sebastian | ITU**: - Nå ja, sådan en IA confirm.

**Filip | ITU**: - Ja, jeg tror lidt.

**Filip | ITU**: - Du downloader den eller andet, rigtig.

**Filip | ITU**: - Ja.

**Simon | ITU**: - Come on man.

**Sebastian | ITU**: - Vs code could never.

**Simon | ITU**: - Der er vildt brudt.

**Hannibal | ITU**: - Så I skal finde ud af at fjerne den der node distance.

**Hannibal | ITU**: - Ja.

**Hannibal | ITU**: - Og så lave den om til det der.

**Hannibal | ITU**: um

