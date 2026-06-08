package com.example.maia.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

object RetrofitInstance {

    private const val GATEWAY_URL  = "http://10.0.2.2:5100/"
    private const val AUTH_DIRECT_URL = "http://10.0.2.2:5000/"

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

    // Bearer token — set after login if backend returns JWT in response body
    @Volatile private var bearerToken: String? = null
    fun setToken(token: String?) { bearerToken = token }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor { chain ->
            // Extract JWT from Set-Cookie on login response; store as bearer for direct-service calls
            val response = chain.proceed(chain.request())
            response.headers("Set-Cookie").forEach { cookie ->
                if (cookie.startsWith("jwt=")) {
                    val value = cookie.substringAfter("jwt=").substringBefore(";").trim()
                    if (value.isNotEmpty()) bearerToken = value
                }
            }
            response
        }
        .addInterceptor { chain ->
            val token = bearerToken
            val request = if (token != null)
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            else
                chain.request()
            chain.proceed(request)
        }
        .addInterceptor(logging)
        .build()

    // Handles 204 No Content for suspend fun foo(): Unit — Gson can't parse empty body
    private val unitConverterFactory = object : Converter.Factory() {
        override fun responseBodyConverter(
            type: Type, annotations: Array<out Annotation>, retrofit: Retrofit
        ): Converter<ResponseBody, *>? {
            return if (type == Unit::class.java) Converter<ResponseBody, Unit> { Unit } else null
        }
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GATEWAY_URL)
            .client(client)
            .addConverterFactory(unitConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Direct to Auth service — gateway strips Cookie headers, so admin calls bypass it
    private val authRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AUTH_DIRECT_URL)
            .client(client)
            .addConverterFactory(unitConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun clearSession() { cookieJar.clear(); bearerToken = null }

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

    // Dashboard APIs — admin uses authRetrofit (direct to Auth service, cookies forwarded)
    val adminApi: AdminApi by lazy { authRetrofit.create(AdminApi::class.java) }
    val womenManagerApi: WomenManagerApi by lazy { retrofit.create(WomenManagerApi::class.java) }
    val menManagerApi: MenManagerApi by lazy { retrofit.create(MenManagerApi::class.java) }

    // Aliases për ViewModels ekzistues
    val cartApi: CartApi by lazy { retrofit.create(CartApi::class.java) }
    val wishlistApi: WishlistApi by lazy { retrofit.create(WishlistApi::class.java) }
    val orderApi: OrderApi by lazy { retrofit.create(OrderApi::class.java) }
    val api: KidsApi get() = kidsApi
}
