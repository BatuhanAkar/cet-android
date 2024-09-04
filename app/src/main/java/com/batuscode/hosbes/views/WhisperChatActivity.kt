package com.batuscode.hosbes.views

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Observer
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.models.Whisper
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.WhisperChatActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WhisperChatActivity:AppCompatActivity(){
    val Cscope = CoroutineScope(Dispatchers.Default)
    var loadMoreChat by mutableStateOf(false)
    lateinit var mWhisperChatActivityViewModel: WhisperChatActivityViewModel


    lateinit var whisperItem:Whisper

    override fun onStop() {
        super.onStop()

        MainActivity.mMainActivityVM.updateInWhisper(false) // fısıltıda mesaj seçeneklerinin kontrolü için ...

        MainActivity.fm.detachWhisperChatListener(whisperItem?.wid!!)

        Cscope.cancel()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoşbeşTheme {

                val whisperChatActivityViewModel:WhisperChatActivityViewModel by viewModels()
                mWhisperChatActivityViewModel = whisperChatActivityViewModel

                whisperChatActivityViewModel.finish.observe(this , Observer {
                    if (it == true){
                        finish()
                    }
                })

                WhisperChat(mainActivityVM = MainActivity.mMainActivityVM ,
                    chatViewModel = MainActivity.mChatViewModel ,
                    whisperChatActivityViewModel = whisperChatActivityViewModel)
            }
        }
        val wdisplayName:String = intent.getStringExtra("wdisplayName").toString()
        val wphotoUrl:String = intent.getStringExtra("wphotoUrl").toString()
        val wuid:String = intent.getStringExtra("wuid").toString()
        val wid:String = intent.getStringExtra("wid").toString()
        var lm:String = intent.getStringExtra("lm").toString()
        var lt:Long = intent.getLongExtra("lt" , 0L)
        var lwuid:String = intent.getStringExtra("lwuid").toString()
        var readed:Boolean = intent.getBooleanExtra("readed" , false)

        whisperItem = Whisper(wdisplayName, wphotoUrl, wuid, wid, lm, lt, lwuid, readed)
        Cscope.launch {
            delay(900000)
            MainActivity.mChatViewModel.refreshChat() // sohbeti sıfırla ...

            MainActivity.mMainActivityVM.updateInWhisper(true) // fısıltıda mesaj seçeneklerinin kontrolü için ...
            MainActivity.PreferenceManager?.saveSession("inPrivateRoom" , false)

            MainActivity.mMainActivityVM.connectChannel("W") // kanal id güncelle ...

            // burda ilk fısıltı mı bak ...

            MainActivity.fm.detachWhisperChatListener(whisperItem?.wid!!)
            MainActivity.fm.pullWhisperChat(whisperItem?.wid!! , loadMoreChat!! , true)

            setReaded(whisperItem = whisperItem!!)


        }

        MainActivity.mMainActivityVM.LoadMoreChat.observe(this , Observer {
            loadMoreChat = it!!
        })

        Log.d("WhisperChat" , "ON_CREATE")
        MainActivity.mChatViewModel.refreshChat() // sohbeti sıfırla ...

        MainActivity.mMainActivityVM.updateInWhisper(true) // fısıltıda mesaj seçeneklerinin kontrolü için ...
        MainActivity.PreferenceManager?.saveSession("inPrivateRoom" , false)
        MainActivity.mMainActivityVM.connectChannel("W") // kanal id güncelle ...

        // burda ilk fısıltı mı bak ...

        MainActivity.fm.detachWhisperChatListener(whisperItem?.wid!!)
        MainActivity.fm.pullWhisperChat(whisperItem?.wid!! , loadMoreChat!! , false)

        setReaded(whisperItem = whisperItem!!)


    }


    fun setReaded(whisperItem: Whisper){

        // fısıltının okunup okunmadığını al son mesaj karşı tarafa ait ise okundu olarak güncelle ...

        val remoteId = whisperItem.wuid
        val lastId = whisperItem.lwuid

        if ((remoteId == lastId) && whisperItem.readed == false){

            // okundu işaretle ...

            MainActivity.fm.updateReaded(whisperItem)

        }

    }
}