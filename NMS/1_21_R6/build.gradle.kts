group = "me.casperge.realisticseasons1_21_R6"

repositories {
  mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.21.10-R0.1-SNAPSHOT")
    implementation(project(":common"))
}