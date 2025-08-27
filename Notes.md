Crear 
build.gradle

Agregar

plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '3.24.0'
}


gradle ca --package=co.com.powerup --type=reactive --name=AuthService --lombok=true --javaVersion=VERSION_17 

.\gradlew gep --type webflux

.\gradlew bootRun