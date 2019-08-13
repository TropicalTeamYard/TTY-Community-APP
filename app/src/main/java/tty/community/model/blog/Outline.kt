package tty.community.model.blog

import android.os.Handler
import tty.community.network.AsyncTaskUtil
import tty.community.network.NetUtils
import tty.community.values.Value
import tty.community.values.Util
import java.util.*
import kotlin.collections.HashMap

open class Outline(
    var blogId: String,
    var title: String,
    var author: String,
    var nickname: String,
    var portrait: String, // url
    var introduction: String,
    var lastActiveTime: Date,
    var tag: String
) {

    companion object {
        fun initBlogList(time: Date, count: Int, tag: String, callback: AsyncTaskUtil.AsyncNetUtils.Callback) {
            // http://localhost:8080/community/api/blog/list?type=time&date=2019/8/25-03:24:52&count=2&tag=ALL # date 及之前日期的 count 条记录
            val url = Value.api["blog"] + "/list"

            val params = HashMap<String, String>()
            params["type"] = "time"
            params["date"] = Util.getTime(time)
            params["tag"] = tag
            params["count"] = if (count > 0) {"$count"} else {"10"}

            val handler = Handler()
            Thread(Runnable {
                val response = NetUtils.post(url, params)
                handler.post { callback.onResponse(response) }
            }).start()
        }

        fun loadMore(blogId: String, count: Int, tag: String, callback: AsyncTaskUtil.AsyncNetUtils.Callback) {
            // http://localhost:8080/community/api/blog/list?type=id&to=1293637237&count=8&tag=ALL # `to` 之前日期的 count 条记录
            val url = Value.api["blog"] + "/list"

            val params = HashMap<String, String>()
            params["type"] = "id"
            params["to"] = blogId
            params["tag"] = tag
            params["count"] = if (count > 0) {"$count"} else {"10"}

            val handler = Handler()
            Thread(Runnable {
                val response = NetUtils.post(url, params)
                handler.post { callback.onResponse(response) }
            }).start()
        }
    }
}