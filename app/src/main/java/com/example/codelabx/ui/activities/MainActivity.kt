package com.example.codelabx.ui.activities

import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codelabx.BuildConfig
import com.example.codelabx.R
import com.example.codelabx.adapters.FilesAdapter
import com.example.codelabx.viewmodels.MainViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import kotlin.math.log

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var spinner : Spinner
    lateinit var codeEditor : EditText
    lateinit var filesRV : RecyclerView
    lateinit var curDirName : TextView
    lateinit var createFile : ImageView

    lateinit var viewModel: MainViewModel
    lateinit var filesAdapter: FilesAdapter
    private  val TAG = "ABHI"

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createView()
        checkStoragePermission()
        createViewModel()
        setupFiles()

        createFile.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "onCreate: clicked")
            showCreateFileDialog()
        })

    }

    private fun setupFiles() {
        curDirName.text = viewModel.getCurDirectoryName()

        viewModel.getAllFilesFromCurDirectory()
        Log.d(TAG, "onCreate: ${viewModel.getCurDirectoryName()}")
        viewModel.files.observe(this , Observer {

            filesAdapter = FilesAdapter()
            filesAdapter.submitList(it)
            filesRV.adapter = filesAdapter
            filesRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL , false)

        })
    }

    fun showCreateFileDialog(){
        val dialog  = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.create_file_dialogue)

        val nameEdt = dialog.findViewById<EditText>(R.id.file_name_edt)

        val dialogSpinner = dialog.findViewById<Spinner>(R.id.languages_spinner_dialogue)
        val adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogSpinner.adapter = adapter

        val cancel = dialog.findViewById<Button>(R.id.btn_cancel_dialog)
        cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        val create = dialog.findViewById<Button>(R.id.btn_create_dialog)
        create.setOnClickListener(View.OnClickListener {
            val fileName = nameEdt.text.toString()
            val type = dialogSpinner.selectedItem.toString()
            createCodeLabXFile(fileName , type);
            dialog.dismiss()
        })
        dialog.show()
    }

    private fun createCodeLabXFile(name:String , type : String) {
        viewModel.createCodeLabXFile(name,type)
    }

    private fun createViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    /*                                  View Creation                                                                    */
    private fun createView() {
        spinner = findViewById(R.id.languages_spinner)
        codeEditor = findViewById(R.id.code_editor)
        filesRV = findViewById(R.id.files_recycler_view)
        curDirName = findViewById(R.id.cur_working_dir)

        createFile = findViewById(R.id.create_file)

        setSpinner()
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    /*                                  Permission Handling                                                                   */
    private fun askAllMediaPermission() {
        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
        startActivityForResult(intent , 500)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 500){
            Toast.makeText(this, "All media permission granted..", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkStoragePermission(){
       if (ContextCompat.checkSelfPermission(this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
           ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
           askStoragePermissions()
       }
        else{
            if (!Environment.isExternalStorageManager()){
                askAllMediaPermission()
            }
       }
    }

    private fun askStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            AlertDialog.Builder(this)
                .setTitle("Storage Permission Required")
                .setMessage("Storage permission is required to manage the file.")
                .setCancelable(false)
                .setPositiveButton("Grant Permission", DialogInterface.OnClickListener { dialogInterface, i ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE) , 200)
                })
                .create().show()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE) , 200)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 200){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted...", Toast.LENGTH_SHORT).show()
                checkStoragePermission()
            }
            else{
                Toast.makeText(this, "Permission Denied!!!", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}