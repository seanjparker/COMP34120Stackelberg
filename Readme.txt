******************
*   DESCRITION   *
******************

In this project we have implemented a solution for the 2 player Stackelberg
game with imperfect information, playing as the leader. The approach we took is
finding the best strategy to predict the follower’s reaction function and
finding our price that would maximise our reward. We find our best approximation
of the follower’s reaction function. This is done by performing linear regression
from a moving window of 60 days worth of history. A window approach was chosen
as from our experiments it proved to produce the highest overall profit. Using
the approximation of the follower’s reaction function we can determine our best
response to this price. Using the provided demand model, we calculate the first
derivative of the function and find the value which provides the optimal price
for the leader in order to maximize our profit.

**************************
*   Running the leader   *
**************************

To run the simulation with the SimpleLeader, you need to

i) run

/usr/java/latest/bin/rmiregistry &

to enable RMI registration;

ii) run

java -classpath poi-3.7-20101029.jar: -Djava.rmi.server.hostname=127.0.0.1 comp34120.ex2.Main &

to run the GUI of the platform;

iii) run

java -Djava.rmi.server.hostname=127.0.0.1 SimpleLeader &

to run the SimpleLeader.

And after these steps, you can play with the GUI to get some ideas of how
the platform works.


*********************************
* 4.Compiling the leader class *
*********************************

No fancy parameters for the compiling, just

javac YourLeader.java
