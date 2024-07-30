package com.batuscode.hosbes.views

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.FirebaseManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccount(){
    var phoneNumber by remember {
        mutableStateOf(TextFieldValue())
    }
    var verificationCode by remember {
        mutableStateOf(TextFieldValue())
    }

    var codeFieldEnabled by remember {
        mutableStateOf(false)
    }
    var sendCodeButtonEnabled by remember {
        mutableStateOf(false)
    }
    var isErrorPhoneNumber by remember {
        mutableStateOf(false)
    }
    var codeSended by remember {
        mutableStateOf(false)
    }
    var vId by remember {
        mutableStateOf("")
    }
    var deleteAccountButtonEnable by remember {
        mutableStateOf(false)
    }
    val verifyCallback =  object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken)
            Log.d("verifyphonenumber" , "onCodeSent :: " + "verfyId :: " + verificationId
                    + " fores :: " + forceResendingToken)


            codeFieldEnabled = true
            codeSended = true
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
            .imePadding() ,
        topBar = {
            TopAppBar(
                title = {

                    Text(text = stringResource(id = R.string.deleteaccount)) // degistir ...

                } ,
                navigationIcon = {

                    Icon(
                        imageVector = Icons.Filled.ArrowBack ,
                        contentDescription = "" ,
                        modifier = Modifier
                            .clickable {
                                MainActivity.navigate?.popBackStack()
                            }
                    )
                }
            )
        }
    ) { inPadding ->

        Column ( modifier = Modifier
            .padding(inPadding)
            .fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally){
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
                        deleteAccountButtonEnable = true
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

                        Log.d("authtrying" , phoneNumber.text)

                        Log.d("authtrying" , verificationCode.text)

                        // run function for verification code sending

                        var options = MainActivity.activity?.let { activity ->
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
                    } ,
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .width(200.dp) ,
                        enabled = sendCodeButtonEnabled
                    ) {
                        Text(text = stringResource(id = R.string.sendverificationcode))
                    }

                } else {


                    // kullanicinin hesabını kapatmak için son güncel girişi yaptır ...
                    OutlinedButton(onClick = { /*TODO: go button authentication*/

                        val credential = PhoneAuthProvider.getCredential(vId , verificationCode.text)

                        FirebaseManager.auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful){




                                } else {
                                    Log.d("verifyphonenumber" , "verifcation not Successful...")
                                }
                            }
                    } ,
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .width(200.dp),
                        enabled = deleteAccountButtonEnable ,


                        ) {
                        Text(text = stringResource(id = R.string.closeaccount))
                    }
                }



            }

        }
    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun DeleteAccountPreview(){
    HoşbeşTheme {
        DeleteAccount()
    }
}