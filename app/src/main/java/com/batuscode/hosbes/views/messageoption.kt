package com.batuscode.hosbes.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.batuscode.hosbes.R
import com.batuscode.hosbes.ui.theme.HoşbeşTheme
import com.batuscode.hosbes.utility.MainActivityVM
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageOption(mainActivityVM: MainActivityVM){
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = { /*TODO*/ mainActivityVM.updateShowMessageOption(false) } ,
        sheetState = sheetState ,
        modifier = Modifier.fillMaxSize() ,
        windowInsets = WindowInsets(
            top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() ,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ) ,
        tonalElevation = 10.dp ,
        shape = RectangleShape ,


        ) {

        OptionContent(mainActivityVM = mainActivityVM)

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionContent(mainActivityVM: MainActivityVM){

    Column {

        OutlinedButton(
            onClick = {

                /*TODO: mesajı düzenle butonu */

                // mesajı düzenle bayrağını true ayarla ...

                mainActivityVM.updateEditMessageFlag(true)
                mainActivityVM.updateEditMessageFieldMode(true)
                mainActivityVM.updateShowMessageOption(false)
            } ,
            border = null,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Edit ,
                contentDescription = "" ,
                modifier = Modifier
                    .size(16.dp)
            )


            Text(text = stringResource(id = R.string.edit))
        }

    }


}
/*

@Preview(showBackground = true , showSystemUi = true)
@Composable
fun OptionContentPreview(){
    HoşbeşTheme {
        OptionContent()
    }
}*/
