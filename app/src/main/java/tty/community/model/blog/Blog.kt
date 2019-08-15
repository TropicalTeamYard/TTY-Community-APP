package tty.community.model.blog

import android.os.Handler
import tty.community.network.AsyncTaskUtil
import tty.community.network.NetUtils
import tty.community.values.Const
import tty.community.values.Time
import java.util.*

interface Blog {
    val blogId: String
    val type: String
    val author: String
    val nickname: String
    val title: String
    val introduction: String
    val tag: String
    val lastActiveTime: Date

    class Outline(
        var blogId: String,
        var type: String,
        var author: String,
        var title: String,
        var introduction: String,
        var tag: String,
        var portrait: String,
        var lastActiveTime: Date,
        var nickname: String
    ) {

        companion object {
            fun initBlogList(
                time: Date,
                count: Int,
                tag: String,
                callback: AsyncTaskUtil.AsyncNetUtils.Callback
            ) {
                // http://localhost:8080/community/api/blog/list?type=time&date=2019/8/25-03:24:52&count=2&tag=ALL # date 及之前日期的 count 条记录
                val url = Const.api[Const.Route.Blog] + "/list"

                val params = HashMap<String, String>()
                params["type"] = "time"
                params["date"] = Time.getTime(time)
                params["tag"] = tag
                params["count"] = if (count > 0) {
                    "$count"
                } else {
                    "10"
                }

                val handler = Handler()
                Thread(Runnable {
                    val response = NetUtils.post(url, params)
                    handler.post { callback.onResponse(response) }
                }).start()
            }

            fun loadMore(
                blogId: String,
                count: Int,
                tag: String,
                callback: AsyncTaskUtil.AsyncNetUtils.Callback
            ) {
                // http://localhost:8080/community/api/blog/list?type=id&to=1293637237&count=8&tag=ALL # `to` 之前日期的 count 条记录
                val url = Const.api[Const.Route.Blog] + "/list"

                val params = HashMap<String, String>()
                params["type"] = "id"
                params["to"] = blogId
                params["tag"] = tag
                params["count"] = if (count > 0) {
                    "$count"
                } else {
                    "10"
                }

                val handler = Handler()
                Thread(Runnable {
                    val response = NetUtils.post(url, params)
                    handler.post { callback.onResponse(response) }
                }).start()
            }
        }
    }

    class Detail(
        override val blogId: String,
        override val type: String,
        override val author: String,
        override val title: String,
        override val introduction: String,
        override val tag: String,
        override val lastActiveTime: Date,
        override val nickname: String
    ) : Blog {
        var content: String = ""
        var comment: String = ""
        var likes = ""
        var status = "deleted"
        var data: String? = null
        var lastEditTime: Date? = null
    }

    companion object {
        class Tag(val id: String, val text: String)

        enum class Type {
            Short, Pro, Other;

            companion object {
                val Type.value: Int
                    get() {
                        return when (this) {
                            Short -> 0
                            Pro -> 1
                            Other -> -1
                        }
                    }

                val String?.parse: Type
                    get() {
                        return when (this) {
                            "0", "Short" -> {
                                Short
                            }

                            "1", "Pro" -> {
                                Pro
                            }

                            else -> {
                                Other
                            }
                        }
                    }


            }
        }
    }
}
