package com.remotedata.data.remote.datasource

import com.remotedata.data.remote.parser.HtmlFetcher
import com.remotedata.data.remote.parser.HtmlParser
import com.remotedata.data.remote.parser.ParsingConfig
import com.remotedata.data.remote.parser.RuleBasedParser
import com.remotedata.domain.entity.ParsedContent
import com.remotedata.utils.RateLimiter
import com.remotedata.utils.Result
import com.remotedata.utils.safeApiCall
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

interface ParserRemoteDataSource {
    suspend fun parseUrl(url: String, config: ParsingConfig? = null): Result<ParsedContent>
    suspend fun parseHtml(html: String, baseUrl: String, config: ParsingConfig? = null): Result<ParsedContent>
}

class ParserRemoteDataSourceImpl(
    private val htmlFetcher: HtmlFetcher,
    private val defaultParser: HtmlParser,
    private val rateLimiter: RateLimiter
) : ParserRemoteDataSource {
    
    override suspend fun parseUrl(url: String, config: ParsingConfig?): Result<ParsedContent> = 
        rateLimiter.execute("parser-$url") {
            logger.info { "Parsing URL: $url" }
            
            safeApiCall {
                val htmlResult = htmlFetcher.fetch(url)
                
                when (htmlResult) {
                    is Result.Success -> {
                        val parser = config?.let { RuleBasedParser(it) } ?: defaultParser
                        parser.parse(htmlResult.data, url)
                    }
                    is Result.Error -> throw htmlResult.exception
                    is Result.Loading -> throw IllegalStateException("Unexpected loading state")
                }
            }
        }
    
    override suspend fun parseHtml(html: String, baseUrl: String, config: ParsingConfig?): Result<ParsedContent> = 
        safeApiCall {
            logger.info { "Parsing HTML for baseUrl: $baseUrl" }
            
            val parser = config?.let { RuleBasedParser(it) } ?: defaultParser
            parser.parse(html, baseUrl)
        }
}
