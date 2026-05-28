package com.example.data

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import java.util.concurrent.TimeUnit

class RealtimeClient(private val onChangeReceived: () -> Unit) {

    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())
    private var heartbeatRunnable: Runnable? = null
    private var reconnectRunnable: Runnable? = null

    fun start() {
        if (isRunning) return
        isRunning = true
        connect()
    }

    fun stop() {
        isRunning = false
        stopHeartbeat()
        stopReconnect()
        webSocket?.close(1000, "App closed")
        webSocket = null
    }

    private fun connect() {
        if (!isRunning) return
        val domain = SupabaseClient.currentUrl
            .replace("https://", "")
            .replace("http://", "")
            .substringBefore("/")
        val url = "wss://$domain/realtime/v1/websocket?apikey=${SupabaseClient.currentApiKey}&vsn=1.0.0"
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("RealtimeClient", "WebSocket connected successfully.")
                joinChannel(webSocket)
                startHeartbeat()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("RealtimeClient", "Message received: $text")
                if (text.contains("postgres_changes") || text.contains("INSERT") || text.contains("UPDATE") || text.contains("DELETE")) {
                    Log.d("RealtimeClient", "Database change detected, refreshing lists...")
                    handler.post { onChangeReceived() }
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.e("RealtimeClient", "WebSocket closing: $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.e("RealtimeClient", "WebSocket closed. Reconnecting...")
                scheduleReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("RealtimeClient", "WebSocket failure: ${t.message}", t)
                scheduleReconnect()
            }
        })
    }

    private fun joinChannel(ws: WebSocket) {
        // Phoenix/Supabase channel join message for public Postgres changes
        val joinMsg = """
            {
                "topic": "realtime:public",
                "event": "phx_join",
                "payload": {
                    "config": {
                        "postgres_changes": [
                            { "event": "*", "schema": "public" }
                        ]
                    }
                },
                "ref": "1"
            }
        """.trimIndent()
        ws.send(joinMsg)
        Log.d("RealtimeClient", "Sent join channel message.")
    }

    private fun startHeartbeat() {
        stopHeartbeat()
        heartbeatRunnable = object : Runnable {
            override fun run() {
                webSocket?.let { ws ->
                    val heartbeatMsg = """
                        {
                            "topic": "phoenix",
                            "event": "heartbeat",
                            "payload": {},
                            "ref": "heartbeat-${System.currentTimeMillis()}"
                        }
                    """.trimIndent()
                    val sent = ws.send(heartbeatMsg)
                    Log.d("RealtimeClient", "Heartbeat sent: $sent")
                    handler.postDelayed(this, 25000) // 25 seconds
                }
            }
        }
        handler.postDelayed(heartbeatRunnable!!, 25000)
    }

    private fun stopHeartbeat() {
        heartbeatRunnable?.let { handler.removeCallbacks(it) }
        heartbeatRunnable = null
    }

    private fun scheduleReconnect() {
        stopHeartbeat()
        stopReconnect()
        if (!isRunning) return
        reconnectRunnable = Runnable {
            Log.d("RealtimeClient", "Attempting websocket reconnect...")
            connect()
        }
        handler.postDelayed(reconnectRunnable!!, 5000) // Reconnect in 5 seconds
    }

    private fun stopReconnect() {
        reconnectRunnable?.let { handler.removeCallbacks(it) }
        reconnectRunnable = null
    }
}
