package com.example.mydiary

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm


class DiaryListFragment : Fragment() {
    private var mRealm: Realm? = null
    private var mListener: OnFragmentInteractionListener? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_diary_list, menu)
        val addDiary = menu.findItem(R.id.menu_item_add_diary)
        val deleteAll = menu.findItem(R.id.menu_item_delete_all)
        MyUtils.tintMenuIcon(context!!, addDiary, android.R.color.white)
        MyUtils.tintMenuIcon(context!!, deleteAll, android.R.color.white)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item_add_diary -> {
                mListener?.onAddDiarySelected()
                return true
            }
            R.id.menu_item_delete_all -> {
                val diaries = mRealm?.where(Diary::class.java)?.findAll()
                mRealm?.executeTransaction { realm: Realm ->
                    diaries?.deleteAllFromRealm()
                }
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val view = inflater.inflate(R.layout.fragment_diary_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        val llm = LinearLayoutManager(activity)
        llm.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = llm
        val diaries = mRealm?.where(Diary::class.java)?.findAll()
        val adapter = DiaryRealmAdapter(activity, diaries, true)
        recyclerView.adapter = adapter
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
//        fun onFragmentInteraction(uri: Uri)

        fun onAddDiarySelected()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DiaryListFragment().apply {
            }
    }
}
