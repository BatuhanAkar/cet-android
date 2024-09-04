package com.batuscode.hosbes.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.giphy.sdk.ui.Giphy
import com.giphy.sdk.ui.views.GiphyDialogFragment

class GiphyActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Giphy.configure(this , "5eryANGrljO1uXPSf7GLEhUAU3q8zF1k")

        setContent {
            HoşbeşTheme {
                GiphyDialogFragment.newInstance().show(supportFragmentManager, "giphyfragment")

            }
        }
    }
}