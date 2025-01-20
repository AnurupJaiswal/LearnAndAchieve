package com.anurupjaiswal.learnandachieve.basic.firebase

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.SavedData.saveFirebaseToken
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.IS_LOGIN
import com.anurupjaiswal.learnandachieve.basic.firebase.NotificationUtils.isAppIsInBackground


import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FirebaseService : FirebaseMessagingService() {
    var activity: Service = this@FirebaseService
    var NOTIFICATION_CHANNEL_ID = "dollop.journalisation" + System.currentTimeMillis()
    var m = 0
    var notificationTitle = Constants.blank
    var messageBody = Constants.blank
    var activityClass: Class<*>? = null
    private var action: String? = null
    private fun handleDataMessage(json: JSONObject) {
        try {
            val data = json.getJSONObject(Constants.data)
            action = data.getString(Constants.action)
            val payload: JSONObject
            if (data.getString(Constants.payload) != "") {
                payload = JSONObject(data.getString(Constants.payload))
                /* cashBack = payload.getString("cash_back");
                availableBalance = payload.getString("wallet");*/
            }
            E("action:$action")
            // if()
            m = Random().nextInt()
            if (!isAppIsInBackground(applicationContext)) {
                val pushNotification = Intent(Config.PUSH_NOTIFICATION)
                pushNotification.putExtra(Constants.action, action)
                pushNotification.putExtra(Constants.title, data.getString(Constants.title))
                pushNotification.putExtra(Constants.message, data.getString(Constants.message))
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
            }
            sendNotification(json)
        } catch (e: Exception) {
            E("Exception: " + e.message)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        E("From: " + remoteMessage.from)
        if (remoteMessage.notification != null) {
            E("From:notification:: " + remoteMessage.notification)
            E(
                "Notification Body: " + remoteMessage.notification!!
                    .body
            )
            val jsonObject = JSONObject()
            try {
                jsonObject.put("title", remoteMessage.notification!!.title)
                jsonObject.put("message", remoteMessage.notification!!.body)
                //Testing Put this i
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            sendNotification(jsonObject)
        }
        if (remoteMessage.data.size > 0) {
            E("Data Payload: " + remoteMessage.data)
            try {
                val json = JSONObject(remoteMessage.data.toString())
                handleDataMessage(json)
            } catch (e: Exception) {
                E("Exception: " + e.message)
            }
        }
    }

    override fun onNewToken(token: String) {
        E("Refreshed token: service : $token")
        saveFirebaseToken(token)
        if (IS_LOGIN()) {
            sendRegistrationToServer(token, activity)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag", "NotificationPermission")
    private fun sendNotification(jsonObject: JSONObject) {
       /* try {
            //Testing
            *//*  notificationTitle = jsonObject.getString("title");
             messageBody = jsonObject.getString("message");*//*

            //Live
            val bundle = Bundle()
            val data = jsonObject.getJSONObject(Constants.data)
            action = data.getString(Constants.action)
            notificationTitle = data.getString(Constants.title)
            messageBody = data.getString(Constants.message)
            *//*bundle.putString(Constants.action, action);
            bundle.putString(Constants.title, data.getString(Constants.title));
            bundle.putString(Constants.message, data.getString(Constants.message));*//*activityClass =
                NotificationActivity::class.java
            val intent = Intent(this, activityClass)
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            var pendingIntent: PendingIntent? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent =
                    PendingIntent.getActivity(this, 123, intent, PendingIntent.FLAG_MUTABLE)
            }
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(notificationTitle)
                .setContentText(messageBody)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(messageBody)
                )
                .setAutoCancel(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setColor(ContextCompat.getColor(activity, R.color.white))
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_ID,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(m, notificationBuilder.build())
        } catch (e: Exception) {
            E("JSONException::$e")
            e.printStackTrace()
        }*/
    }

    companion object {
        fun GenerateToken(context: Context?) {
            FirebaseApp.initializeApp(context!!)
            FirebaseMessaging.getInstance().isAutoInitEnabled = true
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task: Task<String> ->
                    if (!task.isSuccessful) {
                        E("getInstanceId failed" + task.exception)
                        return@addOnCompleteListener
                    }
                    val token = task.result
                    E("FCMtoken>>:::: $token")
                    saveFirebaseToken(token)
                    if (IS_LOGIN()) {
                        sendRegistrationToServer(token, context)
                    }
                }
        }

        fun sendRegistrationToServer(token: String?, activity: Context?) {
          /*  client.UpdateFirebaseToken(SavedData.getToken(), token, Constants.Android)
                ?.enqueue(object : Callback<AllResponseModel?> {
                    override fun onResponse(
                        call: Call<AllResponseModel?>,
                        response: Response<AllResponseModel?>
                    ) {
                        try {
                            if (response.isSuccessful) {
                                E("Update Successfully")
                            } else if (response.code() == StatusCodeConstant.BAD_REQUEST) {
                                assert(response.errorBody() != null)
                                val message = Gson().fromJson(
                                    response.errorBody()!!.charStream(), APIError::class.java
                                )
                                CustomeToast(activity, message.err, Constants.warning)
                            } else if (response.code() == StatusCodeConstant.UNAUTHORIZED) {
                                assert(response.errorBody() != null)
                                val message = Gson().fromJson(
                                    response.errorBody()!!.charStream(), APIError::class.java
                                )
                                CustomeToast(activity, message.err, Constants.warning)
                                UnAuthorizationToken(
                                    activity!!
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<AllResponseModel?>, t: Throwable) {}
                })*/
        }
    }
}