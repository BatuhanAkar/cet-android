package com.batuscode.hosbes.utility

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.models.Calls
import com.batuscode.hosbes.models.Message
import com.batuscode.hosbes.models.Participnat
import com.batuscode.hosbes.models.PrivateRoom
import com.batuscode.hosbes.models.RandomParticipant
import com.batuscode.hosbes.models.User
import com.batuscode.hosbes.models.Whisper
import com.batuscode.hosbes.utility.FirebaseManager.Companion.P1
import com.batuscode.hosbes.utility.FirebaseManager.Companion.firestore
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import kotlin.random.Random


class FirebaseManager {

    val functions:FirebaseFunctions = Firebase.functions
    companion object {


        val auth: FirebaseAuth = Firebase.auth

        var currentUser:FirebaseUser? = null

        @SuppressLint("StaticFieldLeak")
        val firestore:FirebaseFirestore = FirebaseFirestore.getInstance()
        val prvRoomRef:CollectionReference = firestore.collection("prvRoom")
        val usersRef:CollectionReference = firestore.collection("users")

        var storage:FirebaseStorage = FirebaseStorage.getInstance()

        var storageRef = storage.reference

        val C1:DatabaseReference = Firebase.database("https://hosbes-29a99-default-rtdb.europe-west1.firebasedatabase.app/").getReference("chat")
        val C2:DatabaseReference = Firebase.database("https://maviboncuk.europe-west1.firebasedatabase.app/").getReference("chat")

        val P1:DatabaseReference = Firebase.database("https://privaterooms.europe-west1.firebasedatabase.app/").getReference()

        val W:DatabaseReference = Firebase.database("https://whispers-552e7.europe-west1.firebasedatabase.app/").getReference()
        val W_C:DatabaseReference = Firebase.database("https://whispers-chat.europe-west1.firebasedatabase.app/").getReference()
        val Random_History = Firebase.database("https://random-27a8c.europe-west1.firebasedatabase.app/").getReference()


        val random:CollectionReference = firestore.collection("random")
    }


    lateinit var listenMatch:ListenerRegistration
    lateinit var RlistenMatch:ListenerRegistration


    lateinit var chatQuery:Query
    lateinit var whisoerChatQuery:Query
    lateinit var PRChatQuery:Query

    lateinit var chatViewModel: ChatViewModel

    lateinit var whisperViewModel: WhisperViewModel

    var loadMoreChat: Boolean = false

    /*TODO: whisper event listener initialize*/
    val whisperEventListener: ChildEventListener = object :ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            Log.d("whisperList" , "fısıltı geldi...")

            val whisper = snapshot.getValue<Whisper>()
            whisperViewModel.pushWhisper(whisper!!)

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            Log.d("whisperList" , "fısıltı değişti...")

            val whisper = snapshot.getValue<Whisper>()
            whisperViewModel.changedWhisper(whisper!!)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

            val whisper = snapshot.getValue<Whisper>()
            whisperViewModel.removedWhisper(whisper!!)
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }


    }

    val chatEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {


            if (loadMoreChat){
                val message:Message = snapshot.getValue<Message>()!!

                var lastChatId = MainActivity.PreferenceManager?.getuidShared("lastChatId")


                Log.d("message" , "chat :: " + message.message)
//
//                when{
//                    message.type.equals("text") -> {
//                        if (!message.messageId.equals(lastChatId)){
//                            chatViewModel.pushChat(message , loadMoreChat)
//                        }
//                    }
//                }
                if (!message.messageId.equals(lastChatId)){
                    chatViewModel.pushChat(message , loadMoreChat)
                }

            } else {

                val message:Message = snapshot.getValue<Message>()!!

                Log.d("message" , "chat :: " + message.message)
//
//                when{
//                    message.type.equals("text") -> {
//                        chatViewModel.pushChat(message , loadMoreChat)
//                    }
//                }
                chatViewModel.pushChat(message , loadMoreChat)

            }


        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            val message:Message = snapshot.getValue<Message>()!!
            Log.d("channelDocumentChangeType" , "modified chat :: " + message.message)

            chatViewModel.messageChanged(message)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

            val message:Message = snapshot.getValue<Message>()!!
            Log.d("channelDocumentChangeType" , "removed chat :: " + message.message)

            chatViewModel.messageRemmoved(message)
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }


    }

    fun handleJoinRoom( mainActivityVM: MainActivityVM , type:String , room: PrivateRoom){
        val uid = currentUser?.uid
        val displayName = currentUser?.displayName
        val photoUrl = currentUser?.photoUrl.toString()

        val participant = Participnat(
            displayName, photoUrl, uid
        )
        val prvRoomDocRef:DocumentReference = firestore.collection("prvRoom").document(room.roomId!!)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(prvRoomDocRef)

            if (type.equals("joined")){

                val activePar = snapshot.getLong("activePar")!! + 1

                P1.child("participants")
                    .child(room.roomId)
                    .child(uid!!).setValue(participant)


                transaction.update(prvRoomDocRef , "activePar" , activePar)
            } else{
                val activePar = snapshot.getLong("activePar")!! - 1

                P1.child("participants")
                    .child(room.roomId)
                    .child(uid!!).setValue(null)
                transaction.update(prvRoomDocRef , "activePar" , activePar)
            }

        }.addOnSuccessListener {
            Log.d("updateactivepar" , "success...")
        }
    }

    var handler = android.os.Handler(Looper.getMainLooper())

    lateinit var listenPrivateRooms: ListenerRegistration


    fun deletePrivateRoom(room: PrivateRoom){
        firestore.collection("prvRoom").document(room.roomId!!).delete()
            .addOnSuccessListener {

            }

        P1.child(room.roomId!!)
            .setValue(null)

        P1.child("participants")
            .child(room.roomId)
            .setValue(null)

    }

    fun pullPrivateRoomParticipants(privateRoom: PrivateRoom , participantsViewModel: ParticipantsViewModel){
        P1.child("participants")
            .child(privateRoom.roomId!!)
            .get()
            .addOnSuccessListener {
                it.children.forEach { dataSnapshot ->
                    val participnat = dataSnapshot.getValue(Participnat::class.java)
                    Log.d("participant" , " eklendi ... " + participnat?.displayName)

                    participantsViewModel.pushParticipants(participnat!!)
                }
            }
    }

    fun pullPrivateRooms(privateRoomsViewModel: PrivateRoomsViewModel , mainActivityVM: MainActivityVM){

      listenPrivateRooms = prvRoomRef.addSnapshotListener { snapshots , e ->

          if (snapshots != null){



              for (dc in snapshots!!.documentChanges){

                  when (dc.type) {
                      DocumentChange.Type.ADDED -> {


                          Log.d("documentType" , "added... " + dc.document.get("roomName"))

                          val room = dc.document.toObject(PrivateRoom::class.java)

                          privateRoomsViewModel.pushRoom(room)


                      }
                      DocumentChange.Type.MODIFIED -> {

                          Log.d("documentType" , "MODIFIED... " + dc.document.get("roomName"))

                          val room = dc.document.toObject(PrivateRoom::class.java)
                          Log.d("documentType" , "MODIFIED... activePar " + dc.document.get("activePar"))

                          privateRoomsViewModel.modifiedRoom(room)
                      }

                      DocumentChange.Type.REMOVED -> {
                          val room = dc.document.toObject(PrivateRoom::class.java)
                          val prvRoomId = MainActivity.PreferenceManager?.getuidShared("privateRoomId")

                          if (room.roomId.equals(prvRoomId)){
                              MainActivity.navigate?.popBackStack()

                          }
                          mainActivityVM.updateRoomExist(false)
                          privateRoomsViewModel.roomRemoved(room)

                      }
                  }
              }

          }

        }
    }

    fun detachPrivateRoomsListener(){
        if (::listenPrivateRooms.isInitialized){
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()
            listenPrivateRooms.remove()



        }
    }


    fun pullPRChat(channel: DatabaseReference , room:PrivateRoom , loadMoreChat: Boolean , updateListener: Boolean){

        Log.d("pullPRChat" , "roomID :: " + room.roomId!!)

        if (!loadMoreChat){
            PRChatQuery = channel.child(room.roomId).orderByChild("time").limitToLast(13)

            PRChatQuery
                .addChildEventListener(chatEventListener)
        }
        else if (updateListener){
            var lastChatTime = MainActivity.PreferenceManager?.getLastChatTime("firstChatTime")
            var lct = lastChatTime?.toDouble()

            PRChatQuery = channel.child(room.roomId).orderByChild("time").startAt(lct!!).limitToLast(10)

            PRChatQuery
                .addChildEventListener(chatEventListener)
        }

        else {
            Log.d("whisperChatItems" , "firebasemanagerda privateroomda...")


            var lastChatTime = MainActivity.PreferenceManager?.getLastChatTime("firstChatTime")
            var lct = lastChatTime?.toDouble()

            PRChatQuery = channel.child(room.roomId).orderByChild("time").endBefore(lct!!).limitToLast(10)

            PRChatQuery
                .addChildEventListener(chatEventListener)

        }

    }
    fun removePrChatListener(channel: DatabaseReference , room:PrivateRoom){
        if (::PRChatQuery.isInitialized){

            PRChatQuery
                .removeEventListener(chatEventListener)
        }
    }

    fun pullChat(mainActivityVM: MainActivityVM , channel: DatabaseReference , updateListener:Boolean){

        if (updateListener){

            var firstChatTime = MainActivity.PreferenceManager?.getLastChatTime("firstChatTime")

            var fct = firstChatTime?.toDouble()


            chatQuery = channel.orderByChild("time").startAt(fct!!).limitToLast(10)
            chatQuery.addChildEventListener(chatEventListener)


            handler.postDelayed({
                mainActivityVM.updateChatLoading(true)



            } , 1000)
        }
        else {

            Log.d("pullChat" , "ilk seferini çalışıyor...")
            chatQuery = channel.limitToLast(10)
            chatQuery.addChildEventListener(chatEventListener)


            handler.postDelayed({
                mainActivityVM.updateChatLoading(true)



            } , 1000)
        }
    }

    fun removeChatEventListener(channel:DatabaseReference){
        Log.d("chatEventlistener" , "removed listener :: ")
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)

        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)
        channel.removeEventListener(chatEventListener)


    }

    fun uploadPRMediaImage( messageId: String , mainActivityVM: MainActivityVM , mimeType:String , uri: Uri? , bitmap: Bitmap? , room: PrivateRoom , fileName: String){



        when
        {/*
            mimeType.equals("image")->{


                Log.d("pickerResult" , "media is writing on storage now...")

                val profileImageRef = storageRef.child("pvRooms/" + room.roomId + "/" + "medias/" + fileName )

                val baos = ByteArrayOutputStream()

                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , baos)
                }

                val data = baos.toByteArray()

                val uploadTask = profileImageRef.putBytes(data)


                val urlTask = uploadTask
                    .addOnProgressListener {
                        val transferred = it.bytesTransferred
                        val total = it.totalByteCount

                        val progress = (100.0 * transferred) / total

                        mainActivityVM.updateMediaMessageProgress(progress)
                    }
                    .continueWithTask { task ->
                        profileImageRef.downloadUrl
                    }

                urlTask
                    .addOnCompleteListener{
                        if (it.isSuccessful){

                            val url = it.result.toString()

                            Log.d("pickerResult" , "media is writed...")


                            updatePRMessage(mainActivityVM , room ,url , messageId)


                        }
                    }


            }
*/
            mimeType.equals("video")-> {


                Log.d("pickerResult" , "media is writing on storage now...")

                val profileImageRef = storageRef.child("pvRooms/" + room.roomId + "/" + "medias/video.mp4"  )
                var metadata = storageMetadata {
                    contentType = "video/mp4"
                }


                val uploadTask = profileImageRef.putFile(uri!! , metadata)


                val urlTask = uploadTask
                    .continueWithTask { task ->
                        profileImageRef.downloadUrl
                    }

                urlTask
                    .addOnCompleteListener{
                        if (it.isSuccessful){

                            val url = it.result.toString()

                            Log.d("pickerResult" , "media is writed...")


                            updatePRMessage(mainActivityVM , room ,url , messageId)


                        }
                    }


            }
        }



    }

    fun updatePRMessage( mainActivityVM: MainActivityVM , room: PrivateRoom , url:String , messageId: String){

        P1.child(room.roomId!!)
            .child(messageId)
            .child("message")
            .setValue(url)
            .addOnCompleteListener {
                if (it.isSuccessful){

                    Log.d("pickerResult" , "message is updated successfully...")
                    mainActivityVM.updateNewMediaSended(false)

                }
            }

    }
    fun writePRMedia( mainActivityVM: MainActivityVM , room: PrivateRoom , context:Context , mimeType:String , uri:Uri? , messageId:String){

        when {

            mimeType.equals("image")-> {

                Log.d("pickerResult" , "media is getting ready...")
                var fileName = ""

                val cursor: Cursor? = context.contentResolver.query(uri!! , null , null , null , null)

                cursor?.use {

                    if (it.moveToFirst()){

                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                        if (nameIndex != -1){

                            fileName = it.getString(nameIndex)

                        }


                    }

                }


                Glide.with(context)
                    .asBitmap()
                    .load(uri)
                    .into(object: CustomTarget<Bitmap>(){
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {

                            Log.d("pickerResult" , "glide succesfully create bitmap...")
                            uploadPRMediaImage( messageId , mainActivityVM , mimeType , null , resource , room , fileName)


                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            TODO("Not yet implemented")
                        }


                    })



            }

            mimeType.equals("video")->{

                Log.d("pickerResult" , "media is getting ready...")
                var fileName = ""

                val cursor: Cursor? = context.contentResolver.query(uri!! , null , null , null , null)

                cursor?.use {

                    if (it.moveToFirst()){

                        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                        if (nameIndex != -1){

                            fileName = it.getString(nameIndex)

                        }


                    }

                }


                Log.d("pickerResult" , "glide succesfully create bitmap...")
                uploadPRMediaImage( messageId , mainActivityVM , mimeType , uri , null , room , fileName)


            }

        }


    }

    fun deletePrMessage(message: Message , room: PrivateRoom , channel: DatabaseReference){
        channel
            .child(room.roomId!!)
            .child(message.messageId!!)
            .setValue(null)
    }

    fun editPrMessage( type: String? , message: Message , value: String? , p: DatabaseReference , room: PrivateRoom){


        val tStamp = System.currentTimeMillis()
        val value: String? = value
        val edited:Boolean = true

        val messageId = message.messageId
        val senderId: String? = currentUser?.uid
        val senderImage: String? = currentUser?.photoUrl.toString()
        val senderName: String? = currentUser?.displayName

        val message = Message(senderId , senderImage , senderName , value , messageId , type , tStamp , edited)

        val editValue = message.toMap()

        val messageUpdates = hashMapOf<String,Any>(
            messageId!! to editValue
        )

        p.child(room.roomId!!).updateChildren(messageUpdates)


    }


    /*TODO:pull whisper*/
    fun pullWhisper(){
        // kullanıcının fısıltılarını çekecek ...
        val uid = currentUser?.uid.toString()
        W.child(uid)
            .addChildEventListener(whisperEventListener)
    }

    fun detachWhisperListener(){
        val uid = currentUser?.uid.toString()
        W.child(uid).removeEventListener(whisperEventListener)
    }
    fun pullWhisperChat(wid:String , loadMoreChat:Boolean , updateListener: Boolean){
        if (loadMoreChat){
            Log.d("whisperChatItems" , "in...")

            var lastChatId = MainActivity.PreferenceManager?.getuidShared("lastChatId")

            var lastChatTime = MainActivity.PreferenceManager?.getLastChatTime("firstChatTime")
            var lct = lastChatTime?.toDouble()
            whisoerChatQuery = W_C.child(wid).orderByChild("time").endBefore(lct!!).limitToLast(10)

            whisoerChatQuery
                .addChildEventListener(chatEventListener)
        }
        else if (updateListener){
            var lastChatTime = MainActivity.PreferenceManager?.getLastChatTime("firstChatTime")
            var lct = lastChatTime?.toDouble()
            whisoerChatQuery = W_C.child(wid).orderByChild("time").startAt(lct!!).limitToLast(10)

            whisoerChatQuery
                .addChildEventListener(chatEventListener)
        }

        else {
            Log.d("whisperChatItems" , "out...")

            whisoerChatQuery = W_C.child(wid).orderByChild("time").limitToLast(13)

            whisoerChatQuery
                .addChildEventListener(chatEventListener)
        }
    }

    fun detachWhisperChatListener(wid: String){
        if (::whisoerChatQuery.isInitialized){
            whisoerChatQuery
                .removeEventListener(chatEventListener)
        }
    }


    /*TODO: okundu bilgisi güncelle*/
    fun updateReaded(whisperItem: Whisper){

        val uid = currentUser?.uid
        val wuid = whisperItem.wuid


        val childUpdate = hashMapOf<String,Any>(

            uid + "/" + wuid + "/" + "readed" to true ,




        )
        W.updateChildren(childUpdate)

    }

    /*TODO: write whisper message*/
    fun writeWhisperMessage(user: User , type: String , value: String , mainActivityVM: MainActivityVM){
        // hem kendine hem karşıdakine fısıltı ayarla ...
        val time = System.currentTimeMillis()

        // kendini ayarla ...

        val uid = currentUser?.uid
        val displayName = currentUser?.displayName
        val photoUrl = currentUser?.photoUrl.toString()

        val wid = W.push().key.toString() // fısıltı oda id si ...

        mainActivityVM.updateWhisperId(wid)

        // karşıyı ayarla ...
        val wuid = user.uid
        val wdisplayName = user.displayName
        val wphotoUrl = user.photoUrl


        // kullanıcı ile fısıltısı var mı bak ...

        val owner = Whisper(displayName , photoUrl , uid , wid)
        val remote = Whisper(wdisplayName,wphotoUrl,wuid,wid)

        //TODO: create first whisper item ...

        W.child(uid!!).child(user.uid!!).get().addOnCompleteListener {

            // fısıltısı yoksa ekle ...
            if (!it.result.exists()){

                val childUpdate = hashMapOf<String,Any>(

                    uid + "/" + user.uid + "/" to remote.toMap() ,
                    user.uid + "/" + uid + "/" to owner.toMap()
                )

                W.updateChildren(childUpdate)
                    .addOnCompleteListener {
                        if (it.isSuccessful){

                            val Wcount = MainActivity.PreferenceManager?.getLong("Wcount")

                            MainActivity.PreferenceManager?.saveLong("Wcount" , Wcount!! + 1)

                            val messageId = W_C.push().key.toString()
                            val message = Message(uid,photoUrl,displayName,value,messageId,type,time)

                            W_C
                                .child(wid)
                                .child(messageId).setValue(message)
                                .addOnCompleteListener{
                                    if (it.isSuccessful){

                                        mainActivityVM.updatewhisperfirst(true)
                                        val childUpdate = hashMapOf<String,Any>(

                                            uid + "/" + user.uid + "/" + "lm" to value ,
                                            uid + "/" + user.uid + "/" + "lt" to time ,
                                            uid + "/" + user.uid + "/" + "lwuid" to uid ,
                                            uid + "/" + user.uid + "/" + "readed" to true ,


                                            user.uid + "/" + uid + "/" + "lm" to value ,
                                            user.uid + "/" + uid + "/" + "lt" to time ,
                                            user.uid + "/" + uid + "/" + "lwuid" to uid ,
                                            user.uid + "/" + uid + "/" + "readed" to false


                                            )
                                        W.updateChildren(childUpdate)
                                    }
                                }
                        }
                    }
            }
        }



    }

    fun editWMessage(whisperItem: Whisper , value: String , channel: DatabaseReference , messageItem:Message , type: String){

        val tStamp = System.currentTimeMillis() // düzenlenen zaman ...
        val value: String? = value // mesaj ...
        val edited:Boolean = true // düzenlendi ...

        val messageId = messageItem.messageId // sabit id kullan ...
        val senderId: String? = currentUser?.uid
        val senderImage: String? = currentUser?.photoUrl.toString()
        val senderName: String? = currentUser?.displayName

        val message = Message(senderId , senderImage , senderName , value , messageId , type , tStamp , edited)




        val wid = whisperItem.wid // fısıltı oda id ' si ...
        val wuid = whisperItem.wuid // fısıltı karşı taraf id ...

        channel.child(wid!!) // fısıltı odası ...
            .child(messageId!!) // mesaj ...
            .setValue(message)
            .addOnCompleteListener {
                if (it.isSuccessful){

                    val childUpdate = hashMapOf<String,Any>(

                        senderId + "/" + wuid + "/" + "lm" to value!! ,
                        senderId + "/" + wuid + "/" + "lt" to tStamp ,
                        senderId + "/" + wuid + "/" + "lwuid" to senderId!! ,
                        senderId + "/" + wuid + "/" + "readed" to true ,


                        wuid + "/" + senderId + "/" + "lm" to value ,
                        wuid + "/" + senderId + "/" + "lt" to tStamp ,
                        wuid + "/" + senderId + "/" + "lwuid" to senderId ,
                        wuid + "/" + senderId + "/" + "readed" to false


                    )
                    W.updateChildren(childUpdate)

                }
            }

    }

    fun writeWMessage( wuid:String , wid:String , type: String , value: String){

        var messageId = W_C.push().key.toString()

        val tStamp = System.currentTimeMillis()
        val senderId: String? = currentUser?.uid
        val senderImage: String? = currentUser?.photoUrl.toString()
        val senderName: String? = currentUser?.displayName
        val value: String? = value


        val message = Message(senderId , senderImage , senderName , value , messageId , type , tStamp)

        W_C
            .child(wid)
            .child(messageId)
            .setValue(message)
            .addOnCompleteListener{
                if (it.isSuccessful){

                    val childUpdate = hashMapOf<String,Any>(

                        senderId + "/" + wuid + "/" + "lm" to value!! ,
                        senderId + "/" + wuid + "/" + "lt" to tStamp ,
                        senderId + "/" + wuid + "/" + "lwuid" to senderId!! ,
                        senderId + "/" + wuid + "/" + "readed" to true ,


                        wuid + "/" + senderId + "/" + "lm" to value ,
                        wuid + "/" + senderId + "/" + "lt" to tStamp ,
                        wuid + "/" + senderId + "/" + "lwuid" to senderId ,
                        wuid + "/" + senderId + "/" + "readed" to false


                    )
                    W.updateChildren(childUpdate)
                }
            }



    }

    /*TODO: sohbeti benden sil ... fm */

    fun deleteChatFromMe(whisperItem: Whisper , Wref: DatabaseReference , mainActivityVM: MainActivityVM){

        val uid = currentUser?.uid // kendi id si ...
        val wuid = whisperItem.wuid // fısıltılaşılan kişinin id si ... // W referansı için ...

        val wid = whisperItem.wid // fısıltı id si ... // W_C referansı için ...

        Wref.child(uid!!)
            .child(wuid!!)
            .setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    mainActivityVM.updateShowMenu(false)
                }
            }


    }

    /*TODO: sohbeti herkesten sil ... fm */

    fun deleteChatEveryone(whisperItem: Whisper , Wref: DatabaseReference , W_Cref: DatabaseReference , mainActivityVM: MainActivityVM){

        val uid = currentUser?.uid // kendi id si ...
        val wuid = whisperItem.wuid // fısıltılaşılan kişinin id si ... // W referansı için ...

        val wid = whisperItem.wid // fısıltı id si ... // W_C referansı için ...

        Wref.child(wuid!!)
            .child(uid!!)
            .setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    mainActivityVM.updateShowMenu(false)
                }
            }


        Wref.child(uid!!)
            .child(wuid!!)
            .setValue(null)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    mainActivityVM.updateShowMenu(false)
                }
            }
        W_Cref
            .child(wid!!)
            .setValue(null)

    }

    fun writePRMessage(mainActivityVM: MainActivityVM , type:String? , messageValue: String? , p:DatabaseReference , room:PrivateRoom ){
        var messageId = p.push().key.toString()

        val tStamp = System.currentTimeMillis()

        val senderId: String? = currentUser?.uid
        val senderImage: String? = currentUser?.photoUrl.toString()
        val senderName: String? = currentUser?.displayName
        val value: String? = messageValue?.toString()
        val type: String? = type

        val message = Message(senderId , senderImage , senderName , value ,  messageId , type , tStamp)



        mainActivityVM.updateMessageId(messageId)

        val roomId = room.roomId

        p.child(roomId!!)
            .child(messageId)
            .setValue(message)
    }

    fun writePRMediaMessage(mainActivityVM: MainActivityVM , type:String? , messageValue: String? , p:DatabaseReference , room:PrivateRoom ){
        var messageId = p.push().key.toString()

        val tStamp = System.currentTimeMillis()

        val senderId: String? = currentUser?.uid
        val senderImage: String? = currentUser?.photoUrl.toString()
        val senderName: String? = currentUser?.displayName
        val value: String? = messageValue?.toString()
        val type: String? = type

        val message = Message(senderId , senderImage , senderName , value ,  messageId , type , tStamp)



        mainActivityVM.updateMessageId(messageId)

        val roomId = room.roomId

        p.child(roomId!!)
            .child(messageId)
            .setValue(message)
            .addOnCompleteListener {
                if (it.isSuccessful){

                    Log.d("pickerResult" , "message is writed on database...")
                    mainActivityVM.updatePrMessageWrited(true)

                }
            }
    }

    fun deleteMessage(message: Message , channel: DatabaseReference){


        channel
            .child(message.messageId!!)
            .setValue(null)


    }

    fun editMessage(type: String , value:String , message: Message , channel: DatabaseReference){

        val tStamp = System.currentTimeMillis()
        val value: String? = value
        val edited:Boolean = true

        val messageId = message.messageId
        val senderId: String? = currentUser?.uid
        val senderImage: String? = currentUser?.photoUrl.toString()
        val senderName: String? = currentUser?.displayName

        val message = Message(senderId , senderImage , senderName , value , messageId , type , tStamp , edited)

        val editValue = message.toMap()

        val messageUpdates = hashMapOf<String,Any>(
            messageId!! to editValue
        )

        channel.updateChildren(messageUpdates)

    }

    fun writeMessage( type: String , messageValue:String? , channel:DatabaseReference){
        Log.d("writeMessage" , "initialized...")
        var messageId = channel.push().key.toString()

        val tStamp = System.currentTimeMillis()
        val senderId: String? = currentUser?.uid
        val senderImage: String? = currentUser?.photoUrl.toString()
        val senderName: String? = currentUser?.displayName
        val value: String? = messageValue?.toString()


        val message = Message(senderId , senderImage , senderName , value , messageId , type , tStamp)


        Log.d("writeMessage" , "messageId :: $messageId")
        val postValues = message.toMap()


        channel
            .child(messageId)
            .setValue(postValues)

        Log.d("fmanager" , "user control :: " + if (currentUser == null) true else false)


    }

    fun updateSessionStatus(){

        val uid = MainActivity.PreferenceManager?.getuidShared("uid").toString()

        firestore.collection("users")
            .document(uid)
            .update("isOnline" , true)
            .addOnCompleteListener {
                Log.d("updatesessionn" , "true ayarlandı...")
            }
    }


    /*TODO: kullanici adini güncelle fm*/
    fun updateDisplayName(_displayName:String , mainActivityVM: MainActivityVM){

        val profileChangeReguest = userProfileChangeRequest{
            displayName = _displayName
        }

        currentUser?.updateProfile(profileChangeReguest)
            ?.addOnSuccessListener {
                mainActivityVM.updateDisplayName(_displayName)


                val uid = currentUser?.uid
                firestore.collection("users").document(uid!!).update("displayName" , _displayName)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            Log.d("updatedbuserinfo" , "successfully")
                            mainActivityVM.updateStartUpdate(false)
                        }
                    }
            }
    }


    @Composable
    fun writePrivateRoom(roomName:String? , roomId:String? , ownerId:String? ,
                         photoUrl: String? , parCount:Long , mainActivityVM: MainActivityVM){
        val privateRoom = PrivateRoom(
            roomName = roomName!! ,
            roomId = roomId!! ,
            photoUrl = photoUrl!! ,
            parCount = parCount ,
            ownerId = ownerId

        )



        firestore.collection("prvRoom").document(roomId!!).set(privateRoom)
            .addOnFailureListener {
                Log.d("firestorell" , "addOnFailureListener... " + it.message)

            }
            .addOnCompleteListener{

                // TODO: özel oda veri tebabnına yazıldı ... shaiplik bilgilerine odayı ekle ...
                val privRoomCount = MainActivity.PreferenceManager?.getLong("privRoomCount")
                MainActivity.PreferenceManager?.saveLong("privRoomCount" , privRoomCount!! + 1)

                mainActivityVM.updateCreatingPrivateRoom(false)
            }
    }


    @Composable
    fun uploadPrivateRoomPhoto(bitmap: Bitmap? , mainActivityVM: MainActivityVM , uid:String){

        val profileImageRef = storageRef.child("pvRooms/" + uid + "/" + "roomImage/rm.webp")

        val baos = ByteArrayOutputStream()

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.WEBP , 80 , baos)
        }

        val data = baos.toByteArray()

        val uploadTask = profileImageRef.putBytes(data)


        val urlTask = uploadTask.continueWithTask { task ->

            profileImageRef.downloadUrl
        }


        urlTask.addOnCompleteListener{
            if (it.isSuccessful){

                val url = it.result.toString()
                Log.d("firestorell" , "uploadImage complated :: " + url)

                Log.d("firestorell" , "path :: " + url)
                mainActivityVM.updatePhotoUrl(url)

                mainActivityVM.uploadComlated(true)

            }
        }

    }

    /**
     *
     * write user data on firestore if uploaded profile image on storage
     *
     * */

    fun writeUserData(photoUrl:String? , mainActivityVM: MainActivityVM){

        Log.d("firestorell" , "write user data is run...")


        var uid:String = ""

        // geçerli kullanıcı boş değil ise uid i al ...

        currentUser?.let {
            uid = it.uid
        }

        // user nesnesini oluştur ...


        val user = User(
            displayName = currentUser?.displayName ,
            photoUrl = currentUser?.photoUrl.toString() ,
            uid = currentUser?.uid ,
            isOnline = true
        )

        firestore.collection("users").document(uid).set(user)
            .addOnFailureListener {
                Log.d("firestorell" , "addOnFailureListener... " + it.message)

            }
            .addOnCompleteListener{
                Log.d("firestorell" , "write user data is complated :: " + user)

                mainActivityVM.updateSessionC(true)
            }

    }

    // get default profile image as bitmap for upload storage
    fun getDefaultProfileImage(context:Context , fileName:String):Bitmap?{
        val assest = context.assets
        var inputStream: InputStream? = null

        return try {
            inputStream = assest.open(fileName)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            inputStream?.close()
        }
    }

    fun updateProfilePhoto(photoUrl: Uri? , mainActivityVM: MainActivityVM){


        Log.d("firestorell" , "update photo url run...")

        val profileChangeReguest = userProfileChangeRequest{
            photoUri = photoUrl
        }

        currentUser?.updateProfile(profileChangeReguest)?.addOnSuccessListener {

            var Pphoto = FirebaseManager.currentUser?.photoUrl.toString()

            GlideApp.with(MainActivity.context)
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
        }
    }

    // upload profile image on storage

    fun uploadImage(bitmap: Bitmap? , mainActivityVM: MainActivityVM){

        Log.d("firstSignUp" , "oturum açma başarılı profil resmi db ye yükleniyor ....")

        val UID = currentUser?.uid

        val profileImageRef = storageRef.child("users/" + UID + "/" + "profileImage/pp.png")

        val baos = ByteArrayOutputStream()

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG , 100 , baos)
        }

        val data = baos.toByteArray()

        val uploadTask = profileImageRef.putBytes(data)


        val urlTask = uploadTask.continueWithTask { task ->

            profileImageRef.downloadUrl
        }


        urlTask.addOnCompleteListener{
            if (it.isSuccessful){
                it.toString()
                updateProfilePhoto(it.result , mainActivityVM)
                val url = it.result.toString()
                Log.d("firestorell" , "uploadImage complated :: " + url)

                Log.d("firestorell" , "path :: " + url)
                mainActivityVM.updatePhotoUrl(url)
                mainActivityVM.uploadComlated(true)

            }
        }



    }

    fun checkSession(uid: String , mainActivityVM: MainActivityVM){
        firestore
            .collection("users")
            .document(uid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val _isOnline = it.result.getBoolean("isOnline")
                    mainActivityVM.updateIsOnline(_isOnline!!)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @Composable
    fun handleUpdateProfileCard(bitmap: Bitmap? , mainActivityVM: MainActivityVM){

        var url by remember {
            mutableStateOf("")
        }

        val UID = currentUser?.uid

        val profileImageRef = storageRef.child("users/" + UID + "/" + "profileImage/pp.webp")

        val baos = ByteArrayOutputStream()

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.WEBP , 80 , baos)
        }

        val data = baos.toByteArray()

        val uploadTask = profileImageRef.putBytes(data)


        val urlTask = uploadTask.continueWithTask { task ->

            profileImageRef.downloadUrl
        }

        // ikisi güncellendi ise url saklanacak user update i vermek için

        urlTask.addOnCompleteListener{
            if (it.isSuccessful){
                it.toString()
                updateProfilePhoto(it.result , mainActivityVM)
                url = it.result.toString()
                Log.d("firestorell" , "uploadImage complated :: " + url)

                Log.d("firestorell" , "path :: " + url)
                mainActivityVM.updatePhotoUrl(url)
                mainActivityVM.uploadComlated(true)

                mainActivityVM.updateStartUpdate(false)
            }
        }

    }

    @Composable
    fun updateOnDbUserPhotoUrl(photoUrl:String? , mainActivityVM: MainActivityVM){
        var uid:String = ""
        currentUser?.let {
            uid = it.uid
        }

        firestore.collection("users").document(uid).update("photoUrl" , photoUrl)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Log.d("updatedbuserinfo" , "successfully")
                    mainActivityVM.uploadComlated(false)
                    mainActivityVM.updateProfileUpdating(false)
                }
            }
    }

    /*TODO: handle whisper*/
    fun handleWhisper(uid: String , mainActivityVM: MainActivityVM){

        // kullanıcılar koleksiyonuna sorgu at ... kullanıcıyı getir & güncelle
        usersRef.document(uid)
            .get()
            .addOnCompleteListener { document ->
                val user = document.result.toObject(User::class.java)
                mainActivityVM.updateUser(user = user!!) // kullanıcıyı güncelle ...
            }
    }

    /*TODO: listen incoming calls*/

    lateinit var listenICCQueryRegistiration:ListenerRegistration
    fun listenICC(uid:String , mainActivityVM: MainActivityVM){

        listenICCQueryRegistiration = usersRef.document(uid).addSnapshotListener{
            snapshot , e ->

            if (snapshot != null && snapshot.exists()){
                var ICC = snapshot.getBoolean("inCall")
                if (ICC != null){
                    mainActivityVM.updateCall(ICC)
                    Log.d("ICC" , "gelen arama " + ICC)

                }
            }

        }
    }

    fun detachListenerICC(){
        if (::listenICCQueryRegistiration.isInitialized){
            listenICCQueryRegistiration.remove()
        }
    }

    /** arama isteği gönder ...*/

    fun callrequest( ownerId: String , uid: String , displayName:String , photoUrl:String , inCallActivityViewModel: InCallActivityViewModel){

        Log.d( "firstcall", "aramayı başlatanın aramada olduğu true ayarlandı ... ")

        usersRef.document(ownerId)
            .update("inCall" , true)

        /**
         *  aranan kişi herhangi bir aramada mı bak ...
         * */

        usersRef.document(uid)
            .get()
            .addOnCompleteListener {

               var call = it.result.getBoolean("inCall") // aranan kişinin arama durumu burda ...
                Log.d( "firstcall", "arayan kişi karşı tarafın aramada olup olmadığını doğruluyor ... ")

                /**
                 * arama isteği sonucunu karşı tarafın aramada olup olmamasına göre dönder ...
                 * */
                inCallActivityViewModel.updateRequestCall(call!!)

            }

        /**
         * arama isteği atıldığında aranan kişi koridora düşsede düşmesede cevap versede vermesede arama geçmişine kaydet ...
         * */



        /**
         * Arama geçmişine kaydederken arayan kişi zaten aramada olmak istediği için arama durumunu true ayarla ki dışardan arama gelmesin ...
         * arayan kişinin geçmişine aranan kişi bilgileri yazılacak ...
         * */
        val Wcalls = Calls(
            displayName = photoUrl ,
            photoUrl = displayName ,
            uid = uid ,
            type = "in" , // in algıla giden gelen arama ...
            time = System.currentTimeMillis() ,
            act = true , // kabul edildi mi ... arayan kişi ekranda kalması için ilkte true ayarlanır ...
            roomId = ownerId
            )

        W.child("calls")
            .child(ownerId)
            .child(uid)
            .setValue(Wcalls)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    // arama yapan kişinin arama geçmişine arama kaydedildi ise arama durumunu dinlemeye başla ...
                    /**
                     * arama yapan kişinin arama geçmişini dinleme sebebi ise ... karşı tarafın cevabına göre
                     * gelen arama durumunun dinlenmesi ve koridorun akışının şekillenmesi ...
                     * */
                   // listenCalls(ownerId, uid)
                }
            }

        // buda arayan kişinin ... bunuda aranan kişiye kaydedecek ...

        /**
         * bu arama geçmişi aranan kişinin geçmişi ... arayan kişiyi buraya alacaz ...
         * aranan kişinin geçmişi olduğu için arama durumunu yanıta göre güncelleyecez ...
         *
         * */


        val OwndisplayName = currentUser?.displayName
        val OwnphotoUrl = currentUser?.photoUrl.toString()

        val Ownercalls = Calls(
            displayName = OwndisplayName ,
            photoUrl = OwnphotoUrl ,
            uid = ownerId ,
            type = "out" , // out algıla gelen arama ...
            time = System.currentTimeMillis() ,
            act = false ,  // kabul edildi mi ...
            roomId = ownerId
            )

        W.child("calls")
            .child(uid)
            .child(ownerId)

            .setValue(Ownercalls)
            .addOnCompleteListener {
                if (it.isSuccessful){
                  //  listenWCalls(ownerId, uid)
                }
            }


    }

    lateinit var callsQuery:Query
    lateinit var WcallsQuery:Query




    /**
     * arama geçmişinde arayan kişinin aranan kişinin arama geçmişinin dinleyicisi ...
     * */
  /*  private val WcallsValueListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            //TODO: arama dinleyici ...

            val call = snapshot.getValue(Calls::class.java)
            if (call != null){
                voiceCallActivityVM.updateWCalls(call)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }*//*
    @SuppressLint("SuspiciousIndentation")
    fun listenWCalls(ownerId:String, uid: String){
        WcallsQuery = W.child("calls")
            .child(uid)
            .child(ownerId)

        WcallsQuery.addValueEventListener(WcallsValueListener)
    }*/
    /**
     * arama geçmişinde arayan kişinin kendi arama bilgilerinin dinleyicisi
     * */
/*    private val callsValueListener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            //TODO: arama dinleyici ...

            val call = snapshot.getValue(Calls::class.java)
            if (call != null){
                voiceCallActivityVM.updateCalls(call)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }*/
   /* @SuppressLint("SuspiciousIndentation")
    fun listenCalls(ownerId:String, uid: String){
      callsQuery = W.child("calls")
            .child(ownerId)
            .child(uid)

        callsQuery.addValueEventListener(callsValueListener)
    }

    fun detachCallsListener(){
        if (::callsQuery.isInitialized){
            callsQuery.removeEventListener(callsValueListener)
        }
    }
*/
    fun calling(uid: String ){
        /**
         * aranan kişi aramada olmadığı için arayabiliriz ... önce karşı tarafın aramayı kabul veya red onayı vermesi için
         * karşı tarafın ICC (incomingcall) field'ını güncelle ...
         * */

        Log.d( "firstcall", "aramayı başlatan kişi karşı tarafın gelen arama durumunu true ayarladı ... ")

        usersRef.document(uid)
            .update("inCall" , true) // karşı taraf artık bir aramanın geldiğini görebilecek ...

    }

    fun acceptCall(ownerId: String , uid: String){
        usersRef.document(ownerId)
            .update("inCall" , true)
        usersRef.document(uid)
            .update("inCall" , true)

        usersRef.document(uid)
            .update("ICC" , true)

        // kabul etti ... arayan kişinin arama geçmişindeki arama kabul durumunu true ayarla ...
        W.child("calls")
            .child(ownerId)
            .child(uid)
            .child("act")
            .setValue(true)

        W.child("calls")
            .child(uid)
            .child(ownerId)
            .child("act")
            .setValue(true)
    }

    fun declineCall(ownerId: String , uid: String){
        Log.d( "firstcall", "aramayı başlatan kişi aramadan vazgeçti ... ")

        usersRef.document(ownerId)
            .update("inCall" , false)
        Log.d( "firstcall", "aramayı başlatan kişinin aramada durumu false ayarlandı ... ")

        usersRef.document(uid)
            .update("inCall" , false)

        Log.d( "firstcall", "aramayı başlatan kişi karşı tarafın aramada durumu false ayarlandı ... ")


        usersRef.document(uid)
            .update("ICC" , false)
        Log.d( "firstcall", "aramayı başlatan kişi karşı tarafın gelen arama durumu false ayarlandı ... ")


        // kabul etmedin ... arayan kişinin arama geçmişindeki arama kabul durumunu false ayarla ...
        W.child("calls")
            .child(ownerId)
            .child(uid)
            .child("act")
            .setValue(false)

        W.child("calls")
            .child(uid)
            .child(ownerId)
            .child("act")
            .setValue(false)

    }

    /**
     * arama geldiğinde geçmişten en sonuncusunu getir ...
     * */

    fun getLastCallHistory( uid: String , mainActivityVM: MainActivityVM){
        W.child("calls")
            .child(uid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                   val calls = it.result.children.last().getValue(Calls::class.java)
                    mainActivityVM.updateHistoryCalls(calls!!)
                }
            }
    }

    /**
     * TODO: rastgele katılımcısı ekle ...
     * */

    fun addRandomParticipant(uid:String , displayName:String , photoUrl:String){
        val participant = RandomParticipant(
            displayName, photoUrl, uid , null,null,null,null
        )
        Log.d("randomActivity" , "random katılımcı veri tababnına eklendi ... ")


        val par = hashMapOf(
            "uid" to uid ,
            "match" to null ,
            "outId" to null ,
            "displayName" to displayName ,
            "photoUrl" to photoUrl
        )

        random
            .document(uid)
            .set(par)
            .addOnCompleteListener {
                if (it.isSuccessful){

                }
            }
    }
    fun removeRandomParticipant(uid:String){

        random
            .document(uid)
            .delete()
    }

    fun updateMatchRequest(state:Boolean , uid: String){
        Log.d("randomActivity" , "karşılaşma isteği tamam ... ")

        random
            .document(uid)
            .update("match" , state)
    }

    fun declineMatch(status:Boolean? , ruid:String , mainActivityVM: MainActivityVM){
        val selfUid = MainActivity.PreferenceManager?.getuidShared("uid")

        random
            .document(ruid)
            .update("outId" , null)
        random
            .document(selfUid!!)
            .update("outId" , null)
        random
            .document(ruid)
            .update("match" , true)
        random
            .document(selfUid!!)
            .update("match" , true)
        random
            .document(ruid)
            .update("rm" , null)
        random
            .document(selfUid!!)
            .update("rm" , null)

        random
            .document(ruid)
            .update("matched" , status)
        random
            .document(selfUid!!)
            .update("matched" , status)
    }

    fun updateOnMeeting(uid: String , status: Boolean?){
        val selfUid = MainActivity.PreferenceManager?.getuidShared("uid")

        random
            .document(selfUid!!)
            .update("meeting" , status)
    }

    fun updateMatched(state: Boolean , uid:String){
        val selfUid = MainActivity.PreferenceManager?.getuidShared("uid")
        random
            .document(uid)
            .update("match" , state)
        random
            .document(selfUid!!)
            .update("match" , state)


    }

    /**
     * kendi yakalayamadan başkası yakaladıysa kendi oda adını güncelle ...
     * */

    fun updateSelfRoomName(randomParticipant: RandomParticipant , randomActivityViewModel: RandomActivityViewModel){
        val selfUid = MainActivity.PreferenceManager?.getuidShared("uid")
        var selfName = currentUser?.displayName
        var parname = randomParticipant?.displayName

        var roomName =  "$parname"
        random
            .document(selfUid!!)
            .update("rm" , roomName)
            .addOnCompleteListener {
                if (it.isSuccessful){
                  //  mainActivityVM.update_c(true)
                    randomParticipant?.rm = roomName
                  //  randomActivityViewModel.updateliveRandomParticipant(randomParticipant)
                }
            }
    }



    fun listenRandomHistory(randomConnectionActivityViewModel: RandomConnectionActivityViewModel){
        val selfUid = MainActivity.PreferenceManager?.getuidShared("uid")

        Random_History
            .child(selfUid!!)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val random = it.result.children.last().getValue(RandomParticipant::class.java)

                    MainActivity.mMainActivityVM.updateRandomParticipant(random!!)
                    randomConnectionActivityViewModel.updateliveRandomParticipant(random)
                   // randomActivityViewModel.update_x(true)

                }
            }


    }

    fun ListenMatch(Ouid: String , randomConnectionActivityViewModel: RandomConnectionActivityViewModel){
       listenMatch = random
            .addSnapshotListener{
                snapshot , e ->

                for (dc in snapshot?.documentChanges!!){

                    when(dc.type){
                        DocumentChange.Type.ADDED -> {}
                        DocumentChange.Type.MODIFIED -> {

                            var uid = dc.document.getString("uid")

                            if (uid?.equals(Ouid!!) == true) {

                                var matched = dc.document.getBoolean("match")
                                randomConnectionActivityViewModel.updateMatched(matched)
                                // randomActivityViewModel.update_xmatched(matched!!)

                                if (matched == false){
                                    handler.postDelayed({
                                        listenRandomHistory(randomConnectionActivityViewModel =
                                        randomConnectionActivityViewModel)
                                    },800)
                                }


                            }
                        }
                        DocumentChange.Type.REMOVED -> {}
                    }


                }








            }
    }

    fun detachListenMatch(){
        if (::listenMatch.isInitialized){
            listenMatch.remove()
        }
    }

    fun getRandomParticipant(uid: String , randomActivityViewModel: RandomActivityViewModel){
        random.document(uid)
            .get().addOnCompleteListener {
                if (it.isSuccessful){
                    var randomParticipant = it.result.toObject(RandomParticipant::class.java)
                    if (randomParticipant != null){
                     //   randomActivityViewModel.updateRandomParticipant(randomParticipant!!)
                     //   randomActivityViewModel.updateliveRandomParticipant(randomParticipant!!)
                        randomActivityViewModel.update_x(true)
                        randomActivityViewModel.update_X(true)
                    }
                }
            }
    }

    fun updateOwnerMatchedStatus(uid: String , state: Boolean){

        random
            .document(uid)
            .update("match" , state)
    }

    fun matchParticipants(uid: String){
        /**
         * eşleme isteği true olan ve kişinin kendi id sine eşit olmayan rastgele kişiyi getir ...
         * */
        Log.d("randomActivity" , "karşılaştırma çalıştı ... ")

        random
            .whereEqualTo("match" , true) // karşılaşma isteği olan ...
           // .whereEqualTo("matched" , false) // karşılaşmamış olan ...
            .whereEqualTo("outId" , null) // karşı taraf id'si boş olan ...
            .whereNotEqualTo("uid" , uid) // ve kendisi olmayan ...
            .get()
            .addOnCompleteListener {

                if (it.isSuccessful){

                    Log.d("randomActivity" , "karşılaştırma başarılı ... ")
                    val documents = it.result.documents

                    if (documents.isNotEmpty()){
                        Log.d("matchlist" , "not empty ... size ... " + documents.size)


                        for (ds in documents){

                            val randomParticipant = ds.toObject(RandomParticipant::class.java)

                            var outId = randomParticipant?.uid!!
                            var Xname = randomParticipant?.displayName
                            var Xphoto = randomParticipant?.photoUrl

                            var name = MainActivity.PreferenceManager?.getString("displayName")
                            var selfUid = MainActivity.PreferenceManager?.getuidShared("uid")
                            var photo = MainActivity.PreferenceManager?.getString("photoUrl")

                            // karşılaşılan kişinin karşılaşma geçmişine eklenecek ... karşılaşmayı bulan kişi bu ...
                            val rPar = RandomParticipant(displayName = name , photoUrl = photo , uid = selfUid , true , name , tfc = true , outId = null)

                            Random_History
                                .child(outId)
                                .child(selfUid!!)
                                .setValue(rPar)

                            val itPar = RandomParticipant(displayName = Xname , photoUrl = Xphoto , uid = outId , true , name , tfc = false , outId = null)

                            Random_History
                                .child(selfUid!!)
                                .child(outId)
                                .setValue(itPar)


                            updateMatched(false , outId)

/*
                            randomActivityViewModel.updateRandomParticipant(randomParticipant!!)
                            randomActivityViewModel.update_c(true)*/

                            break ;
                        }

                    } else {
                        Log.d("matchlist" , "empty ... ")


                    }


                } else {
                    Log.d("matchlist" , "basarisiz ... ")

                }

                it.addOnFailureListener {
                    error ->
                    Log.d("matchlist" , "hata ... :: " + error.message)

                }
            }
    }


    var whisperLatch = CountDownLatch(1)



    fun deleteAccounWithAllUserData(){
        val selfUid = MainActivity.PreferenceManager?.getuidShared("uid")

        val whisperChildCount = MainActivity?.PreferenceManager?.getLong("Wcount")
        val whisperChilderenlatch = CountDownLatch(whisperChildCount?.toInt()!!)

        val privRoomCount = MainActivity.PreferenceManager?.getLong("privRoomCount")
        val privateRoomsSizeLatch = CountDownLatch(privRoomCount?.toInt()!!)



        Log.d("Wcount" , " size :: " + whisperChildCount)
        Log.d("privRoomCount" , " size :: " + privRoomCount)


        if (whisperChildCount > 0 && privRoomCount > 0){

            val whisperChilderenlatch = CountDownLatch(whisperChildCount?.toInt()!!)
            val privateRoomsSizeLatch = CountDownLatch(privRoomCount?.toInt()!!)

            W.child(selfUid!!)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        it.result.children.forEach { dataSnapshot ->
                            val whisper = dataSnapshot.getValue(Whisper::class.java)

                            val wid = whisper?.wid

                            W_C.child(wid!!).removeValue().addOnCompleteListener {
                                if (it.isSuccessful){
                                    whisperChilderenlatch.countDown()
                                }
                            }
                        }
                    }
                }

            whisperChilderenlatch.await()


            W.child(selfUid)
                .get()
                .addOnCompleteListener {
                        task ->

                    if (!task.result.exists()){
                        whisperLatch.countDown()

                    } else {
                        W.child(selfUid)
                            .removeValue().addOnCompleteListener {
                                if (it.isSuccessful){
                                    whisperLatch.countDown()
                                }
                            }
                    }
                }


            whisperLatch.await()

            prvRoomRef
                .whereEqualTo("ownerId" , selfUid)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){


                        if (!it.result.isEmpty){
                            it.result.documents.forEach { documentSnapshot ->

                                var roomId = documentSnapshot.getString("roomId")

                                P1.child("participants")
                                    .child(roomId!!).removeValue()

                                P1.child(roomId!!).removeValue().addOnCompleteListener {
                                    if (it.isSuccessful){
                                        prvRoomRef.document(roomId).delete()
                                        privateRoomsSizeLatch.countDown()
                                    }
                                }


                            }


                        } else {
                            privateRoomsSizeLatch.countDown()
                        }
                    }
                }

            privateRoomsSizeLatch.await()


            usersRef.document(selfUid).delete()
            auth.currentUser?.delete()?.addOnCompleteListener {
                MainActivity.PreferenceManager?.clear()
            }

        } else if (whisperChildCount > 0){
            val whisperChilderenlatch = CountDownLatch(whisperChildCount?.toInt()!!)
            W.child(selfUid!!)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        it.result.children.forEach { dataSnapshot ->
                            val whisper = dataSnapshot.getValue(Whisper::class.java)

                            val wid = whisper?.wid

                            W_C.child(wid!!).removeValue().addOnCompleteListener {
                                if (it.isSuccessful){
                                    whisperChilderenlatch.countDown()
                                }
                            }
                        }
                    }
                }

            whisperChilderenlatch.await()

            W.child(selfUid)
                .get()
                .addOnCompleteListener {
                        task ->

                    if (!task.result.exists()){
                        whisperLatch.countDown()

                    } else {
                        W.child(selfUid)
                            .removeValue().addOnCompleteListener {
                                if (it.isSuccessful){
                                    whisperLatch.countDown()
                                }
                            }
                    }
                }


            whisperLatch.await()

            usersRef.document(selfUid).delete()
            auth.currentUser?.delete()?.addOnCompleteListener {
                MainActivity.PreferenceManager?.clear()
            }
        } else if (privRoomCount > 0){


            prvRoomRef
                .whereEqualTo("ownerId" , selfUid)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful){


                        if (!it.result.isEmpty){
                            it.result.documents.forEach { documentSnapshot ->

                                var roomId = documentSnapshot.getString("roomId")

                                P1.child("participants")
                                    .child(roomId!!).removeValue()

                                P1.child(roomId!!).removeValue().addOnCompleteListener {
                                    if (it.isSuccessful){
                                        prvRoomRef.document(roomId).delete()
                                        privateRoomsSizeLatch.countDown()
                                    }
                                }


                            }


                        } else {
                            privateRoomsSizeLatch.countDown()
                        }
                    }
                }

            privateRoomsSizeLatch.await()

            usersRef.document(selfUid!!).delete()
            auth.currentUser?.delete()?.addOnCompleteListener {
                MainActivity.PreferenceManager?.clear()
                MainActivity().restartActivity()
            }

        } else {
            usersRef.document(selfUid!!).delete()
            auth.currentUser?.delete()?.addOnCompleteListener {
                MainActivity.PreferenceManager?.clear()
                MainActivity().restartActivity()
            }
        }


    }
}


class SessionService:Service() {
    lateinit var preferenceManager:PreferenceManager
    lateinit var uid:String
    var session:Boolean? = false
    lateinit var privRoomId:String
    override fun onCreate() {
        super.onCreate()
        preferenceManager = PreferenceManager(context = this) ;
        uid = preferenceManager.getuidShared("uid")
        session = preferenceManager.getSession("inPrivateRoom")
        privRoomId = preferenceManager.getuidShared("privateRoomId")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {



        FirebaseManager.firestore.collection("users")
            .document(uid.toString())
            .update("isOnline" , false)

        if (session == true){
            val prvRoomDocRef:DocumentReference = firestore.collection("prvRoom").document(privRoomId!!)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(prvRoomDocRef)
                val activePar = snapshot.getLong("activePar")!! - 1

                P1.child("participants")
                    .child(privRoomId!!)
                    .child(uid!!).setValue(null)
                transaction.update(prvRoomDocRef , "activePar" , activePar)

            }.addOnSuccessListener {
                preferenceManager.saveSession("inPrivateRoom" , false)
                Log.d("updateactivepar" , "success...")
            }
        }

        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }



}

