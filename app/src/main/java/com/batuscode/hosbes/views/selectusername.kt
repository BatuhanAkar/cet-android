package com.batuscode.hosbes.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.viewmodel.MainActivityVM

@Composable
fun SelectUsername(navController: NavController , mainActivityVM: MainActivityVM){

   /* val mainActivityVM: MainActivityVM = viewModel()
*/
    var isVisible by remember {
        mutableStateOf(false)
    }

    var isEnabled by remember {
        mutableStateOf(false)
    }

    var isErrorUsername by remember {
        mutableStateOf(false)
    }
    var username by remember {
        mutableStateOf(TextFieldValue())
    }

    val visibilityAlpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f ,
        animationSpec = tween(durationMillis = 5000)
    )
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold (
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        innerPadding ->

        AnimatedVisibility(
            visible = isVisible ,
            enter = slideInVertically(initialOffsetY = {-40} ) + fadeIn(initialAlpha = 0.3f) ,
            exit = slideOutVertically(targetOffsetY = {-40}) + fadeOut()
            ) {

            Column ( modifier = Modifier
                .padding(innerPadding)
                .alpha(visibilityAlpha)
                .fillMaxSize(), verticalArrangement = Arrangement.Center , horizontalAlignment = Alignment.CenterHorizontally) {

                Text(text = stringResource(id = R.string.app_name) ,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                        fontSize = 80.sp
                    ) ,
                    modifier = Modifier.padding(bottom = 40.dp , top = 20.dp)
                )


                Text(
                    text = stringResource(id = R.string.selectuniqueusername) ,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.pacifico_regular)),
                        fontSize = 20.sp
                    ) ,
                    modifier = Modifier.padding(bottom = 8.5.dp)
                )

                    // username textfield
                OutlinedTextField(value = username, onValueChange = {newText ->

                    if (isErrorUsername && newText.text.isNotEmpty()) isErrorUsername = false

                    if (newText.text.length <= 17) {
                        isEnabled = true
                        username = newText

                    }

                } ,
                    isError = isErrorUsername,
                    placeholder = { Text(text = stringResource(id = R.string.kullaniciadi)) },
                    singleLine = true ,
                    shape = RoundedCornerShape(12.5.dp),
                    modifier = Modifier
                        .padding(top = 8.5.dp)
                )

// go button

                OutlinedButton(onClick = {

                    // check username textfield is empty ??

                    if (username.text.isNotEmpty()){
                        mainActivityVM.updateUsername(username.text)
                        navController.navigate("auth")
                    } else{
                        isErrorUsername = true
                    }

                } ,
                    modifier = Modifier
                        .padding(bottom = 50.dp, top = 20.dp)
                        .width(200.dp) ,
                    enabled = isEnabled
                ) {
                    Text(text = stringResource(id = R.string.devamet))
                }




            }


        }
    }

}



@Preview(showBackground = true , showSystemUi = true)
@Composable
fun SelectUsernamePreview(){
    val navController = rememberNavController()
    val mainActivityVM: MainActivityVM = viewModel()
    HoşbeşTheme {
        SelectUsername(navController = navController , mainActivityVM = mainActivityVM)
    }
}