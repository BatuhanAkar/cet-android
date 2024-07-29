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
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
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
import com.batuscode.hosbes.views.Authentication
import com.batuscode.hosbes.views.Chat
import com.batuscode.hosbes.views.PrivateRoomChat
import com.batuscode.hosbes.views.PrivateRooms
import com.batuscode.hosbes.views.SelectUsername
import com.batuscode.hosbes.views.SplashScreen
import com.batuscode.hosbes.views.Whisper
import com.batuscode.hosbes.views.WhisperChat
import com.batuscode.hosbes.views._WhisperChat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {


    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var context:Context
        var activity: Activity? = null
        val fm = FirebaseManager()
        var navigate:NavController? = null
        var PreferenceManager:PreferenceManager? = null
        lateinit var permissionLauncher:ManagedActivityResultLauncher<String,Boolean>
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

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoşbeşTheme {



                context = LocalContext.current


                permissionLauncher= rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {

            }
                PreferenceManager = PreferenceManager(context)

                var session = PreferenceManager?.getSession("session")

                var uid = PreferenceManager?.getuidShared("uid")

                Log.d("prefemngr" , "uid :: " + uid)

                activity = this

                FirebaseManager.auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)

                val authViewModel: AuthViewModel = viewModel()
                val mainActivityVM: MainActivityVM by viewModels()
                val chatViewModel:ChatViewModel by viewModels()
                val whisperViewModel:WhisperViewModel by viewModels()

                val participantsViewModel:ParticipantsViewModel by viewModels()
                fm.whisperViewModel = whisperViewModel

                val currentUser by authViewModel.currentUser.collectAsState()

                val navController = rememberNavController()

                navigate = navController

                var splash by remember {
                    mutableStateOf(true)
                }

                val channelId by mainActivityVM.channelId.collectAsState()



                LaunchedEffect(Unit) {


                    if (session == true){
                        mainActivityVM.connectChannel("C1")
                        mainActivityVM.updateSelectedChannel("Hoşbeş")

                        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                            run {
                                Log.d(FATG , "statelistenera girdi...")
                                firebaseAuth.currentUser?.let {

                                    authViewModel.updateUser(it)


                                    FirebaseManager.currentUser = firebaseAuth.currentUser

                                    var displayName = FirebaseManager.currentUser?.displayName
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
                                                TODO("Not yet implemented")
                                            }

                                        })
                                    fm.updateSessionStatus()




                                    Log.d(FATG , "kullanici bos degil...")
                                }
                            }
                        }

                        FirebaseManager.auth.addAuthStateListener(authStateListener)
                    }

                }



                handler.postDelayed({
                    splash = false
                                    },5000)

                if (!splash){



                    NavHost(navController = navController, startDestination = if (currentUser != null) "chat" else "selectUsername") {

                        composable("selectUsername"){
                            SelectUsername(navController = navController , mainActivityVM = mainActivityVM)
                        }

                        composable("auth"){
                            Authentication(navController , mainActivityVM = mainActivityVM)
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
                        
                        composable("whisperchat"){
                            WhisperChat(mainActivityVM = mainActivityVM, chatViewModel = chatViewModel)
                        }
                        
                        composable("_whisperchat"){
                            _WhisperChat(mainActivityVM = mainActivityVM, chatViewModel = chatViewModel)
                        }
                    }

                } else {

                    SplashScreen()
                }







            }
        }
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