package com.example.codelabx.ui.activities

import android.Manifest
import android.app.Activity
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
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.codelabx.BuildConfig
import com.example.codelabx.R
import com.example.codelabx.adapters.FilesAdapter
import com.example.codelabx.models.UserEvent
import com.example.codelabx.repos.MainRepo
import com.example.codelabx.utility.SharedPref
import com.example.codelabx.viewmodels.MainViewModel
import com.example.codelabx.viewmodels.MainViewModelFactory
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() , FilesAdapter.CodeLabXFileOnClick{

    lateinit var editor : EditText

    lateinit var saveBtn : ImageView
    lateinit var codeEditor : EditText
    lateinit var filesRV : RecyclerView
    lateinit var curDirName : TextView
    lateinit var openedFileName : TextView
    lateinit var runBtn : ImageView

    lateinit var createFile : ImageView
    lateinit var createFolder : ImageView
    lateinit var backDirectory : ImageView
    lateinit var filesNav : NavigationView
    lateinit var stdoutNav : NavigationView
    lateinit var drawer : DrawerLayout
    lateinit var stdout : TextView
    lateinit var logo : ImageView

    lateinit var viewModel: MainViewModel
    lateinit var filesAdapter: FilesAdapter
    private  val TAG = "ABHI"

    private lateinit var activeFile : File

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createView()
        checkStoragePermission()
        createViewModel()
        setupFiles()
        setOnclicks()
        observeActiveFile()
        observeStdout()
        observeConnFailure()
        observeDrawerState()

    }

    private fun observeActiveFile() {
        viewModel.activeFile.observe(this , Observer{activeFilePath ->
            if (activeFilePath.equals("null")){
                runBtn.visibility = View.INVISIBLE
                saveBtn.visibility = View.INVISIBLE
                openedFileName.text = "Welcome to codelabx"
                editor.isEnabled = false
                editor.setPadding(10 ,50 , 10 , 0)
                editor.setText("")
                editor.gravity = Gravity.CENTER_HORIZONTAL
                editor.hint = resources.getString(R.string.createFileHint)
            }
            else{
                runBtn.visibility = View.VISIBLE
                saveBtn.visibility = View.VISIBLE
                editor.isEnabled = true
                editor.setPadding(20 ,20 , 0 , 0)
                editor.gravity = Gravity.NO_GRAVITY
                editor.hint = "</> Start coding here..."
            }
        })
    }

    private fun setOnclicks() {
        createFile.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "onCreate: clicked")
            showCreateFileDialog()
        })

        createFolder.setOnClickListener(View.OnClickListener {
            showCreateFolderDialog()
        })

        backDirectory.setOnClickListener(View.OnClickListener {
            viewModel.back()
            setupFiles()
        })

        runBtn.setOnClickListener(View.OnClickListener {
            viewModel.saveFile(activeFile , editor.text.toString())
            val userEvent = createUserEvent()
            viewModel.writeMessageToConn(userEvent)
        })

        saveBtn.setOnClickListener(View.OnClickListener {
            viewModel.saveFile(activeFile , editor.text.toString())
        })

        logo.setOnClickListener(View.OnClickListener {
            val rotateAnim = RotateAnimation(0f , 358f , Animation.RELATIVE_TO_SELF , 0.5f , Animation.RELATIVE_TO_SELF , 0.5f)
            rotateAnim.duration = 400L
            rotateAnim.interpolator = LinearInterpolator()
            logo.startAnimation(rotateAnim)
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                drawer.openDrawer(filesNav)
            }
        })
    }

    private fun createUserEvent(): UserEvent {
        var userName = "null"
        userName = SharedPref.getAuthDbInstance(this).getString(SharedPref.USER_KEY , "null").toString()
        val fileExtension = activeFile.extension
        var selectedLanguage = "default"
        when (fileExtension){
            "py" -> selectedLanguage = "python"
            "java" -> selectedLanguage = "java"
        }
        val code = codeEditor.text.toString()
        Log.d(TAG, "createUserEvent: username : $userName, lang : $selectedLanguage, code : $code")

        return UserEvent(userName, selectedLanguage, code)
    }

    private fun observeStdout(){
        viewModel.stdout.observe(this , Observer {
            Log.d("WEBSOCKET", "observeStdout: " +  it)
            if (it != null){
                drawer.closeDrawer(filesNav)
                drawer.openDrawer(Gravity.RIGHT)
                stdout.text = it
            }
        })
    }

    private fun observeConnFailure() {
        viewModel.connFailure.observe(this, Observer{
            if (it != null){
                showConnectionLostAlert()
            }
        })
    }

    private fun observeDrawerState() {
        drawer.setDrawerListener(object : DrawerListener{
            override fun onDrawerSlide(view : View, p1: Float) {
                hideKeyBoard(view)
                editor.clearFocus()
            }

            override fun onDrawerOpened(view: View) {}

            override fun onDrawerClosed(p0: View) {}

            override fun onDrawerStateChanged(p0: Int) {}
        })
    }

    private fun hideKeyBoard(view : View){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken , 0)
    }
    private fun showConnectionLostAlert() {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setPositiveButton("Retry", DialogInterface.OnClickListener { dialogInterface, i ->
                viewModel.writeMessageToConn(createUserEvent())
                dialogInterface.dismiss()
            })
            .setNegativeButton("cancel" , DialogInterface.OnClickListener { dialogInterface, i ->  dialogInterface.dismiss()})
            .setTitle("Connectivity Error")
            .setMessage("Connection lost to the server...")
            .show()
    }

    private fun setupFiles() {
        curDirName.text = viewModel.getCurDirectoryName()

        viewModel.getAllFilesFromCurDirectory()
        Log.d(TAG, "onCreate: ${viewModel.getCurDirectoryName()}")
        viewModel.files.observe(this , Observer {

            filesAdapter = FilesAdapter(this)
            filesAdapter.submitList(it)
            filesRV.adapter = filesAdapter
            filesRV.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL , false)

        })
    }
    fun showCreateFolderDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.create_folder_dialog)


        val nameEdt = dialog.findViewById<EditText>(R.id.folder_name_edt)

        val cancel = dialog.findViewById<Button>(R.id.btn_cancel_folder_dialog)
        cancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        val create = dialog.findViewById<Button>(R.id.btn_create_folder_dialog)
        create.setOnClickListener(View.OnClickListener {
            val folderName = nameEdt.text.toString()
            createCodeLabXFolder(folderName)
            dialog.dismiss()
        })

        dialog.show()
    }

    fun createCodeLabXFolder(folderName : String){
        viewModel.createCodeLabXFolder(folderName)
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
        viewModel = ViewModelProvider(this , MainViewModelFactory(MainRepo)).get(MainViewModel::class.java)
    }

    /*                                  View Creation                                                                    */
    private fun createView() {

        editor = findViewById(R.id.code_editor)
        runBtn = findViewById(R.id.run_icon)

        saveBtn = findViewById(R.id.save_imag)
        codeEditor = findViewById(R.id.code_editor)
        filesRV = findViewById(R.id.files_recycler_view)
        curDirName = findViewById(R.id.cur_working_dir)
        openedFileName = findViewById(R.id.opened_file_title)

        createFile = findViewById(R.id.create_file)
        createFolder = findViewById(R.id.create_folder)
        backDirectory = findViewById(R.id.back_btn)

        filesNav = findViewById(R.id.files_nav)
        drawer = findViewById(R.id.main_drawer)
        stdoutNav = findViewById(R.id.stdout_drawer)
        stdout = findViewById(R.id.stdout_textview)
        logo = findViewById(R.id.logo)

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

    override fun folderClicked(folder: File) {
        viewModel.openFolder(folder.name)
        setupFiles()
    }

    override fun fileClicked(file: File) {
        activeFile = file
        val data = viewModel.readFile(file)
        openedFileName.text = file.name
        editor.setText(data)
        drawer.closeDrawers()
    }

    override fun fileDeleteClicked(file: File) {
        viewModel.deleteFile(file)
    }


}