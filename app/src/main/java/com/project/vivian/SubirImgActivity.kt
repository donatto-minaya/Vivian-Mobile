package com.project.vivian

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.subir_imagenes.*

class SubirImgActivity : AppCompatActivity() {

    private val fileResult = 1

    companion object
    {
        fun newInstance(): SubirImgActivity = SubirImgActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subir_imagenes)

        button_subirimg.setOnClickListener {
            fileManager()
        }

    }

    private fun fileManager() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, fileResult)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileResult) {
            if (resultCode == RESULT_OK && data != null) {
                val uri = data.data
                uri?.let { imageUpload(it) }
            }
        }
    }

    private fun imageUpload(mUri: Uri) {

        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("producto")
        val fileName: StorageReference = folder.child("img-MnHDsh2c8_I319_J1eH")

        fileName.putFile(mUri).addOnSuccessListener { object : OnSuccessListener<UploadTask.TaskSnapshot>{
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                Toast.makeText(SubirImgActivity.newInstance(),"Se subio correctamente",Toast.LENGTH_SHORT)
            }

        }

        }.addOnFailureListener {
            Log.i("TAG", "file upload error")
        }
    }
}