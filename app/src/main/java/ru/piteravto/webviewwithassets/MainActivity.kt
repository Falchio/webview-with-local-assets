package ru.piteravto.webviewwithassets

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val webView = findViewById<WebView>(R.id.web_view)
        val assetLoader = WebViewAssetLoader.Builder()
            /* можно таким способом перехватывать загрузку .css, image, .js и т.д
             добавляем к WebViewAssetLoader.Builder()
             1. доменнное имя
            .setDomain("custom_url.ru")
            2. затем
            .addPathHandler(
                "/js/plugins/",
                WebViewAssetLoader.AssetsPathHandler(App.context)
            )
        WebViewAssetLoader.AssetsPathHandler вначале пытается найти запрашиваемый файл
         по ссылке https://custom_url.ru/js/plugins/имя_файла
         в assets и только потом грузит из сети
          Для папки ресурсов можно сделать тоже самое: .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(App.context))
        .*/

            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(this))
            .build()
        webView.webViewClient = LocalContentWebViewClient(assetLoader)
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html")

    }

    inner class LocalContentWebViewClient(
        private val assetLoader: WebViewAssetLoader
    ) :
        WebViewClientCompat() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (view != null) {
                JavascriptInjector.injectScripts(view)
            }
        }

        @RequiresApi(21)
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(request.url)
        }

        // to support API < 21
        override fun shouldInterceptRequest(
            view: WebView,
            url: String
        ): WebResourceResponse? {
            return assetLoader.shouldInterceptRequest(Uri.parse(url))
        }
    }

}