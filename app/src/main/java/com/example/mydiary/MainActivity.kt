package com.example.mydiary

import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

open class MainActivity : AppCompatActivity(), DiaryListFragment.OnFragmentInteractionListener {

    private var mRealm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mRealm = Realm.getDefaultInstance()
//        createTestData()
        showDiaryList()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm?.close()
    }

    private fun createTestData() {
        mRealm?.executeTransaction { realm: Realm ->
            val oneToNine = 1..9

            for (value in oneToNine) {
                val maxId = mRealm?.where(Diary::class.java)?.max("id")

                var nextId: Long = 0
                if(maxId == null) {
                    nextId = 1L
                } else {
                    nextId = maxId.toLong().plus(1L)
                }

                val diary = realm.createObject(Diary::class.java, nextId)
                diary.title = "テストタイトル${value}"
                diary.bodyText = "テスト${value}本文です本文です。"
                diary.date = "Feb 2${value}"
            }
        }
    }

    private fun showDiaryList() {
        val manager = supportFragmentManager
        var fragment = manager.findFragmentByTag("DiaryListFragment")
        if(fragment == null) {
            fragment = DiaryListFragment()
            val transaction = manager.beginTransaction()
            transaction.add(R.id.content, fragment, "DiaryListFragment")
            transaction.commit()
        }
    }

    override fun onAddDiarySelected() {
        mRealm?.beginTransaction()
        val maxId = mRealm?.where(Diary::class.java)?.max("id")
        var nextId: Long = 1L
        if(maxId != null) {
            nextId = maxId.toLong() + 1L
        }
        val diary = mRealm?.createObject(Diary::class.java, nextId)
        diary?.date = SimpleDateFormat("MMM d", Locale.US).format(Date())
        mRealm?.commitTransaction()

        val inputDiaryFragment = InputDiaryFragment.newInstance(nextId)
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.content, inputDiaryFragment, "InputDiaryFragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
