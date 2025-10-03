import com.vladsch.flexmark.pdf.converter.PdfConverterExtension
import com.vladsch.flexmark.util.options.MutableDataSet
import groovy.xml.XmlUtil
import io.gitlab.gregtechlite.magicbookgradle.utils.deobf
import io.gitlab.gregtechlite.magicbookgradle.utils.modGroup
import io.gitlab.gregtechlite.magicbookgradle.utils.modVersion

plugins {
    `maven-publish`
    id("io.gitlab.gregtechlite.magicbookgradle") version "1.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.vladsch.flexmark:flexmark:0.34.52")
        classpath("com.vladsch.flexmark:flexmark-pdf-converter:0.34.52")
    }
}

magicbook {
    includeWellKnownRepositories()
    usesJUnit()
    java {
        enableModernJavaSyntax()
        withSourcesJar()
    }

    mod {
        generateGradleTokenClass = "$modGroup.api.EIOTags"
        enabledJava17RunTasks = true
        useAccessTransformers()
    }
}

minecraft {
    mcpMappingChannel.set("stable")
    mcpMappingVersion.set("39")
    // EIO uses VERSION, add this field to maintain compatibility
    injectedTags.put("VERSION", modVersion)
}

tasks {
    withType<Javadoc> {
        (options as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    named<ProcessResources>("processResources") {
        fileTree(sourceSets.main.get().output.resourcesDir) {
            include("**/config/recipes/*.xml")
        }.files.forEach {
            val baseFilename = it.name.take(it.name.lastIndexOf("."))
            val inputFile = File(it.parent, "${baseFilename}.xml")
            val outputFile = File(it.parent, "${baseFilename}.pdf")
            PdfConverterExtension.exportToPdf(outputFile.path,
                "<html><head></head><body><div style='white-space:pre-wrap; font-family:monospace;'>" +
                        XmlUtil.escapeXml(inputFile.readText()) +
                        "</div></body></html>", "", MutableDataSet()
            )
        }
    }

    dependencies {
        api("com.enderio:endercore:0.5.78")

        shadowImplementation("info.loenwind:autosave:1.0.11")
        shadowImplementation("info.loenwind:autoconfig:1.0.2")
        localImplementation("info.loenwind:ap:1.0.0")
        annotationProcessor("info.loenwind:ap:1.0.0")

        compileOnlyApi("org.jetbrains:annotations:24.1.0")
        annotationProcessor("org.jetbrains:annotations:24.1.0")

        compileOnly("org.projectlombok:lombok:1.18.24")
        annotationProcessor("org.projectlombok:lombok:1.18.24")

        localImplementation(deobf("curse.maven:journeymap-32274:5172461"))
        localImplementation(deobf("mezz.jei:jei_1.12.2:4.16.1.302"))
        localImplementation(deobf("curse.maven:top-245211:2667280"))
        localImplementation(deobf("curse.maven:ctm-267602:2915363"))// CTM 1.0.2.31
        compileOnly(deobf("curse.maven:ae2-extended-life-570458:4402048")) // AE2UEL 0.55.6
        compileOnly(deobf("curse.maven:baubles-227083:2518667")) // Baubles 1.5.2
        compileOnly(deobf("curse.maven:forestry-59751:2684780"))  // Forestry 5.8.2.387
        compileOnly(deobf("curse.maven:opencomputers-223008:4526246")) // OpenComputers 1.8.0+9833087
        compileOnly(deobf("curse.maven:refined-storage-243076:2940914"))  // Refined Storage 1.6.16
        compileOnly(deobf("curse.maven:thaumcraft-223628:2629023"))  // Thaumcraft 6.1.BETA26
        compileOnly(deobf("curse.maven:cofh-core-69162:2920433"))  // CoFHCore 4.6.6.1
        compileOnly(deobf("curse.maven:redstone-flux-270789:2920436"))  // Redstone Flux 2.1.1.1
        compileOnly(deobf("curse.maven:mantle-74924:2713386"))  // Mantle 1.3.3.55
        compileOnly(deobf("curse.maven:tinkers-construct-74072:2902483"))  // Tinkers" Construct 2.13.0.183
        compileOnly(deobf("curse.maven:mekanism-268560:2835175"))  // Mekanism 9.8.3.390
        //compileOnly("com.mod-buildcraft:buildcraft-api:7.99.24.8")
        //localImplementation rfg.deobf("curse.maven:buildcraft-61811:3204475") // Buildcraft 7.99.24.8
    }
}