package tty.community.model

import tty.community.network.AsyncNetUtils
import tty.community.network.AsyncNetUtils.Callback
import tty.community.util.CONF
import java.util.*

interface Blog {
    val blogId: String
    val type: Int
    val author: String
    val nickname: String
    var title: String
    var introduction: String
    val topic: Topic.Outline
    val lastActiveTime: Date
    val status: Int
    fun portrait(): String = CONF.API.public.portrait + "?" + "id=$author"


    open class Outline(
        override val blogId: String,
        override val type: Int,
        override val author: String,
        override var title: String,
        override var introduction: String,
        override val topic: Topic.Outline,
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
        override val topic: Topic.Outline,
        override val lastActiveTime: Date,
        override val nickname: String,
        override var status: Int,
        var content: String,
        var comments: ArrayList<Comment>,
        var likes: ArrayList<Like>,
        var lastEditTime: Date
    ) : Blog

    data class Comment(val id: String, val nickname: String, val time: String, val content: String)
    data class Like(val id: String, val nickname: String)

    enum class BlogListType {
        TIME, ID;

        val string: String
            get() = when (this) {
                TIME -> "time"
                ID -> "id"
            }
        companion object {
            fun parse(string: String?) = when(string) {
                "time", "TIME" -> TIME
                "id", "ID" -> ID
                else -> null
            }
        }
    }

    companion object {

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

        fun initBlogList(time: Date, count: Int, topic: Topic.Outline, callback: Callback) {
            // http://localhost:8080/community/api/blog/list?type=time&date=2019/8/25-03:24:52&count=2 # date 及之前日期的 count 条记录
            AsyncNetUtils.post(CONF.API.blog.list, Params.blogListByTime(time, topic.id, count), callback)

        }

        fun loadMore(blogId: String, count: Int, topic: Topic.Outline, callback: Callback) {
            // http://localhost:8080/community/api/blog/list?type=id&id=1293637237&count=8&tag=00000 # `to` 之前日期的 count 条记录
            AsyncNetUtils.post(CONF.API.blog.list, Params.blogListById(blogId, topic.id, count), callback)
        }

    }
}
