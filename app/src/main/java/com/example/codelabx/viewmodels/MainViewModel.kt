package com.example.codelabx.viewmodels

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel(){

    /* File Handling */

    private val mainFilePath = Environment.getExternalStoragePublicDirectory("Download").toString() + "/myfile"
    private var currFilePath = mainFilePath

    private val filesLivedata = MutableLiveData<List<File>>()
    val files : LiveData<List<File>>
    get() = filesLivedata

    fun getAllFilesFromCurDirectory(){
        val currFile = File(currFilePath)
        val fileList = getAllCodeLabFilesFolders(currFile)

        if (fileList != null) {
            filesLivedata.postValue(fileList)
        }
        else{
            Log.d("ABHI", "getAllFilesFromCurDirectory: file list is null")
        }
    }

    private fun getAllCodeLabFilesFolders(curDir : File) : List<File>{

        curDir.walk().forEach {
            Log.d("ABHI", "getAllCodeLabFilesFolders: ${it.name}")
        }

        val fileList = curDir.listFiles()
        val names  = curDir.list()
        Log.d("ABHI", "getAllCodeLabFilesFolders: name ${names.size}")
        Log.d("ABHI", "getAllCodeLabFilesFolders: len ${fileList.size}")
        val codeLabFiles : MutableList<File> = ArrayList<File>()
        val files : MutableList<File> = ArrayList<File>()
        for (file in fileList){
            Log.d("ABHI", "getAllCodeLabFilesFolders: ${file.extension}")
            if (file.isDirectory){
                codeLabFiles.add(file)
                continue
            }
            if (file.extension=="py"|| file.extension.equals("java") || file.extension.equals("cpp")){
                files.add(file)
            }
        }
        Log.d("ABHI", "getAllCodeLabFilesFolders: ${files.toString()}")
        codeLabFiles.addAll(files)

        return codeLabFiles
    }

    fun getCurDirectoryName() : String{
        val curDir = File(currFilePath)
        return curDir.name
    }
}