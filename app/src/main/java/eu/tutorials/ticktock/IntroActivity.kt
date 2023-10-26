package eu.tutorials.ticktock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import eu.tutorials.ticktock.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    // activity variables
    private var binding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        // set flags
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // setting up sign up/in btns
        binding?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}