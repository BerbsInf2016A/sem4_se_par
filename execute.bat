"C:\Program Files\Java\jdk1.8.0_112\bin\java" -server -Xms6G -Xmx6G -XX:MaxDirectMemorySize=1024M -XX:NewSize=1G -XX:MaxNewSize=1G -XX:+UseParNewGC -XX:MaxTenuringThreshold=2 -XX:SurvivorRatio=8 -XX:+UnlockDiagnosticVMOptions -XX:ParGCCardsPerStrideChunk=32768 -classpath out\production\sem4_se_par implementation.Main
pause