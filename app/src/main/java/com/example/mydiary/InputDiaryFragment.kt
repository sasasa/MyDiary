package com.example.mydiary

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import java.io.IOException


class InputDiaryFragment : Fragment() {
    private var mDiaryId: Long = 0
    private var mRealm: Realm? = null
    private var mTitleEdit: EditText? = null
    private var mBodyEdit: EditText? = null
    private var mDiaryImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null) {
           mDiaryId = arguments!!.getLong(DIARY_ID)
        }
        mRealm = Realm.getDefaultInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        mRealm?.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_input_diary, container, false)
        mTitleEdit = v.findViewById(R.id.title)
        mBodyEdit = v.findViewById(R.id.bodyEditText)
        mDiaryImage = v.findViewById(R.id.diary_photo)
        mDiaryImage?.setOnClickListener { view: View ->
            requestReadStorage(view)
        }
        mTitleEdit?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                mRealm?.executeTransactionAsync { realm: Realm ->
                    val diary = realm.where(Diary::class.java).equalTo("id", mDiaryId).findFirst()
                    diary?.title = s.toString()
                }
            }
        })
        mBodyEdit?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                mRealm?.executeTransactionAsync { realm: Realm ->
                    val diary = realm.where(Diary::class.java).equalTo("id", mDiaryId).findFirst()
                    diary?.bodyText = s.toString()
                }
            }
        })
        return v
    }

    private fun requestReadStorage(view: View) {
        if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, R.string.rationale, Snackbar.LENGTH_LONG).show()
            }
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
        } else {
            pickImage()
        }
    }
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setType("image/*")
        startActivityForResult(Intent.createChooser(intent, getString(R.string.pick_image)), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mDiaryImage!!, R.string.permission_deny, Snackbar.LENGTH_LONG).show()
            } else {
                pickImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = data?.data
            if(uri != null) {
                try {
                    val img = MyUtils.getImageFromStream(activity!!.contentResolver, uri)
                    mDiaryImage?.setImageBitmap(img)
                } catch (ex : IOException) {
                    ex.printStackTrace()
                }
                

            }
        }
    }

    companion object {
        private const val DIARY_ID = "DIARY_ID"
        private const val REQUEST_CODE = 1
        private const val PERMISSION_REQUEST_CODE = 2

        @JvmStatic
        fun newInstance(diaryId: Long): InputDiaryFragment {
            val fragment = InputDiaryFragment()
            val args = Bundle()
            args.putLong(DIARY_ID, diaryId)
            fragment.arguments = args
            return fragment
        }
    }
}
