Crear 
build.gradle

Agregar

plugins {
    id 'co.com.bancolombia.cleanArchitecture' version '3.24.0'
}


gradle ca --package=co.com.powerup --type=reactive --name=AuthService --lombok=true --javaVersion=VERSION_17 

.\gradlew gep --type webflux

.\gradlew bootRun

Generar Modelos

gradle gm --name User
gradle gm --name Rol


Generar Use Case

gradle guc --name User
gradle guc --name Rol

Generar Driven Adapter

gradle gda --type r2dbc

Generar Entry Point

gradle gep --type webflux