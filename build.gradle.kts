plugins {
    id("java")
}

group = "me.kalmemarq"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:3.3.3"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-jemalloc")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = "natives-windows")
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = "natives-windows")
    runtimeOnly("org.lwjgl", "lwjgl-jemalloc", classifier = "natives-windows")
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = "natives-windows")
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = "natives-windows")
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = "natives-windows")
    implementation("org.joml", "joml", "1.10.5")

    implementation("io.netty:netty-buffer:4.1.94.Final")
    implementation("io.netty:netty-codec:4.1.94.Final")
    implementation("io.netty:netty-common:4.1.94.Final")
    implementation("io.netty:netty-handler:4.1.94.Final")
    implementation("io.netty:netty-resolver:4.1.94.Final")
    implementation("io.netty:netty-transport:4.1.94.Final")
    
    implementation("com.fasterxml.jackson.core", "jackson-core", "2.15.3")
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.15.3")

    implementation("io.github.spair:imgui-java-lwjgl3:1.86.11")
    implementation("io.github.spair:imgui-java-binding:1.86.11")
    runtimeOnly("io.github.spair:imgui-java-natives-windows:1.86.11")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.jetbrains:annotations:24.0.1")
}

tasks.test {
    useJUnitPlatform()
}