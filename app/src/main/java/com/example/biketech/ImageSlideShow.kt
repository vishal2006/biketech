package com.example.biketech

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biketech.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_image_slide_show.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageSlideShow : AppCompatActivity() {

    val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
    val now = Date()
    val fileName = formatter.format(now)
    val storageReference= FirebaseStorage.getInstance().getReference("images/$fileName")
    companion object{
        private const val CAMERA_PERMISSION_CODE=1
        private const val CAMERA =2
        private const val CAMERA_REQUEST_CODE=2
    }

    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection("users")
    val auth = Firebase.auth


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_slide_show)

        val documentRecyclerView = findViewById<RecyclerView>(R.id.documentsRecyclerView)
        val documentCamera = findViewById<ImageButton>(R.id.documentCameraButton)

        documentRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ImageSlideShow,LinearLayoutManager.HORIZONTAL,false)
        }

        documentCamera.setOnClickListener {
            showCamera()
        }

        fetchData()

    }

    private fun showCamera() {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)

        }else{

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== CAMERA_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }else{
                Toast.makeText(this, "Unable to open", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchData() {
        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid


        FirebaseFirestore.getInstance().collection("users").document(currentUserId)
            .get()
            .addOnSuccessListener {document->
                val user = document.toObject(User::class.java)
                if (user != null) {

                    documentsRecyclerView.adapter = UserDocumentAdapter(this,user.Documents)
                }
            }.addOnFailureListener {
                Toast.makeText(this, "An error Occurred", Toast.LENGTH_SHORT).show() }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== CAMERA_REQUEST_CODE){
                val thumBnail : Bitmap = data!!.extras!!.get("data") as Bitmap



                val baos = ByteArrayOutputStream()
                thumBnail.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = storageReference.putBytes(data)

                uploadTask.addOnSuccessListener {
                    Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT).show()

                    storageReference.downloadUrl.addOnSuccessListener {Uri->

                        val imageUri = Uri.toString()
                        val currentUserId = auth.currentUser!!.uid
                        postCollections.document(currentUserId).update("Documents", FieldValue.arrayUnion(imageUri))
                    }

                    finish()

                }.addOnFailureListener{
                    Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


}