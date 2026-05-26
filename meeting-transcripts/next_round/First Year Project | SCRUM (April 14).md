**Speaker 1**
Fedt man.

**Speaker 1**
Skal vi se gutter?

**Speaker 1**
Det er på en af jeres gamle branches, så jeg tror den der er styring, der er helt fucked.

**Speaker 1**
Det er meget sejt, ikke?

**Speaker 1**
Cool.

**Speaker 1**
Jeg har lavet en virkelig simpel LOD, der gør at den render kun de ting, der er større end 16 pixels på skærmen.

**Speaker 1**
Smart.

**Speaker 1**
Men det er ikke den bedste LOD, men det fungerer jo fint.

**Speaker 1**
For eksempel hernede med byen her.

**Speaker 1**
Vi ville gerne have, at byen kunne blive set, men det bliver først renderet, når det kommer tæt på, når du ser større end 16 pixels.

**Speaker 1**
Det er fucking cool.

**Speaker 1**
Ja,

**Speaker 1**
men vi skal bare lige have forbedret den i hvert fald.

**Speaker 1**
Men man skal jo så ikke se, at der er en bygge jo.

**Speaker 1**
Præcis. Ja,

**Speaker 1**
det der.

**Speaker 1**
Så det skal vi bare lige have forbedret lidt.

**Speaker 1**
Også det der med at kunne se vejene, når man er sådan mere zoomet ud.

**Speaker 1**
Præcis.

**Speaker 1**
Og så tænker jeg også på, det ved jeg ikke, om det kan gøre,

**Speaker 1**
men for eksempel her med byen, det er jo et sygt godt eksempel.

**Speaker 1**
Fordi de her, de er jo vildt små, og der er fucking mange detaljer i dem.

**Speaker 1**
Så det er jo sygt mange ting, der ligesom skal tegnes.

**Speaker 1**
Hvad hedder det?

**Speaker 1**
Og det er fedt, men det tror jeg også kræver meget.

**Speaker 1**
Og jeg tror både byen der og med height curves,

**Speaker 1**
der tror jeg gør det.

**Speaker 1**
For eksempel, den tager jo alt,

**Speaker 1**
hvis vi for eksempel tager den her almeningen,

**Speaker 1**
den tager jo kun alt det, der overlapper med ting.

**Speaker 1**
Så hele almeningeneskoven er jo tegnet nu.

**Speaker 1**
Selvom den basic, jeg har gjort det bare for death purposes.

**Speaker 1**
Selvom den er basic næsten inde på skærmen.

**Speaker 1**
Og det er jo fint nok,

**Speaker 1**
men når vi så kommer til height curves,

**Speaker 1**
fordi height curves er vildt mange betalder,

**Speaker 1**
Og der er vildt mange height curves.

**Speaker 1**
Så hvis vi så er ude her,

**Speaker 1**
for eksempel,

**Speaker 1**
så skal den jo tegne.

**Speaker 1**
Altså så kommer vi til at få et kæmpe problem,

**Speaker 1**
fordi at alle height curves kommer til at

**Speaker 1**
overlappe med skærmen.

**Speaker 1**
Så vi kommer til at skulle tegne alle height curves.

**Speaker 1**
Men

**Speaker 1**
hvilket er fint nok,

**Speaker 1**
men height curves har fucking mange detaljer.

**Speaker 1**
Så næsten alle height curves kommer til at

**Speaker 1**
overlappe med det her segment.

**Speaker 1**
Men var det ikke det, der er en ting med,

**Speaker 2**
at vi ikke behøver at tegne height curves?

**Speaker 1**
Det kan godt være vigtigt.

**Speaker 2**
Det kan vi jo gøre for eksempel.

**Speaker 1**
Skal det være med at gøre det?

**Speaker 1**
Lad være med at tegne heightcurves?

**Speaker 1**
Det kan vi sagtens.

**Speaker 3**
Jeg tror det ville være det nemmeste at være med at tegne den.

**Speaker 3**
Så har man bare et knap, og så kan man se kun heightcurves.

**Speaker 3**
Det vigtigste var jo bare funktionaliteten af heightcurves.

**Speaker 3**
Ja.

**Speaker 4**
Helders det der.

**Speaker 4**
Men man kan jo lave sådan en knap,

**Speaker 4**
hvor man kan toggle height curve, så kan man måske have sådan en...

**Speaker 4**
Du ved, når man hopper sin mus herover sådan en...

**Speaker 4**
At hvis det nu er, at det er fucker helt.

**Speaker 1**
Ja.

**Speaker 1**
Ja.

**Speaker 1**
Var det et krav, at man kunne se height curve?

**Speaker 1**
Hvordan det så ud?

**Speaker 1**
Det kan jeg faktisk ikke huske. Det tror jeg ikke.

**Speaker 1**
Fordi vi kunne jo også bare gøre, som han havde i sit, at vi bare har en map.

**Speaker 1**
Og så kan man bare se, hvordan det bliver flottet.

**Speaker 1**
Og så ved jeg ikke, hvordan I har lavet farverne.

**Speaker 1**
Hans var jo bare meget naive,

**Speaker 1**
hvor der så kom sådan et flot overlay.

**Speaker 1**
Det var jo fungeret fint nok,

**Speaker 1**
egentlig.

**Speaker 1**
Men hvis I havde gjort det på en anden måde, hvor I har lavet farverne i forhold til C-level, så kunne man jo også gøre det.

**Speaker 1**
Men det kan være, at det er nemmere,

**Speaker 1**
fordi så behøver vi ikke.

**Speaker 1**
Det, som jeg tænkte på, var ellers, hvis vi skulle tage en tight curves,

**Speaker 1**
Altså så tror jeg, at hvis vi fx kommer helt herud,

**Speaker 1**
der tror jeg, det er fucking hårdt at tegne,

**Speaker 1**
fordi de har så mange detaljer.

**Speaker 1**
Så der giver det måske mening,

**Speaker 1**
at så lave en eller anden form for,

**Speaker 1**
så kan der inden for sådan games fx,

**Speaker 1**
så når du arbejder med level of detail der,

**Speaker 1**
så har du sådan med alle 3D-objekter,

**Speaker 1**
der har du 3 forskellige versioner.

**Speaker 1**
Så har du sådan high-res, mid-res, low-res,

**Speaker 1**
som bare er sådan, du ved,

**Speaker 1**
så med,

**Speaker 1**
altså high-res, der er alle vektorpunkter med,

**Speaker 1**
Og så mid-rest, der er det sådan noget, hver anden vektorpunkt er fjernet,

**Speaker 1**
basically.

**Speaker 1**
Og så tror jeg, at det er sådan 3 ud af 4, der er fjernet.

**Speaker 1**
Fordi det tænker så, at alternativet skulle være.

**Speaker 1**
Og måske også, at det så skulle virke noget med husen her.

**Speaker 1**
Men det kan vi jo se på senere. Det er jo ikke relevant at finde ud af endnu.

**Speaker 1**
Men fordi vi vil jo gerne have, at man kan se byen.

**Speaker 3**
Ja.

**Speaker 3**
Ja.

**Speaker 4**
Men det der,

**Speaker 4**
den er...

**Speaker 4**
Er det ikke, at den er zoomet ud af viewporten?

**Speaker 4**
Altså sådan...

**Speaker 4**
Jo. Det skal vel ikke se sådan der ud

**Speaker 4**
i forhold til

**Speaker 4**
sådan en final product.

**Speaker 4**
Nej nej, det er bare, at man kan se, hvad der bliver.

**Speaker 4**
Ja, præcis. Ja,

**Speaker 4**
ja.

**Speaker 4**
Ja, sådan så.

**Speaker 1**
Ja, fedt.

**Speaker 1**
Og så har jeg lavet en pull request,

**Speaker 1**
som vi lige skal tjekke igennem.

**Speaker 1**
Inde på...

**Speaker 1**
Inde på GitHub.

**Speaker 1**
Daniel,

**Speaker 1**
Simon,

**Speaker 1**
er der en af jer?

**Speaker 1**
For tækker jeg, når det ikke ødelægger noget.

**Speaker 1**
For den migrater vi over i main,

**Speaker 1**
eller merger vi over i main.

**Speaker 1**
Og så derefter skal vi så prøve at tage den branch,

**Speaker 1**
det vil sige main-branchen,

**Speaker 1**
og så skal vi prøve at merge den sammen med jeres branch, det I har lavet siden min på.

**Speaker 1**
Øhm, ja.

**Speaker 1**
Fedt.

**Speaker 1**
Cool.

**Speaker 1**
Nej, skal vi egentlig starte med Scrum?

**Speaker 1**
Sorry.

**Speaker 1**
Ja.

**Speaker 1**
Cool.

**Speaker 1**
Skal du starte, Philip?

**Speaker 4**
Ja, jeg har ikke kommet meget langt med det her benchmarking.

**Speaker 4**
Jeg har importeret, hvad hedder det,

**Speaker 4**
eller sat det der kodestykke ind til det her Max 7.

**Speaker 4**
Jeg synes, det er meget svært at finde ud af, hvordan det lige fungerer.

**Speaker 4**
Så skal jeg bare have en benchmarking-klasse, som jeg kan lave et stykke kod ind.

**Speaker 4**
Jeg tænker bare,

**Speaker 4**
at jeg finder en eller anden test herinde fra min testing.

**Speaker 4**
Men jeg skal stadig finde ud af, hvordan det der benchmarking fungerer.

**Speaker 1**
Kender I det der med Clort?

**Speaker 1**
Vil du have learning mode?

**Speaker 1**
Nej.

**Speaker 1**
Du har sådan noget herinde, når du som bruger det.

**Speaker 1**
Du kan trykke noget på plus,

**Speaker 1**
og så kan du vælge forskellige styles.

**Speaker 1**
Men så kan du sætte den på learning.

**Speaker 1**
Så det jeg nogle gange gør, hvis jeg skal lære det der.

**Speaker 1**
Benchmarking der, som er helt nyt.

**Speaker 1**
Så siger jeg bare sådan: "Hæske, hvor benchmarking har jeg aldrig gjort det før.

**Speaker 1**
Lær mig, hvordan man gør."

**Speaker 1**
Fordi den her learning mode, den er sådan trænet til at den

**Speaker 1**
underviser dig.

**Speaker 1**
Uden at give dig svaret. Forstår du mere?

**Speaker 1**
Sådan at den hjælper dig med at forstå det.

**Speaker 1**
Så kæmpe anbefaling med det i hvert fald.

**Speaker 4**
Og det er også free.

**Speaker 1**
Ja, det er også free.

**Speaker 1**
Løn og land.

**Speaker 4**
Og hvad skal du skrive?

**Speaker 1**
Jamen du kan gøre alting.

**Speaker 1**
Det smarte, du skal gøre faktisk, det er hvis du lige går ind og laver et projekt først.

**Speaker 1**
Og så trykker du bare "new project"

**Speaker 1**
og så kan du bare kalde det "first year project".

**Speaker 1**
Nice.

**Speaker 1**
Og så har du bare alle chats derindelig,

**Speaker 1**
for så har du mulighed for at referere alle chats du har

**Speaker 1**
i forhold til et first year project.

**Speaker 1**
Ja, så det er f*cking smart.

**Speaker 1**
Men så er det bare det umiddelbart, så kan du kopiere

**Speaker 1**
code snippet ind fx.

**Speaker 1**
Fedt. Cool.

**Speaker 1**
Nice.

**Speaker 4**
Men ellers har jeg ikke kommet meget længere end det.

**Speaker 1**
Hey, Sam.

**Speaker 1**
Alright.

**Speaker 1**
Cool.

**Speaker 1**
Hvad med at du briste?

**Speaker 3**
Vi fortsat i gang med at sætte tests op til vores parser.

**Speaker 3**
Og udover det,

**Speaker 3**
så er vi lidt størke, fordi vores program ikke kan køre.

**Speaker 1**
Ah, damn.

**Speaker 1**
Okay.

**Speaker 2**
Ja, der er sket et eller andet,

**Speaker 2**
så den ikke åbner det der,

**Speaker 2**
i en vindue app, hvor man kan se,

**Speaker 2**
hvad der bliver rendered.

**Speaker 2**
Ja, så må vi lige prøve at finde ud af. Men udover det har vi siden sidst lavet en... Hvad har vi lavet?

**Speaker 3**
Du ændrede vel på,

**Speaker 3**
at det ikke var en stakner til at læse...

**Speaker 2**
Nå ja, HideCraft-parseren har vi også lavet om på,

**Speaker 2**
så den fungerer mere optimalt.

**Speaker 1**
Cool.

**Speaker 1**
Ja.

**Speaker 2**
Ja.

**Speaker 3**
Jeg tror bare, vi skal have fixet vores program.

**Speaker 1**
Fedt. Nice.

**Speaker 1**
Hvad hedder det?

**Speaker 1**
Cool.

**Speaker 1**
Og det jeg har gjort,

**Speaker 1**
bare for at få det med record, det er, at jeg har fået R3 til at fungere med parser og rendering.

**Speaker 1**
Med en gammel commit.

**Speaker 1**
Så den har ikke jeres nyste updates.

**Speaker 1**
Men jeg har fået det til at fungere sammen nu.

**Speaker 1**
Og så har jeg da også en meget simpel level of detail,

**Speaker 1**
der fungerer.

**Speaker 1**
Til gengæld er der en udfordring i, at der er nogle features,

**Speaker 1**
som vi ikke kan...

**Speaker 1**
For lige nu har jeg bare fået dem til at fungere sammen.

**Speaker 1**
Så måden jeg har gjort det er sådan at jeg har lavet r-træet, og så har vi så efter jeres parser så kører, så laver jeg så den der map data der,

**Speaker 1**
og så tager jeg så det fra map data,

**Speaker 1**
og så bygger jeg så et relation tree og et way tree op.

**Speaker 1**
Og så tager jeg så to nye arrays eller lister,

**Speaker 1**
der hedder visible ways og visible relations,

**Speaker 1**
som jeg så sætter lige med relationtree.search, og så bruger jeg en viewport-boks, som jeg så har udregnet ud fra jeres minelad.

**Speaker 1**
Og så queryer jeg den træet, og så opbaterer jeg sig det her visible rays.

**Speaker 1**
Og så inde i drawables har jeg så tilføjet til den der new relation render,

**Speaker 1**
har jeg så tilføjet, i stedet for at give den map data.relations, så har jeg givet den der visible relations.

**Speaker 1**
Og så inde i Draw, så er det sådan ved hvert kald, så tjekker den så viewporten igen.

**Speaker 1**
Laver du sådan en viewport-boks,

**Speaker 1**
som den så bruger til at query træet med,

**Speaker 1**
både relation tree og way tree,

**Speaker 1**
og opdaterer så visible relations og visible.

**Speaker 1**
Nej, sorry, den opdaterer så relation renderer og way renderer med de her nye visible relations og way relations.

**Speaker 1**
Så det fungerer.

**Speaker 1**
Men der er et problematik i,

**Speaker 1**
at lige nu så træet er spillet op.

**Speaker 1**
Vi har træ til relation,

**Speaker 1**
træ til way, og det er en måde, det fungerer på lige nu.

**Speaker 1**
Og det er fint nok, at det fungerer lige nu.

**Speaker 1**
Men så gik jeg i gang med at implementere den der nearest neighbor, som vi skal bruge til Dijkstra.

**Speaker 1**
Og det kunne ikke lade sig gøre i et setup.

**Speaker 1**
Så det, som vi bliver nødt til at gøre, er at refakturere træet og potentielt set parserene og den der app der på en eller anden måde,

**Speaker 1**
og mapdater på en eller anden måde, så vi har en klasse,

**Speaker 1**
der kunne for eksempel hedde træet,

**Speaker 1**
eller den kunne også hedde mapdater,

**Speaker 1**
hvor i selve træet er.

**Speaker 1**
Og så må vi gøre sådan, at træet så både er opbygget af relations og nodes og ways.

**Speaker 1**
Sådan at når man så ligesom courier den,

**Speaker 1**
så får du så alt det der er inden for boksen der.

**Speaker 1**
Men den der så har brug for så på en eller anden måde, og det er så det, hvor man lige skal lægge noget tankarbejde i.

**Speaker 1**
Der vil bruge for at stadig spille det op i relation og ways.

**Speaker 1**
Sådan at jeres relation renderer og way renderer kan fungere.

**Speaker 1**
Det er sådan lidt abstrakt.

**Speaker 1**
Men det er bare fordi lige nu så tænker det tre forskellige systemer, der skal snakke sammen.

**Speaker 1**
Og jeg har fået dem til at snakke sammen med sådan en brute force,

**Speaker 1**
men de fungerer ikke ordentligt endnu.

**Speaker 1**
Så derfor bliver man nødt til bare lige at refaktorere tingene på en små måde,

**Speaker 1**
der gør sådan, at man kan query det samme sted fra.

**Speaker 1**
I stedet for at have tre forskellige ting, der skal snakke sammen.

**Speaker 1**
Så det er det næste, jeg vil gå i gang med at arbejde på, i hvert fald.

**Speaker 1**
Altså sådan en form for superklasse,

**Speaker 1**
eller hvad der er?

**Speaker 1**
Ja,

**Speaker 1**
nej, faktisk ikke helt. Det er bare fordi lige nu så har vi sådan, træet gør én ting.

**Speaker 1**
Træet er bare den, der søger i, hvilke ways og relations der er.

**Speaker 1**
Og så kan den returinere, hvilke ways der er synligt på kortet.

**Speaker 1**
Men lige nu kan træet kun...

**Speaker 1**
Altså den returinerer bare det hele.

**Speaker 1**
Så måden jeg bliver nødt til at gøre det på, at jeg laver der relation tree og way tree,

**Speaker 1**
for at det kan fungere.

**Speaker 1**
Men for eksempel er der ikke noget der i.

**Speaker 1**
Så hvis man skal lave nearest neighbor,

**Speaker 1**
så vil jeg ikke kunne query træet lige nu.

**Speaker 1**
Det er en måde, som det fungerer, fordi så vil jeg kunne lave nearest neighbor på en relation,

**Speaker 1**
eller på en way,

**Speaker 1**
men jeg vil ikke kunne lave nearest neighbor på selve noten.

**Speaker 1**
Det vil sige, der vil jeg godt kunne brute force på en eller anden måde,

**Speaker 1**
men det vil være superhårdt.

**Speaker 1**
Så i stedet for, at der bare skal være,

**Speaker 1**
hvor træet skal udvise,

**Speaker 1**
til så både at holde relation, ways og notes,

**Speaker 1**
og så kan man lave en search query, så kan den retunere resultater,

**Speaker 1**
på en måde, som gør at parser og appen,

**Speaker 1**
altså draw script,

**Speaker 1**
der ikke kan bruge det.

**Speaker 1**
Fordi at,

**Speaker 1**
der er også noget andet i, at hver eneste gang jeg så query af træet,

**Speaker 1**
relation tree og way tree, så sorterer jeg det fx hver gang.

**Speaker 1**
Men det gør også noget rod.

**Speaker 1**
Okay.

**Speaker 1**
Ja.

**Speaker 1**
Fedt.

**Speaker 1**
Så er det det jeg arbejder på.

**Speaker 1**
Fedt nok.

**Speaker 1**
Nice.

**Speaker 1**
Fordi når vi sidder nearest neighbor, så kan vi lave dykstra.

**Speaker 1**
Ja,

**Speaker 1**
cool.

**Speaker 3**
Okay,

**Speaker 3**
så vi skal prude det, du har lavet,

**Speaker 3**
indtil du gør det ind i vores nuværende.

**Speaker 1**
Det vil jeg ikke gøre til at starte med.

**Speaker 1**
Det, jeg vil gøre til at starte med,

**Speaker 1**
er, først så skal I kigge på, var en af jer, tror jeg.

**Speaker 1**
Jeg tror, at den anden kan arbejde videre.

**Speaker 1**
Men en af jer skal gå ind på GitHub,

**Speaker 1**
og så se den pull request,

**Speaker 1**
jeg har lavet.

**Speaker 1**
Og så lige læse igennem den.

**Speaker 1**
Jeg har også pushet vores meeting transcripts,

**Speaker 1**
så de ligesom er med.

**Speaker 1**
Hvad hedder det? Det er blevet noget, jeg gør.

**Speaker 1**
Men så er det kun koden, I skal kigge på.

**Speaker 1**
Men så bare kigge igennem,

**Speaker 1**
hvordan det fungerer der.

**Speaker 1**
Fordi det er også vigtigt, at I ligesom også forstår R3.

**Speaker 1**
Og når I så gør det,

**Speaker 1**
når I så approver den pull request,

**Speaker 1**
så kommer den over i main,

**Speaker 1**
som vi så opdaterer mainbranchen.

**Speaker 1**
Og så derfra, så skal der så laves en,

**Speaker 1**
så skal I tage den branch, som I sidder på nu.

**Speaker 1**
Og den skal I så lave en ny branch af.

**Speaker 1**
Hvor I så skal merge mainbranchen ind i.

**Speaker 2**
Det tror jeg lige, du skal hjælpe os med.

**Speaker 1**
Ja, det skal jeg nok hjælpe mig.

**Speaker 1**
Det lyder, ja.

**Speaker 1**
Det er meget simpelt.

**Speaker 1**
Det er bare sådan, i stedet for,

**Speaker 1**
for man må aldrig nogensinde bare merge ind i mainbranchen.

**Speaker 3**
Okay.

**Speaker 1**
Man skal altid lave pull requests ind i mainbranchen.

**Speaker 1**
Så i en måde vi ikke kan gøre det,

**Speaker 1**
så bliver vi nødt til sådan,

**Speaker 1**
for ikke at udlægge den,

**Speaker 1**
nu hvis Simon sidder arbejder videre,

**Speaker 1**
lige nu,

**Speaker 1**
og Danny, du så gør det her,

**Speaker 1**
så er det sådan,

**Speaker 1**
for Simon stadig kan arbejde videre,

**Speaker 1**
så bliver du nødt til at kopiere Simons branch.

**Speaker 1**
Og så derefter så pulle det nye content

**Speaker 1**
from main branch and always CIMAN branch.

**Speaker 1**
It always in the main branch when you copier CIMAN.

**Speaker 1**
Okay.

**Speaker 1**
Can I just give it a minute?

**Speaker 1**
Yeah.

**Speaker 5**
Fit.

**Speaker 5**
Gang gang.

**Speaker 5**
Gang gang.

**Speaker 5**
Gangster.

**Speaker 5**
Yes.

**Speaker 5**
Nice.

**Speaker 5**
Cool.

