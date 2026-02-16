plugins { id("java-quality") }

dependencies {
  testImplementation(platform(libs.spring.boot.bom))
  testImplementation(libs.spring.boot.starter.test)
}

tasks.test { useJUnitPlatform() }
