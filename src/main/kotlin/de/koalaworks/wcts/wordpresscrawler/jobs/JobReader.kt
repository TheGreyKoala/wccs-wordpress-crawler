package de.koalaworks.wcts.wordpresscrawler.jobs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.nio.file.Path

class JobReader(private val jobFile: File) {
    constructor(jobFile: Path) : this(jobFile.toFile())

    init {
        if (!jobFile.exists()) {
            throw FileNotFoundException(jobFile.absolutePath + " could not be found.")
        }
    }

    fun readJob(): Job {
        val mapper = ObjectMapper(YAMLFactory())
        mapper.registerModule(KotlinModule())

        return FileReader(jobFile).use {
            mapper.readValue(it, Job::class.java)
        }
    }
}