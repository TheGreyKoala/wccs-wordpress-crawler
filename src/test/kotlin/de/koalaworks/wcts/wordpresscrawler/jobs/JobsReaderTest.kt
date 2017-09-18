package de.koalaworks.wcts.wordpresscrawler.jobs

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

@DisplayName("JobReader")
class JobsReaderTest {

    @Test
    @DisplayName("should read the configuration file correctly")
    fun readJob() {
        val jobFile = javaClass.getResource("job.yml").file
        val reader = JobReader(File(jobFile))
        val job = reader.readJob()

        assertAll("configurationService",
            Executable { assertEquals("https", job.classificationService.scheme, "Unexpected scheme") },
            Executable { assertEquals("myhost", job.classificationService.host, "Unexpected host") },
            Executable { assertEquals("55555", job.classificationService.port, "Unexpected port") },
            Executable { assertEquals("/myservice", job.classificationService.path, "Unexpected path") }
        )

        assertAll("sites",
            Executable { assertEquals(14, job.sites.size, "Unexpected amount of sites") }
        )
    }

    @Test
    @DisplayName("should use default values")
    fun defaultValues() {
        val jobFile = javaClass.getResource("empty_job.yml").file
        val reader = JobReader(File(jobFile))
        val job = reader.readJob()

        assertAll("configurationService",
            Executable { assertEquals("http", job.classificationService.scheme, "Unexpected scheme") },
            Executable { assertEquals("localhost", job.classificationService.host, "Unexpected host") },
            Executable { assertEquals("44284", job.classificationService.port, "Unexpected port") },
            Executable { assertEquals("/", job.classificationService.path, "Unexpected path") }
        )

        assertAll("sites",
            Executable { assertEquals(14, job.sites.size, "Unexpected amount of sites") }
        )
    }

    @Test
    @DisplayName("Throws if job file not found")
    fun invalidPath() {
        val exception = assertThrows(FileNotFoundException::class.java, { JobReader(Paths.get("/my/path/job.yml")) })
        assertEquals("/my/path/job.yml could not be found.", exception.message, "Unexpected exception message")
    }
}