NSGA-11
Simple GA, weighted-sum fitness function

DONE Velge colorspace:
- RGB
- CIE L*a*b

Representasjon:
- Graph
- Tree

Fitness:
Pareto-optimal
segmentation
- Edge value
    maksimeringsproblem
    belønne store forskjeller i euklidisk distanse mellom nabopiksler som ikke er i samme segment
- Connectivity
    Minimeringsproblem
    Straffe at pixler i nærheten av hverandre ikke er i samme segment
- Overall deviation
    Minimeringsproblem
    Et overordnet mål på hvor homogene alle segmentene er

Probabilistic Rand Index (PRI)

Arbeidsoppgaver:
- Les inn bilde
- Lag graf
- Lag minimale spenntrær

Spørsmål:
- Bra å kjøre med lav eller høy euclidisk distanse?
- Hva er pri-scoren, og hvordan setter man opp evaluatoren til det?




