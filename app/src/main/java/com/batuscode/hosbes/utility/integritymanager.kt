package com.batuscode.hosbes.utility

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.tasks.Task
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import java.util.UUID

class IntegrityManager{

    fun getintegritytoken(context:Context){
        Log.d("myintegritytoken" , "token alma çalıştı ... ")


        val nonce: String = UUID.randomUUID().toString()

        // Create an instance of a manager.
        val integrityManager =
            IntegrityManagerFactory.create(context)

        // Request the integrity token by providing a nonce.
        val integrityTokenResponse: Task<IntegrityTokenResponse> =
            integrityManager.requestIntegrityToken(
                IntegrityTokenRequest.builder()
                    .setCloudProjectNumber(738393464985)
                    .setNonce(nonce)
                    .build())

        integrityTokenResponse.addOnSuccessListener {
            response ->

            Log.d("myintegritytoken" , " response :: ${response.token()}")
        }

        integrityTokenResponse.addOnFailureListener {
            error ->
            Log.d("myintegritytoken" , " error :: " + error.message)
        }


    }






}