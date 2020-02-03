Check Lese inn data fra fil
Check Generere objekter

Check Designe kromosom
-Sett av depoter
-Hvert depot har et ordnet sett av unike kunde-id

Check Tildele depoter kunder
Venter - lage kandidatListe
Seede populasjon

Check Lage RouteScheduler

Check Generere svarfil

Check Visualisere ruter
-XY-plot seems to be good


Check Evaluere ruter

Check Selection
Check crossover

inter-mutation
intra-mutation

Fikse bugs


##
Fixed destination problem
we use a simple and fast procedure that visits one customer at a time, as-
signing each customer to its nearest depot, and then we apply a GA strategy to each
cluster.

We design a dynamic inter-depot mutation operator which may re-assign bor-
derline customers during the evolution process to the final depot placement.

Euclidian distance

Eventuelle problemer:
- Har ikke tatt høyde for antall kjøretøy i routescheduleren
- Check Har ikke lagt til costen å dra tilbake til depotet i distance-akkumulatoren
- Gi farger til rutene
- det er noe hardkoda for duration elns i routescheduler tror jeg
