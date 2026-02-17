plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
}

group = "me.casperge.realisticseasons1_21_R7"

dependencies {
    paperweight.foliaDevBundle("1.21.11-R0.1-SNAPSHOT")
    implementation(project(":common"))
}
