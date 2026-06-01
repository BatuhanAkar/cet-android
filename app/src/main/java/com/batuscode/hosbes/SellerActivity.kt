package com.batuscode.hosbes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.view.SellerActivityView

class SellerActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HoşbeşTheme {
                SellerActivityView()
            }
        }
    }
}