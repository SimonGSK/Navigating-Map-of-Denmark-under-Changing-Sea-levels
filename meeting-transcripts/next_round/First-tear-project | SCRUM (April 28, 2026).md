**Speaker 1**
Ja.

**Speaker 2**
Øhm, som er en color.

**Speaker 2**
Og så den der get color omløber vi bare til fine color, og så har vi en anden get color,

**Speaker 2**
der hvis color er en love,

**Speaker 2**
så kalder den der fine color.

**Speaker 2**
Så den ligesom kun laver alle de her comparisons én gang.

**Speaker 1**
Den tror jeg er en god ide,

**Speaker 1**
ja.

**Speaker 2**
Sådan du initialiser, det synes jeg er en rigtig god ide.

**Speaker 2**
Så de bare gemmer farven.

**Speaker 1**
Ja, syg god ide.

**Speaker 2**
Det synes jeg er rigtig godt.

**Speaker 1**
Ja, nu er det den i videre, den der LOD?

**Speaker 1**
Nej.

**Speaker 1**
Nej.

**Speaker 2**
Jeg fikset det der relations.

**Speaker 2**
Jeg så på den der store multi-polygarn, den der skov,

**Speaker 2**
der var der en masse huller i.

**Speaker 2**
Men det så åbenbart fordi,

**Speaker 2**
jeg fandt der ud af,

**Speaker 2**
efter at jeg har sport,

**Speaker 2**
klover dig og tjæt, men der ikke kunne løse det,

**Speaker 2**
gik jeg selv ind og kiggede.

**Speaker 2**
Det var åbenbart fordi, der var sådan en hiking route,

**Speaker 2**
der var closed path,

**Speaker 2**
inden i den der multi-polygarn.

**Speaker 2**
Så den troede, det var huller.

**Speaker 1**
Åh, på det måde.

**Speaker 2**
Så nu har jeg bare lavet sådan en tjek,

**Speaker 2**
Så tjek om den har en type der hedder Boundary,

**Speaker 2**
inden den stedjer de der relation sammen.

**Speaker 2**
Jeg ved ikke, hvorfor vi ikke havde det problem tidligere.

**Speaker 2**
Jeg tror, det er det måde, som jeg refactorer lidt her.

**Speaker 2**
Ja, det gør jeg.

**Speaker 1**
Okay, good.

**Speaker 1**
Cool boys.

**Speaker 1**
Nice.

**Speaker 1**
You're scrum?

**Speaker 1**
Yeah.

**Speaker 1**
Scrum and done.

**Speaker 1**
Fedt.

**Speaker 1**
Okay,

**Speaker 1**
så Simon, du har repareret relations.

**Speaker 1**
Fordi efter vi så har lavet en refaktorering af scriptet,

**Speaker 1**
der var der den måde, som vi tegnede nogle større relations,

**Speaker 1**
som der ikke er så pæne ud.

**Speaker 1**
Og det er åbenbart,

**Speaker 1**
fordi der var nogle ways der i,

**Speaker 1**
som der var nogle closed paths,

**Speaker 1**
som gjorde den ligesom skahuller i de her store relations her.

**Speaker 1**
Så det har Simon fik sig.

**Speaker 1**
Og så snakkede vi også om,

**Speaker 1**
sidste uge, at vi vil begynde at lave

**Speaker 1**
level of detail nu.

**Speaker 1**
Fordi vi nu har fået

**Speaker 1**
refaktoreret scriptet, således at vi bruger

**Speaker 1**
vores R3

**Speaker 1**
og vores tree data

**Speaker 1**
til at holde al den data,

**Speaker 1**
som vi

**Speaker 1**
skal bruge, så det er derigennem. Det er ligesom

**Speaker 1**
kommer til at være interfacet for alt

**Speaker 1**
den data, vi skal bruge til at render.

**Speaker 1**
Og deri kan vi lave level of detail

**Speaker 1**
ved at give hvert

**Speaker 1**
element et zoom level.

**Speaker 1**
Således at vi så kun

**Speaker 1**
så hvert element har en

**Speaker 1**
hvad hedder det er en metode på sig

**Speaker 1**
der hedder get zoom level

**Speaker 1**
og så kan vi så kigge på

**Speaker 1**
når vi får alt

**Speaker 1**
de ting vi skal rendere fra R-træet

**Speaker 1**
så kan vi gå igennem hvert element

**Speaker 1**
og så kun rendere dem

**Speaker 1**
hvis det er sådan at deres zoom level

**Speaker 1**
er højere end det zoom level

**Speaker 1**
der er på skærmen

**Speaker 1**
fedt

**Speaker 1**
og så hvad hedder det

**Speaker 1**
så har vi fået

**Speaker 1**
Murched Dijkstra ind i træet

**Speaker 1**
eller ind i det samme

**Speaker 1**
Og den ser ud til at virke nu.

**Speaker 1**
Og det vi så skal til at gøre nu, det er så at lave en funktion i vores UI,

**Speaker 1**
der gør man så kan klikke på et punkt A, og så på et punkt B,

**Speaker 1**
og så bruge Dijkso til at udregne ruten mellem de to.

**Speaker 1**
Og det er jeg i gang med at arbejde på nu.

**Speaker 1**
Philip, hvad var det du gik i gang med at arbejde på sidst?

**Speaker 3**
Jeg lavede det der med nearest neighbor.

**Speaker 3**
At klikke,

**Speaker 3**
når man klikker i en boks,

**Speaker 3**
så finder den den node, der er tættest på, inde i den boks.

**Speaker 3**
Og så skal jeg tage radiusen,

**Speaker 3**
hvor jeg udregner afstanden fra musen ud til punktet.

**Speaker 3**
Og så tegner jeg en cirkel rundt om,

**Speaker 3**
og tegner en boks rundt om den.

**Speaker 3**
Og så ser jeg,

**Speaker 3**
om der er noget derinde, der er tættere på.

**Speaker 1**
100.

**Speaker 1**
Okay, helt sikkert.

**Speaker 3**
Det er bare udregningen, der har været 100.

**Speaker 1**
Helt sikkert.

**Speaker 1**
Fedt. Og det var bare for at klargøre, at vi har implementeret en meget simpel, eller sådan en naiv version af nearest neighbor,

**Speaker 1**
hvor den finder den,

**Speaker 1**
kan man sige, R3 query,

**Speaker 1**
der ligesom, hvor som der er tættest på musen, og derfra så tager den element ind i det R3 query.

**Speaker 1**
Og det som Philips skal arbejde på nu, det er at han skal bruge noget geometri til så at lave en radius rundt omkring den tætteste neighbor,

**Speaker 1**
Og så kan vi på en eller anden måde query flere R3-bokses samtidig.

**Speaker 1**
Det vil sige,

**Speaker 1**
at funktionen ikke længere er naiv,

**Speaker 1**
men nu begynder rent faktisk at finde

**Speaker 1**
the true nearest neighbor.

**Speaker 3**
Men nu er der bare et spørgsmål.

**Speaker 3**
Kan vi lige nu,

**Speaker 3**
som det er uden de her udregninger, kan vi godt finde to punkter?

**Speaker 1**
A og B, ja det kan godt.

**Speaker 3**
Kan den tegne?

**Speaker 1**
Nej, det er det, jeg gerne vil lave nu.

**Speaker 3**
Okay, det er jo perfekt.

**Speaker 1**
Fedt.

**Speaker 1**
Daniel, hvad var det du ville gerne arbejde på sidst?

**Speaker 1**
Du fik lavet noget testing til rendering og parsing, som blev færdigt, ikke?

**Speaker 4**
Ikke rendering.

**Speaker 4**
Jeg har alle test til parsing,

**Speaker 4**
der består.

**Speaker 4**
Og så også high curves.

**Speaker 4**
Rendering er ikke lavet, men går også i gang med det der binary conversion.

**Speaker 1**
Cool,

**Speaker 1**
sejt.

**Speaker 1**
Nå ja,

**Speaker 1**
fedt. Og det er fordi,

**Speaker 1**
at vi skal sørge for,

**Speaker 1**
at Bornholm's OSM-filer og height curves,

**Speaker 1**
at de ligger som binare filer inde i programmet.

**Speaker 1**
Så det er det,

**Speaker 1**
som Danny går i gang med.

**Speaker 1**
Og så skal vi også tænke på,

**Speaker 1**
fordi vi har fået nogle benchmarks nu for Backstra.

**Speaker 1**
Vi har ikke fået nogle benchmarks for andre endnu, vel?

**Speaker 2**
Jeg har ikke lært nu.

**Speaker 3**
Jeg har i hvert fald fået importere den der benchmarking class og få det til at virke med mark 7 og mark 8.

**Speaker 1**
Fedt, man.

**Speaker 3**
Men jeg tænker bare, at vi bruger mark 7,

**Speaker 3**
sådan lige umiddelbart.

**Speaker 1**
Ja,

**Speaker 1**
helt sikkert.

**Speaker 3**
Så det er bare at lave en testklasse-agtigt, eller en benchmark.

**Speaker 3**
Jeg tror, det er bedst at gøre det inde i den der benchmarking-klasse,

**Speaker 3**
og så lave nye derinde.

**Speaker 3**
100%

**Speaker 1**
Helt sikkert, ja. Cool.

**Speaker 3**
Men det kan godt hjælpe.

**Speaker 1**
Og så var noget andet, jeg kom til at tænke på.

**Speaker 1**
Lige nu er det i vores træ, at hver træ,

**Speaker 1**
hver trinode,

**Speaker 1**
kan holde mellem 1 og 30 elementer.

**Speaker 1**
Og det er noget, vi har valgt, fordi det er det, som vores TA sagde oprindeligt,

**Speaker 1**
at 30 elementer plejer at være sådan en sweet spot.

**Speaker 1**
Men vi skal så også lave benchmarking på det på et tidspunkt.

**Speaker 3**
For at finde vores eget sweet spot.

**Speaker 1**
Præcis, ja. For at finde det optimum der.

**Speaker 1**
Og så var der også noget andet, vi har brugt. Hvad var det, det hedder det der program der, som man viste os?

**Speaker 1**
hvor man kunne analysere, hvad der skete.

**Speaker 2**
Visual VM.

**Speaker 1**
Ja, der er sådan et program, der hedder Visual VM,

**Speaker 1**
hvor vi så testede vores kode, og så kunne vi se,

**Speaker 1**
at der er nogle,

**Speaker 1**
hvad hedder det,

**Speaker 1**
de klasser og records, vi bruger.

**Speaker 1**
For eksempel koordinater,

**Speaker 1**
som der tager meget memory.

**Speaker 1**
Øhm, ja.

**Speaker 1**
Så det fandt vi ud af.

**Speaker 1**
Vi skal nok kigge mere på Visual VM,

**Speaker 1**
for at se, om vi kan lave mere performance optimization.

**Speaker 1**
Men lige for nu, så virker vi relativt tæt på,

**Speaker 1**
at være færdig med de formelle krav til opgaven.

**Speaker 1**
Og lige så snart vi er færdige med det

**Speaker 1**
De formeltede krav

**Speaker 1**
Så begynder vi så at

**Speaker 1**
Benchmarke

**Speaker 1**
Og vi skal også lade test

**Speaker 1**
Nogle flere testede de her ting

**Speaker 1**
Men så begynder vi så at benchmark

**Speaker 1**
Og derfra begynder vi så

**Speaker 1**
Performance up to myse

**Speaker 1**
Ud for det

**Speaker 1**
Cool

**Speaker 1**
Fedt

**Speaker 1**
Nice

