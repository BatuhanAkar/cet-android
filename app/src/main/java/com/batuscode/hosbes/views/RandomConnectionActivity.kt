package com.batuscode.hosbes.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.RandomConnectionActivityViewModel
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.URL

class RandomConnectionActivity:AppCompatActivity(){

    lateinit var mContext:Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this
        val randomConnectionActivityViewModel:RandomConnectionActivityViewModel by viewModels()
        setContent {
            HoşbeşTheme {
                RandomConnection(randomConnectionActivityViewModel =
                randomConnectionActivityViewModel)
            }
        }
        /**
         * Karşılaşma arama aktivitesi başladı ...
         *
         * Rastgele katılımcı lobisine kendini eklemek için isim foto uid getir ...
         * */
        var name = MainActivity.PreferenceManager?.getString("displayName")
        var photo = MainActivity.PreferenceManager?.getString("photoUrl")

        val uid = MainActivity.PreferenceManager?.getuidShared("uid")


        /**
         * Karşılaşma lobisindeki match (karşılaşma) durumunun güncellemelerini dinlemeye başla ...
         * */

        MainActivity.fm.ListenMatch(uid!! , randomConnectionActivityViewModel) // sonra random'ı dinle ...


        /**
         * KARŞILAŞMA BULUNDU LISTEN MATCH DEN TETİKLENEREK GEÇMİŞTEKİ SON KİŞİ GELMİŞTİR ...
         * */

        randomConnectionActivityViewModel.liverandomParticipant.observe(this , Observer {

            if (it != null){

                Log.d("matchstat" , "live par observleendi ... " + it?.toString())


                val intent = Intent(this , RandomActivity::class.java)
                intent.putExtra("session" , "next")
                intent.putExtra("displayName" , it.displayName)
                intent.putExtra("photoUrl" , it.photoUrl)
                intent.putExtra("uid" , it.uid)
                intent.putExtra("match" , it.match)
                intent.putExtra("rm" , it.rm)
                intent.putExtra("tfc" , it.tfc)
                intent.putExtra("outId" , it.outId)
                mContext.startActivity(intent)

                finish()

             /*   if (it?.tfc == true)
                {


                    fromIn = false

                    randomActivityViewModel.update_fromLobby(true)
                    Log.d("matchstat" , "tfc true çıktı ... " )

                    outhandler.postDelayed(
                        {

                            Log.d("matchstat" , "outhandler tetikledi ... " + it?.toString())

                            val room = participant?.rm

                            val userinfo = JitsiMeetUserInfo().apply {
                                displayName = name
                                avatar = URL(photo)
                            }

                            var options = JitsiMeetConferenceOptions.Builder()
                                .setRoom("https://meet.recommyz.com/$room")
                                .setUserInfo(userinfo)
                                .setFeatureFlag("lobby-mode.enabled" , true)
                                .build()

                            Log.d("randomActivity" , "prejoin view eklendi ... ")

                            join(options)

                        } ,
                        800
                    )


                }
                else if (participant?.tfc == false)
                {
                    Log.d("matchstat" , "tfc false çıktı ... " )
                    fromIn = true

                    inhandler.postDelayed(
                        {

                            randomActivityViewModel.update_fromLobby(true)



                            val room = participant?.rm

                            val userinfo = JitsiMeetUserInfo().apply {
                                displayName = name
                                avatar = URL(photo)
                            }

                            var options = JitsiMeetConferenceOptions.Builder()
                                .setRoom("https://meet.recommyz.com/$room")
                                .setUserInfo(userinfo)
                                .build()

                            Log.d("randomActivity" , "prejoin view eklendi ... ")

                            join(options)
                        } ,
                        800
                    )


                } */


            } else {

                Log.d("matchstat" , "live par observleendi ... sonuç boş ... ")
            }
        })


    }
    
}