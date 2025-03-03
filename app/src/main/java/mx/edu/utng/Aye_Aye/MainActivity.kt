package mx.edu.utng.aye_ayeabts

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import mx.edu.utng.aye_ayeabts.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        splashScreen.setKeepOnScreenCondition{ true }

        Thread.sleep(1500)
        val intent = Intent(this, TareasActivity:: class.java)
        startActivity(intent)
        finish()
    }
}