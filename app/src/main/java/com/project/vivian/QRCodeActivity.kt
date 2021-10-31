package com.project.vivian

import android.R.attr
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.qr_code_prueba.*
import kotlinx.android.synthetic.main.subir_imagenes.*
import androidmads.library.qrgenearator.QRGEncoder

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.Button

import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.zxing.WriterException

import androidmads.library.qrgenearator.QRGContents
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import android.R.attr.data
import android.net.Uri


class QRCodeActivity : AppCompatActivity() {


    private var qrCodeIV: ImageView? = null
    private var dataEdt: EditText? = null
    private var generateQrBtn: Button? = null
    var bitmap: Bitmap? = null
    var qrgEncoder: QRGEncoder? = null

    private val database = FirebaseDatabase.getInstance()
    private val myRef : DatabaseReference = database.getReference("usuario")

    companion object {
        fun newInstance(): SubirImgActivity = SubirImgActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.qr_code_prueba)

        // initializing all variables.
        qrCodeIV = findViewById(R.id.idIVQrcode);
        dataEdt = findViewById(R.id.idEdt);
        generateQrBtn = findViewById(R.id.idBtnGenerateQR);

        idBtnGenerateQR.setOnClickListener {
                // below line is for getting
                // the windowmanager service.
                val manager = getSystemService(WINDOW_SERVICE) as WindowManager

                val display = manager.defaultDisplay

                val point = Point()
                display.getSize(point)

                // getting width and
                // height of a point
                val width = point.x
                val height = point.y

                // generating dimension from width and height.
                var dimen = if (width < height) width else height
                dimen = dimen * 3 / 4

                // setting this dimensions inside our qr code
                // encoder to generate our qr code.
            qrgEncoder =
                QRGEncoder(idEdt.getText().toString(), null, QRGContents.Type.TEXT, dimen)
            try {
                // getting our qrcode in the form of bitmap.
                bitmap = qrgEncoder!!.encodeAsBitmap()
                // the bitmap is set inside our image
                // view using .setimagebitmap method.
                Log.v("BIT",bitmap.toString())
                idIVQrcode.setImageBitmap(bitmap)

                var baos : ByteArrayOutputStream = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG,90,baos)
                var data : ByteArray = baos.toByteArray()

                val folder: StorageReference = FirebaseStorage.getInstance().reference.child("qrcode")
                val fileName: StorageReference = folder.child("reserva-MnMYT8vZ7xLP8RZnGSJ")

                val uploadTask: UploadTask = fileName.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }
                .addOnSuccessListener { taskSnapshot -> // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    val downloadUrl: Uri? = taskSnapshot.uploadSessionUri
                }


            } catch (e: WriterException) {
                // this method is called for
                // exception handling.
                Log.v("Tag", e.toString())
            }
        }
    }
}