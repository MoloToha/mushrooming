
## Mapy terenu do wyświetlania w aplikacji 

### GoogleMaps

od Google są mapy 'terenowe', ale prawie nic na nich nie ma oprócz poziomic
i cieniowania nachylenia terenu i czasem zielonego jak jest zupełny las

można by też próbować używać widoku z satelity albo hybrydowego,
ale on pewnie musi wtedy pobierać całe obrazy a nie pliki opisujące
i to prawdopodobnie używa bardzo dużo danych i baterii

### Inne mapy - thunderforest

na tych mapach jest dużo więcej informacji przydatnych przy chodzeniu w terenie:
https://www.thunderforest.com/maps/outdoors/

#### Integracja z aplikacją androidową

Problemem tych map jest to, że nie są natywne Google'a
więc trzeba sobie z tym radzić pisząc klasy dziedziczące po odpowiednich klasach
ogarniające jak składać mapy z branych stamtąd kawałków (tiles)
(używając GoogleMaps Android API, ale trzeba się upewnić co do licencji)
https://developers.google.com/maps/documentation/android-api/map

Znalazłem nieco informacji o tym jak to robić:
https://gis.stackexchange.com/questions/104325/how-to-use-custom-map-tiles-with-the-google-map-api-v2-for-android

https://stackoverflow.com/questions/14784841/tileprovider-using-local-tiles/14833256#14833256

https://developers.google.com/android/reference/com/google/android/gms/maps/model/TileProvider

https://developers.google.com/android/reference/com/google/android/gms/maps/model/TileOverlay

thunderforest API pozawala brać kawałki map (tiles), jak mu się da współrzędne itp.
https://www.thunderforest.com/docs/map-tiles-api/
https://www.thunderforest.com/docs/tile-numbering/

trzeba założyć sobie konto i jakoś dać do projektu klucz,
na razie nie wiem jak to zrobić nie ujawniając go 
darmowe jest bodajże do 150000 pobrań kawałków mapy
https://www.thunderforest.com/terms

jest też https://www.thunderforest.com/blog/static-maps-api/,
ale to raczej jak by chcieć mieć mapę dla ograniczonej przestrzeni,
a nie dynamicznie sklejaną

branie kawałków mapy trzeba robić przez jakieś zapytanie przez sieć,
(patrz map-tiles-api),
to też trzeba jakoś zrobić w naszej aplikacji

