import java.nio.file.Files
import java.nio.file.StandardCopyOption
import groovy.json.JsonSlurper
import org.gradle.internal.extensions.core.serviceOf
import java.net.URI
import javax.inject.Inject
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.nio.file.Paths

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.3.1"
}


group = "me.casperge.realisticseasons"

allprojects {
    apply(plugin = "java-library")

    version = "11.10.3"

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
        compileOnly("org.apache.commons:commons-lang3:3.17.0")
        implementation("io.papermc:paperlib:1.0.7")
    }
}

repositories {
    // TODO: Configure
    mavenCentral()
}

val nms = project(":NMS")
    .subprojects
    .map { it.name }

dependencies {
    implementation(project(":core"))
    for (project in nms) {
        implementation(project(path = ":NMS:${project}"))
    }

    implementation("commons-io:commons-io:2.18.0")
    implementation("org.bstats:bstats-bukkit:3.1.0")
}


tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set(rootProject.name)

        relocate("org.bstats", "me.casperge.realisticseasons.bstats")
        relocate("org.papermc.lib", "me.casperge.realisticseasons.paperlib")

        manifest {
            attributes(
                "Implementation-Title" to rootProject.name,
                "Implementation-Version" to project.version,
            )
        }
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

val decompileConfig = DecompileConfig(
    inputJar = "sources/RealisticSeasons.jar",
    packageMappings = mapOf(
        "me/casperge/realisticseasons/biome" to ":common",
        "me/casperge/enums" to ":common",
        "me/casperge/interfaces" to ":common",
        "world/ofunny/bpm/Floodgate" to ":core",
        "me/casperge/realisticseasons" to ":core",
        // Remove old NMS
        /*"me/casperge/realisticseasons1_16_R2" to ":NMS:1_16_R2",
        "me/casperge/realisticseasons1_16_R3" to ":NMS:1_16_R3",
        "me/casperge/realisticseasons1_17_R1" to ":NMS:1_17_R1",*/
        // Remove old NMS
        "me/casperge/realisticseasons1_18_R1" to ":NMS:1_18_R1",
        "me/casperge/realisticseasons1_18_R2" to ":NMS:1_18_R2",
        "me/casperge/realisticseasons1_19_R1" to ":NMS:1_19_R1",
        "me/casperge/realisticseasons1_19_R2" to ":NMS:1_19_R2",
        "me/casperge/realisticseasons1_19_R3" to ":NMS:1_19_R3",
        "me/casperge/realisticseasons1_20_R1" to ":NMS:1_20_R1",
        "me/casperge/realisticseasons1_20_R2" to ":NMS:1_20_R2",
        "me/casperge/realisticseasons1_20_R3" to ":NMS:1_20_R3",
        "me/casperge/realisticseasons1_20_R4" to ":NMS:1_20_R4",
        "me/casperge/realisticseasons1_21_R1" to ":NMS:1_21_R1",
        "me/casperge/realisticseasons1_21_R2" to ":NMS:1_21_R2",
        "me/casperge/realisticseasons1_21_R3" to ":NMS:1_21_R3",
        "me/casperge/realisticseasons1_21_R4" to ":NMS:1_21_R4",
        "me/casperge/realisticseasons1_21_R5" to ":NMS:1_21_R5",
        "me/casperge/realisticseasons1_21_R6" to ":NMS:1_21_R6",
        "me/casperge/realisticseasons1_21_R7" to ":NMS:1_21_R7",
    ),
    resourceMappings = mapOf(
        "BADLANDS.yml" to ":core",
        "BEACH.yml" to ":core",
        "BIRCH_FOREST.yml" to ":core",
        "calendar.yml" to ":core",
        "CAVES.yml" to ":core",
        "chunkdata.yml" to ":core",
        "config.yml" to ":core",
        "crops.yml" to ":core",
        "custom-events.yml" to ":core",
        "CUSTOM_EXAMPLE.yml" to ":core",
        "DARK_FOREST.yml" to ":core",
        "data.yml" to ":core",
        "DESERT.yml" to ":core",
        "events.yml" to ":core",
        "factions.yml" to ":core",
        "FLOWER_FOREST.yml" to ":core",
        "FOREST.yml" to ":core",
        "FROZEN_BIOMES.yml" to ":core",
        "FROZEN_MOUNTAINS.yml" to ":core",
        "griefprevention.yml" to ":core",
        "JUNGLE.yml" to ":core",
        "lands.yml" to ":core",
        "lang.yml" to ":core",
        "MOUNTAINS.yml" to ":core",
        "MUSHROOM_FIELDS.yml" to ":core",
        "OCEAN.yml" to ":core",
        "PALE_GARDEN.yml" to ":core",
        "PLAINS.yml" to ":core",
        "plugin.yml" to ":core",
        "RIVER.yml" to ":core",
        "SAVANNA.yml" to ":core",
        "SWAMP.yml" to ":core",
        "TAIGA.yml" to ":core",
        "temperature.yml" to ":core",
        "terralith2.0" to ":core",
    )
)




data class DecompileConfig(
    val inputJar: String = "sources/Decompile-Patcher-Template.jar",
    val decompilerDir: String = "build/decompiler",
    val generatedDir: String = "sources/generated",
    val patchesDir: String = "patches",
    val packageMappings: Map<String, String> = emptyMap(),
    val resourceMappings: Map<String, String> = emptyMap(),
)

val decompilerDir = layout.buildDirectory.dir(decompileConfig.decompilerDir.removePrefix("build/"))
val inputJarFile = layout.projectDirectory.file(decompileConfig.inputJar)
val generatedOutputDir = layout.projectDirectory.dir(decompileConfig.generatedDir)
val patchesDirPath = layout.projectDirectory.dir(decompileConfig.patchesDir)
val buildToolsDir = layout.buildDirectory.dir("buildtools")
val buildToolsJar = buildToolsDir.get().file("BuildTools.jar")

val nmsVersions = listOf(
    "1.18.1",
    "1.18.2",
    "1.19.1",
    "1.19.3",
    "1.19.4",
    "1.20.1",
    "1.20.2",
    "1.20.3",
    "1.20.4",
    "1.20.6",
    "1.21.1",
    "1.21.2",
    "1.21.4",
    "1.21.5",
    "1.21.6",
    "1.21.10",
    "1.21.11"
)

val downloadBuildTools by tasks.registering {
    val jar = buildToolsJar.asFile
    onlyIf { !jar.exists() }
    doLast {
        mkdir(buildToolsDir)
        val url = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
        ant.invokeMethod("get", mapOf("src" to url, "dest" to jar))
        println("Downloaded BuildTools.jar")
    }
}


val jdk17 = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(17))
}.get().executablePath.asFile.absolutePath

val jdk21 = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(21))
}.get().executablePath.asFile.absolutePath

val installTasks = nmsVersions.map { version ->
    val snapshotVersion = "$version-R0.1-SNAPSHOT"
    tasks.register("installSpigot_$version") {
        dependsOn(downloadBuildTools)

        val m2Artifact = Paths.get(
            System.getProperty("user.home"),
            ".m2", "repository", "org", "spigotmc", "spigot",
            snapshotVersion, "spigot-${snapshotVersion}.jar"
        )

        onlyIf { !Files.exists(m2Artifact) }

        val execOps = serviceOf<ExecOperations>()

        doLast {
            execOps.exec {
                workingDir(buildToolsDir)
                val javaExec = if (version < "1.20.5") jdk17 else jdk21
                commandLine(javaExec, "-jar", "BuildTools.jar", "--rev", version, "--remapped")
            }
            println("Spigot $snapshotVersion installed to local Maven repo")
        }
    }
}

val installAllSpigot by tasks.registering {
    dependsOn(installTasks)
}

fun resolveModuleDir(moduleTarget: String): File? {
    if (moduleTarget == ".") return project.projectDir
    return try {
        project(":$moduleTarget").projectDir
    } catch (e: Exception) {
        null
    }
}

fun resolveModuleSrcDir(moduleTarget: String, packagePath: String): File? {
    val moduleDir = resolveModuleDir(moduleTarget) ?: return null
    return moduleDir.resolve("src/main/java/$packagePath")
}

fun resolveModuleResourcesDir(moduleTarget: String): File? {
    val moduleDir = resolveModuleDir(moduleTarget) ?: return null
    return moduleDir.resolve("src/main/resources")
}

// ============================================================================
// GIT-BASED PATCH MANAGEMENT SYSTEM
// ============================================================================

// Helper class to encapsulate git operations with ExecOperations
abstract class GitOperationsService @Inject constructor(
    private val execOps: ExecOperations
) : BuildService<BuildServiceParameters.None> {
    fun init(workingDir: File) {
        execOps.exec {
            this.workingDir = workingDir
            commandLine("git", "init")
            standardOutput = ByteArrayOutputStream()
            errorOutput = ByteArrayOutputStream()
        }
    }

    fun config(workingDir: File, key: String, value: String) {
        execOps.exec {
            this.workingDir = workingDir
            commandLine("git", "config", key, value)
            standardOutput = ByteArrayOutputStream()
            errorOutput = ByteArrayOutputStream()
        }
    }

    fun add(workingDir: File, path: String = ".") {
        execOps.exec {
            this.workingDir = workingDir
            commandLine("git", "add", path)
            standardOutput = ByteArrayOutputStream()
            errorOutput = ByteArrayOutputStream()
        }
    }

    fun commit(workingDir: File, message: String): Int {
        val result = execOps.exec {
            this.workingDir = workingDir
            commandLine("git", "commit", "-m", message)
            standardOutput = ByteArrayOutputStream()
            errorOutput = ByteArrayOutputStream()
            isIgnoreExitValue = true
        }
        return result.exitValue
    }

    fun status(workingDir: File): String {
        val output = ByteArrayOutputStream()
        execOps.exec {
            this.workingDir = workingDir
            commandLine("git", "status", "--short")
            standardOutput = output
        }
        return output.toString()
    }

    fun diffStat(workingDir: File): String {
        val output = ByteArrayOutputStream()
        execOps.exec {
            this.workingDir = workingDir
            commandLine("git", "diff", "--cached", "--stat")
            standardOutput = output
        }
        return output.toString()
    }

    fun diffCached(workingDir: File, outputFile: File) {
        outputFile.outputStream().use { output ->
            execOps.exec {
                this.workingDir = workingDir
                commandLine("git", "diff", "--cached")
                standardOutput = output
            }
        }
    }

    fun apply(workingDir: File, patchFile: File, verbose: Boolean = false): Pair<Int, String> {
        val errorOutput = ByteArrayOutputStream()
        val stdOutput = ByteArrayOutputStream()
        val args = mutableListOf("git", "apply")
        if (verbose) args.add("--verbose")
        args.add(patchFile.absolutePath)

        val result = execOps.exec {
            this.workingDir = workingDir
            commandLine(args)
            standardOutput = stdOutput
            this.errorOutput = errorOutput
            isIgnoreExitValue = true
        }
        return Pair(result.exitValue, errorOutput.toString() + stdOutput.toString())
    }

    fun setupRepo(workingDir: File) {
        init(workingDir)
        config(workingDir, "user.name", "Patcher")
        config(workingDir, "user.email", "patch@local")
    }
}


val gitOpsService = gradle.sharedServices.registerIfAbsent("gitOps", GitOperationsService::class.java) {}

tasks.register("setupVineFlower") {
    doLast {
        decompilerDir.get().asFile.mkdirs()
        inputJarFile.asFile.parentFile.mkdirs()
        generatedOutputDir.asFile.mkdirs()

        if (!inputJarFile.asFile.exists()) {
            println("Input JAR not found at: ${decompileConfig.inputJar}. Nothing to decompile.")
            return@doLast
        }

        println("Fetching latest VineFlower release info...")

        val apiUrl = URI("https://api.github.com/repos/Vineflower/vineflower/releases/latest").toURL()
        val json = JsonSlurper().parse(apiUrl)

        @Suppress("UNCHECKED_CAST")
        val assets = (json as Map<String, Any>)["assets"] as List<Map<String, Any>>
        val jarAsset = assets.firstOrNull { it["name"].toString().endsWith(".jar") }
            ?: error("Could not find Vineflower jar in latest release")

        val downloadUrl = jarAsset["browser_download_url"].toString()
        val vineflowerJar = decompilerDir.get().file(jarAsset["name"].toString()).asFile

        if (!vineflowerJar.exists()) {
            println("Downloading Vineflower...")
            URI(downloadUrl).toURL().openStream().use { input ->
                Files.copy(input, vineflowerJar.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
        } else {
            println("Vineflower already downloaded.")
        }

        println("Vineflower jar: ${vineflowerJar.name}")
    }
}

val decompile by tasks.registering(Exec::class) {
    dependsOn("setupVineFlower")

    inputs.file(inputJarFile)
    outputs.dir(generatedOutputDir)

    doFirst {
        val vineflowerJar = decompilerDir.get().asFile.listFiles()
            ?.firstOrNull { it.name.endsWith(".jar") }
            ?: error("Vineflower jar not found. Run './gradlew setupVineFlower' first.")

        generatedOutputDir.asFile.mkdirs()

        commandLine(
            "java",
            "-jar",
            vineflowerJar.absolutePath,
            inputJarFile.asFile.absolutePath,
            generatedOutputDir.asFile.absolutePath
        )
    }
}

tasks.register("distributeSources") {
    dependsOn(decompile)

    doLast {
        val generatedDir = generatedOutputDir.asFile
        if (!generatedDir.exists()) {
            println("No generated sources found. Run 'decompile' task first.")
            return@doLast
        }

        println("Distributing decompiled sources to modules...")


        decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
            val sourcePackageDir = generatedDir.resolve(packagePath)

            if (!sourcePackageDir.exists()) {
                println("⚠ Package directory not found: $packagePath")
                return@forEach
            }

            val targetSrcDir = resolveModuleSrcDir(moduleTarget, packagePath)
            if (targetSrcDir == null) {
                println("❌ Module not found: $moduleTarget")
                return@forEach
            }

            println("📦 Copying $packagePath -> $moduleTarget")
            targetSrcDir.mkdirs()

            var fileCount = 0
            sourcePackageDir.walkTopDown().forEach { sourceFile ->
                if (sourceFile.isFile && sourceFile.extension == "java") {
                    val relativePath = sourceFile.relativeTo(sourcePackageDir)
                    val targetFile = targetSrcDir.resolve(relativePath)
                    targetFile.parentFile.mkdirs()
                    sourceFile.copyTo(targetFile, overwrite = true)
                    fileCount++
                }
            }
            println("✓ Copied $fileCount files to $moduleTarget")
        }


        if (decompileConfig.resourceMappings.isNotEmpty()) {
            val byModule = decompileConfig.resourceMappings.entries.groupBy({ it.value }, { it.key })

            byModule.forEach { (moduleTarget, resourceNames) ->
                println("\n📦 Copying resources to $moduleTarget...")

                val resourcesDir = resolveModuleResourcesDir(moduleTarget)
                if (resourcesDir == null) {
                    println("❌ Module not found: $moduleTarget")
                    return@forEach
                }
                resourcesDir.mkdirs()

                var resourceCount = 0
                resourceNames.forEach { resourceName ->
                    val sourceResource = generatedDir.resolve(resourceName)
                    if (sourceResource.exists()) {
                        val targetResource = resourcesDir.resolve(resourceName)
                        if (sourceResource.isDirectory) {
                            sourceResource.copyRecursively(targetResource, overwrite = true)
                            val count = sourceResource.walkTopDown().count { it.isFile }
                            println("  ✓ Copied directory: $resourceName ($count files)")
                            resourceCount += count
                        } else {
                            targetResource.parentFile.mkdirs()
                            sourceResource.copyTo(targetResource, overwrite = true)
                            println("  ✓ Copied file: $resourceName")
                            resourceCount++
                        }
                    } else {
                        println("  ⚠ Resource not found: $resourceName")
                    }
                }
                println("✓ Copied $resourceCount resource files to $moduleTarget")
            }
        }

        println("\n✓ Source distribution complete!")
    }
}

tasks.register("cleanDistributedSources") {
    doLast {
        println("Cleaning distributed sources from modules...")

        decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
            try {
                val targetSrcDir = resolveModuleSrcDir(moduleTarget, packagePath)
                if (targetSrcDir != null && targetSrcDir.exists()) {
                    targetSrcDir.deleteRecursively()
                    println("🗑 Cleaned $moduleTarget/src/main/java/$packagePath")
                }
            } catch (e: Exception) {
                println("⚠ Could not clean $moduleTarget - ${e.message}")
            }
        }

        val byModule = decompileConfig.resourceMappings.entries.groupBy({ it.value }, { it.key })
        byModule.forEach { (moduleTarget, resourceNames) ->
            try {
                val resourcesDir = resolveModuleResourcesDir(moduleTarget) ?: return@forEach
                resourceNames.forEach { resourceName ->
                    val targetResource = resourcesDir.resolve(resourceName)
                    if (targetResource.exists()) {
                        if (targetResource.isDirectory) {
                            targetResource.deleteRecursively()
                        } else {
                            targetResource.delete()
                        }
                        println("🗑 Cleaned $moduleTarget/src/main/resources/$resourceName")
                    }
                }
            } catch (e: Exception) {
                println("⚠ Could not clean $moduleTarget resources - ${e.message}")
            }
        }

        println("✓ Clean complete!")
    }
}

tasks.register("cleanGenerated") {
    doLast {
        if (generatedOutputDir.asFile.exists()) {
            generatedOutputDir.asFile.deleteRecursively()
            println("🗑 Cleaned ${decompileConfig.generatedDir}")
        }
    }
}

tasks.register("createPatch") {
    usesService(gitOpsService)

    doLast {
        val patchNameValue = project.findProperty("patchName") as String?
            ?: throw GradleException("Please specify patch name: -PpatchName=\"name\"")

        val gitOps = gitOpsService.get()
        val patchesDirFile = patchesDirPath.asFile
        patchesDirFile.mkdirs()

        val patchFile = patchesDirFile.resolve("$patchNameValue.patch")

        println("\n🔧 Creating patch: $patchNameValue.patch")
        println("=".repeat(60))

        val gitDir = project.projectDir.resolve(".patch-git")
        if (gitDir.exists()) gitDir.deleteRecursively()
        gitDir.mkdirs()

        try {
            gitOps.setupRepo(gitDir)

            println("📋 Setting up git workspace...")
            decompileConfig.packageMappings.forEach { (packagePath, _) ->
                val sourceDir = generatedOutputDir.asFile.resolve(packagePath)
                if (sourceDir.exists()) {
                    val targetDir = gitDir.resolve(packagePath)
                    sourceDir.copyRecursively(targetDir, overwrite = true)
                }
            }
            decompileConfig.resourceMappings.forEach { (resourceName, _) -> // resources into __resources__ subdir
                val sourceResource = generatedOutputDir.asFile.resolve(resourceName)
                if (sourceResource.exists()) {
                    val targetResource = gitDir.resolve("__resources__/$resourceName")
                    if (sourceResource.isDirectory) {
                        sourceResource.copyRecursively(targetResource, overwrite = true)
                    } else {
                        targetResource.parentFile.mkdirs()
                        sourceResource.copyTo(targetResource, overwrite = true)
                    }
                }
            }

            gitOps.add(gitDir)
            gitOps.commit(gitDir, "Original decompiled sources")

            println("📝 Comparing with modified sources...")
            decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
                val moduleSrcDir = resolveModuleSrcDir(moduleTarget, packagePath)
                val targetDir = gitDir.resolve(packagePath)

                if (targetDir.exists()) targetDir.deleteRecursively()

                if (moduleSrcDir != null && moduleSrcDir.exists() && moduleSrcDir.walkTopDown().any { it.isFile }) {
                    moduleSrcDir.copyRecursively(targetDir, overwrite = true)
                }
            }
            decompileConfig.resourceMappings.forEach { (resourceName, moduleTarget) -> // compare module resources
                val moduleResourcesDir = resolveModuleResourcesDir(moduleTarget) ?: return@forEach
                val moduleResource = moduleResourcesDir.resolve(resourceName)
                val targetResource = gitDir.resolve("__resources__/$resourceName")

                if (targetResource.exists()) {
                    if (targetResource.isDirectory) targetResource.deleteRecursively()
                    else targetResource.delete()
                }

                if (moduleResource.exists()) {
                    if (moduleResource.isDirectory) {
                        moduleResource.copyRecursively(targetResource, overwrite = true)
                    } else {
                        targetResource.parentFile.mkdirs()
                        moduleResource.copyTo(targetResource, overwrite = true)
                    }
                }
            }

            val hasChanges = gitOps.status(gitDir).isNotBlank()
            if (!hasChanges) {
                println("=".repeat(60))
                println("ℹ️  No changes detected - no patch created")
                return@doLast
            }

            gitOps.add(gitDir, "-A")
            println(gitOps.diffStat(gitDir))
            gitOps.diffCached(gitDir, patchFile)

            println("\n📦 Updating generated sources...")
            decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
                val moduleSrcDir = resolveModuleSrcDir(moduleTarget, packagePath)
                val generatedTargetDir = generatedOutputDir.asFile.resolve(packagePath)

                if (generatedTargetDir.exists()) generatedTargetDir.deleteRecursively()

                if (moduleSrcDir != null && moduleSrcDir.exists() && moduleSrcDir.walkTopDown().any { it.isFile }) {
                    moduleSrcDir.copyRecursively(generatedTargetDir, overwrite = true)
                    println("  ✓ Updated generated: $packagePath")
                } else if (generatedTargetDir.exists()) {
                    println("  ✓ Removed from generated: $packagePath")
                }
            }
            decompileConfig.resourceMappings.forEach { (resourceName, moduleTarget) -> // sync resources to generated
                val moduleResourcesDir = resolveModuleResourcesDir(moduleTarget) ?: return@forEach
                val moduleResource = moduleResourcesDir.resolve(resourceName)
                val generatedResource = generatedOutputDir.asFile.resolve(resourceName)

                if (generatedResource.exists()) {
                    if (generatedResource.isDirectory) generatedResource.deleteRecursively()
                    else generatedResource.delete()
                }

                if (moduleResource.exists()) {
                    if (moduleResource.isDirectory) {
                        moduleResource.copyRecursively(generatedResource, overwrite = true)
                    } else {
                        generatedResource.parentFile.mkdirs()
                        moduleResource.copyTo(generatedResource, overwrite = true)
                    }
                    println("Updated generated resource: $resourceName")
                }
            }

            println("\n" + "=".repeat(60))
            println("✨ Created patch: ${decompileConfig.patchesDir}/$patchNameValue.patch")

        } finally {
            gitDir.deleteRecursively()
        }
    }
}

tasks.register("applyPatch") {
    usesService(gitOpsService)

    doLast {
        val patchNameValue = project.findProperty("patchName") as String?
            ?: throw GradleException("Please specify patch name: -PpatchName=\"name\"")

        val gitOps = gitOpsService.get()
        val patchesDirFile = patchesDirPath.asFile
        val patchFile = patchesDirFile.resolve("$patchNameValue.patch")

        if (!patchFile.exists()) {
            println("❌ Patch not found: $patchNameValue.patch")
            println("\nAvailable patches:")
            patchesDirFile.listFiles()
                ?.filter { it.extension == "patch" }
                ?.forEach { println("  - ${it.nameWithoutExtension}") }
            return@doLast
        }

        println("\n🔧 Applying patch: $patchNameValue.patch")
        println("=".repeat(60))

        val gitDir = project.projectDir.resolve(".patch-git")
        if (gitDir.exists()) gitDir.deleteRecursively()
        gitDir.mkdirs()

        try {
            gitOps.setupRepo(gitDir)

            println("📋 Setting up workspace...")
            decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
                val moduleSrcDir = resolveModuleSrcDir(moduleTarget, packagePath) ?: return@forEach
                if (!moduleSrcDir.exists()) return@forEach
                val targetDir = gitDir.resolve(packagePath)
                moduleSrcDir.copyRecursively(targetDir, overwrite = true)
            }
            decompileConfig.resourceMappings.forEach { (resourceName, moduleTarget) -> // setup resources
                val moduleResourcesDir = resolveModuleResourcesDir(moduleTarget) ?: return@forEach
                val moduleResource = moduleResourcesDir.resolve(resourceName)
                if (!moduleResource.exists()) return@forEach
                val targetResource = gitDir.resolve("__resources__/$resourceName")
                if (moduleResource.isDirectory) {
                    moduleResource.copyRecursively(targetResource, overwrite = true)
                } else {
                    targetResource.parentFile.mkdirs()
                    moduleResource.copyTo(targetResource, overwrite = true)
                }
            }

            gitOps.add(gitDir)
            gitOps.commit(gitDir, "Current state")

            println("📝 Applying patch...")
            val (exitCode, output) = gitOps.apply(gitDir, patchFile, verbose = true)

            if (exitCode != 0) {
                println("❌ Failed to apply patch:")
                println(output)
                return@doLast
            }

            println("📦 Copying patched files back to modules and generated sources...")
            decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
                val gitSourceDir = gitDir.resolve(packagePath)
                val moduleTargetDir = resolveModuleSrcDir(moduleTarget, packagePath) ?: return@forEach
                val generatedTargetDir = generatedOutputDir.asFile.resolve(packagePath)

                if (moduleTargetDir.exists()) moduleTargetDir.deleteRecursively()
                if (gitSourceDir.exists()) gitSourceDir.copyRecursively(moduleTargetDir, overwrite = true)

                if (generatedTargetDir.exists()) generatedTargetDir.deleteRecursively()
                if (gitSourceDir.exists()) {
                    gitSourceDir.copyRecursively(generatedTargetDir, overwrite = true)
                    println("  ✓ Updated $moduleTarget (+ generated sources)")
                } else {
                    println("  ✓ Updated $moduleTarget (package deleted)")
                }
            }
            decompileConfig.resourceMappings.forEach { (resourceName, moduleTarget) -> // copy resources back
                val gitResource = gitDir.resolve("__resources__/$resourceName")
                val moduleResourcesDir = resolveModuleResourcesDir(moduleTarget) ?: return@forEach
                val moduleResource = moduleResourcesDir.resolve(resourceName)
                val generatedResource = generatedOutputDir.asFile.resolve(resourceName)

                if (moduleResource.exists()) {
                    if (moduleResource.isDirectory) moduleResource.deleteRecursively()
                    else moduleResource.delete()
                }
                if (generatedResource.exists()) {
                    if (generatedResource.isDirectory) generatedResource.deleteRecursively()
                    else generatedResource.delete()
                }

                if (gitResource.exists()) {
                    if (gitResource.isDirectory) {
                        gitResource.copyRecursively(moduleResource, overwrite = true)
                        gitResource.copyRecursively(generatedResource, overwrite = true)
                    } else {
                        moduleResource.parentFile.mkdirs()
                        generatedResource.parentFile.mkdirs()
                        gitResource.copyTo(moduleResource, overwrite = true)
                        gitResource.copyTo(generatedResource, overwrite = true)
                    }
                    println("Updated resource: $resourceName")
                }
            }

            println("=".repeat(60))
            println("✨ Patch applied successfully!")

        } catch (e: Exception) {
            println("❌ Error applying patch: ${e.message}")
            e.printStackTrace()
        } finally {
            gitDir.deleteRecursively()
        }
    }
}

tasks.register("applyAllPatches") {
    usesService(gitOpsService)

    doLast {
        val gitOps = gitOpsService.get()
        val patchesDirFile = patchesDirPath.asFile

        if (!patchesDirFile.exists() || patchesDirFile.listFiles()?.isEmpty() != false) {
            println("❌ No patches found in ${decompileConfig.patchesDir}/")
            return@doLast
        }

        println("\n🔧 Applying all patches...")
        println("=".repeat(60))

        var successCount = 0
        var failedCount = 0
        val failedPatches = mutableListOf<String>()

        patchesDirFile.listFiles()
            ?.filter { it.extension == "patch" }
            ?.sortedBy { it.name }
            ?.forEach { patchFile ->
                println("\n📄 Applying: ${patchFile.nameWithoutExtension}")

                val gitDir = project.projectDir.resolve(".patch-git")
                if (gitDir.exists()) gitDir.deleteRecursively()
                gitDir.mkdirs()

                try {
                    gitOps.setupRepo(gitDir)

                    decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
                        val moduleSrcDir = resolveModuleSrcDir(moduleTarget, packagePath) ?: return@forEach
                        if (moduleSrcDir.exists()) {
                            val targetDir = gitDir.resolve(packagePath)
                            moduleSrcDir.copyRecursively(targetDir, overwrite = true)
                        }
                    }
                    decompileConfig.resourceMappings.forEach { (resourceName, moduleTarget) ->
                        val moduleResourcesDir = resolveModuleResourcesDir(moduleTarget) ?: return@forEach
                        val moduleResource = moduleResourcesDir.resolve(resourceName)
                        if (moduleResource.exists()) {
                            val targetResource = gitDir.resolve("__resources__/$resourceName")
                            if (moduleResource.isDirectory) {
                                moduleResource.copyRecursively(targetResource, overwrite = true)
                            } else {
                                targetResource.parentFile.mkdirs()
                                moduleResource.copyTo(targetResource, overwrite = true)
                            }
                        }
                    }

                    gitOps.add(gitDir)
                    gitOps.commit(gitDir, "Current state")

                    val (exitCode, errorMsg) = gitOps.apply(gitDir, patchFile)

                    if (exitCode != 0) {
                        println("  ❌ Failed: ${errorMsg.take(200)}")
                        failedPatches.add(patchFile.nameWithoutExtension)
                        failedCount++
                    } else {
                        decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
                            val gitSourceDir = gitDir.resolve(packagePath)
                            val moduleTargetDir = resolveModuleSrcDir(moduleTarget, packagePath) ?: return@forEach
                            val generatedTargetDir = generatedOutputDir.asFile.resolve(packagePath)

                            if (moduleTargetDir.exists()) moduleTargetDir.deleteRecursively()
                            if (gitSourceDir.exists()) gitSourceDir.copyRecursively(moduleTargetDir, overwrite = true)

                            if (generatedTargetDir.exists()) generatedTargetDir.deleteRecursively()
                            if (gitSourceDir.exists()) gitSourceDir.copyRecursively(generatedTargetDir, overwrite = true)
                        }
                        decompileConfig.resourceMappings.forEach { (resourceName, moduleTarget) ->
                            val gitResource = gitDir.resolve("__resources__/$resourceName")
                            val moduleResourcesDir = resolveModuleResourcesDir(moduleTarget) ?: return@forEach
                            val moduleResource = moduleResourcesDir.resolve(resourceName)
                            val generatedResource = generatedOutputDir.asFile.resolve(resourceName)

                            if (moduleResource.exists()) {
                                if (moduleResource.isDirectory) moduleResource.deleteRecursively()
                                else moduleResource.delete()
                            }
                            if (generatedResource.exists()) {
                                if (generatedResource.isDirectory) generatedResource.deleteRecursively()
                                else generatedResource.delete()
                            }

                            if (gitResource.exists()) {
                                if (gitResource.isDirectory) {
                                    gitResource.copyRecursively(moduleResource, overwrite = true)
                                    gitResource.copyRecursively(generatedResource, overwrite = true)
                                } else {
                                    moduleResource.parentFile.mkdirs()
                                    generatedResource.parentFile.mkdirs()
                                    gitResource.copyTo(moduleResource, overwrite = true)
                                    gitResource.copyTo(generatedResource, overwrite = true)
                                }
                            }
                        }

                        println("  ✓ Success (+ updated generated sources)")
                        successCount++
                    }
                } catch (e: Exception) {
                    println("  ❌ Error: ${e.message}")
                    failedPatches.add(patchFile.nameWithoutExtension)
                    failedCount++
                } finally {
                    gitDir.deleteRecursively()
                }
            }

        println("\n" + "=".repeat(60))
        println("✨ Applied: $successCount | Failed: $failedCount")

        if (failedPatches.isNotEmpty()) {
            println("\n❌ Failed patches:")
            failedPatches.forEach { println("  - $it") }
        }
    }
}

tasks.register("listPatches") {
    doLast {
        val patchesDirFile = patchesDirPath.asFile

        if (!patchesDirFile.exists() || patchesDirFile.listFiles()?.isEmpty() != false) {
            println("❌ No patches found in ${decompileConfig.patchesDir}/")
            return@doLast
        }

        println("\n📋 Available Patches")
        println("=".repeat(60))

        patchesDirFile.listFiles()
            ?.filter { it.extension == "patch" }
            ?.sortedBy { it.name }
            ?.forEach { patchFile ->
                val lines = patchFile.readLines()
                val filesChanged = lines.count { it.startsWith("diff --git") }
                val additions = lines.count { it.startsWith("+") && !it.startsWith("+++") }
                val deletions = lines.count { it.startsWith("-") && !it.startsWith("---") }
                val sizeKb = patchFile.length() / 1024

                println("📄 ${patchFile.nameWithoutExtension}")
                println("   Files: $filesChanged | +$additions -$deletions | ${sizeKb}KB")
            }

        println("=".repeat(60))
    }
}

tasks.register("patchStatus") {
    doLast {
        println("\n📊 Patch Status - Modified Files")
        println("=".repeat(60))

        var modifiedCount = 0
        var newFilesCount = 0
        var deletedCount = 0

        decompileConfig.packageMappings.forEach { (packagePath, moduleTarget) ->
            val moduleSrcDir = resolveModuleSrcDir(moduleTarget, packagePath)
            val generatedDir = generatedOutputDir.asFile.resolve(packagePath)

            if (moduleSrcDir == null) return@forEach


            if (!moduleSrcDir.exists() && generatedDir.exists()) {
                val deletedFiles = generatedDir.walkTopDown()
                    .filter { it.isFile && it.extension == "java" }
                    .count()
                if (deletedFiles > 0) {
                    println("\n📦 $moduleTarget")
                    println("  🗑️  DELETED ENTIRE PACKAGE ($deletedFiles files)")
                    deletedCount += deletedFiles
                }
                return@forEach
            }

            if (!generatedDir.exists()) return@forEach

            val moduleModified = mutableListOf<String>()
            val moduleNew = mutableListOf<String>()
            val moduleDeleted = mutableListOf<String>()

            if (moduleSrcDir.exists()) {
                moduleSrcDir.walkTopDown()
                    .filter { it.isFile && it.extension == "java" }
                    .forEach { modifiedFile ->
                        val relativePath = modifiedFile.relativeTo(moduleSrcDir)
                        val originalFile = generatedDir.resolve(relativePath)

                        if (!originalFile.exists()) {
                            moduleNew.add(relativePath.path)
                            newFilesCount++
                        } else if (originalFile.readText() != modifiedFile.readText()) {
                            moduleModified.add(relativePath.path)
                            modifiedCount++
                        }
                    }
            }

            generatedDir.walkTopDown()
                .filter { it.isFile && it.extension == "java" }
                .forEach { originalFile ->
                    val relativePath = originalFile.relativeTo(generatedDir)
                    val moduleFile = if (moduleSrcDir.exists()) moduleSrcDir.resolve(relativePath) else null

                    if (moduleFile == null || !moduleFile.exists()) {
                        moduleDeleted.add(relativePath.path)
                        deletedCount++
                    }
                }

            if (moduleModified.isNotEmpty() || moduleNew.isNotEmpty() || moduleDeleted.isNotEmpty()) {
                println("\n📦 $moduleTarget")
                moduleModified.forEach { println("  ✏️  Modified: $it") }
                moduleNew.forEach { println("  ➕ New: $it") }
                moduleDeleted.forEach { println("  🗑️  Deleted: $it") }
            }
        }

        // check resources
        val resourceModified = mutableListOf<String>()
        val resourceNew = mutableListOf<String>()
        val resourceDeleted = mutableListOf<String>()

        decompileConfig.resourceMappings.forEach { (resourceName, moduleTarget) ->
            val moduleResourcesDir = resolveModuleResourcesDir(moduleTarget) ?: return@forEach
            val moduleResource = moduleResourcesDir.resolve(resourceName)
            val generatedResource = generatedOutputDir.asFile.resolve(resourceName)

            if (moduleResource.isDirectory || generatedResource.isDirectory) {
                // directory resource - compare files inside
                val moduleFiles = if (moduleResource.exists())
                    moduleResource.walkTopDown().filter { it.isFile }.toSet() else emptySet()
                val generatedFiles = if (generatedResource.exists())
                    generatedResource.walkTopDown().filter { it.isFile }.toSet() else emptySet()

                moduleFiles.forEach { modFile ->
                    val relPath = modFile.relativeTo(moduleResource)
                    val genFile = generatedResource.resolve(relPath)
                    if (!genFile.exists()) {
                        resourceNew.add("$resourceName/$relPath")
                        newFilesCount++
                    } else if (genFile.readText() != modFile.readText()) {
                        resourceModified.add("$resourceName/$relPath")
                        modifiedCount++
                    }
                }
                generatedFiles.forEach { genFile ->
                    val relPath = genFile.relativeTo(generatedResource)
                    val modFile = moduleResource.resolve(relPath)
                    if (!modFile.exists()) {
                        resourceDeleted.add("$resourceName/$relPath")
                        deletedCount++
                    }
                }
            } else {
                // single file resource
                if (!moduleResource.exists() && generatedResource.exists()) {
                    resourceDeleted.add(resourceName)
                    deletedCount++
                } else if (moduleResource.exists() && !generatedResource.exists()) {
                    resourceNew.add(resourceName)
                    newFilesCount++
                } else if (moduleResource.exists() && generatedResource.exists()) {
                    if (moduleResource.readText() != generatedResource.readText()) {
                        resourceModified.add(resourceName)
                        modifiedCount++
                    }
                }
            }
        }

        if (resourceModified.isNotEmpty() || resourceNew.isNotEmpty() || resourceDeleted.isNotEmpty()) {
            println("\n📋 Resources")
            resourceModified.forEach { println("  ✏️  Modified: $it") }
            resourceNew.forEach { println("  ➕ New: $it") }
            resourceDeleted.forEach { println("  🗑️  Deleted: $it") }
        }

        println("\n" + "=".repeat(60))
        println("Summary: $modifiedCount modified, $newFilesCount new, $deletedCount deleted")

        if (modifiedCount > 0 || newFilesCount > 0 || deletedCount > 0) {
            println("\n💡 Run './gradlew createPatch -PpatchName=\"name\"' to save these changes")
        } else {
            println("\nℹ️  No modifications detected")
        }
    }
}

tasks.register("decompileAndApplyPatches") {
    dependsOn(decompile, "distributeSources")
    finalizedBy("applyAllPatches")

    doLast {
        println("\n✨ Decompilation complete! Patches will be applied next...")
    }
}

tasks.register("inspectDecompiledStructure") {
    doLast {
        val generatedDir = generatedOutputDir.asFile
        if (!generatedDir.exists()) {
            println("❌ No generated sources found. Run 'decompile' task first.")
            return@doLast
        }

        println("\n📂 Inspecting decompiled package structure...")
        println("=".repeat(60))

        generatedDir.listFiles()?.filter { it.isDirectory }?.sorted()?.forEach { dir ->
            fun printTree(file: File, prefix: String, depth: Int) {
                if (depth > 4) return
                val javaCount = file.walkTopDown().count { it.isFile && it.extension == "java" }
                if (javaCount == 0) return
                println("$prefix📦 ${file.relativeTo(generatedDir)} ($javaCount files)")
                file.listFiles()?.filter { it.isDirectory }?.sorted()?.forEach { child ->
                    printTree(child, "$prefix   ", depth + 1)
                }
            }
            printTree(dir, "", 0)
        }

        val resources = generatedDir.listFiles()?.filter { !it.isDirectory || it.name !in listOf("com", "org", "net") }
        if (resources != null && resources.isNotEmpty()) {
            println("\n📋 Resources at root:")
            resources.sorted().forEach { res ->
                if (res.isDirectory) {
                    val count = res.walkTopDown().count { it.isFile }
                    println("  📁 ${res.name}/ ($count files)")
                } else {
                    println("  📄 ${res.name}")
                }
            }
        }

        println("\n" + "=".repeat(60))
        println("💡 Use this information to configure packageMappings and resourceMappings")
    }
}