package tty.community.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

import java.util.ArrayList


class ComplexEditText : AppCompatEditText, View.OnClickListener {
    private lateinit var mContext: Context
    private var onSelectChangeListener: OnSelectChangeListener? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        mContext = context
        setOnClickListener(this)
    }

    override fun onClick(view: View) {
        var start = selectionStart - 1
        if (start == -1) {
            start = 0
        }
        if (start < -1) {
            return
        }
        val fontStyle = getFontStyle(start, start)
        setBold(fontStyle.isBold)
        setItalic(fontStyle.isItalic)
        setUnderline(fontStyle.isUnderline)
        setStreak(fontStyle.isStreak)
        setFontSize(fontStyle.fontSize)
        setFontColor(fontStyle.color)
        if (onSelectChangeListener != null) {
            onSelectChangeListener!!.onSelect(start, start)
            onSelectChangeListener!!.onFontStyleChang(fontStyle)
        }
    }

    /**
     * public setting
     */
    fun setBold(isBold: Boolean) {
        setStyleSpan(isBold, Typeface.BOLD)
    }

    fun setItalic(isItalic: Boolean) {
        setStyleSpan(isItalic, Typeface.ITALIC)
    }

    fun setUnderline(isUnderline: Boolean) {
        setUnderlineSpan(isUnderline)
    }

    fun setStreak(isStreak: Boolean) {
        setStreakSpan(isStreak)
    }

    fun setFontSize(size: Int) {
        setFontSizeSpan(size)
    }

    fun setFontColor(color: Int) {
        setForcegroundColor(color)
    }

    private fun extendRange(spanPart: SpanPart): SpanPart{
        val t = text.toString()
        while (t[spanPart.start - 1] != '\n' && spanPart.start > 0){
            spanPart.start -= 1
        }
        while (t[spanPart.end] != '\n' && spanPart.end < t.length + 1 ){
            spanPart.end += 1
        }
        return spanPart
    }

    /**
     * bold italic
     * @param isSet
     * @param type
     */
    private fun setStyleSpan(isSet: Boolean, type: Int) {
        val fontStyle = FontStyle()
        if (type == Typeface.BOLD) {
            fontStyle.isBold = true
        } else if (type == Typeface.ITALIC) {
            fontStyle.isItalic = true
        }
        setSpan(fontStyle, isSet, StyleSpan::class.java)
    }

    /**
     * underline
     * @param isSet
     */
    private fun setUnderlineSpan(isSet: Boolean) {
        val fontStyle = FontStyle()
        fontStyle.isUnderline = true
        setSpan(fontStyle, isSet, UnderlineSpan::class.java)
    }

    /**
     * Strikethrough
     * @param isSet
     */
    private fun setStreakSpan(isSet: Boolean) {
        val fontStyle = FontStyle()
        fontStyle.isStreak = true
        setSpan(fontStyle, isSet, StrikethroughSpan::class.java)
    }

    /**
     * 设置 字体大小
     * @param size
     */
    private fun setFontSizeSpan(size: Int) {
        var size = size
        if (size == 0) {
            size = FontStyle.NORMAL
        }
        val fontStyle = FontStyle()
        fontStyle.fontSize = size
        setSpan(fontStyle, true, AbsoluteSizeSpan::class.java)
    }

    /**
     * 设置字体颜色
     * @param color
     */
    private fun setForcegroundColor(color: Int) {
        var color = color
        if (color == 0) {
            color = Color.parseColor(FontStyle.BLACK)
        }
        val fontStyle = FontStyle()
        fontStyle.color = color
        setSpan(fontStyle, true, ForegroundColorSpan::class.java)
    }

    /**
     * 通用set Span
     * @param fontStyle
     * @param isSet
     * @param tClass
     * @param <T>
    </T> */
    private fun <T> setSpan(fontStyle: FontStyle, isSet: Boolean, tClass: Class<T>) {
        Log.d("setSpan", "start")
        val start = selectionStart
        val end = selectionEnd

        var mode = EXCLUD_INCLUD_MODE
        val spans = editableText.getSpans(start, end, tClass)


        //获取
        val spanStyles = getOldFontSytles(spans, fontStyle)
        for (spanStyle in spanStyles) {
            if (spanStyle.start < start) {
                if (start == end) {
                    mode = EXCLUD_MODE
                }
                editableText.setSpan(getInitSpan(spanStyle), spanStyle.start, start, mode)
            }
            if (spanStyle.end > end) {
                editableText.setSpan(getInitSpan(spanStyle), end, spanStyle.end, mode)
            }
        }
        if (isSet) {
            if (start == end) {
                mode = INCLUD_INCLUD_MODE
            }
            editableText.setSpan(getInitSpan(fontStyle), start, end, mode)
        }


    }

    /**
     * 获取当前 选中 spans
     * @param spans
     * @param fontStyle
     * @param <T>
     * @return
    </T> */
    private fun <T> getOldFontSytles(spans: Array<T>, fontStyle: FontStyle): List<SpanPart> {
        val spanStyles = ArrayList<SpanPart>()
        for (span in spans) {
            var isRemove = false
            if (span is StyleSpan) {//特殊处理 styleSpan
                val style_type = (span as StyleSpan).style
                if (fontStyle.isBold && style_type == Typeface.BOLD || fontStyle.isItalic && style_type == Typeface.ITALIC) {
                    isRemove = true
                }
            } else {
                isRemove = true
            }
            if (isRemove) {
                val spanStyle = SpanPart(fontStyle)
                spanStyle.start = editableText.getSpanStart(span)
                spanStyle.end = editableText.getSpanEnd(span)

                // ADD 添加FontSize自动扩展的机制
                if (span is AbsoluteSizeSpan) {
                    spanStyle.fontSize = (span as AbsoluteSizeSpan).size

                } else if (span is ForegroundColorSpan) {
                    spanStyle.color = (span as ForegroundColorSpan).foregroundColor
                }
                spanStyles.add(spanStyle)
                editableText.removeSpan(span)
            }
        }
        return spanStyles
    }

    /**
     * 返回 初始化 span
     * @param fontStyle
     * @return
     */
    private fun getInitSpan(fontStyle: FontStyle): CharacterStyle? {
        if (fontStyle.isBold) {
            return StyleSpan(Typeface.BOLD)
        } else if (fontStyle.isItalic) {
            return StyleSpan(Typeface.ITALIC)
        } else if (fontStyle.isUnderline) {
            return UnderlineSpan()
        } else if (fontStyle.isStreak) {
            return StrikethroughSpan()
        } else if (fontStyle.fontSize > 0) {
            return AbsoluteSizeSpan(fontStyle.fontSize, true)
        } else if (fontStyle.color !== 0) {
            return ForegroundColorSpan(fontStyle.color)
        }
        return null
    }

    /**
     * 获取某位置的  样式
     * @param start
     * @param end
     * @return
     */
    private fun getFontStyle(start: Int, end: Int): FontStyle {
        val fontStyle = FontStyle()
        val characterStyles = editableText.getSpans(start, end, CharacterStyle::class.java)
        for (style in characterStyles) {
            if (style is StyleSpan) {
                val type = style.style
                if (type == Typeface.BOLD) {
                    fontStyle.isBold = true
                } else if (type == Typeface.ITALIC) {
                    fontStyle.isItalic = true
                }
            } else if (style is UnderlineSpan) {
                fontStyle.isUnderline = true
            } else if (style is StrikethroughSpan) {
                fontStyle.isStreak = true
            } else if (style is AbsoluteSizeSpan) {
                fontStyle.fontSize = style.size
            } else if (style is ForegroundColorSpan) {
                fontStyle.color = style.foregroundColor
            }
        }
        return fontStyle
    }


    fun setOnSelectChangeListener(onSelectChangeListener: OnSelectChangeListener) {
        this.onSelectChangeListener = onSelectChangeListener
    }

    interface OnSelectChangeListener {
        fun onFontStyleChang(fontStyle: FontStyle)
        fun onSelect(start: Int, end: Int)
    }

    companion object {
        val TAG = "ComplexEditText"
        val EXCLUD_MODE = Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        val EXCLUD_INCLUD_MODE = Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        val INCLUD_INCLUD_MODE = Spannable.SPAN_INCLUSIVE_INCLUSIVE
    }

    val selectionFontStyle:FontStyle
    get() = getFontStyle(selectionStart, selectionEnd)
}



/**
 * 字体样式
 */
open class FontStyle {

    var isBold: Boolean = false
    var isItalic: Boolean = false
    var isUnderline: Boolean = false
    var isStreak: Boolean = false
    var fontSize: Int = 0
    var color: Int = 0

    companion object {

        val NORMAL = 16
        val SMALL = 14
        val BIG = 18

        val BLACK = "#FF212121"
        val GREY = "#FF878787"
        val RED = "#FFF64C4C"
        val BLUE = "#FF007AFF"
    }

}

/**
 * span 样式记录
 */
class SpanPart : FontStyle {
    var start: Int = 0
    var end: Int = 0

    constructor(start: Int, end: Int) {
        this.start = start
        this.end = end
    }

    constructor(fontStyle: FontStyle) {
        this.isBold = fontStyle.isBold
        this.isItalic = fontStyle.isItalic
        this.isStreak = fontStyle.isStreak
        this.isUnderline = fontStyle.isUnderline
        this.fontSize = fontStyle.fontSize
    }
}