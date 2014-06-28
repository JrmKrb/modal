modal
=====

Modal Réseau INF441

Ne pas oublier
export _JAVA_OPTIONS="-Djava.net.preferIPv4Stack=true"
dans .profile...

Compilation et exécution de Test.java à partir de la console :
$cd racine_du_projet
$find . -name '*.class' -exec rm '{}' \;
$javac -d bin/ src/**/*.java
$javac -d bin/ src/*.java
$cd bin
$java bin.Test
