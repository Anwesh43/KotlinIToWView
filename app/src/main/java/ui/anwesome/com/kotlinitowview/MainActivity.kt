package ui.anwesome.com.kotlinitowview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.itowview.IToWView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        IToWView.create(this)
    }
}
