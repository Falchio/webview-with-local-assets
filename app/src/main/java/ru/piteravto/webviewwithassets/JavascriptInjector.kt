package ru.piteravto.webviewwithassets
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.charset.Charset

object JavascriptInjector {
    private val TAG = javaClass.simpleName

    /* путь отсчитывается от папки Assets, файлов в проекте нет, просто для примера */
    private val scriptsFileNames: List<String> = arrayListOf(
        "extjs/ext-modern-all.js",
        "jquery/jquery-3.6.0.min.js",
        "extjs/theme-triton/theme-triton-debug.js",
        "leaflet/leaflet.js",
        "qr-scanner/qr-scanner.min.js",
        "crypto-js.min.js"
    )


    fun injectScripts(webView: WebView) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsInjection = collectScriptsFromFiles()
            Handler(Looper.getMainLooper()).post {
                evaluateJs(webView, jsInjection)
            }
        }
    }

    private fun evaluateJs(webView: WebView, jsInjection: String) {
        try {
            webView.evaluateJavascript(jsInjection, null)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to inject scripts.")
            e.printStackTrace()
        }
    }

    private fun collectScriptsFromFiles(): String {
        val script = StringBuilder()
        for (filename in scriptsFileNames) {
            val content = getContentsFromFile(filename)
            if (content == null || content.isEmpty()) {
                continue
            }
            script.append(content)
            script.append("\n;\n")
        }
        return script.toString()
    }

    private fun getContentsFromFile(filename: String): String? {
        try {
            val assetsInputStream = App.resources.assets.open(filename)
            assetsInputStream.use { stream ->
                val size = stream.available()
                val byteArray = ByteArray(size)
                stream.read(byteArray)
                return String(byteArray, Charset.defaultCharset())
            }
        } catch (e: IOException) {
            Log.e(TAG, String.format("Failed to read file: %s.", filename))
            return null
        }
    }

}