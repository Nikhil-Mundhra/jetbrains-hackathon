package com.yallapark

import com.yallapark.ai.OpenRouterClient
import com.yallapark.ai.OpenRouterMessage
import com.yallapark.ai.YallaAiViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OpenRouterClientTest {

    @Test
    fun testEmptyApiKeyReturnsLocalHeuristicFallback() = runTest {
        val client = OpenRouterClient(apiKey = "")
        val messages = listOf(OpenRouterMessage(role = "user", content = "Where can I find pink bays?"))
        val result = client.queryParkingAssistantDetailed(messages)

        assertTrue(result.isSuccess)
        val response = result.getOrThrow()
        assertFalse(response.isLive)
        assertEquals("local-heuristic-fallback", response.modelUsed)
        assertTrue(response.content.contains("Women-Only Pink Bays"))
    }

    @Test
    fun testSmartFallbackHeuristicsCoverage() {
        val client = OpenRouterClient(apiKey = "")

        val pinkResp = client.generateSmartFallback("Need pink parking")
        assertTrue(pinkResp.contains("Women-Only Pink Bays"))

        val podResp = client.generateSmartFallback("Accessible POD bay needed")
        assertTrue(podResp.contains("People of Determination (POD) Bays"))

        val deliveryResp = client.generateSmartFallback("Delivery rider quick stop")
        assertTrue(deliveryResp.contains("Delivery Rider Quick Bays"))

        val karamaResp = client.generateSmartFallback("Karama street parking")
        assertTrue(karamaResp.contains("Al Karama Parking Status"))

        val burDubaiResp = client.generateSmartFallback("Al Fahidi heritage parking")
        assertTrue(burDubaiResp.contains("Bur Dubai Status"))

        val defaultResp = client.generateSmartFallback("General info")
        assertTrue(defaultResp.contains("YallaPark Smart Concierge"))
    }

    @Test
    fun testSuccessfulOpenRouterMockResponse() = runTest {
        val mockJson = """
            {
              "choices": [
                {
                  "message": {
                    "role": "assistant",
                    "content": "Recommended: Park at Al Fahidi Heritage Lot, 22 bays available at AED 4/hr."
                  }
                }
              ]
            }
        """.trimIndent()

        val mockEngine = MockEngine { _ ->
            respond(
                content = mockJson,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val testHttpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }

        val client = OpenRouterClient(apiKey = "sk-mock-test-key", httpClient = testHttpClient)
        val messages = listOf(OpenRouterMessage(role = "user", content = "Best lot in Bur Dubai?"))
        val result = client.queryParkingAssistantDetailed(messages)

        assertTrue(result.isSuccess)
        val response = result.getOrThrow()
        assertTrue(response.isLive)
        assertEquals("openai/gpt-4o-mini", response.modelUsed)
        assertTrue(response.content.contains("Al Fahidi Heritage Lot"))
    }

    @Test
    fun testOpenRouterHttpErrorFallsBackGracefully() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"error": {"message": "Rate limit exceeded", "code": 429}}""",
                status = HttpStatusCode.TooManyRequests,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val testHttpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                })
            }
        }

        val client = OpenRouterClient(apiKey = "sk-mock-test-key", httpClient = testHttpClient)
        val messages = listOf(OpenRouterMessage(role = "user", content = "Where is POD parking?"))
        val result = client.queryParkingAssistantDetailed(messages)

        assertTrue(result.isSuccess)
        val response = result.getOrThrow()
        assertFalse(response.isLive)
        assertEquals("local-heuristic-fallback", response.modelUsed)
        assertTrue(response.content.contains("People of Determination"))
    }

    @Test
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun testYallaAiViewModelFlow() = runTest {
        val client = OpenRouterClient(apiKey = "")
        // Inject the runTest scope so sendMessage launches on the test dispatcher
        val viewModel = YallaAiViewModel(client = client, testScope = this)

        assertEquals(1, viewModel.messages.value.size)
        assertEquals("init_0", viewModel.messages.value[0].id)

        viewModel.sendMessage("Need pink bays in Karama")

        // Drain all pending coroutines in the test scope
        advanceUntilIdle()
        val messages = viewModel.messages.value
        assertEquals(3, messages.size)
        assertEquals("user", messages[1].sender)
        assertEquals("Need pink bays in Karama", messages[1].text)
        assertEquals("assistant", messages[2].sender)
        assertTrue(messages[2].text.contains("Women-Only Pink Bays"))
        assertFalse(viewModel.isLoading.value)
    }
}
