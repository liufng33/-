package com.yingshi.video.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.yingshi.video.data.local.dao.ParserSourceDao
import com.yingshi.video.data.local.database.YingshiDatabase
import com.yingshi.video.data.local.entity.ParserSourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ParserSourceDaoTest {

    private lateinit var database: YingshiDatabase
    private lateinit var parserSourceDao: ParserSourceDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            YingshiDatabase::class.java
        ).allowMainThreadQueries().build()
        
        parserSourceDao = database.parserSourceDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetParser() = runTest {
        val parser = createTestParser("1", "Test Parser")
        
        parserSourceDao.insertParser(parser)
        val result = parserSourceDao.getParserById("1")
        
        assert(result != null)
        assert(result?.name == "Test Parser")
    }

    @Test
    fun insertMultipleParsers() = runTest {
        val parsers = listOf(
            createTestParser("1", "Parser 1"),
            createTestParser("2", "Parser 2"),
            createTestParser("3", "Parser 3")
        )
        
        parserSourceDao.insertParsers(parsers)
        val result = parserSourceDao.getAllParsers().first()
        
        assert(result.size == 3)
    }

    @Test
    fun updateParser() = runTest {
        val parser = createTestParser("1", "Original Name")
        parserSourceDao.insertParser(parser)
        
        val updated = parser.copy(name = "Updated Name")
        parserSourceDao.updateParser(updated)
        
        val result = parserSourceDao.getParserById("1")
        assert(result?.name == "Updated Name")
    }

    @Test
    fun deleteParser() = runTest {
        val parser = createTestParser("1", "Test Parser")
        parserSourceDao.insertParser(parser)
        
        parserSourceDao.deleteParserById("1")
        val result = parserSourceDao.getParserById("1")
        
        assert(result == null)
    }

    @Test
    fun getEnabledParsersOnly() = runTest {
        val parsers = listOf(
            createTestParser("1", "Enabled Parser", isEnabled = true),
            createTestParser("2", "Disabled Parser", isEnabled = false),
            createTestParser("3", "Another Enabled", isEnabled = true)
        )
        
        parserSourceDao.insertParsers(parsers)
        val result = parserSourceDao.getEnabledParsers().first()
        
        assert(result.size == 2)
        assert(result.all { it.isEnabled })
    }

    @Test
    fun getParsersByDomain() = runTest {
        val parsers = listOf(
            createTestParser("1", "Parser 1", domains = listOf("example.com", "test.com")),
            createTestParser("2", "Parser 2", domains = listOf("another.com")),
            createTestParser("3", "Parser 3", domains = listOf("example.com"))
        )
        
        parserSourceDao.insertParsers(parsers)
        val result = parserSourceDao.getParsersByDomain("example.com").first()
        
        assert(result.size == 2)
    }

    private fun createTestParser(
        id: String,
        name: String,
        isEnabled: Boolean = true,
        domains: List<String> = listOf("example.com")
    ) = ParserSourceEntity(
        id = id,
        name = name,
        parserUrl = "https://parser.example.com",
        supportedDomains = domains,
        isEnabled = isEnabled,
        priority = 0,
        timeout = 30000,
        headers = emptyMap(),
        description = "Test parser"
    )
}
