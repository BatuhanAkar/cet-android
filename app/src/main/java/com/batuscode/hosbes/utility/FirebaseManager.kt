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
import android.os.IBinder
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import com.batuscode.hosbes.MainActivity
import com.batuscode.hosbes.models.Message
import com.batuscode.hosbes.models.PrivateRoom
import com.batuscode.hosbes.models.User
import com.batuscode.hosbes.models.Whisper
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
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import java.io.ByteArrayOutputStream
import java.io.InputStream


class FirebaseManager {

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
    }





    lateinit var chatViewModel: ChatViewModel

    lateinit var whisperViewModel: WhisperViewModel

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
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }


    }

    val chatEventListener: ChildEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            val message:Message = snapshot.getValue<Message>()!!

            Log.d("message" , "chat :: " + message.message)


            when{


                message.type.equals("text") -> {
                    chatViewModel.pushChat(message)
                }

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
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }


    }

    fun handleJoinRoom( mainActivityVM: MainActivityVM , type:String , room: PrivateRoom){

        val prvRoomDocRef:DocumentReference = firestore.collection("prvRoom").document(room.roomId!!)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(prvRoomDocRef)

            if (type.equals("joined")){

                val activePar = snapshot.getLong("activePar")!! + 1
                transaction.update(prvRoomDocRef , "activePar" , activePar)
            } else {
                val activePar = snapshot.getLong("activePar")!! - 1
                transaction.update(prvRoomDocRef , "activePar" , activePar)
            }
        }.addOnSuccessListener {
            Log.d("updateactivepar" , "success...")
        }
    }

    var handler = android.os.Handler(Looper.getMainLooper())

    lateinit var listenPrivateRooms: ListenerRegistration

    fun pullPrivateRooms(privateRoomsViewModel: PrivateRoomsViewModel){

      listenPrivateRooms = prvRoomRef.addSnapshotListener { snapshots , e ->


            for (dc in snapshots!!.documentChanges){

                when
                {

                    dc.type == DocumentChange.Type.ADDED -> {


                        Log.d("documentType" , "added... " + dc.document.get("roomName"))

                        val room = dc.document.toObject(PrivateRoom::class.java)

                        privateRoomsViewModel.pushRoom(room)



                    }

                    dc.type == DocumentChange.Type.MODIFIED -> {

                        Log.d("documentType" , "MODIFIED... " + dc.document.get("roomName"))

                        val room = dc.document.toObject(PrivateRoom::class.java)
                        Log.d("documentType" , "MODIFIED... activePar " + dc.document.get("activePar"))

                        privateRoomsViewModel.modifiedRoom(room)
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


    fun pullPRChat(channel: DatabaseReference , room:PrivateRoom){

        Log.d("pullPRChat" , "roomID :: " + room.roomId!!)

        channel.child(room.roomId)
            .addChildEventListener(chatEventListener)

    }
    fun removePrChatListener(channel: DatabaseReference , room:PrivateRoom){
        channel.child(room.roomId!!)
            .removeEventListener(chatEventListener)
    }

    fun pullChat(mainActivityVM: MainActivityVM , channel: DatabaseReference){



        channel.addChildEventListener(chatEventListener)


        handler.postDelayed({
            mainActivityVM.updateChatLoading(true)



        } , 1000)    }

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

    fun pullWhisperChat(whisperItem:Whisper){
        W_C
            .child(whisperItem.wid!!)
            .addChildEventListener(chatEventListener)
    }

    fun detachWhisperChatListener(whisperItem: Whisper){
        W_C
            .child(whisperItem.wid!!)
            .removeEventListener(chatEventListener)
    }

    /*TODO: write whisper message*/
    fun writeWhisperMessage(user: User , type: String , value: String){
        // hem kendine hem karşıdakine fısıltı ayarla ...
        val time = System.currentTimeMillis()

        // kendini ayarla ...

        val uid = currentUser?.uid
        val displayName = currentUser?.displayName
        val photoUrl = currentUser?.photoUrl.toString()

        val wid = W.push().key.toString() // fısıltı oda id si ...

        // karşıyı ayarla ...
        val wuid = user.uid
        val wdisplayName = user.displayName
        val wphotoUrl = user.photoUrl


        // kullanıcı ile fısıltısı var mı bak ...

        val owner = Whisper(displayName , photoUrl , uid , wid)
        val remote = Whisper(wdisplayName,wphotoUrl,wuid,wid)

        W.child(uid!!).child(user.uid!!).get().addOnCompleteListener {

            // fısıltısı yoksa ekle ...
            if (!it.result.exists()){

                val childUpdate = hashMapOf<String,Any>(

                    uid to remote.toMap() ,
                    user.uid to owner.toMap()
                )

                W.updateChildren(childUpdate)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            val messageId = W_C.push().key.toString()
                            val message = Message(uid,photoUrl,displayName,value,messageId,type,time)

                            W_C
                                .child(wid)
                                .child(messageId).setValue(message)
                                .addOnCompleteListener{
                                    if (it.isSuccessful){

                                        val childUpdate = hashMapOf<String,Any>(

                                            uid + "/" + user.uid + "/" + "lm" to value ,
                                            uid + "/" + user.uid + "/" + "lt" to time ,
                                            user.uid + "/" + uid + "/" + "lm" to value ,
                                            user.uid + "/" + uid + "/" + "lt" to value
                                        )
                                        W.updateChildren(childUpdate)
                                    }
                                }
                        }
                    }
            }
        }



    }

    fun writeWMessage(whisperItem: Whisper , type: String , value: String){

        var messageId = W_C.push().key.toString()

        val tStamp = System.currentTimeMillis()
        val senderId: String? = currentUser?.uid
        val senderImage: String? = currentUser?.photoUrl.toString()
        val senderName: String? = currentUser?.displayName
        val value: String? = value


        val message = Message(senderId , senderImage , senderName , value , messageId , type , tStamp)

        W_C
            .child(whisperItem.wid!!)
            .child(messageId)
            .setValue(message)



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



        channel
            .child(messageId)
            .setValue(message)

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


    fun updateDisplayName(_displayName:String , mainActivityVM: MainActivityVM){

        val profileChangeReguest = userProfileChangeRequest{
            displayName = _displayName
        }

        currentUser?.updateProfile(profileChangeReguest)
            ?.addOnSuccessListener {
                mainActivityVM.updateDisplayName(_displayName)
                mainActivityVM.updateStartUpdate(false)


            }
    }


    @Composable
    fun writePrivateRoom(roomName:String? , roomId:String? , photoUrl: String? , parCount:Long , mainActivityVM: MainActivityVM){
        val privateRoom = PrivateRoom(
            roomName = roomName!! ,
            roomId = roomId!! ,
            photoUrl = photoUrl!! ,
            parCount = parCount
        )



        firestore.collection("prvRoom").document(roomId!!).set(privateRoom)
            .addOnFailureListener {
                Log.d("firestorell" , "addOnFailureListener... " + it.message)

            }
            .addOnCompleteListener{
                mainActivityVM.updateCreatingPrivateRoom(false)
            }
    }

    @Composable
    fun uploadPrivateRoomPhoto(bitmap: Bitmap? , mainActivityVM: MainActivityVM , uid:String){

        val profileImageRef = storageRef.child("pvRooms/" + uid + "/" + "roomImage/rm.png")

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

    @Composable
    fun handleUpdateProfileCard(bitmap: Bitmap? , mainActivityVM: MainActivityVM){

        var url by remember {
            mutableStateOf("")
        }

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

}


class SessionService:Service() {
    lateinit var preferenceManager:PreferenceManager
    var uid:String? = ""

    override fun onCreate() {
        super.onCreate()
        preferenceManager = PreferenceManager(context = this) ;
        uid = preferenceManager.getuidShared("uid")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {



        FirebaseManager.firestore.collection("users")
            .document(uid.toString())
            .update("isOnline" , false)

        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}

