package com.example.codelabx.viewmodels

import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codelabx.models.UserEvent
import com.example.codelabx.repos.MainRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class MainViewModel constructor(private val repo : MainRepo) : ViewModel(){

    /* WebSocket Handling */
    val stdout : LiveData<String>
    get() = repo.stdout

    val connFailure : LiveData<Int>
        get() = repo.connFailure

    fun setWebSocketConn(){
        repo.setWebSocketConn()
    }

    fun writeMessageToConn(userEvent: UserEvent){
        repo.writeMessageToConn(userEvent)
    }

    /* File Handling */

    private val mainFilePath = Environment.getExternalStoragePublicDirectory("Download").toString() + "/myfile"
    private var currDirPath = mainFilePath
    private var currOpenFile = ""

    private val filesLivedata = MutableLiveData<List<File>>()
    val files : LiveData<List<File>>
    get() = filesLivedata

    private val activeFileLivedata = MutableLiveData<String>("null")
    val activeFile : LiveData<String>
        get() = activeFileLivedata

    fun getAllFilesFromCurDirectory(){
        val currFile = File(currDirPath)
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
        val curDir = File(currDirPath)
        return curDir.name
    }

    fun createCodeLabXFile(fileName : String , type : String){
        var extension = ".txt"
        when(type){
            "python" -> extension = ".py"
            "java" -> extension =".java"
            "cpp" -> extension=".cpp"
        }
        val filePath = currDirPath + "/" + fileName + extension

        val newFile = File(filePath)
        newFile.createNewFile()
        getAllFilesFromCurDirectory()
    }

    fun createCodeLabXFolder(folderName : String){
        val folderPath = currDirPath + "/" + folderName
        val folder = File(folderPath)
        folder.mkdir()
        getAllFilesFromCurDirectory()
    }

    fun openFolder(name : String){
        currDirPath = currDirPath + "/" + name
    }

    fun back(){
        if (currDirPath != mainFilePath){
            val curDirectory = File(currDirPath)
            currDirPath = curDirectory.parent
        }
    }

    fun readFile(file : File) : String{
        if (file.exists()){
            activeFileLivedata.postValue(file.absolutePath)
            var data : StringBuilder = StringBuilder()
            val sc : Scanner = Scanner(file)

            while (sc.hasNextLine()){
                data.append(sc.nextLine())
                data.append("\n")
            }
            currOpenFile = file.path
            return data.toString()
        }
        return "No Readable Data Found!!!"
    }

    fun saveFile(file : File , code : String ){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileWriter  = FileWriter(file)
                val bw = BufferedWriter(fileWriter)
                bw.write("")
                bw.write(code)
                bw.close()
                Log.d("ABHI", "saveFile: File saved successfully")
            }
            catch (ex: Exception){
                Log.d("ABHI", "saveFile: cant write exception : ${ex.message}")
            }
        }
    }

    fun deleteFile(file : File){
        try{
            if (file.absolutePath.equals(activeFile.value)) activeFileLivedata.postValue("null")
            file.delete()
            getAllFilesFromCurDirectory()
            getAllCodeLabFilesFolders(file.parentFile)
        }
        catch (ex : Exception){
            Log.d("ABHI", "deleteFile: can't delete file")
        }
    }
}