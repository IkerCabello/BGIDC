package com.example.myapplication.dao

import com.example.myapplication.BuildConfig
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

const val api_url: String = BuildConfig.SENDINBLUE_API_URL
const val api_key: String = BuildConfig.SENDINBLUE_API_KEY // sendinblue

fun sendEmail(email: String, username: String, password: String) {

    val client = OkHttpClient()

    // URL of Sendinblue API
    val url = api_url

    // Create the JSON object that represents the email
    val jsonObject = JSONObject()
    jsonObject.put("sender", JSONObject().put("email", "idvkm.sender@gmail.com"))
    jsonObject.put("to", JSONArray().put(JSONObject().put("email", email)))
    jsonObject.put("subject", "Temporary credentials")
    jsonObject.put("htmlContent", "<html>" +
            "<body>" +
            "<h1>Welcome</h1>" +
            "Your temporal user name is: $username"+
            "Your temporal password is: $password"+
            "In order to finish with the registration, login in the app and add your personal information" +
            "</body>" +
            "</html>")

    val requestBody =
        jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .addHeader("api-key", api_key)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                println("Email sent successfully")
            } else {
                println("Error sending the email: ${response.message}")
            }
        }
    })
}

fun sendSecurityEmail(email: String) {

    val client = OkHttpClient()

    // URL of Sendinblue API
    val url = api_url

    // Create the JSON object that represents the email
    val jsonObject = JSONObject()
    jsonObject.put("sender", JSONObject().put("email", "idvkm.sender@gmail.com"))
    jsonObject.put("to", JSONArray().put(JSONObject().put("email", email)))
    jsonObject.put("subject", "Security warning")
    jsonObject.put("htmlContent", "<html>" +
            "<body>" +
            "<h1>Warning</h1>" +
            "<p>Your account is in danger" +
            "<p>We've noticed suspicious activity on your account. Someone has tried to log in to your account several times without success.</p>" +
            "<p>We recommend changing your password to ensure the security of your data.</p>" +
            "</body>" +
            "</html>")

    val requestBody =
        jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .addHeader("api-key", api_key)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                println("Email sent successfully")
            } else {
                println("Error sending the email: ${response.message}")
            }
        }
    })
}

fun sendPasswordResetEmail(email: String, code: String) {

    val client = OkHttpClient()

    // URL of Sendinblue API
    val url = api_url

    // Create the JSON object that represents the email
    val jsonObject = JSONObject()
    jsonObject.put("sender", JSONObject().put("email", "idvkm.sender@gmail.com"))
    jsonObject.put("to", JSONArray().put(JSONObject().put("email", email)))
    jsonObject.put("subject", "Password reset")
    jsonObject.put("htmlContent", "<html>" +
            "<body>" +
            "<p>Here is your verification code in order to change the password</p>" +
            "<h2>$code</h2>" +
            "</body>" +
            "</html>")

    val requestBody =
        jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .addHeader("api-key", api_key)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                println("Email sent successfully")
            } else {
                println("Error sending the email: ${response.message}")
            }
        }
    })
}