package com.example.mydiary

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

class DiaryRealmAdapter(context: Context?, data: OrderedRealmCollection<Diary>?, autoUpdate: Boolean) : RealmRecyclerViewAdapter<Diary, DiaryRealmAdapter.DiaryViewHolder>(data, autoUpdate) {
    var context: Context

    init {
        this.context = context!!
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diary = data?.get(position)
        holder.title.setText(diary?.title)
        holder.bodyText.setText(diary?.bodyText)
        holder.date.setText(diary?.date)
        if(diary?.image != null && diary?.image.size != 0) {
            val bmp = MyUtils.getImageFromByte(diary?.image)
            holder.photo.setImageBitmap(bmp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        val holder = DiaryViewHolder(itemView)
        Log.d("My Diary", "val holder")
        holder.itemView.setOnClickListener { view: View ->
            val position = holder.adapterPosition
            Log.d("My Diary", "val position")
            val diary = data?.get(position)
            Log.d("My Diary", "val diary")
            val diaryId = diary?.id
            Log.d("My Diary", "val diaryId")
            val intent = Intent(context, ShowDiaryActivity::class.java)
            Log.d("My Diary", "val intent")
            intent.putExtra("DIARY_ID", diaryId)
            context.startActivity(intent)
            Log.d("My Diary", "context.startActivity(intent)")
        }

        return holder
    }

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var bodyText: TextView
        var date: TextView
        var photo: ImageView
        init {
            title = itemView.findViewById<TextView>(R.id.title)
            bodyText = itemView.findViewById<TextView>(R.id.body)
            date = itemView.findViewById<TextView>(R.id.date)
            photo = itemView.findViewById<ImageView>(R.id.diary_photo)
        }
    }
}