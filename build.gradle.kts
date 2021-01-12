plugins {
	java
	maven
	`maven-publish`
	kotlin("jvm") version "1.4.21"
	id("com.github.johnrengelman.shadow") version "6.1.0"
	id("org.jetbrains.dokka") version "1.4.20"
}

group = "com.ruthlessjailer.api.theseus"
version = "1.2.4"

val finalName = "${project.name}.jar"
val copyDir = "D:/Gaming/Minecraft/Server/paper 1.16/plugins"
val mainClass = "com.ruthlessjailer.api.theseus.Theseus"
val javaVersion = "1.8"

repositories {
	mavenCentral()
	mavenLocal()
	jcenter()
	maven {
		name = "spigotmc-repo"
		url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

		content {
			includeGroup("org.spigotmc")
		}
	}

	maven {
		name = "codemc-repo"
		url = uri("https://repo.codemc.org/repository/maven-public/")
	}

	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
	maven { url = uri("https://oss.sonatype.org/content/repositories/central") }
	maven { url = uri("https://jitpack.io") }

}

dependencies {
	compileOnly("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")
	compileOnly("org.projectlombok:lombok:1.18.8")
	compileOnly("org.apache.commons:commons-lang3:3.11")
	compileOnly("commons-io:commons-io:2.8.0")
	annotationProcessor("org.projectlombok:lombok:1.18.8")
	api(kotlin("stdlib-jdk8"))
//	implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
	dependsOn(tasks.dokkaJavadoc)
	from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
	archiveClassifier.set("javadoc")
	classifier = "javadoc"
}

val sourceJar by tasks.register<Jar>("sourceJar") {
	from(sourceSets["main"].allSource)
	archiveClassifier.set("sources")
	classifier = "sources"
}

tasks {

	shadowJar {
		archiveFileName.set(finalName)
		mergeServiceFiles()
		minimize()
		manifest {
			attributes(mapOf("Main-Class" to mainClass))
		}
	}

	copy {
		from(file("$buildDir/libs/$finalName"))
		into(file(copyDir))
	}

	build {
		dependsOn(shadowJar)
	}

	processResources {
		expand("version" to project.version, "name" to project.name, "mainClass" to mainClass)
	}

	kotlinSourcesJar {
		from("src/main/java", "src/main/kotlin", "src/main/resources")
	}

	dokkaJavadoc {
		failOnWarning.set(false)
	}

	jar {
		archiveFileName.set("${project.name}-${project.version}-unshaded.jar")
	}

	compileKotlin {
		kotlinOptions.jvmTarget = javaVersion
	}
}

java {
	withSourcesJar()
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["kotlin"])
			version = project.version.toString()
			groupId = project.group.toString()

			afterEvaluate {
				artifactId = tasks.jar.get().archiveBaseName.get()
			}

			artifact(dokkaJavadocJar)
			artifact(sourceJar)
		}
	}
}