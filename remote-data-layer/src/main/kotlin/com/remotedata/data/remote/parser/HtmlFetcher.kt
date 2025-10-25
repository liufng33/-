package com.remotedata.data.remote.parser

import com.remotedata.utils.RemoteException
import com.remotedata.utils.Result
import com.remotedata.utils.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException

interface HtmlFetcher {
    suspend fun fetch(url: String, timeout: Int = 10000): Result<String>
}

class JsoupHtmlFetcher : HtmlFetcher {
    
    override suspend fun fetch(url: String, timeout: Int): Result<String> = withContext(Dispatchers.IO) {
        safeApiCall {
            try {
                val document = Jsoup.connect(url)
                    .timeout(timeout)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .get()
                
                document.html()
            } catch (e: IOException) {
                throw RemoteException.NetworkError("Failed to fetch HTML from $url", e)
            } catch (e: Exception) {
                throw RemoteException.UnknownError("Error fetching HTML: ${e.message}", e)
            }
        }
    }
}
