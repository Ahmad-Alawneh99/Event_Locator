package com.eventlocator.eventlocator.utilities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import java.io.FileDescriptor
import java.io.IOException


class Utils {

    companion object{
        val instance: Utils = Utils()
    }

    fun isEmail(s: String): Boolean{
        if (s.indexOf('@') == -1 || s.indexOf('.') == -1)return false
        else if (!isChar(s[0]) || !isChar(s[s.length - 1])) return false
        var at = -1
        var lastDot = -1
        var atCount = 0
        for(i in s.length-1 downTo 0){
            if (s[i] == '@'){
                if (at == -1)at = i
                atCount++
            }
            else if (s[i] == '.' && lastDot == -1)
                lastDot = i

        }
        if (atCount> 1 || at > lastDot) return false
        return true
    }

    private fun isChar(c: Char): Boolean{
        return c in 'a'..'z' || c in 'A'..'Z'
    }

    fun countWords(s: String): Int {
        var count = 0
        var res = s.split(' ')
        for(i in res.indices){
            if (res[i].trim()!="")
                count++
        }
        return count
    }

    fun connectWordsIntoString(words: List<String>): String{
        var res = ""
        for(i in words.indices){
            if (words[i].trim()!="")
                res+=words[i]+' '
        }
        return res.trim()
    }

    fun getLongestCommonSubsequenceLength(s1: String, s2: String): Int{
        val matrix = Array(s1.length+1) {IntArray(s2.length+1) {0}}

        for(i in 1 .. s1.length){
            for(j in 1 .. s2.length){
                if (i==0 || j==0)
                    matrix[i][j] = 0
                else if (s1[i-1] == s2[j-1])
                    matrix[i][j] = matrix[i-1][j-1] + 1
                else
                    matrix[i][j] = Math.max(matrix[i][j-1], matrix[i-1][j])
            }
        }
        return matrix[s1.length][s2.length]
    }

    fun displayInformationalDialog(context: Context, title: String, message:String, finish: Boolean){
        AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("OK")
        { di, i -> if (finish) (context as Activity).finish() }.create().show()
    }

    fun createSimpleDialog(context: Context, title: String, message: String): AlertDialog.Builder{
        return AlertDialog.Builder(context).setTitle(title).setMessage(message)
    }
}