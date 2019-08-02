package tty.community.pages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import tty.community.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    companion object {
        const val TAG = "MainActivity"
    }


}
