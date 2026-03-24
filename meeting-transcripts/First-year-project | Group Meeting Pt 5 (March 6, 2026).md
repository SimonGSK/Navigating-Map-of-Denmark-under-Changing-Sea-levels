**Speaker 1**: Fedt, men lad mig lige sige det færdigt.

**Speaker 1**: Jo, så det man så kommer til at gøre, når vi så skal arbejde med de her to ting.

**Speaker 1**: I kan load in osm filen, dem der laver rendering, dem der laver algoritmer her,

**Speaker 1**: de skal så definere det af selv.

**Speaker 1**: Så tager jeg optegner i det her eksempel her, så kan enhver idiot se,

**Speaker 1**: at det er nemmest at gå fra, hvis du skal fra A til F, så er det nemmest at gå fra A til C til D, og så til F.

**Speaker 1**: Fordi det er det, der koster mindst tal.

**Speaker 1**: Og så kan man lave det eksempel her.

**Speaker 1**: Så har vi aftalt i fællesskab, hvordan det her skal repræsenteres.

**Speaker 1**: Så kan det være, at det måske er repræsenteret af en hashmap.

**Speaker 1**: Der baser bare sig sådan her ud.

**Speaker 1**: A, B, C. Det har vi gjort.

**Speaker 1**: Og så inden i hverd, så står der så, hvad de er connectet til.

**Speaker 1**: Så A er connectet til B og til C.

**Speaker 1**: Men vi bliver så nødt til at have en måde, så at sørge for at...

**Speaker 1**: Det kan være måske i stedet for at det er an area node, så er det måske en area node, vi kalder edges.

**Speaker 1**: Og så ser vi, at en edge er defineret af start node, slut node og afstanden mellem to.

**Speaker 1**: Så vil det her så være en Edge 1 og en Edge 2.

**Speaker 1**: Så man kan manuelt indlæse den her data.

**Speaker 1**: Kan I det menige, hvad jeg siger?

**Speaker 1**: Kan I det ikke bare finde lagebordene til det, som vi gjorde i den der Caddy-sopgave?

**Speaker 1**: Jo, præcis. Det er bare pointen her, at når vi indlæser på en hånd,

**Speaker 1**: Så kommer der til at være en masse notes, og så er det så det her datalag, der er ansvarlig for at tage de her notes,

**Speaker 1**: og så oversætte dem til den datastruktur, vi aftaler.

**Speaker 1**: Og den, der arbejder på algoritmer, hvis det var sådan, at den, der arbejdede på algoritmer, skulle vende på, at det her datalag fungerede,

**Speaker 1**: så ville det tage fucking lang tid, så kan den person ikke gå i gang.

**Speaker 1**: Så den person bliver nødt til manuelt at opstille de her eksempler, og manuelt at bare lave data ind i Java.

**Speaker 1**: For så udvikle de test, tænker jeg, til at starte mig.

**Speaker 1**: Og så selvfølgelig også lige læse om Dijkstra.

**Speaker 1**: Jo, altså den person skal også lige lave en benchmarking.

**Speaker 1**: Og der skal vi, du ved, der skal den person lige finde ud af,

**Speaker 1**: skal benchmarking, øhm,

**Speaker 1**: hvordan skal vi kunne se, hvilken benchmark behøres til hvilket tidspunkt?

**Speaker 1**: Hvordan kan vi tracke det?

**Speaker 1**: Der skal måske lige være en benchmarking-algoritme, der opretter en tekstfil inde i den her benchmarks folder.

**Speaker 1**: Og i den testfil står der så "bar benchmarks".

**Speaker 1**: Og så er det så sådan, at den så ligesom bare altid tilføjer en linje slutning af dokumentet, hvor der står den nyeste tid.

**Speaker 1**: Men der skal måske også lige stå en eller anden indikation for...

**Speaker 1**: Det ved jeg ikke.

**Speaker 1**: - Og så en ændring.

**Speaker 1**: - Ja.

**Speaker 1**: - Specifikke ændringssætter, der skriver ned i det.

**Speaker 1**: - Så det vil også være en lille ting, vi skulle gøre, så jeg starte med.

**Speaker 1**: Og så algoritme her, eller datadaget, det sidder med at indlæse OSM-filerne.

**Speaker 1**: Og så opbygge denne her parser her, der både kan tage højde for, hvis der er height curves og ikke height curves.

**Speaker 1**: Vi skal udvikle den her spatial structure, vi har lavet, og så til at den kan supporte de funktioner, vi har snakket om.

**Speaker 1**: Det er fx, at Dynamic Rendering kommer til at få brug for en eller anden måde at kunne se, hvor hende lande med at placere sig på kortet.

**Speaker 1**: Hvis man ikke kan se det, så vil Dynamic Rendering være lort, fordi så skal den gå igennem alle relationer, der findes på bundenholm,

**Speaker 1**: Og så kan man se, at det her er i viewporten, nej, det her er i viewporten, nej.

**Speaker 1**: Så bliver vi nødt til, og det ved jeg ikke, hvordan vi gør.

**Speaker 1**: Men det er så den her spatial structure, der skal finde ud af det.

**Speaker 1**: Okay.

**Speaker 1**: Ja.

**Speaker 2**: Jeg ved ikke, om vi også skal prøve at skrive et diagram op for alle de her punkter,

**Speaker 2**: hvor vi kan pege hen på det, der er afhængig af hinanden.

**Speaker 2**: Det tror jeg også er en god idé.

**Speaker 2**: Hvad hedder det?

**Speaker 2**: Bare sådan, at man kan se, okay, nu står jeg på det her punkt.

**Speaker 2**: Og så kan man kigge sig på, at man selv skal tænke over, hvad det gør til dig.

**Speaker 2**: Ja, helt sikkert.

**Speaker 2**: Hvorfor har man det visuelt, det gør det lidt nemmere, tror jeg.

**Speaker 1**: Det tror jeg også.

**Speaker 1**: God idé. Jeg har lige det der møde der.

**Speaker 1**: Det tager nok ikke så lang tid.

**Speaker 1**: Og så kommer jeg tilbage, og så tænker jeg i mellemtiden måske.

**Speaker 1**: Det kunne I eventuelt gøre.

**Speaker 1**: Det kan også være, at I måske skal finde ud af, om I stadig synes,

**Speaker 1**: - Og så er det samme

