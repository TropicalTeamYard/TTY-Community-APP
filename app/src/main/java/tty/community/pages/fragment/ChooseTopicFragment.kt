package tty.community.pages.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_choose_topic.*

import tty.community.R
import tty.community.model.Params
import tty.community.model.Shortcut
import tty.community.model.Topic
import tty.community.network.AsyncNetUtils
import tty.community.util.CONF
import tty.community.util.Message
import java.lang.Exception


class ChooseTopicFragment : DialogFragment() {

    interface OnTopicChangeListener {
        var topic: Topic.Outline
        fun onTopicChange(topic: Topic.Outline)
    }
    private lateinit var listener: OnTopicChangeListener

    override fun onAttach (context: Context) {
        super.onAttach(context)
        if (context is OnTopicChangeListener) {
            this.listener = context
        } else {
            throw Exception("please let activity implements OnTopicChangeListener")
        }
    }

    override fun onStart() {
        super.onStart()
        val dm = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(dm)
        dialog!!.window!!.setLayout(dm.widthPixels, dialog!!.window!!.attributes.height)
        val params: WindowManager.LayoutParams = dialog!!.window!!.attributes
        params.gravity= Gravity.CENTER
        dialog!!.window!!.attributes=params
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_choose_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        choose_topic_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val name = p0.toString()
                if (name.isBlank()) return

                AsyncNetUtils.post(CONF.API.topic.similar, Params.similarTopic(name), object: AsyncNetUtils.Callback {
                    fun onFail(msg: String): Int {
                        Log.e(TAG, msg)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        return 1
                    }

                    fun onSuccess(topics: ArrayList<Topic.Outline>): Int {
                        Log.d(TAG, "success, size${topics.size}")
                        return 0
                    }

                    override fun onResponse(result: String?): Int {
                        val message: Message.MsgData<ArrayList<Topic.Outline>>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<ArrayList<Topic.Outline>>>(){})
                        return if (message != null) {
                            when(message.shortcut) {
                                Shortcut.OK -> onSuccess(message.data)
                                else -> onFail("shortcut异常")
                            }
                        } else {
                            onFail("解析异常")
                        }
                    }

                    override fun onFailure(msg: String): Int {
                        return onFail("网络异常")
                    }

                })
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })
    }

    companion object {
        const val TAG = "ChooseTopicFragment"
    }

}
