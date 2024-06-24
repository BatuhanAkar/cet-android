package com.batuscode.hosbes.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.batuscode.hosbes.ui.theme.HoşbeşTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyChats(navController: NavController){

    val backstack = navController.previousBackStackEntry != null
    val tabs = listOf("Eşleşmeler" , "Arkadaşlar")
    var selectedIndex by remember {
        mutableStateOf(0)
    }

    Scaffold ( modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Hoşbeşlerim")
                } ,
                navigationIcon = {
                    if (backstack){
                        OutlinedIconButton(
                            onClick = {
                                navController.popBackStack()
                            } ,
                            border = BorderStroke(0.dp, Color.Transparent)
                        ) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        Column (
            modifier = Modifier
                .padding(innerPadding)
        ) {

            TabRow(selectedTabIndex = selectedIndex) {
                tabs.forEachIndexed { index, tab ->

                    Tab(
                        selected =  selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        }) {
                        Text(text = tab)
                    }

                }
            }
            when (selectedIndex){
                0 -> Matches()
                1 -> Friends()
            }

        }
    }

}

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun MyChatsPreview(){
    val navController = rememberNavController()
    HoşbeşTheme {
        MyChats(navController)
    }
}