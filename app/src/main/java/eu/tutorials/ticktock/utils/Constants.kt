package eu.tutorials.ticktock.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import eu.tutorials.ticktock.activities.CreateBoardActivity

object Constants {
    const val USERS: String = "Users"
    const val BOARDS: String = "Boards"
    const val NAME: String = "name"
    const val IMAGE: String = "image"
    const val MOBILE: String = "mobile"
    const val ASSIGNED_TO: String = "assignedTo"
    const val READ_STORAGE_PERMISSION_CODE: Int = 1
    const val PICK_IMAGE_REQUEST_CODE: Int = 2
    const val DOC_ID: String = "docID"
    const val TASK_LIST: String = "taskList"
    const val BOARD_DETAIL: String = "boardDetail"
    const val ID: String = "id"
    const val EMAIL: String = "email"
    const val TASK_LIST_ITEM_POS: String = "task_list_item_pos"
    const val CARD_LIST_ITEM_POS: String = "card_list_item_pos"
    const val BOARD_MEMBERS_LIST: String = "board_members_list"
    const val SELECT: String = "Select"
    const val UN_SELECT: String = "UnSelect"
    const val TICKTOCK_PREFERENCES: String = "TickTock_Preferences"
    const val FCM_TOKEN_UPDATED: String = "fcmTokenUpdated"
    const val FCM_TOKEN: String = "fcmToken"
    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAA7-eGZbI:APA91bHKr76Gkxi4gFtqDqkqz_Td7Adgq2jekYZ3B5R_YJ27hbEvFuzWo8GqLMZ6PQyrU0OjvQYN5GF-3Bw1s038YYIWI7gg8QYQtP-gaBSY7d06J8WEqIf0usRCa0wv9lZyJpU7_2VI"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    fun showImageChooser(activity: Activity) {
        var galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}