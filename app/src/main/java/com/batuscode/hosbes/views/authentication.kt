package com.batuscode.hosbes.views

import android.app.Activity
import android.content.Context
import android.graphics.drawable.shapes.Shape
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.utility.FirebaseManager
import com.batuscode.hosbes.utility.MainActivityVM
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import java.util.concurrent.TimeUnit

@Composable
fun Authentication(navController: NavController , mainActivityVM: MainActivityVM){

    /*val mainActivityVM: MainActivityVM = viewModel()*/



    val context = LocalContext.current

    val username by mainActivityVM.username.collectAsState()

    var isErrorPhoneNumber by remember {
        mutableStateOf(false)
    }
    var isVisible by remember {
        mutableStateOf(false)
    }

    var codeSended by remember {
        mutableStateOf(false)
    }

    var checkCode by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        isVisible = true

        Log.d("authfusername" , "username windowdan gelen username :: " + username)
    }

    val visibilityAlpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f ,
        animationSpec = tween(durationMillis = 5000)
    )
    var phoneNumber by remember {
        mutableStateOf(TextFieldValue())
    }
    var verificationCode by remember {
        mutableStateOf(TextFieldValue())
    }

    var codeFieldEnabled by remember {
        mutableStateOf(false)
    }

    var createButtonEnable by remember {
        mutableStateOf(false)
    }

    var sendCodeButtonEnabled by remember {
        mutableStateOf(false)
    }

    var vId by remember {
        mutableStateOf("")
    }


    val signUp by mainActivityVM.signUp.collectAsState()
    val signUpC by mainActivityVM.signUpC.collectAsState()
    val uploadComplated by mainActivityVM.uploadComplated.collectAsState()
    val photoUrl by mainActivityVM.photoUrl.collectAsState()

    val verifyCallback =  object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken)
            Log.d("verifyphonenumber" , "onCodeSent :: " + "verfyId :: " + verificationId
                    + " fores :: " + forceResendingToken)

            vId = verificationId



        }

        override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            Log.d("verifyphonenumber" , "verifcation is complated...")
        }

        override fun onVerificationFailed(p0: FirebaseException) {

            Log.d("verifyphonenumber" , "verifcation is failed :: " + p0.toString() + " " + p0.message)
        }

    }




    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .imePadding()
    ) { innerPadding ->
        if (signUp == true){
            mainActivityVM.updateSession(false)
            Log.d("verifyphonenumber" , "verifcation is Successful...")
            MainActivity.fm.uploadImage(

                MainActivity.fm.getDefaultProfileImage(
                    context , "352002_account_circle_icon.png") ,
                mainActivityVM

            )
        }
        if (uploadComplated == true){
            mainActivityVM.uploadComlated(false)
            MainActivity.fm.writeUserData(photoUrl , mainActivityVM)
        }

        if (signUpC == true){
            mainActivityVM.updateSessionC(false)
            navController.navigate("chat")
        }
        AnimatedVisibility(visible = true ,
            enter = slideInVertically(initialOffsetY = {-40} ) + fadeIn(initialAlpha = 0.3f) ,
            exit = slideOutVertically(targetOffsetY = {-40}) + fadeOut()
        ) {


            Column ( modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(), verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = stringResource(id = R.string.onelaststep) ,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                        fontSize = 40.sp ,
                    )
                )

                // phone textfield
                OutlinedTextField(value = phoneNumber, onValueChange = {newText ->

                    if (isErrorPhoneNumber && newText.text.isNotEmpty()) isErrorPhoneNumber = false

                    if (newText.text.length <= 12) {
                        Log.d("verifyphonenumber" , "enable now send code button...")

                        phoneNumber = newText
                        if (newText.text.length == 12){
                            Log.d("verifyphonenumber" , "enable now send code button...")
                            sendCodeButtonEnabled = true
                        }
                    }
                } ,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    isError = isErrorPhoneNumber,
                    placeholder = { Text(text = stringResource(id = R.string.telefonnumarası)) },
                    singleLine = true ,
                    shape = RoundedCornerShape(12.5.dp),
                    modifier = Modifier.padding(top = 30.dp)
                )

                // verification code textfield
                OutlinedTextField(value = verificationCode, onValueChange = {newText ->

                    // verification code length equal 6 enable button
                    if (newText.text.length <= 6){
                        verificationCode = newText
                        if (verificationCode.text.length == 6){
                            createButtonEnable = true
                        }
                    }

                } ,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    enabled = codeFieldEnabled,
                    isError = isErrorPhoneNumber,
                    placeholder = { Text(text = stringResource(id = R.string.verificationCode)) },
                    singleLine = true ,
                    shape = RoundedCornerShape(12.5.dp),
                    modifier = Modifier.padding(top = 8.5.dp)
                )


                Box {



                    if (!codeSended){

                        // send verification code button

                        OutlinedButton(onClick = { /*TODO: send verification code button*/


                            // run function for verification code sending

                            var options = MainActivity.activity?.let {activity ->
                                PhoneAuthOptions.newBuilder(FirebaseManager.auth)
                                    .setPhoneNumber(phoneNumber.text)
                                    .setTimeout(60L , TimeUnit.SECONDS)
                                    .setActivity(activity)
                                    .setCallbacks(verifyCallback)
                                    .build()
                            }

                            if (options != null) {
                                PhoneAuthProvider.verifyPhoneNumber(options)
                            }

                            codeFieldEnabled = true
                            codeSended = true
                        } ,
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .width(200.dp) ,
                            enabled = sendCodeButtonEnabled
                        ) {
                            Text(text = stringResource(id = R.string.sendverificationcode))
                        }

                    } else {
                        // go button
                        OutlinedButton(onClick = { /*TODO: go button authentication*/

                            val credential = PhoneAuthProvider.getCredential(vId , verificationCode.text)

                            FirebaseManager.auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful){

                                        // firstly update fm.current user && update profile for display name

                                        val profileChangeReguest = userProfileChangeRequest{
                                            displayName = username
                                        }

                                        task.result.user?.updateProfile(profileChangeReguest)

                                        FirebaseManager.currentUser = task.result.user


                                        //save uid on sharedpreference

                                        Log.d("verifyphonenumber" , "uid :: " + FirebaseManager.currentUser?.uid.toString())

                                        val uid = FirebaseManager.currentUser?.uid.toString()

                                        MainActivity.PreferenceManager?.saveuid(key = "uid" , value = uid)

                                         /**
                                          * after upload profile image on storage.. everything needed in FM
                                          * &&&
                                          * write user information on firestore...
                                          * */

                                        MainActivity.PreferenceManager?.saveSession(key = "session" , true)
                                        mainActivityVM.updateSession(true)



                                    } else {

                                        Log.d("verifyphonenumber" , "verifcation not Successful...")
                                    }
                                }
                        } ,
                            modifier = Modifier
                                .padding(top = 15.dp)
                                .width(200.dp),
                            enabled = createButtonEnable ,


                            ) {
                            Text(text = stringResource(id = R.string.signin))
                        }
                    }



                }

            }

        }
    }



}



fun VerifyAuthPhoneNumber(phoneNumber:String, mainActivityVM: MainActivityVM){

    Log.d("verifyphonenumber" , "verify fun is run...")



}


fun validatePhoneNumber(x:String) : Boolean{
    // x -> phoneNumber !!!
    Log.d("validatingPhoneNumber" , "isGlobal? :: " + PhoneNumberUtils.isGlobalPhoneNumber(x).toString())
    Log.d("validatingPhoneNumber" , "formatNumber :: " + PhoneNumberUtils.formatNumber(x , "TR"))
   return PhoneNumberUtils.isGlobalPhoneNumber(x)
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun AuthenticationPreview(){
    val navController = rememberNavController()
    val mainActivityVM:MainActivityVM = viewModel()
    Authentication(navController , mainActivityVM = mainActivityVM)
}