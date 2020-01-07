package com.example.mydiary

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.palette.graphics.Palette
import com.google.android.material.appbar.CollapsingToolbarLayout
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_show_diary.*
import org.w3c.dom.Text

class ShowDiaryActivity : MainActivity() {
    companion object {
        private const val DIARY_ID = "DIARY_ID"
        private const val ERROR_CODE = -1L
    }
    private var mBodyText: String? = null
    private var mRealm: Realm? = null
    private var mBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_diary)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, mBodyText)
            shareIntent.setType("text/plain")
            startActivity(shareIntent)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mRealm = Realm.getDefaultInstance()
        val diaryId: Long = intent.getLongExtra(DIARY_ID, ERROR_CODE)
        val body = findViewById<TextView>(R.id.body)
        val imageView = findViewById<ImageView>(R.id.toolbar_image)
        val scrollView = findViewById<NestedScrollView>(R.id.scroll_view)

        val diary = mRealm?.where(Diary::class.java)?.equalTo("id", diaryId)?.findFirst()
        val layout = findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)
        layout.title = diary?.title
        mBodyText = diary?.bodyText
        body.setText(diary?.bodyText)
        val bytes = diary?.image
        if(bytes != null && bytes.size > 0) {
            mBitmap = MyUtils.getImageFromByte(bytes)
            imageView.setImageBitmap(mBitmap)

            val palette = Palette.from(mBitmap!!).generate()

            val titleColor = palette.getLightVibrantColor(Color.WHITE)
            val bodyColor = palette.getDarkMutedColor(Color.BLACK)
            val scrimColor = palette.getMutedColor(Color.DKGRAY)
            val iconColor = palette.getLightMutedColor(Color.LTGRAY)

            layout.setExpandedTitleColor(titleColor)
            layout.setContentScrimColor(scrimColor)
            scrollView.setBackgroundColor(bodyColor)
            body.setTextColor(titleColor)
            fab.backgroundTintList = ColorStateList.valueOf(iconColor)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm?.close()
    }
}
