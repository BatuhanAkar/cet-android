package com.batuscode.hosbes.views

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
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
import com.batuscode.hosbes.utility.MainActivityVM
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccount(mainActivityVM: MainActivityVM){

    val userVerified by mainActivityVM.userVerified.collectAsState()

    if (userVerified == false){
        AuthenticationForm(mainActivityVM)
    }
    else {
        DeleteScreen()
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationForm(mainActivityVM: MainActivityVM){
    var phoneNumber by remember {
        mutableStateOf(TextFieldValue())
    }
    var isErrorPhoneNumber by remember {
        mutableStateOf(false)
    }
    var deleteAccountButtonEnable by remember {
        mutableStateOf(false)
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
            .fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally)
        {
            // phone textfield
            OutlinedTextField(value = phoneNumber, onValueChange = {newText ->

                if (isErrorPhoneNumber && newText.text.isNotEmpty()) isErrorPhoneNumber = false

                if (newText.text.length <= 11) {
                    Log.d("verifyphonenumber" , "enable now send code button...")

                    phoneNumber = newText
                    if (newText.text.length == 11){
                        Log.d("verifyphonenumber" , "enable now send code button...")
                        deleteAccountButtonEnable = true
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



            Box {

                // kullanicinin hesabını kapatmak için son güncel girişi yaptır ...
                OutlinedButton(
                    onClick = { /*TODO: go button authentication*/

                        val data = hashMapOf(
                            "phoneNumber" to phoneNumber.text
                        )
                        MainActivity.fm.functions.getHttpsCallable("deleteAccount")
                            .call(data)
                            .continueWith { task ->
                                Log.d("deleteaccountHttps" , "silinecek tamamdır ... ")


                                val customToken = task.result?.data as String

                                FirebaseManager.auth.signInWithCustomToken(customToken)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful){
                                            mainActivityVM.updateUserVerified(true)
                                        }
                                    }


                            }
                            .addOnFailureListener {
                                error ->

                                Log.d("deleteaccountHttps" , " hata :: " + error.message)

                            }

                    },
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .width(200.dp),
                    enabled = deleteAccountButtonEnable,


                    ) {
                    Text(text = stringResource(id = R.string.closeaccount))
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteScreen(){

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

        Column( horizontalAlignment = Alignment.CenterHorizontally , verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(inPadding)
                .fillMaxSize()
        ) {
            OutlinedButton(onClick = {
                // TODO: delete account with all user data ...
                MainActivity.fm.deleteAccounWithAllUserData()
            }) {
                Text(text = stringResource(id = R.string.deleteaccount))
            }
        }
    }
}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun DeleteAccountPreview(){
    HoşbeşTheme {
        DeleteScreen()
    }
}