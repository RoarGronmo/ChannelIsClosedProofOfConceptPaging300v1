package no.rogo.channelisclosedproofofconceptpaging300v1.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import no.rogo.channelisclosedproofofconceptpaging300v1.R
import no.rogo.channelisclosedproofofconceptpaging300v1.services.ForegroundOnlyLocationService
import no.rogo.channelisclosedproofofconceptpaging300v1.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    private val TAG by lazy { this::class.java.simpleName }

    private var foregroundOnlyLocationServiceBound = false

    private var foregroundOnlyLocationService: ForegroundOnlyLocationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
    }
}