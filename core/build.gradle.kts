group = "me.casperge.realisticseasons.core"

repositories {
    mavenCentral()
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://repo.viaversion.com/everything")
    maven("https://repo.helpch.at/releases")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.mikeprimm.com/")
    maven("https://repo.bluecolored.de/releases")
    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/main/")
}

val nms = project(":NMS")
    .subprojects
    .map { it.name }


dependencies {
    // NMS Implementations
    for (project in nms) {
        compileOnly(project(path = ":NMS:${project}"))
    }
    implementation(project(":common"))

    implementation("commons-io:commons-io:2.18.0")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    compileOnly("com.viaversion:viaversion-api:5.7.2-SNAPSHOT")
    // Source: https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core
    compileOnly("org.apache.logging.log4j:log4j-core:2.25.3")
    compileOnly("io.lumine:Mythic-Dist:5.9.2-SNAPSHOT")
    compileOnly("io.lumine:Mythic-API:5.9.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.12")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.0.12")
    compileOnly("us.dynmap:dynmap-api:2.3")
    compileOnly("de.bluecolored.bluemap:BlueMapAPI:2.7.2")
    compileOnly("com.github.angeschossen:LandsAPI:7.24.1")
    compileOnly("com.github.GriefPrevention:GriefPrevention:16.18.2")
    // Source: https://mvnrepository.com/artifact/com.massivecraft.factions/Factions
    //compileOnly("com.massivecraft.factions:Factions:3.3.0")
    compileOnly("org.geysermc.floodgate:api:2.2.4-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
}