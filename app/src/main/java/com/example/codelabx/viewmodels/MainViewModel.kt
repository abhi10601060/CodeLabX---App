package com.example.codelabx.viewmodels

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FilenameFilter
import java.lang.Exception
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
        val fileList =  currFile.listFiles()

        if (fileList != null) {
            filesLivedata.postValue(fileList.asList())
        }
        else{
            Log.d("ABHI", "getAllFilesFromCurDirectory: file list is null")
        }
    }

    fun getCurDirectoryName() : String{
        val curDir = File(currFilePath)
        return curDir.name
    }
}