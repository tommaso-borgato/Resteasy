Run bootable jar test like:

```bash
mvn -f testsuite/pom.xml \
    clean verify \
    -Dts.bootable=true \
    -Ddefault=false \
    -Ddisable.microprofile.tests \
    -Dserver.version=32.0.1.Final \
    -Dversion.org.wildfly.jar.plugin=11.0.2.Final \
    --batch-mode \
    --fail-at-end \
    -Denforcer.skip \
    -Dserver.home=/tmp/wildfly-32.0.1.Final \
    -Dversion.resteasy.testsuite=6.2.9.Final \
    -Dmaven.test.failure.ignore=true
```