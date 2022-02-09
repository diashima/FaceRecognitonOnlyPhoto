package kz.tengrilab.fr_cppr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
    }
}

fun Fragment.showSnack(text: String) {
    Snackbar.make(requireView(), text, Snackbar.LENGTH_LONG).show()
}