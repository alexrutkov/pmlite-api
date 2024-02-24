package ru.pmlite.api

import org.junit.jupiter.api.Test
import java.nio.file.FileSystems


class PmliteApiApplicationTests {

	@Test
	fun contextLoads() {
		println(FileSystems.getDefault().getPath("").toAbsolutePath())
	}

}
