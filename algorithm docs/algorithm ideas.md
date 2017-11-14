

## Terrain accessibility map 

in my opinion it is essential to build terrain-accessibility map based on users position seen by each device
every instance would build own (~identical) map
then, every user can see how to round the bushes etc

terrain availability map would be 2-dimensional grid made of squares

## mapping GPS position on map

##### note
bluetooth has limited range, so we can make map eg. for 1000x1000 metres, each sqare is 1x1 meter
when users drift too far, we can order an assembly

##### mapping
for example like here https://www.movable-type.co.uk/scripts/latlong.html
*TODO* check if there is some way to minimize error caused by terrain shape

## Assembly place choice

we should probably choose assembly place from places marked as available

we can run Dijkstra from each user's position,
and store distance to each map square ; terrain not marked as available can be eg. twice expensive
then we minimize sum of (squared? - to minimize distance for the farthest user) distances to each user's position

graph is not dense (near squares), so m*log(m) Dijksta 
(where m is edge count, ~4 times vertex count on our map) is problably ok,
maybe later optimize for lower power consumption


## Map centering

map should be quite big, usually centered on user, but we sould not shift it by each field passed by user -
we can shift eg. when user goes out of somehow defined 'map center' - eg. inner 1/3 on each dimension, 1/9 of area


## When to order assembly

### when  graph becomes inconsistent

some function from core module gives information about devices which we can connect to

### when graph is still consistent but stretched too far

to determine if we should order an assembly when the graph is still consistent, 
we can calculate "connectivity count" - each device should pass information about 
the number of devices it sees directly, and "connectivity count" would be 
the sum of such numbers for all devices

Eg. we can order an assembly when "connectivity count" is 
