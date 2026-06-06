package com.example.maia.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val GATEWAY_URL = "http://10.0.2.2:5100/"

    // Cookie jar — ruan jwt + refresh cookie automatikisht mes kërkesave
    private val cookieJar = object : CookieJar {
        private val store = mutableListOf<Cookie>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            store.removeAll { c -> cookies.any { it.name == c.name } }
            store.addAll(cookies)
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> = store.toList()

        fun clear() = store.clear()
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(logging)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GATEWAY_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun clearSession() = cookieJar.clear()

    // Auth
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }

    // Sections
    val womenApi: WomenApi by lazy { retrofit.create(WomenApi::class.java) }
    val menApi: MenApi by lazy { retrofit.create(MenApi::class.java) }
    val kidsApi: KidsApi by lazy { retrofit.create(KidsApi::class.java) }

    // Order Service i dedikuar
    val orderServiceApi: OrderServiceApi by lazy { retrofit.create(OrderServiceApi::class.java) }

    // Shërbime tjera
    val notificationApi: NotificationApi by lazy { retrofit.create(NotificationApi::class.java) }
    val fileUploadApi: FileUploadApi by lazy { retrofit.create(FileUploadApi::class.java) }
    val settingsApi: SettingsApi by lazy { retrofit.create(SettingsApi::class.java) }

    // Aliases për ViewModels ekzistues
    val cartApi: CartApi by lazy { retrofit.create(CartApi::class.java) }
    val wishlistApi: WishlistApi by lazy { retrofit.create(WishlistApi::class.java) }
    val orderApi: OrderApi by lazy { retrofit.create(OrderApi::class.java) }
    val api: KidsApi get() = kidsApi
}
