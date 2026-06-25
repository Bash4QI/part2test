package com.bash4qi.mytest


import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

// 1. كلاس التمهيد الأساسي الذي يطلبه السيرفر لربط البلجن بالتطبيق
@CloudstreamPlugin
class MyPluginInstance: Plugin() {
    override fun load(context: Context) {
        // تسجيل كلاس الموفر الرئيسي الخاص بك هنا عند تشغيل البلجن
        registerMainAPI(MyPlugin())
    }
}

// 2. كلاس الموفر الخاص بك (كود جلب المواقع والأفلام)
class MyPlugin : MainAPI() {
    override var mainUrl = "https://example.com"
    override var name = "ExAr"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get(mainUrl).document
        val home = ArrayList<HomePageList>()

        val items = doc.select(".movie-item").mapNotNull {
            val title = it.select(".title").text()
            val url = it.select("a").attr("href")
            val img = it.select("img").attr("src")

            if (title.isNotBlank() && url.isNotBlank()) {
                newMovieSearchResponse(title, url, TvType.Movie) {
                    this.posterUrl = img
                }
            } else null
        }

        if (items.isNotEmpty()) {
            home.add(HomePageList("Movies", items))
        }

        return newHomePageResponse(home, false)
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document

        val title = doc.select("h1.title").text()
        val description = doc.select(".description").text()
        val poster = doc.select(".poster img").attr("src")
        val year = doc.select(".year").text().toIntOrNull()

        val episodes = doc.select(".episode-link").mapNotNull {
            val epUrl = it.attr("href")
            val epName = it.text()
            if (epUrl.isNotBlank()) {
                newEpisode(epUrl) {
                    this.name = epName
                }
            } else null
        }

        return if (episodes.isNotEmpty()) {
            newTvSeriesLoadResponse(title, url, TvType.TvSeries, episodes) {
                this.posterUrl = poster
                this.plot = description
                this.year = year
            }
        } else {
            newMovieLoadResponse(title, url, TvType.Movie, url) {
                this.posterUrl = poster
                this.plot = description
                this.year = year
            }
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("$mainUrl/search?q=$query").document
        return doc.select(".result").mapNotNull {
            val title = it.select(".title").text()
            val url = it.select("a").attr("href")
            val img = it.select("img").attr("src")

            if (title.isNotBlank() && url.isNotBlank()) {
                newMovieSearchResponse(title, url, TvType.Movie) {
                    this.posterUrl = img
                }
            } else null
        }
    }
}
