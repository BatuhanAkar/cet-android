package com.batuscode.hosbes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.AuthViewModel
import com.batuscode.hosbes.utility.ChatViewModel
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.utility.GlideApp
import com.batuscode.hosbes.utility.MainActivityVM
import com.batuscode.hosbes.utility.ParticipantsViewModel
import com.batuscode.hosbes.utility.PreferenceManager
import com.batuscode.hosbes.utility.SessionService
import com.batuscode.hosbes.utility.WhisperViewModel
import com.batuscode.hosbes.utility.mainactivitylife
import com.batuscode.hosbes.views.Authentication
import com.batuscode.hosbes.views.Chat
import com.batuscode.hosbes.views.DeleteAccount
import com.batuscode.hosbes.views.EntryScreen
import com.batuscode.hosbes.views.OutCallActivity
import com.batuscode.hosbes.views.PrivateRoomChat
import com.batuscode.hosbes.views.PrivateRooms
import com.batuscode.hosbes.views.SelectUsername
import com.batuscode.hosbes.views.SplashScreen
import com.batuscode.hosbes.views.Whisper
import com.batuscode.hosbes.views._WhisperChat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.giphy.sdk.core.BuildConfig
import com.giphy.sdk.ui.Giphy
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() , mainactivitylife {


    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var context:Context
        var activity: Activity? = null
        val fm = FirebaseManager()
        var navigate:NavController? = null
        var PreferenceManager:PreferenceManager? = null
        lateinit var permissionLauncher:ManagedActivityResultLauncher<String,Boolean>
        lateinit var mMainActivityVM: MainActivityVM
        lateinit var mChatViewModel: ChatViewModel
        lateinit var mainactivitylife: mainactivitylife
    }


    var handler = Handler(Looper.getMainLooper())

    val FATG:String = "authentication00"

    lateinit var authStateListener: FirebaseAuth.AuthStateListener


    override fun onDestroy() {
        super.onDestroy()



        val serviceIntent = Intent(this , SessionService::class.java)
        startService(serviceIntent)

        Log.d(FATG , "onDetach...Main")

        if (::authStateListener.isInitialized){

            FirebaseManager.auth.removeAuthStateListener(authStateListener)
        }

        fm.detachListenerICC()
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            HoşbeşTheme{

                val authViewModel: AuthViewModel = viewModel()
                val mainActivityVM: MainActivityVM by viewModels()
                val chatViewModel:ChatViewModel by viewModels()
                val whisperViewModel:WhisperViewModel by viewModels()
                mainActivityVM.updateSelectedChannel("Hoşbeş")

                val appUpdated by mainActivityVM.AppUpdated.collectAsState()

                mainactivitylife = this

                fm.loadMoreChat = false
                mChatViewModel = chatViewModel

                context = this
                Giphy.configure(context , "5eryANGrljO1uXPSf7GLEhUAU3q8zF1k")


                mainActivityVM.setFragmentManager(supportFragmentManager)

                PreferenceManager = PreferenceManager(context)

                var session = PreferenceManager?.getSession("session")

                var uid = PreferenceManager?.getuidShared("uid")

                Log.d("prefemngr" , "uid :: " + uid)

                activity = this



                val historyCallItem by mainActivityVM.Historycalls.collectAsState()

                mMainActivityVM = mainActivityVM

                val incall by mainActivityVM.incall.collectAsState()

                val participantsViewModel:ParticipantsViewModel by viewModels()
                fm.whisperViewModel = whisperViewModel

                val currentUser by authViewModel.currentUser.collectAsState()

                val navController = rememberNavController()

                navigate = navController

                var splash by remember {
                    mutableStateOf(true)
                }


                if (session == true){




                    authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        run {
                            Log.d(FATG , "statelistenera girdi...")
                            firebaseAuth.currentUser?.let {

                                authViewModel.updateUser(it)

                                FirebaseManager.currentUser = firebaseAuth.currentUser

                                var displayName = FirebaseManager.currentUser?.displayName
                                var photo = FirebaseManager.currentUser?.photoUrl.toString()

                                PreferenceManager?.saveString("displayName" , displayName!!)
                                PreferenceManager?.saveString("photoUrl" , photo!!)

                                mainActivityVM.updateDisplayName(displayName!!)
                                var Pphoto = FirebaseManager.currentUser?.photoUrl.toString()

                                GlideApp.with(context)
                                    .asBitmap()
                                    .load(Pphoto)
                                    .into(object : CustomTarget<Bitmap>() {
                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            var imageBitmap = resource.asImageBitmap()

                                            mainActivityVM.updatePhoto(imageBitmap)
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {
                                        }

                                    })
                                fm.updateSessionStatus()
                                fm.listenICC(it.uid , mainActivityVM)




                                Log.d(FATG , "kullanici bos degil...")
                            }
                        }
                    }

                    FirebaseManager.auth.addAuthStateListener(authStateListener)
                }


                NavHost(navController = navController, startDestination = if (currentUser != null) "entryscreen" else "selectUsername") {

                    composable("selectUsername"){
                        SelectUsername(navController = navController , mainActivityVM = mainActivityVM)
                    }

                    composable("auth"){
                        Authentication(navController , mainActivityVM = mainActivityVM)
                    }

                    composable("entryscreen"){
                        EntryScreen(mainActivityVM = mainActivityVM)
                    }

                    composable("chat"){
                        Chat(mainActivityVM , chatViewModel)
                    }


                    composable("privaterooms"){
                        Log.d("firedb" , "navigated...")

                        PrivateRooms(mainActivityVM = mainActivityVM)
                    }

                    composable("privateroomchat"){
                        PrivateRoomChat(mainActivityVM = mainActivityVM , chatViewModel , participantsViewModel)
                    }

                    composable("whisper"){
                        Whisper(mainActivityVM = mainActivityVM, whisperViewModel = whisperViewModel)
                    }

                    composable("_whisperchat"){
                        _WhisperChat(mainActivityVM = mainActivityVM, chatViewModel = chatViewModel)
                    }

                    composable("deleteaccount"){
                        DeleteAccount(mainActivityVM , mainactivitylife = mainactivitylife)
                    }

                }





                if (incall == true){

                    /**
                     * arama geldiği zaman arama geçmişinin en son öğesini getir ...
                     * */

                    fm.getLastCallHistory(uid!! , mainActivityVM)

                    // navigate?.navigate("ICC")

                    val historycalls by mainActivityVM.Historycalls.collectAsState()

                    if (historycalls != null && historycalls?.type?.equals("out") == true){

                        val intent = Intent(this , OutCallActivity::class.java)
                        intent.putExtra("type" , "ICC")
                        intent.putExtra("wuid" , historyCallItem?.uid)
                        intent.putExtra("wphotoUrl" , historyCallItem?.photoUrl)
                        intent.putExtra("wdisplayName" , historyCallItem?.displayName)
                        startActivity(intent)
                    }


                }



            }
        }

    }

    override fun restart() {
        val intent = Intent(this , MainActivity::class.java)
        finish()
        startActivity(intent)
    }

}


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun GreetingPreview() {
    HoşbeşTheme {
/*
        Authentication()
*/
    }
}