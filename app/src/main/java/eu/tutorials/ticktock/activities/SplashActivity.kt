
package eu.tutorials.ticktock.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import eu.tutorials.ticktock.databinding.ActivitySplashBinding
import eu.tutorials.ticktock.firebase.FireStoreClass

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    // activity variables
    private var binding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val typeFace: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")

        binding?.tvAppName?.typeface = typeFace

        Handler().postDelayed({
            val currentUserID = FireStoreClass().getCurrentUserID()
            if(currentUserID.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            finish()
        }, 2500)
    }
}