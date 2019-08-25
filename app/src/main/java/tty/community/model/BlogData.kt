package tty.community.model

import tty.community.util.CONF
import java.io.File

class BlogData(var title:String, var content:String, var picLinks:ArrayList<Int> = ArrayList(), var pics:ArrayList<File> = ArrayList()){
    val introduction: Introduction
    get() {
        val picFiles =  ArrayList<String>()
        for (pic in pics){
            picFiles.add(pic.name)
        }
        val summary = try {
            content.substring(0, 120) + "..."
        } catch (e: StringIndexOutOfBoundsException) {
            content
        }
        return Introduction(picFiles, picLinks, summary)
    }


    class Introduction(var pics:ArrayList<String>, var picLinks: ArrayList<Int>, var summary:String)

    interface IGetBlogData{
        fun getBlogData(): BlogData
    }

    companion object {
        fun toUrl(blogId:String, picName: String):String{
            return "${CONF.API.blog.picture}?id=$blogId&key=$picName"
        }
    }
}

