export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m -noverify"
mvn clean -X  assembly:assembly -DskipTests -Dmaven.test.skip=true   -Pdistributed,hadoop-2
##mvn clean assembly:assembly -DskipTests -Dmaven.test.skip=true   -Phadoop-2
