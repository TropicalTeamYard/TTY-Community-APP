package tty.community.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import tty.community.pages.activity.LoginActivity

object AlertDialogUtil {

    fun registerSuccessDialog(context: Context, nickname: String) {
        val ac = TextView(context)
        ac.height = 120
        ac.setLines(2)
        ac.text = "注册成功"
        ac.textSize = 16f
        ac.setPadding(64, 64, 64, 64)
        AlertDialog.Builder(context)
            .setTitle("欢迎 $nickname 加入TTY Community!")
            .setView(ac)
            .setPositiveButton("跳转登录界面") { _, _ ->
                val intent = Intent(context, LoginActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
            .setCancelable(false)
            .show()

    }

}