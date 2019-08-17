package tty.community.model.blog

import tty.community.network.AsyncNetUtils
import tty.community.network.AsyncNetUtils.Callback
import tty.community.values.CONF
import tty.community.values.Time
import java.util.*

interface Blog {
    val blogId: String
    val type: Int
    val author: String
    val nickname: String
    var title: String
    var introduction: String
    val tag: String
    val lastActiveTime: Date
    val status: Int
    fun portrait(): String = CONF.API.public.portrait + "?id=$blogId"


    open class Outline(
        override val blogId: String,
        override val type: Int,
        override val author: String,
        override var title: String,
        override var introduction: String,
        override val tag: String,
        override val lastActiveTime: Date,
        override val nickname: String,
        override val status: Int
    ) : Blog

    class Detail(
        override val blogId: String,
        override val type: Int,
        override val author: String,
        override var title: String,
        override var introduction: String,
        override val tag: String,
        override val lastActiveTime: Date,
        override val nickname: String,
        override var status: Int,
        var content: String,
        var comments: ArrayList<Comment>,
        var likes: ArrayList<Like>,
        var lastEditTime: Date
    ) : Outline(blogId, type, author, title, introduction, tag, lastActiveTime, nickname, status)

    data class Comment(val id: String, val nickname: String, val time: String)
    data class Like(val id: String, val nickname: String)

    companion object {
        class Tag(val id: String, val text: String)

        enum class BlogType {
            Short, Pro, Other;
            companion object {
                fun String?.parse() = when (this) {
                    "0", "Short" -> Short
                    "1", "Pro" -> Pro
                    else -> Other
                }
            }

            fun string() = when (this) {
                Short -> "0"
                Pro -> "1"
                Other -> "-1"
            }

            fun value() = when (this) {
                Short -> 0
                Pro -> 1
                Other -> -1
            }
        }

        fun initBlogList(time: Date, count: Int, tag: String, callback: Callback) {
            // http://localhost:8080/community/api/blog/list?type=time&date=2019/8/25-03:24:52&count=2 # date 及之前日期的 count 条记录

            val params = HashMap<String, String>()
            params["type"] = "time"
            params["date"] = Time.getTime(time)
            params["tag"] = tag
            params["count"] = "$count"

            AsyncNetUtils.post(CONF.API.blog.list, params, callback)

        }

        fun loadMore(blogId: String, count: Int, tag: String, callback: Callback) {
            // http://localhost:8080/community/api/blog/list?type=id&id=1293637237&count=8&tag=00000 # `to` 之前日期的 count 条记录

            val params = HashMap<String, String>()
            params["type"] = "id"
            params["id"] = blogId
            params["tag"] = tag
            params["count"] = "$count"

            AsyncNetUtils.post(CONF.API.blog.list, params, callback)
        }

    }
}
