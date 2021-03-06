package com.srnunios.contatosdemo

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_listagem.*
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.support.v4.app.ActivityCompat
import java.util.*


class ListagemActivity : AppCompatActivity() {

    var adapterContactos: ContactoAdapter? = null
    val CONTACTO_PERMISION_REQUEST = 2018

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listagem)

        recycler.setHasFixedSize(false)
        recycler.layoutManager = LinearLayoutManager(this)
        this.adapterContactos = ContactoAdapter()
        this.recycler.adapter = adapterContactos
    }


    fun onPermision(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) { //verifica se app tem permissão para ler os contactos
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_CONTACTS
                    , Manifest.permission.WRITE_CONTACTS), CONTACTO_PERMISION_REQUEST)

            return
        }
        read_contact()
    }

    private fun rawContactsList(): List<Contacto> {
        var list = ArrayList<Contacto>()
        val contentResolver = contentResolver
        val projection = arrayOf<String>(ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DISPLAY_NAME,
                ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.Data.DATA1,
                ContactsContract.Contacts.Data.DATA2, ContactsContract.Contacts.Data.DATA3)

        val selection = ContactsContract.Data.MIMETYPE + " IN ('" + Phone.CONTENT_ITEM_TYPE + "')"
        val cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            do {
                list.add(Contacto(cursor.getString(1), cursor.getString(3)))
            } while (cursor.moveToNext())
        }
        cursor!!.close()
        return list
    }

    override fun onStart() {
        super.onStart()
        onPermision(this)
    }

    private fun read_contact() {
        var list = rawContactsList()
        Collections.sort(list)
        adapterContactos!!.contactos.addAll(list)
        adapterContactos!!.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        Log.println(Log.ERROR,"RequestPermissions"," requestCode=> "+requestCode)
        when (requestCode) {
            CONTACTO_PERMISION_REQUEST -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    read_contact()
                }else{
                    finish()
                }
            }
        }
    }


}
