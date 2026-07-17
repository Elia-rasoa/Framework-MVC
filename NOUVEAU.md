# HAMBOTRA FRAMEWORK PERSONNALISE | SPRING MVC 
mampiasa git : branche main -> manao branche anatiny de tsy tode mi merge vao vita fa manao "pull request" aloha de tsy asina code review fa afaka tode mi merge avy eo

### compilation
javac --release 17 -d bin -cp "lib/*:." src/main/java/servlet/*/*.java
javac --release 17 -d web/WEB-INF/classes/ src/etu/controller/Test1.java -cp "web/WEB-INF/lib/*:."
javac --release 17 -d web/WEB-INF/classes/ -cp "web/WEB-INF/lib/*:." $(find src -name "*.java")
### Creation jar et war
. cd bin
jar -cvf Sprint1.jar servlet/
-- fijerevana ny ao anatiny
jar tf web/WEB-INF/lib/Sprint1.jar

. cd web
jar -cvf AppTest.war WEB-INF/


## Sprint 1
### Fonctionnalités

FRONT-CONTROLLER-SERVLET : à creer (io no havoka .jar), izay application rehetra testé-na dia mitsatoka(manadalo) ao anatinio servlet io daholo
-doGet -> processRequest() -> outprint(get URL) : ilay URL no affiché eo am navigateur pour voir si ca marche
-doPost
Comment sur le coté de l'application de test ? -> web.xml : declare-na ao ilay controller atao classe principale miteny hoe izay rehetra dia mandalo ao anaty ilay controller daholo

- mitady script mi generer .jar rehefa vita 

 - creer le .tar 
tar -cvf mon-framework.tar mon-framework/

 - apres cette commande on execute : mvn clean install pour compiler le framework et le place dans le repertoire locale de maven 
 pour qu'il soit pret à etre utilisé 

 - compilation
  javac --release 17 -cp "lib/servlet-api.jar" -d . src/main/java/servlet/framework/FrontControllerServlet.java

  - creer le .jar
   jar -cf servlet.jar servlet

   - puis creation du .war dans le projet de test
   jar cvf AppTest.war .

### Sprint 2
but : Quelle methdode est appeller par un url

Creation : Annotation sur une methode avec une variable

@UrlMapping("/emp/list") "/emp/list" par defaut, c'est la methode qui est appeller quand on tape cette url

= emp/list => EmpCOntroller - methode listEmp()
= emp/add => EmpController - methode addEmp()

gesiont des erreurs si url non touver : on dis 404 et liste tout les url disponible dans le projet

### Sprint 3
tohiny sprint 2
Url ho an get
Url ho an post
mampiasa meth equals surdefini (mi test hoe mitovy sa tsia ilay url)
meth: map<URL/(Get/Post),Class/method>, cle: utilmethod -> contenir hoe ity url de ity ny metohd (post / get) , valeur : Classe na methode 

### Sprint 3 bis
miantso an'ilay fonction mifanaraka @ url
fonction mi affiche ny valeur de retour-


### Sprint 4
avadika atao anaty listener ilay map

### Sprint 5
manambotra model and view
web.xml (test): suffixe (WEB-inf/dossier)
                prefixe (.jsp)
controller (test) : afaka atao anaty variable ilay zavatra affiche-na
                     afaka ilay chemin misy ilay affichage no atao anaty model and view

### Sprint 5 bis
